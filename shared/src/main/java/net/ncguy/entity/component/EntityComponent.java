package net.ncguy.entity.component;

import net.ncguy.entity.Entity;
import net.ncguy.io.Json;
import net.ncguy.io.RuntimeTypeAdapterFactory;

import java.util.HashSet;
import java.util.List;

/**
 * Base entity component, has no runtime functionality but defines instantiation behaviours such as serialization registry and such
 */
public class EntityComponent {

    public String name;
    public SceneComponent owningComponent;

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

    public EntityComponent(String name) {
        _RegisterClass();
        this.name = name;
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

    public boolean CanReplicate() {
        return true;
    }

    public boolean Has(Class<? extends EntityComponent> type) {
        return false;
    }

    public <T extends EntityComponent> T GetComponent(Class<T> type, boolean searchDescendants) {
        return null;
    }

    public <T extends EntityComponent> void GetComponents(Class<T> type, boolean searchDescendants, List<T> componentList) {
    }


    public void Update(float delta) {
    }

}
