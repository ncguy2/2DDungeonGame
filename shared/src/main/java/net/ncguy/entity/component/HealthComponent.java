package net.ncguy.entity.component;

import net.ncguy.entity.Entity;
import net.ncguy.lib.dmg.hp.Health;

public class HealthComponent extends EntityComponent {

    public final Health health;

    public HealthComponent(String name) {
        super(name);
        health = new Health();
        health.userData = this;
    }

    @Override
    public void Update(float delta) {
        super.Update(delta);
        health._Update(delta);
    }

    public static HealthComponent SingleHealthComponent(String name, float hp) {
        HealthComponent healthComponent = new HealthComponent(name);
        Health health = healthComponent.health;
        health.SetMaxHealth(hp);
        health.tempHealth = 0;
        health.onDeath = () -> {
            Entity entity = healthComponent.GetOwningEntity();
            if(entity != null)
                entity.Destroy();
        };
        return healthComponent;
    }

}
