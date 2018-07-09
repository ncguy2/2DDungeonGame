package net.ncguy.lib.dmg.types;

import net.ncguy.lib.dmg.hp.Health;

public class DamageType {

    public String name;

    public DamageType(String name) {
        this.name = name;
    }

    public float ModifyDamage(Health hp, float baseDamage) {
        return baseDamage;
    }

    public void OnAfflictPreDamage(Health hp, float baseDamage) {}
    public void OnAfflictPostDamage(Health hp, float baseDamage) {}

}
