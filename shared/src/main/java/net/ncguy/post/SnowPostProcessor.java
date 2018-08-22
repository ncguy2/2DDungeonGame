package net.ncguy.post;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import net.ncguy.assets.TextureResolver;
import net.ncguy.util.ReloadableShaderProgram;
import net.ncguy.viewport.FBO;
import net.ncguy.world.MainEngine;
import net.ncguy.world.WindManager;

import static net.ncguy.particles.AbstractParticleSystem.GlobalLife;

public class SnowPostProcessor extends PostProcessor {

    Texture tex;
    Mesh mesh;

    public boolean flipTexture = false;
    public float colourStrength = 1f;

    public SnowPostProcessor(MainEngine engine) {
        super(engine);
    }

    @Override
    public void Init() {
        frameBuffer = new FBO(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        shader = new ReloadableShaderProgram("Snow", Gdx.files.internal("shaders/screen.vert"), Gdx.files.internal("shaders/snow.frag"));
        TextureResolver.GetTextureAsync("textures/clouds.png", t -> tex = t);

        mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.TexCoords(0));

        mesh.setVertices(new float[]{
                -1, -1, 0, 0, 0,
                1, -1, 0, 1, 0,
                -1, 1, 0, 0, 1,
                1, 1, 0, 1, 1
        });

        mesh.setIndices(new short[]{
                0, 1, 2,
                1, 3, 2
        });
    }

    @Override
    public Texture Render(Batch batch, Camera camera, Texture input, float delta) {

        long frameId = Gdx.graphics.getFrameId();

        frameBuffer.begin();
        frameBuffer.clear(0, 0, 0, 0, false);

        shader.Program().begin();

        if(input != null) {
            input.bind(0);
            shader.Program().setUniformi("u_texture", 0);
        }
        if(tex != null) {
            tex.bind(1);
            shader.Program().setUniformi("u_cloud", 1);
        }
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

        shader.Program().setUniformMatrix("u_projTrans", new Matrix4());
        shader.Program().setUniformf("u_time", GlobalLife);
        shader.Program().setUniformf("u_windScale", 1);
        shader.Program().setUniformf("u_windVelocity", WindManager.Get().GetWind());
        shader.Program().setUniformf("u_colourStrength", colourStrength);
        shader.Program().setUniformi("u_flipTexture", flipTexture ? 1 : 0);

        mesh.render(shader.Program(), GL20.GL_TRIANGLES);

//        batch.draw(input, 0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());
        shader.Program().end();
        frameBuffer.end();

        return frameBuffer.getColorBufferTexture();
    }

    @Override
    public void Shutdown() {
        frameBuffer.dispose();
        frameBuffer = null;

        shader.Shutdown();
        shader = null;
    }
}
