package net.ncguy.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.asset.Sprites;
import net.ncguy.entity.Entity;
import net.ncguy.entity.component.LightComponent;
import net.ncguy.entity.component.PrimitiveCircleComponent;
import net.ncguy.entity.component.RenderComponent;
import net.ncguy.util.ReloadableShader;
import net.ncguy.viewport.FBO;
import net.ncguy.world.Engine;

import java.util.ArrayList;
import java.util.List;

public class LightRenderer2 extends BaseRenderer {

    FBO occluderFBO;
    FBO shadowmapFBO;
    FBO lightMapFBO;
    ReloadableShader occluderShader;
    ReloadableShader shadowShader;
    ReloadableShader applicationShader;
    ReloadableShader screenShader;
    int lightSize = 1024;
    float lightScale = 4;

    DeferredRenderer baseRenderer;
    TextureRegion occluders;
    TextureRegion shadowMap1D;
    OrthographicCamera cam;

    public LightRenderer2(Engine engine, SpriteBatch batch, Camera camera) {
        super(engine, batch, camera);

        cam = new OrthographicCamera();

        occluderFBO = new FBO(Pixmap.Format.RGBA8888, lightSize, lightSize, false);
        Texture occluderTex = occluderFBO.getColorBufferTexture();
        occluderTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        occluders = new TextureRegion(occluderTex);
        occluders.flip(false, true);

        shadowmapFBO = new FBO(Pixmap.Format.RGBA8888, lightSize, 1, false);
        Texture shadowTexture = shadowmapFBO.getColorBufferTexture();
        shadowTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        shadowTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        shadowMap1D = new TextureRegion(shadowTexture);
        shadowMap1D.flip(false, true);

        lightMapFBO = new FBO(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

        occluderShader = new ReloadableShader("LightRenderer2::Occluder", Gdx.files.internal("shaders/screen.vert"), Gdx.files.internal("shaders/occluder.frag"));
        shadowShader = new ReloadableShader("LightRenderer2::Shadow", Gdx.files.internal("shaders/screen.vert"), Gdx.files.internal("shaders/lighting2.frag"));
        applicationShader = new ReloadableShader("LightRenderer2::Application", Gdx.files.internal("shaders/screen.vert"), Gdx.files.internal("shaders/shadowApplication.frag"));
        screenShader = new ReloadableShader("LightRenderer2::Screen", Gdx.files.internal("shaders/screen.vert"), Gdx.files.internal("shaders/screenLighting.frag"));

        System.out.println(occluderShader.getLog());
        System.out.println(shadowShader.getLog());
        System.out.println(applicationShader.getLog());
        baseRenderer = new DeferredRenderer(engine, batch, camera);
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
    }

    @Override
    public Texture GetTexture() {
        return screenBuffer.getColorBufferTexture();
    }

    @Override
    public void Render(float delta) {
        baseRenderer.Render(delta);

        lightMapFBO.begin();
        lightMapFBO.clear(0, 0, 0, 0, false);
        lightMapFBO.end();

        List<Entity> entities = engine.world.GetFlattenedEntitiesWithComponents(LightComponent.class);
        List<LightComponent> collectedLights = new ArrayList<>();
        for (Entity entity : entities)
            collectedLights.addAll(entity.GetComponents(LightComponent.class, true));

        int max = collectedLights.stream()
                .map(l -> l.radius)
                .max(Float::compareTo)
                .map(Math::round)
                .orElse(256);

        ResizeBuffers(max * 4, true);

        collectedLights.stream().sorted((l1, l2) -> Float.compare(l1.radius, l2.radius)).forEach(this::RenderLight);

        cam.setToOrtho(true);
        screenBuffer.begin();
        screenBuffer.clear(0, 0, 0, 1, false);
        batch.setShader(screenShader.Program());
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        lightMapFBO.getColorBufferTexture().bind(4);
        screenShader.Program().setUniformi("u_Lighting", 4);
        screenShader.Program().setUniformf("intensity", .2f);
        screenShader.Program().setUniformf("ambientScale", .5f);
        screenShader.Program().setUniformf("gamma", 1f);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        batch.draw(baseRenderer.gBuffer.getColorBufferTexture(), 0, 0, screenBuffer.getWidth(), screenBuffer.getHeight());
//        batch.draw(lightMapFBO.getColorBufferTexture(), 0, 0, screenBuffer.getWidth(), screenBuffer.getHeight());
        batch.end();
        screenBuffer.end();
    }

    void RenderLight(LightComponent light) {
        int round = Math.round(light.radius) * 4;
        ResizeBuffers(round, false);

        occluderFBO.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Vector2 pos = new Vector2();
        light.transform.WorldTransform().getTranslation(pos);
        pos.sub(lightSize * .5f, lightSize * .5f);
//        cam.setToOrtho(false, occluderFBO.getWidth(), occluderFBO.getHeight());
        cam.setToOrtho(false, lightSize, lightSize);
        cam.translate(pos);
        cam.update();

        batch.setProjectionMatrix(cam.combined);
        batch.setShader(occluderShader.Program());
        batch.begin();

        RenderScene(light.GetOwningEntity());

        batch.end();
        occluderFBO.end();

        {
            shadowmapFBO.begin();
            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.setShader(shadowShader.Program());
            batch.begin();
            shadowShader.Program().setUniformf("u_resolution", lightSize, lightSize);
            shadowShader.Program().setUniformf("u_scale", lightScale);
//            cam.setToOrtho(false, shadowmapFBO.getWidth(), shadowmapFBO.getHeight());
            cam.setToOrtho(false, lightSize, 1);
            batch.setProjectionMatrix(cam.combined);

            batch.draw(occluderFBO.getColorBufferTexture(), 0, 0, lightSize, 1);

            batch.end();
            shadowmapFBO.end();

            AdditiveRenderLight(light);
        }

        batch.setShader(null);
    }

    public void AdditiveRenderLight(LightComponent light) {
        Vector2 pos = new Vector2();
        light.transform.WorldTransform().getTranslation(pos);
        float size = lightSize * lightScale;
        pos.sub(size * .5f, size * .5f);
        lightMapFBO.begin();
        batch.setShader(applicationShader.Program());
        cam.setToOrtho(false);
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        applicationShader.Program().setUniformf("u_resolution", lightSize, lightSize);
        batch.setColor(light.colour);
        Vector3 v = new Vector3(pos, 0.f);

        camera.project(v);
        batch.draw(shadowmapFBO.getColorBufferTexture(), v.x, v.y, size, size);
        batch.end();
        batch.setColor(Color.WHITE);
        lightMapFBO.end();
    }

    public void RenderScene(Entity exclusion) {
        List<Entity> entities = engine.world.GetFlattenedEntitiesWithComponents(RenderComponent.class);
        entities.remove(exclusion);
        entities.forEach(this::Accept);

        Vector2 pos = new Vector2();
        entities = engine.world.GetFlattenedEntitiesWithComponents(PrimitiveCircleComponent.class);
        for (Entity entity : entities) {
            if(entity.equals(exclusion))
                continue;
            List<PrimitiveCircleComponent> circles = entity.GetComponents(PrimitiveCircleComponent.class, true);
            for (PrimitiveCircleComponent circle : circles) {
                circle.transform.WorldTransform().getTranslation(pos);
                Sprites.Ball()
                        .setBounds(pos.x - 32, pos.y - 32, 64, 64);
                Sprites.Ball()
                        .setColor(circle.colour);
                Sprites.Ball()
                        .draw(batch);
            }
        }
    }

    public void Accept(Entity entity) {
        List<RenderComponent> renderComponents = entity.GetComponents(RenderComponent.class, true);
        renderComponents.forEach(r -> r.RenderShadow(batch));
    }

}

