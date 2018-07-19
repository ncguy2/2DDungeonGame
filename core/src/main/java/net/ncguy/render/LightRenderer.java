package net.ncguy.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.entity.Entity;
import net.ncguy.entity.component.LightComponent;
import net.ncguy.viewport.FBO;
import net.ncguy.world.Engine;

import java.util.List;

import static com.badlogic.gdx.graphics.GL20.GL_BLEND;
import static com.badlogic.gdx.graphics.GL20.GL_FUNC_ADD;
import static com.badlogic.gdx.graphics.GL30.GL_MAX;

public class LightRenderer extends BaseRenderer {

    ShaderProgram lightingShader;
    ShaderProgram screenShader;
    FBO lightingFBO;
    Mesh mesh;
    DeferredRenderer baseRenderer;

    public LightRenderer(Engine engine, SpriteBatch batch, Camera camera) {
        super(engine, batch, camera);
        lightingShader = new ShaderProgram(Gdx.files.internal("shaders/lights.vert"), Gdx.files.internal("shaders/lights.frag"));
        screenShader = new ShaderProgram(Gdx.files.internal("shaders/screen.vert"), Gdx.files.internal("shaders/screen.frag"));
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
//        return screenBuffer.getColorBufferTexture();
        return lightingFBO.getColorBufferTexture();
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
        batch.setShader(lightingShader);
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, lightingFBO.getWidth(), lightingFBO.getHeight()));
        batch.begin();
//        lightingShader.setUniformMatrix("u_projTrans", new Matrix4().setToOrtho2D(0, 0, lightingFBO.getWidth(), lightingFBO.getHeight()));
        lightingShader.setUniformMatrix("u_projectionMatrix", camera.projection);
        lightingShader.setUniformMatrix("u_viewMatrix", camera.view);
        lightingShader.setUniformMatrix("u_combinedMatrix", camera.combined);

        Texture occlusionBuffer = baseRenderer.gBuffer.getTextureAttachments()
                .get(3);
        occlusionBuffer.bind(4);
        lightingShader.setUniformi("u_occluderMap", 4);
        lightingShader.setUniformi("u_occluderMapChannel", 2);
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

                lightingShader.setUniformf("u_lightPos", screenPos);
                lightingShader.setUniformf("safeRadiusPixels", 32);
                lightingShader.setUniformf("safeRadiusPixelBias", .25f);
                lightingShader.setUniformf("u_lightScreenPos", worldPos.x, worldPos.y);
                lightingShader.setUniformf("u_radius", light.radius);
                lightingShader.setUniformf("u_lightColour", light.colour);
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
        batch.setShader(screenShader);
        batch.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        screenShader.setUniformMatrix("u_projTrans", new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        baseRenderer.gBuffer.getColorBufferTexture().bind(4);
        screenShader.setUniformi("u_BaseColour", 4);
        lightingFBO.getColorBufferTexture().bind(5);
        screenShader.setUniformi("u_Lighting", 5);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

//        mesh.render(screenShader, GL20.GL_TRIANGLES);

        batch.draw(lightingFBO.getColorBufferTexture(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch.end();
        screenBuffer.end();

        if(Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            Gdx.app.postRunnable(() -> {
                ShaderProgram shader = new ShaderProgram(Gdx.files.internal("shaders/lights.vert"), Gdx.files.internal("shaders/lights.frag"));
                System.out.println(shader.getLog());
                if(shader.isCompiled()) {
                    this.lightingShader.dispose();
                    this.lightingShader = shader;
                    return;
                }
                System.out.println("Shader is not compiled");
            });
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            Gdx.app.postRunnable(() -> {
                ShaderProgram shader = new ShaderProgram(Gdx.files.internal("shaders/screen.vert"), Gdx.files.internal("shaders/screen.frag"));
                System.out.println(shader.getLog());
                if(shader.isCompiled()) {
                    this.screenShader.dispose();
                    this.screenShader = shader;
                    return;
                }
                System.out.println("Shader is not compiled");
            });
        }
    }
}
