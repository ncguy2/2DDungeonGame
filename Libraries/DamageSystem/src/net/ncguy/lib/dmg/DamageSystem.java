package net.ncguy.lib.dmg;

import net.ncguy.lib.dmg.hp.Health;
import net.ncguy.lib.dmg.status.StatusEffect;
import net.ncguy.lib.dmg.types.SimpleDamageType;
import net.ncguy.lib.dmg.types.StatusDamageType;

public class DamageSystem {


    public static void main(String[] args) {

        Health health = new Health();
        health.maxHealth = 500;
        health.health = 500;

        // TODO damage curves

        StatusEffect effect = new StatusEffect(null, 5) {

            @Override
            public void OnStackChange(int oldCount, int newCount, int incrementCount) {

            }

            @Override
            public void OnUpdate(float delta) {
                for (int i = 0; i < stackCount; i++) {
                    target.Damage(5 * delta);
                }
            }

            @Override
            public void OnSecond() {
                target.Damage(20 + (stackCount * 5));
            }

            @Override
            public String GetName() {
                return "Test status effect";
            }
        };

        Damage damage = Damage.Of(new SimpleDamageType(), 50)
                .With(new StatusDamageType(effect.Stacks(5)), 15);
        damage.Apply(health);

        health._Update(.1f);
        System.out.printf("%f: %f\n", .1f, health.health);
        health._Update(.1f);
        System.out.printf("%f: %f\n", .2f, health.health);
        health._Update(.1f);
        System.out.printf("%f: %f\n", .3f, health.health);
        health._Update(.1f);
        System.out.printf("%f: %f\n", .4f, health.health);
        health._Update(.1f);
        System.out.printf("%f: %f\n", .5f, health.health);
        health._Update(.4f);
        System.out.printf("%f: %f\n", .9f, health.health);
        health._Update(.1f);
        System.out.printf("%f: %f\n", 1f, health.health);
        health._Update(.1f);
        System.out.printf("%f: %f\n", 1.1f, health.health);
    }

}
