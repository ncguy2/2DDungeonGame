package net.ncguy.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import net.ncguy.particles.ParticleManager;
import net.ncguy.profile.ProfilerHost;
import net.ncguy.util.ReloadableShaderProgram;
import net.ncguy.world.MainEngine;

public class ParticleRenderer extends BaseRenderer {

    ReloadableShaderProgram shader;
    Texture texture;
    InstancedMesh mesh;

    public ParticleRenderer(MainEngine engine, SpriteBatch batch, Camera camera) {
        super(engine, batch, camera);
        shader = new ReloadableShaderProgram("Particle Render", Gdx.files.internal("particles/particle.vert"), Gdx.files.internal("particles/particle.frag"));
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
        shader.Program().begin();
        screenBuffer.begin();
        screenBuffer.clear(0, 0, 0, 0, false);
        ProfilerHost.End("Initialization");

        shader.Program().setUniformMatrix("u_projViewTrans", camera.combined);

        ProfilerHost.Start("Texture binding");
        texture.bind(0);
        shader.Program().setUniformi("u_texture", 0);
        ProfilerHost.End("Texture binding");

        ProfilerHost.Start("System iteration");
        ParticleManager.instance().Systems(sys -> {
            ProfilerHost.Start("System render [" + sys.desiredAmount + "]");
            mesh.instanceCount = sys.desiredAmount;
            mesh.render(shader.Program(), GL20.GL_TRIANGLES);
            ProfilerHost.End("System render");
        });
        ProfilerHost.End("System iteration");

        ProfilerHost.Start("Cleanup");
        screenBuffer.end();
        shader.Program().end();
        ProfilerHost.End("Cleanup");
        ProfilerHost.End("ParticleRenderer::Render");
    }
}
