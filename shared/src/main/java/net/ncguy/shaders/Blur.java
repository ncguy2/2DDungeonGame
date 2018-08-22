package net.ncguy.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import net.ncguy.profile.ProfilerHost;
import net.ncguy.util.ReloadableShaderProgram;
import net.ncguy.viewport.FBO;

public class Blur {

    OrthographicCamera camera;
    SpriteBatch batch;
    FBO[] fboPair;
    int currentWidth = -1;
    int currentHeight = -1;
    Texture lastResult;
    ReloadableShaderProgram shader;
    float blurBufferScale = .8f;

    public Blur() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shader = new ReloadableShaderProgram("Blur shader", Gdx.files.internal("shaders/blur/blur.vert"), Gdx.files.internal("shaders/blur/blur.frag"));
        LoadShader();
    }

    public Texture GetLastResult() {
        return lastResult;
    }

    public void LoadShader() {
        shader.Reload();
    }

    void UpdateFBO(Texture tex) {
        blurBufferScale = .25f;
        if(fboPair == null || (currentWidth != tex.getWidth() || currentHeight != tex.getHeight())) {
            ProfilerHost.Start("FBO Invalidation");

            if(fboPair == null) {
                fboPair = new FBO[]{
                        new FBO(Pixmap.Format.RGBA8888, tex.getWidth(), tex.getHeight(), false).Name("Blur 0"),
                        new FBO(Pixmap.Format.RGBA8888, tex.getWidth(), tex.getHeight(), false).Name("Blur 1")
                };

                camera.setToOrtho(true, tex.getWidth(), tex.getHeight());
                camera.position.set(0, 0, 0);
                batch.setProjectionMatrix(camera.combined);
            }

            currentWidth = tex.getWidth();
            currentHeight = tex.getHeight();
            ProfilerHost.End("FBO Invalidation");
        }
    }

    public Texture BlurQuick(Texture tex) {
        ProfilerHost.Start("Quick blur");

        UpdateFBO(tex);
        Texture texture = BlurImpl(fboPair[1], BlurImpl(fboPair[0], tex, false), true);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        ProfilerHost.End("Quick blur");
        return lastResult = texture;
    }

    public Texture BlurGaussian(Texture tex) {
        return BlurGaussian(tex, 8);
    }
    public Texture BlurGaussian(Texture tex, int amt) {

        ProfilerHost.Start("Gaussian blur, " + amt);
        UpdateFBO(tex);

        Texture texture = BlurImpl(fboPair[1], BlurImpl(fboPair[0], tex, false), true);

        for (int i = 1; i < amt; i++) {
            texture = BlurImpl(fboPair[1], BlurImpl(fboPair[0], texture, false), true);
        }

        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        ProfilerHost.End("Gaussian Blur");
        return lastResult = texture;
    }

    protected Texture BlurImpl(FBO fbo, Texture input, boolean horizontal) {

        input.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        input.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);

        fbo.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setShader(shader.Program());
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, fbo.getWidth(), fbo.getHeight()));
        batch.begin();
        shader.Program().setUniformi("u_horizontal", horizontal ? 1 : 0);
        batch.draw(input, 0, 0, fbo.getWidth(), fbo.getHeight());
        batch.end();

        fbo.end();

        Texture texture = fbo.getColorBufferTexture();
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        return lastResult = texture;
    }

}
