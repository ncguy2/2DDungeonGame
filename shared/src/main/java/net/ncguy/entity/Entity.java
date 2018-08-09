package net.ncguy.entity;

import net.ncguy.entity.component.EntityComponent;
import net.ncguy.entity.component.SceneComponent;
import net.ncguy.lib.net.shared.IReplicationConfigurable;
import net.ncguy.world.EntityWorld;

import java.util.*;

public class Entity implements IReplicationConfigurable {

    public transient Entity parent;
    public transient boolean managed;
    public transient EntityWorld world;
    protected final Set<Entity> childEntities;
    public SceneComponent rootComponent;
    public UUID uuid;

    public Entity() {
        uuid = UUID.randomUUID();
        childEntities = new HashSet<>();
        SetRootComponent(new SceneComponent("Root"));
    }

    public EntityComponent GetFromPath(String path) {
        if(!path.startsWith(uuid.toString())) {
            System.out.println("Incorrect domain");
            return null;
        }

        String substring = path.substring(path.indexOf("://") + 3);

        EntityComponent entityComponent = rootComponent.GetFromPath(substring);
        return entityComponent;
    }

    public boolean IsManaged() {
        return managed;
    }

    public Set<Entity> GetChildren() {
        synchronized (childEntities) {
            return new HashSet<>(childEntities);
        }
    }

    public void Update(float delta) {
        if(this.rootComponent != null)
            rootComponent._Update(delta);
        for (Entity entity : GetChildren())
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

    public void SetWorld(EntityWorld world) {
        this.world = world;
        this.GetChildren().forEach(e -> e.SetWorld(world));
    }

    public <T extends Entity> T AddEntity(T entity) {
        synchronized (childEntities) {
            childEntities.add(entity);
        }
        entity.SetWorld(world);
        entity.parent = this;
        entity.Transform().parent = this.Transform();
        return entity;
    }

    public <T extends Entity> T RemoveEntity(T entity) {
        synchronized (childEntities) {
            childEntities.remove(entity);
        }
        entity.SetWorld(null);
        entity.parent = null;
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

    public EntityComponent GetComponent(String name, boolean searchDescendants) {
        return rootComponent.GetChildByName(name, searchDescendants);
    }

    public <T extends EntityComponent> T GetComponent(String name, Class<T> type, boolean searchDescendants) {
        List<T> cs = GetComponents(type, searchDescendants);
        return cs.stream().filter(c -> c.name.equalsIgnoreCase(name)).findFirst().orElse(null);
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
        SceneComponent root = GetRootComponent();
        if(type.isInstance(root))
            list.add((T) root);
        root.GetComponents(type, searchDescendants, list);
        return list;
    }

    public Transform2D Transform() {
        return rootComponent.transform;
    }

    public EntityWorld GetWorld() {
        return world;
    }

    public void Destroy() {
        world.PostRunnable(this::DestroyImmediate);
    }

    public void DestroyImmediate() {
        EntityWorld world = this.world;

        synchronized (childEntities) {
            childEntities.forEach(Entity::DestroyImmediate);
            childEntities.clear();
        }

        rootComponent.Destroy();
        rootComponent._OnRemoveFromComponent();

        if(parent != null)
            parent.RemoveEntity(this);
        else {

            if(world == null) {
                System.err.println("Orphaned entity detected, no reference to owning world");
            }else {
                world.Remove(this);
            }

        }
    }

    @Override
    public String toString() {
        return Id();
    }

    public String Id() {
        return uuid.toString();
    }

    public void AddEntities(Iterable<Entity> generatedEntities) {
        generatedEntities.forEach(this::AddEntity);
    }

    @Override
    public boolean CanReplicate() {
        return true;
    }

    @Override
    public void PostReplicate() {
        if(rootComponent != null)
            rootComponent.owningEntity = this;
    }
}
