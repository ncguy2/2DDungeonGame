package net.ncguy.lib.dmg.types;

import net.ncguy.lib.dmg.hp.Health;
import net.ncguy.lib.dmg.status.StatusEffect;

public class StatusDamageType extends DamageType {

    protected final StatusEffect effect;

    public StatusDamageType(StatusEffect effect) {
        super(effect.GetName());
        this.effect = effect;
        effect._SetDamageType(this);
    }

    @Override
    public void OnAfflictPreDamage(Health hp, float baseDamage) {
        effect.Attach(hp);
    }
}
