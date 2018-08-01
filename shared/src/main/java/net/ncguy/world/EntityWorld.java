package net.ncguy.world;

import net.ncguy.entity.Entity;
import net.ncguy.entity.component.EntityComponent;
import net.ncguy.script.ScriptUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class EntityWorld {

    public static EntityWorld instance;

    public final List<Entity> entities;
    public final List<Runnable> tasks;

    public Consumer<Entity> onManagedEntityAdd;

    public EntityWorld() {
        instance = this;
        entities = new ArrayList<>();
        tasks = new ArrayList<>();
    }

    public Optional<Entity> GetFromDomain(String path) {
        int endIndex = path.indexOf("://");
        String substring = path.substring(0, endIndex);
        Optional<Entity> entity = entities.stream()
                .filter(e -> e.uuid.toString()
                        .equalsIgnoreCase(substring))
                .findFirst();
        return entity;
    }

    public EntityComponent GetFromPath(String path) {
        Optional<Entity> entity = GetFromDomain(path);
        Optional<EntityComponent> component = entity
                .map(e -> e.GetFromPath(path));
        return component.orElse(null);
    }

    public void AddUniqueEntities(Collection<Entity> entities) {
        synchronized (this.entities) {
            this.entities.removeIf(e -> entities.stream()
                    .anyMatch(ent -> ent.uuid.equals(e.uuid)));
            this.entities.addAll(entities);
        }
    }

    public void AddManagedEntity(Entity entity) {
        entity.managed = true;
        Add(entity);
        if(onManagedEntityAdd != null)
            onManagedEntityAdd.accept(entity);
    }

    public void AddUniqueEntity(Entity entity) {
        synchronized (this.entities) {
            List<Entity> entities = new ArrayList<>(this.entities);
            for (Entity e : entities) {
                if (e.uuid.equals(entity.uuid)) {
                    if (!e.managed) {
                        this.entities.remove(e);
                    } else System.out.println("Attempt made to replace managed entity");
                }
            }
                this.entities.add(entity);
        }
    }

    public void PostRunnable(Runnable task) {
        tasks.add(task);
    }

    public void Update(float delta) {
        for (Entity entity : getEntities())
            entity.Update(delta);

        if (tasks.isEmpty())
            return;

        for (Runnable task : new ArrayList<>(tasks)) {
            task.run();
        }
        tasks.clear();
    }

    public synchronized List<Entity> FlattenedEntities() {
        final ArrayList<Entity> entities = new ArrayList<>();
        getEntities().forEach(e -> FlattenedEntities(e, entities));
        return entities;
    }

    public void FlattenedEntities(Entity entity, List<Entity> entities) {
        entities.add(entity);
        entity.GetChildren()
                .forEach(c -> FlattenedEntities(c, entities));
    }

    public synchronized List<Entity> GetRootEntitiesOfComponent(Class<? extends EntityComponent> componentType) {
        return entities.stream()
                .filter(this::Alive)
                .filter(e -> componentType.isInstance(e.rootComponent))
                .collect(Collectors.toList());
    }

    public synchronized List<Entity> GetRootEntitiesWithComponents(Class<? extends EntityComponent>... componentType) {
        return entities.stream()
                .filter(this::Alive)
                .filter(e -> e.All(componentType))
                .collect(Collectors.toList());
    }

    public synchronized List<Entity> GetFlattenedEntitiesOfComponent(Class<? extends EntityComponent> componentType) {
        return FlattenedEntities().stream()
                .filter(this::Alive)
                .filter(e -> componentType.isInstance(e.rootComponent))
                .collect(Collectors.toList());
    }

    public synchronized List<Entity> GetFlattenedEntitiesWithComponents(Class<? extends EntityComponent>... componentType) {
        return FlattenedEntities().stream()
                .filter(this::Alive)
                .filter(e -> e.All(componentType))
                .collect(Collectors.toList());
    }

//    public synchronized void _GetFlattenedEntitiesOfComponent(Entity entity, List<Entity> entities, Class<? extends EntityComponent> componentType) {
//        entities.stream().filter(this::Alive).filter(e -> componentType.isInstance(e.rootComponent)).forEach(entities::add);
//        entity.childEntities.forEach(e -> _GetFlattenedEntitiesOfComponent(e, entities, componentType));
//    }
//
//    public synchronized void _GetFlattenedEntitiesWithComponent(Entity entity, List<Entity> entities, Class<? extends EntityComponent>... componentType) {
//        entities.stream().filter(this::Alive).filter(e -> e.All(componentType)).forEach(entities::add);
//        entity.childEntities.forEach(e -> _GetFlattenedEntitiesWithComponent(e, entities, componentType));
//    }

    public synchronized void Add(Entity entity) {
        entity.SetWorld(this);
        this.entities.add(entity);
    }

    public boolean Alive(Entity entity) {
        return ScriptUtils.instance()
                .IsEntityAlive(entity);
    }

    public List<Entity> getEntities() {
        return new ArrayList<>(entities);
    }

    public synchronized void Remove(Entity entity) {
        this.entities.remove(entity);
        entity.SetWorld(null);
    }
}
