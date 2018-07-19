package net.ncguy.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.asset.Sprites;
import net.ncguy.entity.Entity;
import net.ncguy.entity.component.LightComponent;
import net.ncguy.entity.component.PrimitiveCircleComponent;
import net.ncguy.entity.component.RenderComponent;
import net.ncguy.util.ReloadableShader;
import net.ncguy.viewport.FBO;
import net.ncguy.viewport.FBOBuilder;
import net.ncguy.world.Engine;

import java.util.List;

public class DeferredRenderer extends BaseRenderer {

    FBO gBuffer;
    ReloadableShader gBufferShader;
    FBO occludersFBO;
    FBO lightingBuffer;
    ReloadableShader lightingShader;

    FBO shadowBuffer;
    ReloadableShader shadowShader;
    ReloadableShader screenShader;

    OrthographicCamera lightingCamera;
    int lightSize = 256;

    public DeferredRenderer(Engine engine, SpriteBatch batch, Camera camera) {
        super(engine, batch, camera);
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        gBuffer = FBOBuilder.BuildDefaultGBuffer(width, height);
        lightingBuffer = FBOBuilder.BuildLightingBuffer(lightSize, 1);
        occludersFBO = FBOBuilder.BuildScreenBuffer(lightSize, lightSize);
        shadowBuffer = FBOBuilder.BuildScreenBuffer(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Texture lightingTexture = lightingBuffer.getColorBufferTexture();
        lightingTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        lightingTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        gBufferShader = new ReloadableShader("DeferredRenderer::GBuffer", Gdx.files.internal("shaders/world.vert"), Gdx.files.internal("shaders/gbuffer.frag"));
        lightingShader = new ReloadableShader("DeferredRenderer::Lighting", Gdx.files.internal("shaders/screen.vert"), Gdx.files.internal("shaders/lighting.frag"));

        lightingCamera = new OrthographicCamera();

        shadowShader = new ReloadableShader("DeferredRenderer::Shadow", Gdx.files.internal("shaders/screen.vert"), Gdx.files.internal("shaders/shadowMap.frag"));
        screenShader = new ReloadableShader("DeferredRenderer::Screen", Gdx.files.internal("shaders/screen.vert"), Gdx.files.internal("shaders/screen.frag"));
    }

    @Override
    public void Resize(int width, int height) {
        super.Resize(width, height);
        gBuffer.Resize(width, height);
        lightingBuffer.Resize(lightSize, 1);
        shadowBuffer.Resize(width, height);
    }

    @Override
    public Texture GetTexture() {
        return gBuffer.getTextureAttachments().get(0);
    }

    @Override
    public void Render(float delta) {
        // GBuffer

        gBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.setShader(gBufferShader.Program());
        batch.begin();

        //noinspection unchecked
        List<Entity> entities = engine.world.GetFlattenedEntitiesWithComponents(RenderComponent.class);
        entities.forEach(this::Accept);

        Vector2 pos = new Vector2();
        entities = engine.world.GetFlattenedEntitiesWithComponents(PrimitiveCircleComponent.class);
        for (Entity entity : entities) {
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

        batch.flush();
        gBuffer.end();

        occludersFBO.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setShader(null);

//        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, occludersFBO.getWidth(), occludersFBO.getHeight()));
        entities = engine.world.GetFlattenedEntitiesWithComponents(LightComponent.class);
        lightingCamera.setToOrtho(false, occludersFBO.getWidth(), occludersFBO.getHeight());
        Vector2 vec = new Vector2();
        for (Entity entity : entities) {
            List<LightComponent> lights = entity.GetComponents(LightComponent.class, true);
            for (LightComponent light : lights) {
                light.transform.WorldTransform().getTranslation(vec);
                lightingCamera.position.set(vec.sub(lightSize * .5f, lightSize * .5f), 0.f);
                lightingCamera.update();

//                batch.setProjectionMatrix(camera.combined);
                Texture tex = gBuffer.getTextureAttachments().get(3);
                batch.draw(tex, 0, 0, tex.getWidth(), tex.getHeight());
            }
        }
        batch.flush();

        occludersFBO.end();

        lightingBuffer.begin();
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, lightingBuffer.getWidth(), lightingBuffer.getHeight()));
        batch.setShader(lightingShader.Program());

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        String[] texNames = new String[] {
                "gDiffuse",
                "gNormal",
                "gEmissive",
                "gTexOcc",
        };

        for (int i = 0; i < gBuffer.getTextureAttachments().size(); i++) {
            int unit = 8 + i;
            Texture texture = gBuffer.getTextureAttachments()
                    .get(i);
            texture.bind(unit);
            lightingShader.Program().setUniformi(texNames[i], unit);
        }
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

        lightingShader.Program().setUniformf("u_resolution", lightSize, lightSize);


        batch.draw(occludersFBO.getTextureAttachments().get(0), 0, 0, lightSize, lightingBuffer.getHeight());

        batch.flush();
        lightingBuffer.end();

        shadowBuffer.begin();
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, shadowBuffer.getWidth(), shadowBuffer.getHeight()));
        batch.setShader(shadowShader.Program());
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shadowShader.Program().setUniformf("u_resolution", lightSize, lightSize);

        entities = engine.world.GetFlattenedEntitiesWithComponents(LightComponent.class);
        Vector3 vec3 = new Vector3();
        for (Entity entity : entities) {
            List<LightComponent> lights = entity.GetComponents(LightComponent.class, true);
            for (LightComponent light : lights) {
                light.transform.WorldTransform().getTranslation(vec);
                vec.sub(lightSize * .5f, lightSize * .5f);
                vec3.set(vec, 0.f);
//                camera.project(vec3);
                batch.draw(occludersFBO.getTextureAttachments().get(0), vec3.x, vec3.y, lightSize, lightSize);
            }
        }

        batch.flush();
        shadowBuffer.end();

        screenBuffer.begin();
        batch.setShader(screenShader.Program());
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, screenBuffer.getWidth(), screenBuffer.getHeight()));

        gBuffer.getTextureAttachments().get(0).bind(4);
        screenShader.Program().setUniformi("u_BaseColour", 4);
        shadowBuffer.getTextureAttachments().get(0).bind(5);
        screenShader.Program().setUniformi("u_Shadows", 5);

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        batch.draw(gBuffer.getTextureAttachments().get(0), 0, 0);

        batch.flush();
        screenBuffer.end();

        if(Gdx.input.isKeyJustPressed(Input.Keys.R))
            gBufferShader.Reload();
        if(Gdx.input.isKeyJustPressed(Input.Keys.T))
            lightingShader.Reload();
        if(Gdx.input.isKeyJustPressed(Input.Keys.Y))
            shadowShader.Reload();
        if(Gdx.input.isKeyJustPressed(Input.Keys.U))
            screenShader.Reload();

        batch.end();
        batch.setShader(null);
    }

    public void Accept(Entity entity) {
        List<RenderComponent> renderComponents = entity.GetComponents(RenderComponent.class, true);
        renderComponents.forEach(r -> r.Render(batch));
    }
}
