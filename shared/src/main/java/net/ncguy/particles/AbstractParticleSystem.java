package net.ncguy.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.buffer.ShaderStorageBufferObject;
import net.ncguy.lib.foundation.utils.Curve;
import net.ncguy.particles.render.ParticleRenderData;
import net.ncguy.profile.ProfilerHost;
import net.ncguy.shaders.ComputeShader;
import net.ncguy.util.curve.GLColourCurve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public abstract class AbstractParticleSystem {

    public static float GlobalLife = 0;

    public static final int INVOCATIONS_PER_WORKGROUP = 16;

    public ParticleRenderData renderer = new ParticleRenderData();

    public int bufferId;
    public int desiredAmount;
    protected float life;
    protected float duration;

    protected LoopingBehaviour loopingBehaviour = LoopingBehaviour.None;
    protected int loopingAmount = 1;

    protected ParticleShader spawnScript;
    protected ParticleShader updateScript;

//    protected ReloadableComputeShader compute;
//    protected ReloadableComputeShader updateScript;
    protected ShaderStorageBufferObject particleBuffer;

//    protected ShaderStorageBufferObject deadBuffer;
    public final Map<String, Consumer<Integer>> uniformSetters;
    public final List<Consumer<ShaderProgram>> uniformTasks;

    public final Map<String, String> macroParams;

    public static final int PARTICLE_BYTES = 48;

    public Runnable onFinish;
    public Runnable onLoop;

    public AbstractParticleSystem(int desiredAmount, float duration, String... blockNames) {
        macroParams = new HashMap<>();
        this.duration = duration;
        uniformSetters = new HashMap<>();
        uniformTasks = new ArrayList<>();
        life = 0;

        FileHandle spawnHandle = Gdx.files.internal("particles/compute/framework.comp");
        FileHandle updateHandle = Gdx.files.internal("particles/compute/framework.comp");

//        this.desiredAmount = desiredAmount;
//        particleBuffer = new ShaderStorageBufferObject(desiredAmount * PARTICLE_BYTES);


//        deadBuffer = new ShaderStorageBufferObject(desiredAmount);

        ParticleManager.instance().AddSystem(this);

        macroParams.put("p_BindingPoint", String.valueOf(bufferId));

//        compute = new ReloadableComputeShader("Particle::Spawn script", spawnHandle, macroParams);
//        updateScript = new ReloadableComputeShader("ParticleManager::Update script", updateHandle, macroParams);

        spawnScript = new ParticleShader("Particle::Spawn script", spawnHandle, macroParams);
        updateScript = new ParticleShader("Particle::Update script", updateHandle, macroParams);

        for (String block : blockNames)
            ParticleManager.instance()
                    .GetParticleBlock(block)
                    .ifPresent(this::AddBlock);

//        AddBlock(Json.From(Gdx.files.internal("metadata/shaders/lifecycle.json").readString(), ParticleBlock.class));
//        AddBlock(Json.From(Gdx.files.internal("metadata/shaders/triangleVectorField.json").readString(), ParticleBlock.class));
//        AddBlock(Json.From(Gdx.files.internal("metadata/shaders/followColourCurve.json").readString(), ParticleBlock.class));
//        AddBlock(Json.From(Gdx.files.internal("metadata/shaders/followVelocity.json").readString(), ParticleBlock.class));
//
//        AddBlock(Json.From(Gdx.files.internal("metadata/shaders/initialState.json").readString(), ParticleBlock.class));
//        AddBlock(Json.From(Gdx.files.internal("metadata/shaders/lineSpawn.json").readString(), ParticleBlock.class));

        spawnScript.ReloadImmediate();
        updateScript.ReloadImmediate();

        SetAmount(desiredAmount);

        BindBuffer();
    }

    public void AddBlock(ParticleBlock block) {
        AddBlock(block, block.type);
    }

    public void AddBlock(ParticleBlock block, ParticleBlock.Type typeOverride) {
        switch(typeOverride) {
            case Spawn: spawnScript.AddBlock(block); break;
            case Update: updateScript.AddBlock(block); break;
        }
    }

    public void SetAmount(float amt) {
        SetAmount(Math.round(amt));
    }

    public void SetAmount(int amt) {
        if(particleBuffer != null) {
            particleBuffer.Unbind();
            particleBuffer.dispose();
        }
//        deadBuffer.Unbind();
//        deadBuffer.dispose();

        amt = round(amt, 256);
        desiredAmount = amt;

        particleBuffer = new ShaderStorageBufferObject(desiredAmount * PARTICLE_BYTES);
//        deadBuffer = new ShaderStorageBufferObject(desiredAmount);

        BindBuffer();
    }

    public void AddUniform(String name, Consumer<Integer> loc) {
        uniformSetters.put(name, loc);
    }

    public void Reset() {
        life = 0;
    }

    int round(double i, int v){
        return (int) (Math.round(i/v) * v);
    }

    public int Spawn(int offset, int amount) {

        ComputeShader program = spawnScript.Program();

        program.Bind();
        program.SetUniform("u_startId", loc -> Gdx.gl.glUniform1i(loc, offset));
        program.SetUniform("iTime", loc -> Gdx.gl.glUniform1f(loc, life));
        program.SetUniform("u_rngBaseSeed", loc -> Gdx.gl.glUniform1i(loc, ThreadLocalRandom.current().nextInt()));
        program.SetUniform("gTime", loc -> Gdx.gl.glUniform1f(loc, GlobalLife));
        program.SetUniform("imaxParticleCount", loc -> Gdx.gl.glUniform1i(loc, desiredAmount));
        uniformSetters.forEach(program::SetUniform);
//        BindBuffer();
//        BindBuffers(compute.Program());

        int amtSpawned = round(amount, INVOCATIONS_PER_WORKGROUP);
        program.Dispatch(amtSpawned);
        program.Unbind();
        return amtSpawned;
    }

    public void BindBuffer() {
        BindBuffer(bufferId);
    }
    public void BindBuffer(int location) {
        if(particleBuffer == null)
            return;

        spawnScript.SetParticleBuffer(location, particleBuffer);
        updateScript.SetParticleBuffer(location, particleBuffer);
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
        program.SetUniform("u_rngBaseSeed", loc -> Gdx.gl.glUniform1i(loc, ThreadLocalRandom.current().nextInt()));
        program.SetUniform("u_delta", loc -> Gdx.gl.glUniform1f(loc, delta));
        program.SetUniform("iTime", loc -> Gdx.gl.glUniform1f(loc, life));
        program.SetUniform("gTime", loc -> Gdx.gl.glUniform1f(loc, GlobalLife));
        program.SetUniform("imaxParticleCount", loc -> Gdx.gl.glUniform1i(loc, desiredAmount));
        uniformSetters.forEach(program::SetUniform);

        int amtSpawned = round(desiredAmount, INVOCATIONS_PER_WORKGROUP);
        program.Dispatch(amtSpawned);
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

    public void BeginFinish() {
        loopingBehaviour = LoopingBehaviour.None;
    }

    public void Finish() {
        Finish(false);
    }

    public void Finish(boolean force) {

        if(!force && loopingBehaviour.equals(LoopingBehaviour.Forever) || (loopingBehaviour.equals(LoopingBehaviour.Amount) && loopingAmount > 0)) {
            if(loopingBehaviour.equals(LoopingBehaviour.Amount))
                loopingAmount--;
            if(onLoop != null)
                onLoop.run();
            Reset();
            return;
        }

        if(onFinish != null)
            onFinish.run();
        if(spawnScript != null)
            spawnScript.Shutdown();
        if(updateScript != null)
            updateScript.Shutdown();

        spawnScript = null;
        updateScript = null;

        ParticleManager.instance().RemoveSystem(this);
    }

    public boolean IsFinished() {
        return spawnScript == null;
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

    public static enum LoopingBehaviour {
        None,
        Amount,
        Forever
    }

}
