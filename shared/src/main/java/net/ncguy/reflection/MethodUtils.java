package net.ncguy.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodUtils {

    public static ExistanceType HasMethod(Class cls, String methodName, Class... paramTypes) {
        if(MethodExists(cls, methodName, paramTypes))
            return ExistanceType.Declares;

        Class target = cls.getSuperclass();
        while(target != null) {
            if(MethodExists(target, methodName, paramTypes))
                return ExistanceType.Inherits;
            target = cls.getSuperclass();
        }

        return ExistanceType.None;
    }

    static boolean MethodExists(Class cls, String methodName, Class... paramTypes) {
        try {
            return cls.getDeclaredMethod(methodName, paramTypes) != null;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return false;
    }

    static Method GetMethod(Class cls, String methodName, Class... paramTypes) {

        do {

            try {
                Method mth = cls.getDeclaredMethod(methodName, paramTypes);
                if(mth != null)
                    return mth;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            cls = cls.getSuperclass();
        }while(cls.getSuperclass() != null);

        return null;
    }

    public static <T> T Invoke(Object owner, String methodName, Class<T> returnType, Object... args) {
        return Invoke(owner, owner.getClass(), methodName, returnType, args);
    }
    public static <T> T Invoke(Class<?> cls, String methodName, Class<T> returnType, Object... args) {
        return Invoke(null, cls, methodName, returnType, args);
    }
    public static <T> T Invoke(Object owner, Class<?> cls, String methodName, Class<T> returnType, Object... args) {
        Class[] paramTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++)
            paramTypes[i] = args[i].getClass();
        Method method = GetMethod(cls, methodName, paramTypes);
        if(method != null) {
            try {
                return returnType.cast(method.invoke(owner, args));
            } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static enum ExistanceType {
        Declares(true),
        Inherits(true),
        None(false),
        ;
        public final boolean exists;

        ExistanceType(boolean exists) {
            this.exists = exists;
        }
    }

}
