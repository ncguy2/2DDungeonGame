package net.ncguy.threading;

public class ThreadUtils {

    public static StackTraceElement GetFirstElementNotOfType(Class... typesToAvoid) {
        return GetFirstElementNotOfType(Thread.currentThread(), typesToAvoid);
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

}
