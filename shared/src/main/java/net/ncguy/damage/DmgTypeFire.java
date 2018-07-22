package net.ncguy.damage;

import net.ncguy.damage.status.BurningStatus;
import net.ncguy.lib.dmg.hp.Health;
import net.ncguy.lib.dmg.types.DamageType;

public class DmgTypeFire extends DamageType {

    public DmgTypeFire() {
        super("Fire");
    }

    @Override
    public float ModifyDamage(Health hp, float baseDamage) {
        // TODO account for weaknesses to fire
        return baseDamage;
    }

    @Override
    public void OnAfflictPostDamage(Health hp, float baseDamage) {
        super.OnAfflictPostDamage(hp, baseDamage);
        new BurningStatus(hp, 3).baseDps = baseDamage * .50f;
    }
}
