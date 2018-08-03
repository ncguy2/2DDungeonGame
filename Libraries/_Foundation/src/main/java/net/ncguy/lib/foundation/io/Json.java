package net.ncguy.lib.foundation.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;

public class Json {

    private static Json instance;
    public static Json instance() {
        if (instance == null)
            instance = new Json();
        return instance;
    }

    Gson gson;
    GsonBuilder builder;

    private Json() {
        builder = new GsonBuilder();

        builder.setPrettyPrinting();
        builder.serializeNulls();
        builder.registerTypeAdapter(Class.class, new ClassTypeAdapter());

        _InvalidateGson();
//        RuntimeTypeAdapterFactory<EntityComponent> entityAdapter = RuntimeTypeAdapterFactory.of(EntityComponent.class)
//        builder.registerTypeAdapter()
    }

    public static void WithBuilder(Consumer<GsonBuilder> task) {
        task.accept(instance().builder);
        instance()._InvalidateGson();
    }

    protected void _Register(TypeAdapterFactory factory) {
        builder.registerTypeAdapterFactory(factory);
    }

    protected void _InvalidateGson() {
        this.gson = builder.create();
    }

    public String ToJson(Object obj) {
        return gson.toJson(obj);
    }

    public <T> T FromJson(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

    public static void Register(TypeAdapterFactory... factories) {
        for (TypeAdapterFactory factory : factories)
            instance()._Register(factory);
        instance()._InvalidateGson();
    }

    public static String To(Object obj) {
        return instance().ToJson(obj);
    }

    public static <T> T From(String json, Class<T> type) {
        return instance().FromJson(json, type);
    }

    public static <T> T From(File file, Class<T> type) {
        try {
            String json = String.join("\n", Files.readAllLines(file.toPath()));
            return From(json, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
