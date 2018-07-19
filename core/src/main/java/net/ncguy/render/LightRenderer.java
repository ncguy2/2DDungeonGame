package net.ncguy.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.entity.Entity;
import net.ncguy.entity.component.LightComponent;
import net.ncguy.util.ReloadableShader;
import net.ncguy.viewport.FBO;
import net.ncguy.world.Engine;

import java.util.List;

import static com.badlogic.gdx.graphics.GL20.GL_BLEND;
import static com.badlogic.gdx.graphics.GL20.GL_FUNC_ADD;
import static com.badlogic.gdx.graphics.GL30.GL_MAX;

public class LightRenderer extends BaseRenderer {

    ReloadableShader lightingShader;
    ReloadableShader screenShader;
    FBO lightingFBO;
    Mesh mesh;
    DeferredRenderer baseRenderer;

    public LightRenderer(Engine engine, SpriteBatch batch, Camera camera) {
        super(engine, batch, camera);
        lightingShader = new ReloadableShader("LightRenderer::Lighting", Gdx.files.internal("shaders/lights.vert"), Gdx.files.internal("shaders/lights.frag"));
        screenShader = new ReloadableShader("LightRenderer::Screen", Gdx.files.internal("shaders/screen.vert"), Gdx.files.internal("shaders/screen.frag"));
        System.out.println(lightingShader.getLog());
        mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.TexCoords(0));
        mesh.setVertices(new float[] {
                -1, -1, 0, -1, -1,
                -1,  1, 0, -1,  1,
                 1,  1, 0,  1,  1,
                 1, -1, 0,  1, -1
        });
        mesh.setIndices(new short[] {
                0, 3, 1,
                3, 2, 1
        });

        baseRenderer = new DeferredRenderer(engine, batch, camera);

        lightingFBO = new FBO(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    }

    @Override
    public Texture GetTexture() {
//        return baseRenderer.GetTexture();
//        return super.GetTexture();
        return screenBuffer.getColorBufferTexture();
//        return lightingFBO.getColorBufferTexture();
    }

    @Override
    public void Resize(int width, int height) {
        super.Resize(width, height);
        baseRenderer.Resize(width, height);
        lightingFBO.Resize(width, height);
    }

    @Override
    public void Render(float delta) {

        baseRenderer.Render(delta);

        lightingFBO.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setShader(lightingShader.Program());
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, lightingFBO.getWidth(), lightingFBO.getHeight()));
        batch.begin();
//        lightingShader.setUniformMatrix("u_projTrans", new Matrix4().setToOrtho2D(0, 0, lightingFBO.getWidth(), lightingFBO.getHeight()));
        lightingShader.Program().setUniformMatrix("u_projectionMatrix", camera.projection);
        lightingShader.Program().setUniformMatrix("u_viewMatrix", camera.view);
        lightingShader.Program().setUniformMatrix("u_combinedMatrix", camera.combined);

        Texture occlusionBuffer = baseRenderer.gBuffer.getTextureAttachments()
                .get(3);
        occlusionBuffer.bind(4);
        lightingShader.Program().setUniformi("u_occluderMap", 4);
        lightingShader.Program().setUniformi("u_occluderMapChannel", 2);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

        Gdx.gl.glEnable(GL_BLEND);
        Gdx.gl.glBlendEquation(GL_MAX);
//        Gdx.gl.glBlendFunc(GL_ONE, GL_ONE);

        List<Entity> entities = engine.world.GetFlattenedEntitiesWithComponents(LightComponent.class);
        Vector2 screenPos = new Vector2();
        Vector3 worldPos = new Vector3();
        for (Entity entity : entities) {
            List<LightComponent> lights = entity.GetComponents(LightComponent.class, true);
            for (LightComponent light : lights) {
                light.transform.WorldTransform().getTranslation(screenPos);

                worldPos.set(screenPos, 0.f);
                camera.project(worldPos);
                worldPos.x /= screenBuffer.getWidth();
                worldPos.y /= screenBuffer.getHeight();

                lightingShader.Program().setUniformf("u_lightPos", screenPos);
                lightingShader.Program().setUniformf("safeRadiusPixels", 32);
                lightingShader.Program().setUniformf("safeRadiusPixelBias", .25f);
                lightingShader.Program().setUniformf("u_lightScreenPos", worldPos.x, worldPos.y);
                lightingShader.Program().setUniformf("u_radius", light.radius);
                lightingShader.Program().setUniformf("u_lightColour", light.colour);
//                mesh.render(lightingShader, GL20.GL_TRIANGLES);
                batch.draw(occlusionBuffer, 0, 0, lightingFBO.getWidth(), lightingFBO.getHeight());
                batch.flush();
            }
        }
        batch.end();
        lightingFBO.end();

        Gdx.gl.glBlendEquation(GL_FUNC_ADD);
        Gdx.gl.glDisable(GL_BLEND);

        screenBuffer.begin();
        batch.setShader(screenShader.Program());
        batch.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        baseRenderer.gBuffer.getColorBufferTexture().bind(4);
        screenShader.Program().setUniformi("u_BaseColour", 4);
        lightingFBO.getColorBufferTexture().bind(5);
        screenShader.Program().setUniformi("u_Lighting", 5);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

//        mesh.render(screenShader, GL20.GL_TRIANGLES);

        TextureRegion reg = new TextureRegion(lightingFBO.getColorBufferTexture());
        reg.flip(false, true);

        batch.draw(reg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch.setShader(null);
        float height = Gdx.graphics.getHeight() * .2f;
        batch.draw(reg, 0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth() * .2f, -height);

        batch.end();
        screenBuffer.end();

        if(Gdx.input.isKeyJustPressed(Input.Keys.R))
            lightingShader.Reload();

        if(Gdx.input.isKeyJustPressed(Input.Keys.T))
            screenShader.Reload();
    }
}
