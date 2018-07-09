package net.ncguy.cmd;

import net.ncguy.reflection.TypeUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodCommand extends Command {

    Object owner;
    Method method;

    public MethodCommand(Method method) {
        this.owner = this;
        this.method = method;
    }

    public MethodCommand(Object owner, Method method) {
        this.owner = owner;
        this.method = method;
    }

    @Override
    public void Invoke(String... args) {
        Class<?>[] types = method.getParameterTypes();
        Object[] coerced = new Object[types.length];

        for (int i = 0; i < coerced.length; i++)
            coerced[i] = TypeUtils.Coerce(args[i], types[i], null);

        try {
            method.invoke(owner, coerced);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
