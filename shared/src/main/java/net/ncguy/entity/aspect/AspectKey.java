package net.ncguy.entity.aspect;

public class AspectKey<T> {

    public final String name;
    public final Class<T> type;

    public AspectKey(Class<T> type) {
        this(type.getSimpleName(), type);
    }

    public AspectKey(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }
}
