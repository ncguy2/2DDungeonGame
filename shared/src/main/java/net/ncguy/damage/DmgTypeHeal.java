package net.ncguy.damage;

import net.ncguy.lib.dmg.hp.Health;
import net.ncguy.lib.dmg.types.DamageType;

public class DmgTypeHeal extends DamageType {

    public DmgTypeHeal() {
        super("Heal");
    }

    @Override
    public float ModifyDamage(Health hp, float baseDamage) {
        return -baseDamage;
    }
}
