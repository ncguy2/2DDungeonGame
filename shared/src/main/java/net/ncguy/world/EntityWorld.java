package net.ncguy.world;

import net.ncguy.entity.Entity;
import net.ncguy.entity.component.EntityComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EntityWorld {

    public List<Entity> entities;

    public EntityWorld() {
        entities = new ArrayList<>();
    }

    public void Update(float delta) {
        for (Entity entity : entities)
            entity.Update(delta);
    }

    public List<Entity> GetRootEntitiesOfComponent(Class<? extends EntityComponent> componentType) {
        return entities.stream().filter(e -> componentType.isInstance(e.rootComponent)).collect(Collectors.toList());
    }

    public List<Entity> GetRootEntitiesWithComponents(Class<? extends EntityComponent>... componentType) {
        return entities.stream().filter(e -> e.All(componentType)).collect(Collectors.toList());
    }

    public List<Entity> GetFlattenedEntitiesOfComponent(Class<? extends EntityComponent> componentType) {
        return entities.stream().filter(e -> componentType.isInstance(e.rootComponent)).collect(Collectors.toList());
    }

    public List<Entity> GetFlattenedEntitiesWithComponents(Class<? extends EntityComponent>... componentType) {
        return entities.stream().filter(e -> e.All(componentType)).collect(Collectors.toList());
    }

    public void _GetFlattenedEntitiesOfComponent(Entity entity, List<Entity> entities, Class<? extends EntityComponent> componentType) {
        entities.stream().filter(e -> componentType.isInstance(e.rootComponent)).forEach(entities::add);
        entity.childEntities.forEach(e -> _GetFlattenedEntitiesOfComponent(e, entities, componentType));
    }

    public void _GetFlattenedEntitiesWithComponent(Entity entity, List<Entity> entities, Class<? extends EntityComponent>... componentType) {
        entities.stream().filter(e -> e.All(componentType)).forEach(entities::add);
        entity.childEntities.forEach(e -> _GetFlattenedEntitiesWithComponent(e, entities, componentType));
    }

    public void Add(Entity entity) {
        this.entities.add(entity);
    }
}
