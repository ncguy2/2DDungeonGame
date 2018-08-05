package net.ncguy.entity.component;

import com.badlogic.gdx.Gdx;
import net.ncguy.particles.AbstractParticleSystem;
import net.ncguy.particles.ParticleManager;
import net.ncguy.particles.ParticleProfile;
import net.ncguy.util.DeferredCalls;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ParticleProfileComponent extends SceneComponent {

    @EntityProperty(Type = String.class, Description = "The system profile name", Category = "Particle", Name = "Profile name")
    public String systemName;

    public transient ParticleProfile profile;
    public transient AbstractParticleSystem particleSystem;
    public BiConsumer<ParticleProfileComponent, AbstractParticleSystem> onInit;
    public Consumer<ParticleProfileComponent> onFinish;

    public ParticleProfileComponent() {
        this(null);
    }

    public ParticleProfileComponent(String name) {
        super(name);
    }

    @Override
    public void Update(float delta) {
        if(profile == null || !profile.name.equalsIgnoreCase(systemName))
            Reinit();

        super.Update(delta);


        if(particleSystem != null && particleSystem.ShouldFinish()) {
            if(!particleSystem.IsFinished())
                particleSystem.Finish();
            GetOwningEntity().GetWorld().PostRunnable(this::Destroy);
        }
    }

    @EntityFunction(Name = "Reinitialize", Category = "Particle", Description = "Reinitializes the particle system")
    public void Reinit() {
        Gdx.app.postRunnable(this::ReinitImmediate);
    }
    public void ReinitImmediate() {
        if(particleSystem != null) {
            if(onFinish != null)
                onFinish.accept(ParticleProfileComponent.this);
            particleSystem.onFinish = null;
            particleSystem.Finish();
            particleSystem = null;
        }
        particleSystem = BuildSystem();

        if(onInit != null && particleSystem != null)
            onInit.accept(this, particleSystem);


        DeferredCalls.Instance().Post(profile.duration, () -> {
            if(onFinish != null)
                onFinish.accept(ParticleProfileComponent.this);
        });
    }

    public AbstractParticleSystem BuildSystem() {
        Optional<ParticleProfile> p = ParticleManager.instance()
                .GetProfile(systemName);
        if(!p.isPresent())
            return null;

        this.profile = p.get();

        return ParticleManager.instance()
                .BuildSystem(this.profile)
                .orElse(null);
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


