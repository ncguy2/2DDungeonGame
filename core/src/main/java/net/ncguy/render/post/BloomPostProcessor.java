package net.ncguy.render.post;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import net.ncguy.shaders.Blur;
import net.ncguy.util.ReloadableShaderProgram;
import net.ncguy.viewport.FBO;
import net.ncguy.world.MainEngine;

public class BloomPostProcessor extends PostProcessor {

    public BloomPostProcessor(MainEngine engine) {
        super(engine);
    }

    Blur lightBlur;
    Blur particleBlur;

    @Override
    public void Init() {
        frameBuffer = new FBO(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        shader = new ReloadableShaderProgram("Bloom", Gdx.files.internal("shaders/screen.vert"), Gdx.files.internal("shaders/bloom.frag"));
        lightBlur = new Blur();
        particleBlur = new Blur();
    }

    public Texture lightTexture;
    public Texture particleTexture;

    @Override
    public Texture Render(Batch batch, Camera camera, Texture input, float delta) {

        Texture blurredTex = particleTexture;
        blurredTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        frameBuffer.begin();
        frameBuffer.clear(0, 0, 0, 0, false);
        batch.setShader(shader.Program());
        Matrix4 matrix4 = new Matrix4().setToOrtho2D(0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());
        batch.setProjectionMatrix(matrix4);
        batch.begin();
//        if(lightTexture != null)
//            shader.BindTexture("u_light", lightBlur.BlurGaussian(lightTexture), 4);
        if(particleTexture != null)
            shader.BindTexture("u_particles", blurredTex, 5);
        batch.draw(input, 0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());
        batch.end();
        frameBuffer.end();
        batch.setShader(null);

        return frameBuffer.getColorBufferTexture();
    }

    @Override
    public void Shutdown() {

    }
}
