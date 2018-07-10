package net.ncguy.entity;

import net.ncguy.entity.component.EntityComponent;
import net.ncguy.entity.component.SceneComponent;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Entity {

    public Set<Entity> childEntities;
    public SceneComponent rootComponent;

    public Entity() {
        childEntities = new LinkedHashSet<>();
        SetRootComponent(new SceneComponent("Root"));
    }

    public void Update(float delta) {
        if(this.rootComponent != null)
            rootComponent.Update(delta);
        for (Entity entity : childEntities)
            entity.Update(delta);
    }

    public <T extends SceneComponent> T SetRootComponent(T component) {
        if(this.rootComponent != null)
            this.rootComponent.SetOwningEntity(null);
        this.rootComponent = component;
        if(this.rootComponent != null)
            this.rootComponent.SetOwningEntity(this);

        return component;
    }

    public <T extends EntityComponent> T AddComponent(T component) {
        if(this.rootComponent != null)
            this.rootComponent.Add(component);
        return component;
    }

    public <T extends EntityComponent> T RemoveComponent(T component) {
        if(this.rootComponent != null)
            this.rootComponent.Remove(component);
        return component;
    }

    public <T extends Entity> T AddEntity(T entity) {
        childEntities.add(entity);
        return entity;
    }

    public <T extends Entity> T RemoveEntity(T entity) {
        childEntities.remove(entity);
        return entity;
    }

    public boolean HasComponent(Class<? extends EntityComponent> componentType) {

        if(componentType.isInstance(rootComponent))
            return true;

        return rootComponent.Has(componentType);
    }

    public boolean Any(Class<? extends EntityComponent>... componentTypes) {
        for (Class<? extends EntityComponent> type : componentTypes) {
            if(HasComponent(type))
                return true;
        }
        return false;
    }

    public boolean One(Class<? extends EntityComponent>... componentTypes) {
        boolean hasOne = false;
        for (Class<? extends EntityComponent> type : componentTypes) {
            if(HasComponent(type)) {
                if(hasOne)
                    return false;
                hasOne = true;
            }

        }
        return hasOne;
    }

    public boolean All(Class<? extends EntityComponent>... componentTypes) {
        for (Class<? extends EntityComponent> type : componentTypes) {
            if(!HasComponent(type))
                return false;
        }
        return true;
    }

    public <T extends EntityComponent> T GetComponent(Class<T> type, boolean searchDescendants) {
        if(rootComponent == null)
            return null;

        if(type.isInstance(rootComponent))
            return (T) rootComponent;

        return rootComponent.GetComponent(type, searchDescendants);
    }

    public SceneComponent GetRootComponent() {
        return rootComponent;
    }

    public <T extends EntityComponent> List<T> GetComponents(Class<T> type, boolean searchDescendants) {
        List<T> list = new ArrayList<>();
        GetRootComponent().GetComponents(type, searchDescendants, list);
        return list;
    }

    public Transform2D Transform() {
        return rootComponent.transform;
    }
}
