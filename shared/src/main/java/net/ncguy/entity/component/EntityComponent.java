package net.ncguy.entity.component;

import net.ncguy.entity.Entity;
import net.ncguy.entity.aspect.Aspect;
import net.ncguy.entity.aspect.AspectKey;
import net.ncguy.entity.aspect.IAspectProvider;
import net.ncguy.lib.foundation.io.Json;
import net.ncguy.lib.foundation.io.RuntimeTypeAdapterFactory;
import net.ncguy.lib.net.shared.IReplicationConfigurable;

import java.util.HashSet;
import java.util.List;

/**
 * Base entity component, has no runtime functionality but defines instantiation behaviours such as serialization registry and such
 */
public class EntityComponent implements IReplicationConfigurable, IAspectProvider {

    @EntityProperty(Type = Boolean.class, Category = "Internal", Description = "Enabled state of the component", Name = "Enabled")
    public boolean enabled = true;
    @EntityProperty(Type = String.class, Category = "Internal", Description = "Name of the component", Name = "Name")
    public String name;
    public transient SceneComponent owningComponent;

    // Serialization stuffs
    protected static final RuntimeTypeAdapterFactory<EntityComponent> adapter = RuntimeTypeAdapterFactory.of(EntityComponent.class, "Internal_ComponentType");
    protected static final HashSet<Class<? extends EntityComponent>> registeredClasses = new HashSet<>();

    static {
        Json.Register(adapter);
    }

    private synchronized void _RegisterClass() {
        Class<? extends EntityComponent> cls = getClass();
        if (registeredClasses.contains(cls))
            return;

        registeredClasses.add(cls);
        adapter.registerSubtype(cls);
    }

    public EntityComponent() {
        this("Unnamed Scene component");
    }

    public EntityComponent(String name) {
        _RegisterClass();
        if(name == null || name.isEmpty())
            name = "Unnamed " + getClass().getSimpleName();
        this.name = name.replace("/", "_");
    }

    public void RemoveFromParent() {
        if(owningComponent != null)
            owningComponent.Remove(this);
    }

    public void _OnAddToComponent(SceneComponent component) {
        owningComponent = component;
    }

    public void _OnRemoveFromComponent() {
        owningComponent = null;
    }

    public Entity GetOwningEntity() {
        if (owningComponent != null)
            return owningComponent.GetOwningEntity();
        return null;
    }

    public String GetType() {
        return getClass().getSimpleName();
    }

    public boolean Has(Class<? extends EntityComponent> type) {
        return false;
    }

    public <T extends EntityComponent> T GetComponent(Class<T> type, boolean searchDescendants) {
        return null;
    }

    public <T extends EntityComponent> void GetComponents(Class<T> type, boolean searchDescendants, List<T> componentList) {
    }

    public void _Update(float delta) {
        if(enabled)
            Update(delta);
    }

    public void Update(float delta) {
    }

    @Override
    public String toString() {
        return name;
    }

    public void Destroy() {
        RemoveFromParent();
    }

    @Override
    public boolean CanReplicate() {
        return true;
    }
    @Override
    public void PostReplicate() {
    }

    public SceneComponent GetOwningComponent() {
        return owningComponent;
    }

    public EntityComponent GetFromPath(String path) {
        if(path.equalsIgnoreCase(name))
            return this;
        return null;
    }

    @Override
    public <T> Aspect<T> ProvideAspect(AspectKey<T> key) {
        return null;
    }
}
