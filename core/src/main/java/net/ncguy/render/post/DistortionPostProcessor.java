package net.ncguy.render.post;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import net.ncguy.asset.Sprites;
import net.ncguy.entity.Entity;
import net.ncguy.entity.component.DistortionComponent;
import net.ncguy.util.ReloadableShader;
import net.ncguy.viewport.FBO;
import net.ncguy.world.Engine;

import java.util.List;

public class DistortionPostProcessor extends PostProcessor {

    FBO applicationBuffer;
    ReloadableShader applicationShader;

    public DistortionPostProcessor(Engine engine) {
        super(engine);
        Init();
    }

    @Override
    public void Init() {
        frameBuffer = new FBO(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        shader = new ReloadableShader("Distortion", Gdx.files.internal("shaders/screen.vert"), Gdx.files.internal("shaders/distortionMap.frag"));

        applicationBuffer = new FBO(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        applicationShader = new ReloadableShader("Distortion Application", Gdx.files.internal("shaders/screen.vert"), Gdx.files.internal("shaders/distortionApplication.frag"));
    }

    @Override
    public void Resize(int width, int height) {
        super.Resize(width, height);
        applicationBuffer.Resize(width, height);
    }

    @Override
    public Texture Render(Batch batch, Camera camera, Texture input, float delta) {
        frameBuffer.begin();
//        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(camera.position.x, camera.position.y, frameBuffer.getWidth(), frameBuffer.getHeight()));
        Matrix4 matrix4 = new Matrix4().setToOrtho2D(0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());
//        matrix4.translate(camera.position);
        batch.setProjectionMatrix(matrix4);
        batch.begin();
        batch.setShader(null);
        Sprites.Pixel().setSize(frameBuffer.getWidth(), frameBuffer.getHeight());
        Sprites.Pixel().setColor(0, 0, 0, .1f);
        Sprites.Pixel().draw(batch);

        batch.setProjectionMatrix(camera.combined);
        batch.setShader(shader.Program());

        List<Entity> entities = engine.world.GetFlattenedEntitiesWithComponents(DistortionComponent.class);
        for (Entity entity : entities) {
            List<DistortionComponent> distortionComponents = entity.GetComponents(DistortionComponent.class, true);
            for (DistortionComponent distortionComponent : distortionComponents) {
                if(distortionComponent.sprite == null)
                    continue;
                distortionComponent.transform.scale.set(256, 256);
                distortionComponent.Update(delta);
//                distortionComponent.sprite.setSize(256, 256);
//                Vector2 pos = new Vector2();
//                distortionComponent.transform.WorldTransform().getTranslation(pos);
//                Vector3 pos3 = new Vector3(pos, 0.f);
//                pos3.mul(matrix4);
//                distortionComponent.sprite.setPosition(pos3.x, pos3.y);

                distortionComponent.sprite.draw(batch);
            }
        }

        batch.end();
        frameBuffer.end();

        applicationBuffer.begin();
        applicationBuffer.clear(0, 0, 0, 1, false);
        batch.setProjectionMatrix(matrix4);
        batch.setShader(applicationShader.Program());
        batch.begin();
        frameBuffer.getColorBufferTexture().bind(4);
        applicationShader.Program().setUniformi("u_distortionMap", 4);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        batch.draw(input, 0, 0, applicationBuffer.getWidth(), applicationBuffer.getHeight());
        batch.end();
        batch.setShader(null);
        applicationBuffer.end();

        return applicationBuffer.getColorBufferTexture();
    }


    @Override
    public void Shutdown() {

    }
}
