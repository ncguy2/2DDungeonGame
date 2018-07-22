package net.ncguy.system;

import net.ncguy.entity.Entity;
import net.ncguy.entity.component.AbilityComponent;
import net.ncguy.world.EntityWorld;

import java.util.List;

public class AbilitySystem extends BaseSystem {

    public AbilitySystem(EntityWorld operatingWorld) {
        super(operatingWorld);
    }

    @Override
    public void Startup() {

    }

    @Override
    public void Update(float delta) {
        List<Entity> entities = operatingWorld.GetFlattenedEntitiesWithComponents(AbilityComponent.class);
        for (Entity entity : entities) {
            List<AbilityComponent> abilities = entity.GetComponents(AbilityComponent.class, true);

            for (AbilityComponent ability : abilities) {
                AbilityComponent.AbilityState state = ability.State();
                if(state.enabled) {
                    if(state.just)
                        InvokeJustEnabled(entity, ability);
                    else InvokeEnabled(entity, ability, delta);
                }else {
                    if(state.just)
                        InvokeJustDisabled(entity, ability);
                    else InvokeDisabled(entity, ability, delta);
                }

                InvokeUpdate(entity, ability, delta);
            }
        }
    }
    
    void InvokeEnabled(Entity entity, AbilityComponent component, float delta) {
        if(component.script != null)
            component.script.InvokeActiveUpdate(entity, delta);
    }
    
    void InvokeJustEnabled(Entity entity, AbilityComponent component) {
        if(component.script != null)
            component.script.InvokeEnabled(entity);
        component.SetState(AbilityComponent.AbilityState.Enabled);
    }

    void InvokeDisabled(Entity entity, AbilityComponent component, float delta) {
        if(component.script != null)
            component.script.InvokeInactiveUpdate(entity, delta);
    }

    void InvokeJustDisabled(Entity entity, AbilityComponent component) {
        if(component.script != null)
            component.script.InvokeDisabled(entity);
        component.SetState(AbilityComponent.AbilityState.Disabled);
    }

    void InvokeUpdate(Entity entity, AbilityComponent component, float delta) {
        if(component.script != null)
            component.script.InvokeUpdate(entity, delta);
    }

    @Override
    public void Shutdown() {

    }
}
