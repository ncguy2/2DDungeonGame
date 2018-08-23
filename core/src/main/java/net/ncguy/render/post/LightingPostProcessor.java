package net.ncguy.render.post;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.assets.Sprites;
import net.ncguy.entity.Entity;
import net.ncguy.entity.component.EntityProperty;
import net.ncguy.entity.component.LightComponent;
import net.ncguy.entity.component.PrimitiveCircleComponent;
import net.ncguy.entity.component.RenderComponent;
import net.ncguy.lib.foundation.collections.ArrayUtils;
import net.ncguy.post.MultiPostProcessor;
import net.ncguy.profile.ProfilerHost;
import net.ncguy.util.ReloadableShaderProgram;
import net.ncguy.viewport.FBO;
import net.ncguy.viewport.FBOBuilder;
import net.ncguy.world.MainEngine;

import java.util.ArrayList;
import java.util.List;

public class LightingPostProcessor extends MultiPostProcessor<LightingPostProcessor> {

    FBO occluderFBO;
    FBO shadowmapFBO;
    FBO lightMapFBO;
    ReloadableShaderProgram occluderShader;
    ReloadableShaderProgram shadowShader;
    ReloadableShaderProgram applicationShader;

    TextureRegion occluders;
    TextureRegion shadowMap1D;

    SpriteBatch batch;
    OrthographicCamera cam;

    int lightSize = 256;
    @EntityProperty(Type = Float.class, Category = "Post processing", Description = "Scale of the lightmap", Name = "Light scale")
    float lightScale = 4;

    public void RebuildLightingBuffers() {

    }

    public LightingPostProcessor(MainEngine engine) {
        super(engine);
    }

    @Override
    public Texture[] Render(Batch batch, Camera camera, Texture[] input, float delta) {
        if(enabled)
            return _Render(batch, camera, input, delta);

        Texture[] output = new Texture[input.length + 1];
        System.arraycopy(input, 0, output, 0, input.length);
        output[input.length] = input[0];
        return output;
    }

    @Override
    public LightingPostProcessor Init() {
        ProfilerHost.Start("LightingPostProcessor::Init");

        batch = new SpriteBatch();
        cam = new OrthographicCamera();

        ProfilerHost.Start("FBO Initialization");

        frameBuffer = FBOBuilder.BuildScreenBuffer(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        ProfilerHost.Start("Occluders");
        occluderFBO = new FBO(Pixmap.Format.RGBA8888, lightSize, lightSize, false);
        Texture occluderTex = occluderFBO.getColorBufferTexture();
        occluderTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        occluders = new TextureRegion(occluderTex);
        occluders.flip(false, true);
        ProfilerHost.End("Occluders");

        ProfilerHost.Start("Shadow map buffer");
        shadowmapFBO = new FBO(Pixmap.Format.RGBA8888, lightSize, 1, false);
        Texture shadowTexture = shadowmapFBO.getColorBufferTexture();
        shadowTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        shadowTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        shadowMap1D = new TextureRegion(shadowTexture);
        shadowMap1D.flip(false, true);
        ProfilerHost.End("Shadow map buffer");

        ProfilerHost.Start("Lightmap buffer");
        lightMapFBO = new FBO(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        ProfilerHost.End("Lightmap buffer");

        ProfilerHost.End("FBO Initialization");

        ProfilerHost.Start("Shaders");
        occluderShader = new ReloadableShaderProgram("LightRenderer2::Occluder", Gdx.files.internal("shaders/screen.vert"), Gdx.files.internal("shaders/occluder.frag"));
        shadowShader = new ReloadableShaderProgram("LightRenderer2::Shadow", Gdx.files.internal("shaders/screen.vert"), Gdx.files.internal("shaders/lighting2.frag"));
        applicationShader = new ReloadableShaderProgram("LightRenderer2::Application", Gdx.files.internal("shaders/screen.vert"), Gdx.files.internal("shaders/shadowApplication.frag"));
        shader = new ReloadableShaderProgram("LightRenderer2::Screen", Gdx.files.internal("shaders/screen.vert"), Gdx.files.internal("shaders/screenLighting.frag"));
        ProfilerHost.End("Shaders");

        ProfilerHost.End("LightingPostProcessor::Init");
        return this;
    }

    public void ResizeBuffers(int lightSize, boolean actuallyResize) {
        lightSize /= lightScale;

        if(this.lightSize == lightSize)
            return;

        this.lightSize = lightSize;

        if(actuallyResize) {
            occluderFBO.Resize(lightSize, lightSize);
            shadowmapFBO.Resize(lightSize, 1);
        }
    }

    @Override
    public Texture[] _Render(Batch batch, Camera camera, Texture[] input, float delta) {

        ProfilerHost.Start("LightingPostProcessor::Render");

        ProfilerHost.Start("Lightmap clear");
        lightMapFBO.begin();
        lightMapFBO.clear(0, 0, 0, 0, false);
        lightMapFBO.end();
        ProfilerHost.End("Lightmap clear");

        ProfilerHost.Start("Lights");
        ProfilerHost.Start("Collection");
        List<Entity> entities = engine.world.GetFlattenedEntitiesWithComponents(LightComponent.class);
        List<LightComponent> collectedLights = new ArrayList<>();
        for (Entity entity : entities)
            collectedLights.addAll(entity.GetComponents(LightComponent.class, true));
        ProfilerHost.End("Collection");

        ProfilerHost.Start("Sorting");
        int max = collectedLights.stream()
                .map(l -> l.radius)
                .max(Float::compareTo)
                .map(Math::round)
                .orElse(256);
        ProfilerHost.End("Sorting");

        ProfilerHost.Start("Buffer resize");
        ResizeBuffers((int) (max), true);
        ProfilerHost.End("Buffer resize");

        ProfilerHost.Start("Render");
        collectedLights.stream().sorted((l1, l2) -> Float.compare(l1.radius, l2.radius)).forEach(l -> {
            this.RenderLight(l, input[3], camera);
        });
        ProfilerHost.End("Render");
        ProfilerHost.End("Lights");

        ProfilerHost.Start("Lighting render");
        cam.zoom = 1f;
        cam.setToOrtho(true);
        frameBuffer.begin();
        frameBuffer.clear(0, 0, 0, 1, false);
        batch.setShader(shader.Program());
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        lightMapFBO.getColorBufferTexture().bind(4);
        ProfilerHost.Start("Uniforms");
        shader.Program().setUniformi("u_Lighting", 4);
        shader.Program().setUniformf("intensity", .2f);
        shader.Program().setUniformf("ambientScale", .5f);
        shader.Program().setUniformf("gamma", 1f);
        ProfilerHost.End("Uniforms");
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        ProfilerHost.Start("Queue draw");
        batch.draw(input[0], 0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());
        ProfilerHost.End("Queue draw");
        ProfilerHost.Start("Draw");
        batch.end();
        ProfilerHost.End("Draw");
        frameBuffer.end();
        ProfilerHost.End("Lighting render");

        ProfilerHost.End("LightingPostProcessor::Render");
        return ArrayUtils.Append(Texture.class, input, frameBuffer.getColorBufferTexture());
    }

    void RenderLight(LightComponent light, Texture occluderTex, Camera camera) {
        ProfilerHost.Start("LightRenderer2::RenderLight [" + light.name + "]");

        ProfilerHost.Start("Fake resize");
        int round = Math.round(light.radius);
        ResizeBuffers(round, false);
        ProfilerHost.End("Fake resize");

        ProfilerHost.Start("Occluder render");
        occluderFBO.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Vector2 pos = new Vector2();
        light.transform.WorldTransform().getTranslation(pos);
        float size = lightSize * lightScale;
        pos.sub(size * .5f, size * .5f);
//        cam.setToOrtho(false, occluderFBO.getWidth(), occluderFBO.getHeight());
        cam.setToOrtho(false, size, size);
        cam.translate(pos);
        cam.update();

        ProfilerHost.Start("Scene render");
        batch.setProjectionMatrix(cam.combined);
        batch.setShader(occluderShader.Program());
        batch.begin();

        RenderScene(light.GetOwningEntity());
//        batch.draw(occluderTex, 0, 0, occluderFBO.getWidth(), occluderFBO.getHeight());

        batch.end();
        occluderFBO.end();
        ProfilerHost.End("Scene render");
        ProfilerHost.End("Occluder render");

        ProfilerHost.Start("Shadow render");
        shadowmapFBO.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setShader(shadowShader.Program());

//            cam.setToOrtho(false, shadowmapFBO.getWidth(), shadowmapFBO.getHeight());
        size = lightSize;
        cam.zoom = 1f;
        cam.setToOrtho(false, size, 1);
        batch.setProjectionMatrix(cam.combined);
        ProfilerHost.Start("Scene render");
        batch.begin();
        shadowShader.Program().setUniformf("u_resolution", lightSize, lightSize);
        shadowShader.Program().setUniformf("u_scale", lightScale);
        batch.draw(occluderFBO.getColorBufferTexture(), 0, 0, lightSize, 1);
        batch.end();
        shadowmapFBO.end();
        ProfilerHost.End("Scene render");
        ProfilerHost.End("Shadow render");

        AdditiveRenderLight(light, camera);

        batch.setShader(null);
        ProfilerHost.End("LightRenderer2::RenderLight");
    }

    public void AdditiveRenderLight(LightComponent light, Camera camera) {
        ProfilerHost.Start("LightRenderer2::AdditiveRenderLight [" + light.name + "]");
        ProfilerHost.Start("Reposition");
        Vector2 pos = new Vector2();
        light.transform.WorldTransform().getTranslation(pos);
        float size = lightSize * lightScale;
//        float size = lightSize;
        float s = size * ((OrthographicCamera) camera).zoom;
        pos.sub(s * .5f, s * .5f);
        ProfilerHost.End("Reposition");
        ProfilerHost.Start("Render");
        lightMapFBO.begin();
        batch.setShader(applicationShader.Program());
//        if(camera instanceof OrthographicCamera)
//            cam.zoom = ((OrthographicCamera) camera).zoom;
        cam.zoom = 1;
        cam.setToOrtho(false);
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        applicationShader.Program().setUniformf("u_zoom", ((OrthographicCamera) camera).zoom);
        applicationShader.Program().setUniformf("u_resolution", lightSize, lightSize);
        batch.setColor(light.colour);
        Vector3 v = new Vector3(pos, 0.f);
//        size = lightSize;
        camera.project(v);
        batch.draw(shadowmapFBO.getColorBufferTexture(), v.x, v.y, size, size);
        batch.end();
        batch.setColor(Color.WHITE);
        lightMapFBO.end();
        ProfilerHost.End("Render");
        ProfilerHost.End("LightRenderer2::AdditiveRenderLight");
    }

    // TODO replace with performant alternative
    public void RenderScene(Entity exclusion) {
        ProfilerHost.Start("LightRenderer2::RenderScene");
        ProfilerHost.Start("Entity render");
        List<Entity> entities = engine.world.GetFlattenedEntitiesWithComponents(RenderComponent.class);
        entities.remove(exclusion);
        entities.forEach(this::Accept);
        ProfilerHost.End("Entity render");

        entities = engine.world.GetFlattenedEntitiesWithComponents(PrimitiveCircleComponent.class);
        ProfilerHost.Start("Primitive render [" + entities.size() + "]");
        Vector2 pos = new Vector2();
        for (Entity entity : entities) {
            if(entity.equals(exclusion))
                continue;
            List<PrimitiveCircleComponent> circles = entity.GetComponents(PrimitiveCircleComponent.class, true);
            for (PrimitiveCircleComponent circle : circles) {
                ProfilerHost.Start("Render primitive circle [" + circle.name + "]");
                circle.transform.WorldTransform().getTranslation(pos);
                Sprites.Ball()
                        .setBounds(pos.x - 32, pos.y - 32, 64, 64);
                Sprites.Ball()
                        .setColor(circle.colour);
                Sprites.Ball()
                        .draw(batch);
                ProfilerHost.End("Render primitive circle");
            }
        }
        ProfilerHost.End("Primitive render");
        ProfilerHost.End("LightRenderer2::RenderScene");
    }

    public void Accept(Entity entity) {
        ProfilerHost.Start("LightRenderer2::Accept [" + entity.Id() + "]");
        List<RenderComponent> renderComponents = entity.GetComponents(RenderComponent.class, true);
        renderComponents.forEach(r -> r.RenderShadow(batch));
        ProfilerHost.End("LightRenderer2::Accept");
    }

    @Override
    public void Resize(int width, int height) {
        super.Resize(width, height);
        lightMapFBO.Resize(width, height);
    }

    @Override
    public void Shutdown() {

    }
}
