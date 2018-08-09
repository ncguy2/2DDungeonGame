package net.ncguy.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import net.ncguy.assets.TextureResolver;
import net.ncguy.particles.ParticleManager;
import net.ncguy.profile.ProfilerHost;
import net.ncguy.util.ReloadableShaderProgram;
import net.ncguy.world.MainEngine;

import java.util.HashMap;
import java.util.Map;

// TODO convert to use geometry shader and upload single vertex to GPU
public class ParticleRenderer extends BaseRenderer {

    ReloadableShaderProgram shader;
    Texture texture;
    InstancedMesh mesh;

    public ParticleRenderer(MainEngine engine, SpriteBatch batch, Camera camera) {
        super(engine, batch, camera);

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

    @Override
    public void Render(float delta) {
        ProfilerHost.Start("ParticleRenderer::Render");
        ProfilerHost.Start("Initialization");
        shader.Program()
                .begin();
        screenBuffer.begin();
        screenBuffer.clear(0, 0, 0, 0, false);
        ProfilerHost.End("Initialization");

        shader.Program()
                .setUniformMatrix("u_projViewTrans", camera.combined);

        Texture[] boundTexture = new Texture[1];

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

                    sys.BindBuffer(0);
                    mesh.instanceCount = sys.desiredAmount;
                    ProfilerHost.Start("Mesh rendering");
                    mesh.render(shader.Program(), GL20.GL_TRIANGLES);
                    ProfilerHost.End("Mesh rendering");
                    ProfilerHost.End("System render");
                });
        ProfilerHost.End("System iteration");

        ProfilerHost.Start("Cleanup");
        screenBuffer.end();
        shader.Program()
                .end();
        ProfilerHost.End("Cleanup");


        ProfilerHost.End("ParticleRenderer::Render");
    }
}
