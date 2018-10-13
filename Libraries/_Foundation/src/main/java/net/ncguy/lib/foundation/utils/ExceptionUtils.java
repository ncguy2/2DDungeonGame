package net.ncguy.lib.foundation.utils;

import java.util.function.Consumer;

public class ExceptionUtils {

    public static void ContainException(Runnable task) {
        ContainException(task, null);
    }

    public static void ContainException(Runnable task, Consumer<Exception> exc) {
        try{
            task.run();
        }catch (Exception e) {
            if(exc != null)
                exc.accept(e);
        }
    }

}
