package net.ncguy.lib.dmg;

import net.ncguy.lib.dmg.hp.Health;
import net.ncguy.lib.dmg.types.DamageType;

import java.util.HashMap;
import java.util.Map;

public class Damage {

    public Map<DamageType, Float> damageMap;
    public float damageScale;

    public Damage() {
        damageMap = new HashMap<>();
        damageScale = 1;
    }

    public void Add(DamageType type, float dmg) {
        damageMap.put(type, dmg);
    }

    public void SetScale(float scale) {
        damageScale = scale;
    }

    public void Apply(Health target) {
        if(target == null)
            return;

        damageMap.forEach((t, f) -> target.Afflict(t, f * damageScale));
    }

    public Damage With(DamageType type, float damage) {
        Add(type, damage);
        return this;
    }

    public Damage Scale(float scale) {
        SetScale(scale);
        return this;
    }

    public static Damage Of(DamageType type, float damage) {
        Damage dmg = new Damage();
        dmg.Add(type, damage);
        return dmg;
    }

}
