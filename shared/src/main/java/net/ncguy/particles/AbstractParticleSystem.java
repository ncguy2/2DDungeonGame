package net.ncguy.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.buffer.ShaderStorageBufferObject;
import net.ncguy.lib.foundation.utils.Curve;
import net.ncguy.profile.ProfilerHost;
import net.ncguy.shaders.ComputeShader;
import net.ncguy.util.ReloadableComputeShader;
import net.ncguy.util.curve.GLColourCurve;

import java.util.*;
import java.util.function.Consumer;

public abstract class AbstractParticleSystem {

    public static float GlobalLife = 0;

    public final int desiredAmount;
    protected float life;
    protected ReloadableComputeShader compute;
    protected ReloadableComputeShader updateScript;
    protected ShaderStorageBufferObject particleBuffer;
    protected ShaderStorageBufferObject deadBuffer;
    public final Map<String, Consumer<Integer>> uniformSetters;
    public final List<Consumer<ShaderProgram>> uniformTasks;

    public static final int PARTICLE_BYTES = 48;

    public AbstractParticleSystem(int desiredAmount) {
        this.desiredAmount = desiredAmount;
        uniformSetters = new HashMap<>();
        uniformTasks = new ArrayList<>();
        life = 0;

        particleBuffer = new ShaderStorageBufferObject(desiredAmount * PARTICLE_BYTES);
        deadBuffer = new ShaderStorageBufferObject(desiredAmount);

        compute = new ReloadableComputeShader("Particle::Spawn script", Gdx.files.internal("particles/compute/spawn.comp"));
        updateScript = new ReloadableComputeShader("ParticleManager::Update script", Gdx.files.internal("particles/compute/noiseAttractor.comp"));
        ParticleManager.instance().systems.add(this);
    }

    public void AddUniform(String name, Consumer<Integer> loc) {
        uniformSetters.put(name, loc);
    }

    public void Reset() {
        life = 0;
    }

    public void Spawn(int offset, int amount) {
        compute.Program()
                .Bind();
        compute.Program().SetUniform("u_startId", loc -> Gdx.gl.glUniform1i(loc, offset));
        compute.Program().SetUniform("iTime", loc -> Gdx.gl.glUniform1f(loc, life));
        compute.Program().SetUniform("u_rngBaseSeed", loc -> Gdx.gl.glUniform1i(loc, new Random().nextInt()));
        compute.Program().SetUniform("gTime", loc -> Gdx.gl.glUniform1f(loc, GlobalLife));
        uniformSetters.forEach(compute.Program()::SetUniform);
        BindBuffers(compute.Program());
        compute.Program()
                .Dispatch((int) Math.ceil(amount / 360f));
        compute.Program()
                .Unbind();
    }

    public void BindBuffer(int location) {
        particleBuffer.Bind(location);
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
        ComputeShader program = updateScript.Program();
        program.Bind();
        BindBuffers(program);
        program.SetUniform("u_delta", loc -> Gdx.gl.glUniform1f(loc, delta));
        program.SetUniform("iTime", loc -> Gdx.gl.glUniform1f(loc, life));
        program.SetUniform("gTime", loc -> Gdx.gl.glUniform1f(loc, GlobalLife));
        uniformSetters.forEach(program::SetUniform);
        program.Dispatch((int) Math.ceil(desiredAmount / 360f));
        program.Unbind();
        ProfilerHost.End("Particle buffer update");

        ProfilerHost.End("AbstractParticleSystem::Update");
    }

    public void Finish() {
        compute.Shutdown();
        ParticleManager.instance().systems.remove(this);
    }

    public void Bind(String uniform, GLColourCurve curve) {
        List<Curve.Item<Color>> items = curve.items;
        AddUniform(uniform + ".Length", l -> Gdx.gl.glUniform1i(l, items.size()));
        for (int i = 0; i < items.size(); i++) {
            Curve.Item<Color> col = items.get(i);
            String prefix = uniform + ".Entries[" + i + "]";
            AddUniform(prefix + ".Key", l -> Gdx.gl.glUniform1f(l, col.value));
            AddUniform(prefix + ".Value", l -> Gdx.gl.glUniform4f(l, col.item.r, col.item.g, col.item.b, col.item.a));
        }
    }

    public static enum SystemType {
        Temporal(TemporalParticleSystem.class),
        Burst(BurstParticleSystem.class),
        ;
        public final Class<? extends AbstractParticleSystem> type;
        SystemType(Class<? extends AbstractParticleSystem> type) {
            this.type = type;
        }

        public boolean Is(AbstractParticleSystem system) {
            if(system == null)
                return false;
            return type.isInstance(system);
        }
    }

}
