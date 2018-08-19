package net.ncguy.lib.foundation.startup;

import com.google.gson.GsonBuilder;
import net.ncguy.lib.foundation.io.Json;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class Initialisation {

    public enum Target {
        General,
        Json,
    }

    public static void Init() {
        General();
        Json.WithBuilder(Initialisation::Json);
    }

    private static void General() {
        GetStartupMethodsForTarget(Target.General).forEach(Initialisation::Invoke);
    }

    private static void Json(GsonBuilder b) {
        GetStartupMethodsForTarget(Target.Json).forEach(m -> Invoke(m, b));
        // Json builder injection target
        /*
        b.registerTypeAdapter(FileHandle.class, new FileHandleTypeAdapter());
        b.registerTypeAdapter(Color.class, new ColourTypeAdapter());
         */
    }

    private static Set<Method> startupMethods;

    private static Set<Method> GetStartupMethods() {
        if (startupMethods == null) {
            startupMethods = new HashSet<>();
//            startupMethods = new Reflections().getMethodsAnnotatedWith(Startup.class);
            new Reflections().getTypesAnnotatedWith(Startup.class)
                    .stream()
                    .flatMap(cls -> Arrays.stream(cls.getDeclaredMethods()))
                    .filter(mth -> mth.isAnnotationPresent(Startup.class))
                    .forEach(mth -> startupMethods.add(mth));
        }
        return startupMethods;
    }

    private static Stream<Method> GetStartupMethodsForTarget(Target target) {
        return GetStartupMethods().stream()
                .filter(m -> {
                    Startup startup = m.getAnnotation(Startup.class);
                    Target value = startup.value();
                    return value.equals(target);
                });
    }

    private static void Invoke(Method method, Object... args) {
        try {
            method.invoke(null, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
