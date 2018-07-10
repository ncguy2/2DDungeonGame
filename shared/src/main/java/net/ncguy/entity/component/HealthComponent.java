package net.ncguy.entity.component;

import net.ncguy.lib.dmg.hp.Health;

public class HealthComponent extends EntityComponent {

    public final Health health;

    public HealthComponent(String name) {
        super(name);
        health = new Health();
    }
}
