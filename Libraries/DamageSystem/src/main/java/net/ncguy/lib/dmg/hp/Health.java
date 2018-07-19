package net.ncguy.lib.dmg.hp;

import net.ncguy.lib.dmg.status.StatusEffect;
import net.ncguy.lib.dmg.types.DamageType;

import java.util.HashMap;
import java.util.Map;

public class Health {

    public transient Map<Class<? extends StatusEffect>, StatusEffect> statusEffectMap;
    public transient float healCooldown;

    public Runnable onDeath;

    public float health;
    public float maxHealth;
    public float tempHealth;

    public Object userData;

    public Health() {
        statusEffectMap = new HashMap<>();
        maxHealth = health = 100;
        tempHealth = 0;
    }

    public void SetMaxHealth(float maxHealth) {
        this.maxHealth = maxHealth;
        if(this.health > maxHealth) {
            tempHealth = Math.abs(this.health - maxHealth) * .5f;
            this.health = maxHealth;
        }
    }

    public void Damage(float amt) {
        _Damage(amt);
    }

    public void Afflict(DamageType type, float baseDamage) {
        type.OnAfflictPreDamage(this, baseDamage);
        Damage(type.ModifyDamage(this, baseDamage));
        type.OnAfflictPostDamage(this, baseDamage);
    }

    public boolean CanBeHealed() {
        return healCooldown <= 0;
    }

    public float GetHealCooldownFactor(float amtHealed) {
//        return (float) (Math.log10(amtHealed) / Math.log10(2));
        return (float) Math.sqrt(amtHealed * .2);
    }

    public void Die() {
        if(onDeath != null)
            onDeath.run();
    }

    // Internal API

    public void _Damage(float amt) {

        if(amt < 0) {
            _Heal(-amt);
            return;
        }

        // TempHealth gate
        if(tempHealth > 0) {
            tempHealth -= amt;
            if(tempHealth < 0)
                tempHealth = 0;
            return;
        }

        health -= amt;

        if(health <= 0)
            Die();
    }

    public void _Heal(float amt) {
        if (!CanBeHealed())
            return;

        health += amt;
        float overflow = health - maxHealth;
        health = Math.min(health, maxHealth);
        if (overflow > 0) {
            tempHealth = overflow * .5f;
            healCooldown = GetHealCooldownFactor(overflow);
        }
    }

    public void _Update(float delta) {
        if (healCooldown > 0)
            healCooldown -= delta;
        statusEffectMap.values()
                .forEach(e -> e._Update(delta));
    }

    public void _AddStatusEffect(StatusEffect effect) {
        if (statusEffectMap.containsKey(effect.getClass())) {
            statusEffectMap.get(effect.getClass())
                    .IncrementStack();
            return;
        }
        statusEffectMap.put(effect.getClass(), effect);
    }

    public void _Remove(StatusEffect effect) {
        statusEffectMap.remove(effect.getClass());
    }
}
