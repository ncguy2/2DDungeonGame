package net.ncguy.entity.component;

import net.ncguy.entity.Entity;
import net.ncguy.entity.Transform2D;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Similar to the {@link EntityComponent}, the Scene component provides a 2d transform and functionality to allow for child components
 */
public class SceneComponent extends EntityComponent {

    public Transform2D transform;
    public final Set<EntityComponent> childrenComponents;
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
        if(component != null) {
            this.transform.parent = component.transform;
            owningEntity = null;
        }
    }

    @Override
    public void _OnRemoveFromComponent() {
        this.transform.parent = null;
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
        Set<EntityComponent> childrenComponents = GetComponents();

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
        Set<EntityComponent> childrenComponents = GetComponents();

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
    public <T extends EntityComponent> void GetComponents(Class<T> type, boolean searchDescendants, List<T> componentList) {

        Set<EntityComponent> childrenComponents = GetComponents();

        for (EntityComponent component : childrenComponents) {
            if(type.isInstance(component))
                componentList.add((T) component);
        }

        if(searchDescendants) {
            for (EntityComponent component : childrenComponents) {
                if (component.Has(type))
                    component.GetComponents(type, searchDescendants, componentList);
            }
        }
    }

    private Set<EntityComponent> GetComponents() {
        synchronized (childrenComponents) {
            return new LinkedHashSet<>(childrenComponents);
        }
    }

    @Override
    public void Update(float delta) {
        super.Update(delta);
        for (EntityComponent childrenComponent : GetComponents())
            childrenComponent.Update(delta);
    }

    @Override
    public void Destroy() {
        GetComponents().forEach(EntityComponent::Destroy);
        synchronized (childrenComponents) {
            childrenComponents.clear();
        }
        super.Destroy();
    }
}
