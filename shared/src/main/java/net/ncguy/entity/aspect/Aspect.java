package net.ncguy.entity.aspect;

import java.util.Optional;

public class Aspect<T> {

    private Object owner;
    private AspectKey<T> key;
    private T object;

    public Object getOwner() {
        return owner;
    }

    public AspectKey<T> getKey() {
        return key;
    }

    public T getObject() {
        return object;
    }

    public static <T> Aspect<T> of(Object owner, AspectKey<T> key, T object) {
        Aspect<T> t = new Aspect<>();
        t.owner = owner;
        t.key = key;
        t.object = object;
        return t;
    }

    public static <T> Optional<Aspect<T>> of(Object owner, AspectKey<T> key) {
        if(owner instanceof IAspectProvider) {
            return ((IAspectProvider) owner).GetAspect(key);
        }
        return Optional.empty();
    }

}
