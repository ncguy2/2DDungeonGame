package net.ncguy.lib.dmg.types;

import net.ncguy.lib.dmg.hp.Health;

import java.util.function.BiConsumer;

public class InlineDamageType extends DamageType {

    protected final BiConsumer<Health, Float> onAfflict;

    public InlineDamageType(String name, BiConsumer<Health, Float> onAfflict) {
        super(name);
        this.onAfflict = onAfflict;
    }

    @Override
    public void OnAfflictPreDamage(Health hp, float baseDamage) {
        if(onAfflict != null)
            onAfflict.accept(hp, baseDamage);
    }
}
