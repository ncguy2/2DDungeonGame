package net.ncguy.entity.component;

import net.ncguy.entity.Entity;
import net.ncguy.entity.Transform2D;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Similar to the {@link EntityComponent}, the Scene component provides a 2d transform and functionality to allow for child components
 */
public class SceneComponent extends EntityComponent {

    @EntityProperty(Type = Transform2D.class, Category = "Scene", Description = "The transformation of this component", Name = "Transform")
    public Transform2D transform;
    //    @CollectionSerializer.BindCollection(elementSerializer = ConfigurableElementSerializer.class)
    public final Set<EntityComponent> childrenComponents;
    public transient Entity owningEntity;

    public SceneComponent() {
        this(null);
    }

    public SceneComponent(String name) {
        super(name);
        transform = new Transform2D();
        childrenComponents = new LinkedHashSet<>();
    }

    public <T extends EntityComponent> T Add(T component) {
        childrenComponents.add(component);
        component._OnAddToComponent(this);
        return component;
    }

    public void Remove(EntityComponent component) {
        childrenComponents.remove(component);
        component._OnRemoveFromComponent();
    }

    @Override
    public void _OnAddToComponent(SceneComponent component) {
        super._OnAddToComponent(component);
        if (component != null) {
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
        if (owningEntity != null)
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
            if (type.isInstance(component))
                return true;
        }

        for (EntityComponent component : childrenComponents) {
            if (component.Has(type))
                return true;
        }

        return false;
    }

    @Override
    public <T extends EntityComponent> T GetComponent(Class<T> type, boolean searchDescendants) {
        Set<EntityComponent> childrenComponents = GetComponents();

        for (EntityComponent component : childrenComponents) {
            if (type.isInstance(component))
                return (T) component;
        }

        if (searchDescendants) {
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
            if (type.isInstance(component))
                componentList.add((T) component);
        }

        if (searchDescendants) {
            for (EntityComponent component : childrenComponents) {
                if (component.Has(type))
                    component.GetComponents(type, searchDescendants, componentList);
            }
        }
    }

    public Set<EntityComponent> GetComponents() {
        synchronized (childrenComponents) {
            return new HashSet<>(childrenComponents);
        }
    }

    @Override
    public void Update(float delta) {
        super.Update(delta);
        for (EntityComponent childrenComponent : GetComponents())
            childrenComponent._Update(delta);
    }

    @Override
    public void Destroy() {
        GetComponents().forEach(EntityComponent::Destroy);
        synchronized (childrenComponents) {
            childrenComponents.clear();
        }
        super.Destroy();
    }

    public String GetLocalPath() {
        String path = this.name;

        if (owningComponent != null)
            path = owningComponent.GetLocalPath() + "/";

        return path;
    }

    public String GetDomain() {
        return GetOwningEntity().uuid.toString();
    }

    public String GetFullPath() {
        return GetDomain() + "://" + GetLocalPath();
    }

    public EntityComponent GetChildByName(String name) {
        return GetComponents().stream()
                .filter(c -> c.name.equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public EntityComponent GetFromPath(String path) {
        String[] split = path.split("/");


        if (split[0].equalsIgnoreCase(name)) {
            if (split.length == 1)
                return this;

            String[] split2 = new String[split.length - 1];
            System.arraycopy(split, 1, split2, 0, split2.length);
            split = split2;
        }

        EntityComponent c = GetChildByName(split[0]);
        if (c instanceof SceneComponent && split.length > 1) {
            String subPath = "";
            for (int i = 1; i < split.length; i++)
                subPath += split[i] + "/";
            subPath = subPath.substring(0, subPath.length() - 1);
            c = c.GetFromPath(subPath);
        }
        return c;
    }

    @Override
    public void PostReplicate() {
        super.PostReplicate();
        GetComponents().forEach(c -> c._OnAddToComponent(this));
    }
}
