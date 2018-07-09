package net.ncguy.entity.component;

import net.ncguy.io.Json;
import net.ncguy.io.RuntimeTypeAdapterFactory;

import java.util.HashSet;

/**
 * Base entity component, has no runtime functionality but defines instantiation behaviours such as serialization registry and such
 */
public class EntityComponent {

    public String name;

    // Serialization stuffs

    protected static final RuntimeTypeAdapterFactory<EntityComponent> adapter = RuntimeTypeAdapterFactory.of(EntityComponent.class);
    protected static final HashSet<Class<? extends EntityComponent>> registeredClasses = new HashSet<>();

    static {
        Json.Register(adapter);
    }

    private synchronized void _RegisterClass() {
        Class<? extends EntityComponent> cls = getClass();
        if(registeredClasses.contains(cls))
            return;

        registeredClasses.add(cls);
        adapter.registerSubtype(cls);
    }

    public EntityComponent(String name) {
        _RegisterClass();
        this.name = name;
    }

    public String GetType() {
        return getClass().getSimpleName();
    }

    public boolean CanReplicate() {
        return true;
    }

}
