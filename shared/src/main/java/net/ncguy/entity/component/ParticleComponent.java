package net.ncguy.entity.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import net.ncguy.particles.AbstractParticleSystem;
import net.ncguy.particles.BurstParticleSystem;
import net.ncguy.particles.TemporalParticleSystem;
import net.ncguy.util.DeferredCalls;
import net.ncguy.util.curve.GLColourCurve;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ParticleComponent extends SceneComponent {

    @EntityProperty(Type = Float.class, Description = "Seconds this particle system should llive for", Category = "Particle", Name = "Duration")
    public float duration;

    @EntityProperty(Type = Integer.class, Description = "Amount of particles managed by this system", Category = "Particle", Name = "Particle count")
    public int particleCount;
    @EntityProperty(Type = Float.class, Description = "Amount of seconds to spawn the particles over", Category = "Particle", Name = "Particle spawn rate")
    public float spawnOverTime;
    @EntityProperty(Type = AbstractParticleSystem.SystemType.class, Description = "The type of system", Category = "Particle", Name = "System type")
    public AbstractParticleSystem.SystemType systemType;
    @EntityProperty(Type = GLColourCurve.class, Description = "Colour curve", Category = "Particle", Name = "Colour curve")
    public GLColourCurve curve;

    public transient AbstractParticleSystem particleSystem;
    public BiConsumer<ParticleComponent, AbstractParticleSystem> onInit;
    public Consumer<ParticleComponent> onFinish;

    public ParticleComponent() {
        this(null);
    }

    public ParticleComponent(String name) {
        super(name);
        curve = new GLColourCurve();
        curve.Add(Color.RED.cpy(), 0.f);
        curve.Add(Color.GREEN.cpy(), 2.5f);
        curve.Add(Color.BLUE.cpy(), 5f);
        curve.Add(Color.WHITE.cpy(), 7.5f);
    }

    @Override
    public void Update(float delta) {
        if(!systemType.Is(particleSystem))
            Reinit();

        super.Update(delta);
    }

    @EntityFunction(Name = "Reinitialize", Category = "Particle", Description = "Reinitializes the particle system")
    public void Reinit() {
        Gdx.app.postRunnable(this::ReinitImmediate);
    }
    public void ReinitImmediate() {
        if(particleSystem != null) {
            if(onFinish != null)
                onFinish.accept(ParticleComponent.this);
            particleSystem.Finish();
            particleSystem = null;
        }
        particleSystem = BuildSystem();

        if(onInit != null && particleSystem != null)
            onInit.accept(this, particleSystem);

        DeferredCalls.Instance().Post(duration, () -> {
            if(onFinish != null)
                onFinish.accept(ParticleComponent.this);
        });
    }

    public AbstractParticleSystem BuildSystem() {
        switch(systemType) {
            case Burst: return new BurstParticleSystem(particleCount);
            case Temporal: return new TemporalParticleSystem(particleCount, spawnOverTime);
        }
        return null;
    }

    @Override
    public void _OnRemoveFromComponent() {
        if(particleSystem != null) {
            particleSystem.Finish();
            particleSystem = null;
        }
        super._OnRemoveFromComponent();
    }
}


