package net.ncguy.threading;

import sun.reflect.Reflection;

public class ThreadUtils {

    public static StackTraceElement GetFirstElementNotOfType(Class... typesToAvoid) {
        return GetFirstElementNotOfType(new Exception().getStackTrace(), typesToAvoid);
    }
    public static StackTraceElement GetFirstElementNotOfType(Thread thread, Class... typesToAvoid) {
        return GetFirstElementNotOfType(thread.getStackTrace(), typesToAvoid);
    }

    public static StackTraceElement GetFirstElementNotOfType(StackTraceElement[] stack, Class... typesToAvoid) {

        if(stack.length == 0)
            return null;

        Class[] blacklist = new Class[typesToAvoid.length + 1];
        blacklist[0] = Thread.class;
        System.arraycopy(typesToAvoid, 0, blacklist, 1, typesToAvoid.length);

        for (int i = 0; i < stack.length; i++) {
            StackTraceElement e = stack[i];
            boolean valid = true;
            for (Class cls : blacklist) {
                String eCls = e.getClassName();
                if(eCls.equalsIgnoreCase(cls.getCanonicalName())) {
                    valid = false;
                    break;
                }
            }
            if(valid)
                return e;
        }
        return stack[0];
    }

    public static StackTraceElement GetFirstElementNotOfTypeReflection(Class... typesToAvoid) {
        Class[] types = new Class[typesToAvoid.length + 1];
        types[0] = ThreadUtils.class;
        System.arraycopy(typesToAvoid, 0, types, 1, typesToAvoid.length);
        return GetFirstElementNotOfTypeReflection(0, types);
    }
    public static StackTraceElement GetFirstElementNotOfTypeReflection(int depth, Class... typesToAvoid) {
        Class<?> callerCls = Reflection.getCallerClass(depth);
        for (Class cls : typesToAvoid) {
            if(callerCls.equals(cls))
                return GetFirstElementNotOfTypeReflection(depth + 1, typesToAvoid);
        }
        return new StackTraceElement(callerCls.getCanonicalName(), "Unknown", callerCls.getCanonicalName().replace(".", "/") + ".java", -1);
    }

}
