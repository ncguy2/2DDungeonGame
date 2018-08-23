package net.ncguy.render.post;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import net.ncguy.assets.TextureResolver;
import net.ncguy.particles.ParticleManager;
import net.ncguy.profile.ProfilerHost;
import net.ncguy.render.InstancedMesh;
import net.ncguy.util.ReloadableShaderProgram;
import net.ncguy.viewport.FBO;

import java.util.HashMap;
import java.util.Map;

// TODO convert to use geometry shader and upload single vertex to GPU
public class ParticlePostRenderer {

    FBO fbo;
    SpriteBatch batch;

    ReloadableShaderProgram shader;
    Texture texture;
    InstancedMesh mesh;


    public ParticlePostRenderer() {

        Map<String, String> params = new HashMap<>();
        params.put("p_BindingPoint", "0");

        shader = new ReloadableShaderProgram("Particle Render", Gdx.files.internal("particles/particle.vert"), Gdx.files.internal("particles/particle.frag"), params);
        texture = new Texture(Gdx.files.internal("particles/particle.png"));

        mesh = new InstancedMesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.TexCoords(0));

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

    public SpriteBatch GetBatch() {
        if (batch == null)
            batch = new SpriteBatch();
        return batch;
    }

    public FBO GetFbo() {
        return GetFbo(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    public FBO GetFbo(int width, int height) {
        if (fbo == null)
            fbo = new FBO(Pixmap.Format.RGBA8888, width, height, true);

        fbo.Resize(width, height);

        return fbo;
    }

    public Texture RenderToTexture(Matrix4 projection) {
        return RenderToTexture(null, projection);
    }
    public Texture RenderToTexture(Texture base, Matrix4 projection) {
        FBO fbo = GetFbo();
        fbo.begin();
        fbo.clear(0, 0, 0, 0, true);

        if(base != null) {
            SpriteBatch batch = GetBatch();
            batch.setProjectionMatrix(projection);
            batch.setShader(null);
            batch.begin();
            batch.draw(base, 0, 0, fbo.getWidth(), fbo.getHeight());
            batch.end();
        }

        Render(projection);
        fbo.end();
        return fbo.getColorBufferTexture();
    }

    public void Render(Matrix4 projection) {
        ProfilerHost.Start("ParticleRenderer::Render");
        ProfilerHost.Start("Initialization");
        shader.Program()
                .begin();
        ProfilerHost.End("Initialization");

        shader.Program()
                .setUniformMatrix("u_projViewTrans", projection);

        Texture[] boundTexture = new Texture[1];

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendEquation(GL30.GL_MAX);

        ProfilerHost.Start("System iteration");
        ParticleManager.instance()
                .Systems(sys -> {
                    ProfilerHost.Start("System render [" + sys.desiredAmount + "]");

                    ProfilerHost.Start("Texture resolution");

                    Texture[] targetTexture = new Texture[1];
                    targetTexture[0] = null;
                    if(sys.renderer.textureRef != null && !sys.renderer.textureRef.isEmpty())
                        TextureResolver.GetTextureAsync(sys.renderer.textureRef, t -> targetTexture[0] = t);

                    if(targetTexture[0] == null)
                        targetTexture[0] = texture;

                    ProfilerHost.End("Texture resolution");

                    if(!targetTexture[0].equals(boundTexture[0])) {
                        boundTexture[0] = targetTexture[0];
                        ProfilerHost.Start("Texture binding");
                        boundTexture[0].bind(0);
                        shader.Program()
                                .setUniformi("u_texture", 0);
                        ProfilerHost.End("Texture binding");
                    }

                    if(sys.renderer != null) {
                        shader.Program()
                                .setUniformi("u_alphaChannel", sys.renderer.alphaChannel);
                        shader.Program().setUniformf("u_alphaCutoff", sys.renderer.alphaCutoff);
                    }

                    sys.BindBuffer(0);

                    mesh.instanceCount = sys.desiredAmount;
                    ProfilerHost.Start("Mesh rendering");
                    mesh.render(shader.Program(), GL20.GL_TRIANGLES);
                    ProfilerHost.End("Mesh rendering");
                    ProfilerHost.End("System render");

                    sys.BindBuffer();
                });
        ProfilerHost.End("System iteration");

        Gdx.gl.glBlendEquation(GL30.GL_FUNC_ADD);

        ProfilerHost.Start("Cleanup");
        shader.Program()
                .end();
        ProfilerHost.End("Cleanup");


        ProfilerHost.End("ParticleRenderer::Render");
    }
}
