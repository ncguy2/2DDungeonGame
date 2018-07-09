package net.ncguy.entity.component;

import net.ncguy.entity.Entity;
import net.ncguy.entity.Transform2D;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Similar to the {@link EntityComponent}, the Scene component provides a 2d transform and functionality to allow for child components
 */
public class SceneComponent extends EntityComponent {

    public Transform2D transform;
    public Set<EntityComponent> childrenComponents;
    public transient Entity owningEntity;

    public SceneComponent(String name) {
        super(name);
        transform = new Transform2D();
        childrenComponents = new LinkedHashSet<>();
    }

    public void Add(EntityComponent component) {
        childrenComponents.add(component);
        component._OnAddToComponent(this);
    }

    public void Remove(EntityComponent component) {
        childrenComponents.remove(component);
        component._OnRemoveFromComponent();
    }

    @Override
    public void _OnAddToComponent(SceneComponent component) {
        super._OnAddToComponent(component);
        if(component != null)
            owningEntity = null;
    }

    @Override
    public void _OnRemoveFromComponent() {
        super._OnRemoveFromComponent();
        owningEntity = null;
    }

    @Override
    public Entity GetOwningEntity() {
        if(owningEntity != null)
            return owningEntity;
        return super.GetOwningEntity();
    }

    public void SetOwningEntity(Entity entity) {
        this.owningEntity = entity;
    }

    @Override
    public boolean Has(Class<? extends EntityComponent> type) {
        for (EntityComponent component : childrenComponents) {
            if(type.isInstance(component))
                return true;
        }

        for (EntityComponent component : childrenComponents) {
            if(component.Has(type))
                return true;
        }

        return false;
    }

    @Override
    public <T extends EntityComponent> T GetComponent(Class<T> type, boolean searchDescendants) {
        for (EntityComponent component : childrenComponents) {
            if(type.isInstance(component))
                return (T) component;
        }

        if(searchDescendants) {
            for (EntityComponent component : childrenComponents) {
                if (component.Has(type))
                    return component.GetComponent(type, searchDescendants);
            }
        }

        return null;
    }

    @Override
    public void Update(float delta) {
        super.Update(delta);
        for (EntityComponent childrenComponent : childrenComponents)
            childrenComponent.Update(delta);
    }
}
