package net.ncguy.entity;

import net.ncguy.entity.component.EntityComponent;
import net.ncguy.entity.component.SceneComponent;

import java.util.LinkedHashSet;
import java.util.Set;

public class Entity {

    public Set<Entity> childEntities;
    public SceneComponent rootComponent;

    public Entity() {
        childEntities = new LinkedHashSet<>();
        rootComponent = new SceneComponent("Root");
    }

    public void SetRootComponent(SceneComponent component) {
        this.rootComponent = component;
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

}
