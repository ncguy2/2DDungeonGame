package net.ncguy.damage.status;

import net.ncguy.entity.component.HealthComponent;
import net.ncguy.entity.component.LightComponent;
import net.ncguy.entity.component.SceneComponent;
import net.ncguy.lib.dmg.hp.Health;
import net.ncguy.lib.dmg.status.StatusEffect;

public class BurningStatus extends StatusEffect {

    /**
     * Creates a status effect and attaches itself to the provided Health object
     *
     * @param target The target to attach the effect to
     * @param life   The life of the status effect
     */
    public BurningStatus(Health target, float life) {
        super(target, life);
    }

    public float baseDps;

    transient LightComponent light;

    @Override
    public void OnAttach(Health target) {
        if (target.userData instanceof HealthComponent) {
            SceneComponent owningComponent = ((HealthComponent) target.userData).owningComponent;
            light = new LightComponent("Burning light");
            light.radius = 64;
            light.colour.set(0xE25822FF);
            owningComponent.Add(light);
        }
    }

    @Override
    public void OnRemove(Health target) {
        super.OnRemove(target);
        if(light != null) {
            light.RemoveFromParent();
            light = null;
        }
    }

    @Override
    public void OnStackChange(int oldCount, int newCount, int incrementCount) {

    }

    @Override
    public void OnUpdate(float delta) {
        if (target != null)
            target.Damage((baseDps * stackCount) * delta);
    }

    @Override
    public void OnSecond() {

    }

    @Override
    public String GetName() {
        return "Burning";
    }
}
