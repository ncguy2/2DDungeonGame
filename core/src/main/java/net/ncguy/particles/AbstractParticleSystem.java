package net.ncguy.particles;

import com.badlogic.gdx.Gdx;
import net.ncguy.buffer.ShaderStorageBufferObject;
import net.ncguy.profile.ProfilerHost;
import net.ncguy.shaders.ComputeShader;
import net.ncguy.util.ReloadableComputeShader;

public abstract class AbstractParticleSystem {

    public final int desiredAmount;
    protected float life;
    protected ReloadableComputeShader compute;
    protected ReloadableComputeShader updateScript;
    protected ShaderStorageBufferObject particleBuffer;
    protected ShaderStorageBufferObject deadBuffer;

    public static final int PARTICLE_BYTES = 48;

    public AbstractParticleSystem(int desiredAmount) {
        this.desiredAmount = desiredAmount;
        life = 0;

        particleBuffer = new ShaderStorageBufferObject(desiredAmount * PARTICLE_BYTES);
        deadBuffer = new ShaderStorageBufferObject(desiredAmount);

        compute = new ReloadableComputeShader("Particle::Spawn script", Gdx.files.internal("particles/compute/spawn.comp"));
        updateScript = new ReloadableComputeShader("ParticleManager::Update script", Gdx.files.internal("particles/compute/update.comp"));
        ParticleManager.instance().systems.add(this);
    }

    public void Reset() {
        life = 0;
    }

    public void Spawn(int amount) {
        final int[] indices = ParticleManager.instance()
                .RequestIndices(amount);

        compute.Program()
                .Bind();
        for (int i = 0; i < indices.length; i++) {
            int finalI = i;
            compute.Program()
                    .SetUniform("u_indices[" + i + "]", loc -> Gdx.gl.glUniform1i(loc, indices[finalI]));
        }
        BindBuffers(compute.Program());
        compute.Program()
                .Dispatch((int) Math.ceil(amount / 360f));
        compute.Program()
                .Unbind();
    }

    public void BindBuffers(ComputeShader program) {
        ProfilerHost.Start("ParticleManager::BindBuffers");
        program.BindSSBO(0, particleBuffer);
//        program.BindSSBO(1, deadBuffer);
        ProfilerHost.End("ParticleManager::BindBuffers");
    }

    public void Update(float delta) {
        ProfilerHost.Start("AbstractParticleSystem::Update");
        life += delta;

        ProfilerHost.Start("Particle buffer update");
        updateScript.Program()
                .Bind();
        BindBuffers(updateScript.Program());
        updateScript.Program()
                .SetUniform("u_delta", loc -> Gdx.gl.glUniform1f(loc, delta));
        updateScript.Program()
                .Dispatch((int) Math.ceil(desiredAmount / 360f));
        updateScript.Program()
                .Unbind();
        ProfilerHost.End("Particle buffer update");

        ProfilerHost.End("AbstractParticleSystem::Update");
    }

    public void Finish() {
        compute.Shutdown();
        ParticleManager.instance().systems.remove(this);
    }

}
