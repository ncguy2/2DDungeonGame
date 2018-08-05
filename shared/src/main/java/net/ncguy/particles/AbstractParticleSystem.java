package net.ncguy.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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

    public int bufferId;
    public int desiredAmount;
    protected float life;
    protected float duration;
    protected ReloadableComputeShader compute;
    protected ReloadableComputeShader updateScript;
    protected ShaderStorageBufferObject particleBuffer;
    protected ShaderStorageBufferObject deadBuffer;
    public final Map<String, Consumer<Integer>> uniformSetters;
    public final List<Consumer<ShaderProgram>> uniformTasks;

    public final Map<String, String> macroParams;

    public static final int PARTICLE_BYTES = 48;

    public Runnable onFinish;

    public AbstractParticleSystem(int desiredAmount, float duration) {
        this(desiredAmount, null, null, duration);
    }
    public AbstractParticleSystem(int desiredAmount, FileHandle spawnHandle, FileHandle updateHandle, float duration) {
        macroParams = new HashMap<>();
        this.duration = duration;
        this.desiredAmount = desiredAmount;
        uniformSetters = new HashMap<>();
        uniformTasks = new ArrayList<>();
        life = 0;

        if(spawnHandle == null)
            spawnHandle = Gdx.files.internal("particles/compute/spawn.comp");
        if(updateHandle == null)
            updateHandle = Gdx.files.internal("particles/compute/noiseAttractor.comp");

        particleBuffer = new ShaderStorageBufferObject(desiredAmount * PARTICLE_BYTES);
        deadBuffer = new ShaderStorageBufferObject(desiredAmount);

        ParticleManager.instance().AddSystem(this);

        macroParams.put("p_BindingPoint", String.valueOf(bufferId));

        compute = new ReloadableComputeShader("Particle::Spawn script", spawnHandle, macroParams);
        updateScript = new ReloadableComputeShader("ParticleManager::Update script", updateHandle, macroParams);

        BindBuffer();
    }

    public void SetAmount(float amt) {
        SetAmount(Math.round(amt));
    }

    public void SetAmount(int amt) {
        particleBuffer.Unbind();
        particleBuffer.dispose();
        deadBuffer.Unbind();
        deadBuffer.dispose();

        desiredAmount = amt;

        particleBuffer = new ShaderStorageBufferObject(desiredAmount * PARTICLE_BYTES);
        deadBuffer = new ShaderStorageBufferObject(desiredAmount);

        BindBuffer();
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
//        BindBuffer();
//        BindBuffers(compute.Program());


        compute.Program().Dispatch((int) Math.ceil(amount / 256f));
        compute.Program()
                .Unbind();
    }

    public void BindBuffer() {
        BindBuffer(bufferId);
    }
    public void BindBuffer(int location) {
        particleBuffer.Bind(location);
    }

    @Deprecated
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
//        BindBuffer();
        program.SetUniform("u_delta", loc -> Gdx.gl.glUniform1f(loc, delta));
        program.SetUniform("iTime", loc -> Gdx.gl.glUniform1f(loc, life));
        program.SetUniform("gTime", loc -> Gdx.gl.glUniform1f(loc, GlobalLife));
        uniformSetters.forEach(program::SetUniform);

        program.Dispatch((int) Math.ceil(desiredAmount / 256f));
        program.Unbind();
        ProfilerHost.End("Particle buffer update");

        if(ShouldFinish())
            Finish();

        ProfilerHost.End("AbstractParticleSystem::Update");
    }

    public boolean ShouldFinish() {
        if(duration < 0)
            return false;

        return life >= duration;
    }

    public void Finish() {
        if(onFinish != null)
            onFinish.run();
        compute.Shutdown();
        ParticleManager.instance().RemoveSystem(this);
    }

    public boolean IsFinished() {
        return compute == null;
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
        TextureBurst(TextureBurstParticleSystem.class),
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
