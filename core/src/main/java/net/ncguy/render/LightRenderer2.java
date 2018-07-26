package net.ncguy.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.assets.Sprites;
import net.ncguy.entity.Entity;
import net.ncguy.entity.component.LightComponent;
import net.ncguy.entity.component.PrimitiveCircleComponent;
import net.ncguy.entity.component.RenderComponent;
import net.ncguy.profile.ProfilerHost;
import net.ncguy.render.post.PostProcessor;
import net.ncguy.util.ReloadableShaderProgram;
import net.ncguy.viewport.FBO;
import net.ncguy.world.Engine;

import java.util.ArrayList;
import java.util.List;

public class LightRenderer2 extends BaseRenderer {

    FBO occluderFBO;
    FBO shadowmapFBO;
    FBO lightMapFBO;
    ReloadableShaderProgram occluderShader;
    ReloadableShaderProgram shadowShader;
    ReloadableShaderProgram applicationShader;
    ReloadableShaderProgram screenShader;
    List<PostProcessor> processors;
    Texture postProcessedTexture;
    int lightSize = 1024;
    float lightScale = 4;

    SimpleRenderer baseRenderer;
    TextureRegion occluders;
    TextureRegion shadowMap1D;
    OrthographicCamera cam;

    public LightRenderer2(Engine engine, SpriteBatch batch, Camera camera) {
        super(engine, batch, camera);

        ProfilerHost.Start("LightRenderer2::LightRenderer2");
        cam = new OrthographicCamera();
        processors = new ArrayList<>();

        ProfilerHost.Start("FrameBuffer objects");
        ProfilerHost.Start("Occluder buffer");
        occluderFBO = new FBO(Pixmap.Format.RGBA8888, lightSize, lightSize, false);
        Texture occluderTex = occluderFBO.getColorBufferTexture();
        occluderTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        occluders = new TextureRegion(occluderTex);
        occluders.flip(false, true);
        ProfilerHost.End("Occluder buffer");

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
        ProfilerHost.End("FrameBuffer objects");

        ProfilerHost.Start("Shaders");
        occluderShader = new ReloadableShaderProgram("LightRenderer2::Occluder", Gdx.files.internal("shaders/screen.vert"), Gdx.files.internal("shaders/occluder.frag"));
        shadowShader = new ReloadableShaderProgram("LightRenderer2::Shadow", Gdx.files.internal("shaders/screen.vert"), Gdx.files.internal("shaders/lighting2.frag"));
        applicationShader = new ReloadableShaderProgram("LightRenderer2::Application", Gdx.files.internal("shaders/screen.vert"), Gdx.files.internal("shaders/shadowApplication.frag"));
        screenShader = new ReloadableShaderProgram("LightRenderer2::Screen", Gdx.files.internal("shaders/screen.vert"), Gdx.files.internal("shaders/screenLighting.frag"));
        ProfilerHost.End("Shaders");

        System.out.println(occluderShader.getLog());
        System.out.println(shadowShader.getLog());
        System.out.println(applicationShader.getLog());
        baseRenderer = new SimpleRenderer(engine, batch, camera);
        ProfilerHost.End("LightRenderer2::LightRenderer2");

//        processors.add(new DistortionPostProcessor(engine));
    }

    public void ResizeBuffers(int lightSize, boolean actuallyResize) {
        lightScale = 1;
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
    public void Resize(int width, int height) {
        super.Resize(width, height);
        baseRenderer.Resize(width, height);
        lightMapFBO.Resize(width, height);
        processors.forEach(p -> p.Resize(width, height));
    }

    @Override
    public Texture GetTexture() {
//        return shadowmapFBO.getColorBufferTexture();
        return postProcessedTexture;
    }

    @Override
    public void Render(float delta) {
        ProfilerHost.Start("LightRenderer2::Render");
        ProfilerHost.Start("World renderer");
        baseRenderer.Render(delta);
        ProfilerHost.End("World renderer");

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
        collectedLights.stream().sorted((l1, l2) -> Float.compare(l1.radius, l2.radius)).forEach(this::RenderLight);
        ProfilerHost.End("Render");
        ProfilerHost.End("Lights");

        ProfilerHost.Start("Lighting render");
        cam.setToOrtho(true);
        screenBuffer.begin();
        screenBuffer.clear(0, 0, 0, 1, false);
        batch.setShader(screenShader.Program());
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        lightMapFBO.getColorBufferTexture().bind(4);
        ProfilerHost.Start("Uniforms");
        screenShader.Program().setUniformi("u_Lighting", 4);
        screenShader.Program().setUniformf("intensity", .2f);
        screenShader.Program().setUniformf("ambientScale", .5f);
        screenShader.Program().setUniformf("gamma", 1f);
        ProfilerHost.End("Uniforms");
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        ProfilerHost.Start("Queue draw");
        batch.draw(baseRenderer.gBuffer.getColorBufferTexture(), 0, 0, screenBuffer.getWidth(), screenBuffer.getHeight());
        ProfilerHost.End("Queue draw");
        ProfilerHost.Start("Draw");
        batch.end();
        ProfilerHost.End("Draw");
        screenBuffer.end();
        ProfilerHost.End("Lighting render");

        ProfilerHost.Start("PostProcess");
        postProcessedTexture = screenBuffer.getColorBufferTexture();
        for (PostProcessor p : processors)
            postProcessedTexture = p.Render(batch, camera, postProcessedTexture, delta);
        ProfilerHost.End("PostProcess");
        ProfilerHost.End("LightRenderer2::Render");
    }

    void RenderLight(LightComponent light) {
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

        AdditiveRenderLight(light);

        batch.setShader(null);
        ProfilerHost.End("LightRenderer2::RenderLight");
    }

    public void AdditiveRenderLight(LightComponent light) {
        ProfilerHost.Start("LightRenderer2::AdditiveRenderLight [" + light.name + "]");
        ProfilerHost.Start("Reposition");
        Vector2 pos = new Vector2();
        light.transform.WorldTransform().getTranslation(pos);
        float size = lightSize * lightScale;
//        float size = lightSize;
        pos.sub(size * .5f, size * .5f);
        ProfilerHost.End("Reposition");
        ProfilerHost.Start("Render");
        lightMapFBO.begin();
        batch.setShader(applicationShader.Program());
        cam.setToOrtho(false);
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
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

}

