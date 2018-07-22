package net.ncguy.world;

import net.ncguy.entity.Entity;
import net.ncguy.entity.component.EntityComponent;
import net.ncguy.script.ScriptUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EntityWorld {

    public static EntityWorld instance;

    public List<Entity> entities;
    public List<Runnable> tasks;

    public EntityWorld() {
        instance = this;
        entities = new ArrayList<>();
        tasks = new ArrayList<>();
    }

    public void PostRunnable(Runnable task) {
        tasks.add(task);
    }

    public void Update(float delta) {
        for (Entity entity : getEntities())
            entity.Update(delta);

        if(tasks.isEmpty())
            return;

        for (Runnable task : new ArrayList<>(tasks)) {
            task.run();
        }
        tasks.clear();
    }

    public synchronized List<Entity> GetRootEntitiesOfComponent(Class<? extends EntityComponent> componentType) {
        return entities.stream().filter(this::Alive).filter(e -> componentType.isInstance(e.rootComponent)).collect(Collectors.toList());
    }

    public synchronized List<Entity> GetRootEntitiesWithComponents(Class<? extends EntityComponent>... componentType) {
        return entities.stream().filter(this::Alive).filter(e -> e.All(componentType)).collect(Collectors.toList());
    }

    public synchronized List<Entity> GetFlattenedEntitiesOfComponent(Class<? extends EntityComponent> componentType) {
        return entities.stream().filter(this::Alive).filter(e -> componentType.isInstance(e.rootComponent)).collect(Collectors.toList());
    }

    public synchronized List<Entity> GetFlattenedEntitiesWithComponents(Class<? extends EntityComponent>... componentType) {
        return entities.stream().filter(this::Alive).filter(e -> e.All(componentType)).collect(Collectors.toList());
    }

    public synchronized void _GetFlattenedEntitiesOfComponent(Entity entity, List<Entity> entities, Class<? extends EntityComponent> componentType) {
        entities.stream().filter(this::Alive).filter(e -> componentType.isInstance(e.rootComponent)).forEach(entities::add);
        entity.childEntities.forEach(e -> _GetFlattenedEntitiesOfComponent(e, entities, componentType));
    }

    public synchronized void _GetFlattenedEntitiesWithComponent(Entity entity, List<Entity> entities, Class<? extends EntityComponent>... componentType) {
        entities.stream().filter(this::Alive).filter(e -> e.All(componentType)).forEach(entities::add);
        entity.childEntities.forEach(e -> _GetFlattenedEntitiesWithComponent(e, entities, componentType));
    }

    public synchronized void Add(Entity entity) {
        entity.SetWorld(this);
        this.entities.add(entity);
    }

    public boolean Alive(Entity entity) {
        return ScriptUtils.instance().IsEntityAlive(entity);
    }

    public List<Entity> getEntities() {
        return new ArrayList<>(entities);
    }

    public synchronized void Remove(Entity entity) {
        this.entities.remove(entity);
        entity.SetWorld(null);
    }
}
