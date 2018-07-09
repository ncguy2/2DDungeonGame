package net.ncguy.reflection;

public class TypeUtils {

    public static <T> T Coerce(String str, Class<T> target, T _default) {
        if(MethodUtils.HasMethod(target, "valueOf", String.class).exists) {
            T v = MethodUtils.Invoke(target, "valueOf", target, str);
            if(v != null)
                return v;
        }
        return _default;
    }

}
