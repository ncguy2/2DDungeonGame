package net.ncguy.lib.foundation.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Configuration {

    private static Configuration instance;

    public static Configuration instance() {
        if (instance == null)
            instance = new Configuration();
        return instance;
    }

    public static final String DEFAULT_CONFIG_FILE = "config.props";
    public final String configFile;
    protected Map<String, String> loadedConfigEntries;
    protected Map<Class<?>, BiFunction<String, Class, ?>> typeMappers;

    private Configuration() {
        this(DEFAULT_CONFIG_FILE);
    }

    private Configuration(String configFile) {
        this.configFile = configFile;
        loadedConfigEntries = new HashMap<>();
        typeMappers = new HashMap<>();
        RegisterDefaults();
        Load();
    }

    private void RegisterDefaults() {
        RegisterDefaultTypeMappers();
    }

    private void RegisterDefaultTypeMappers() {
        RegisterTypeMapper(String.class, (s, c) -> s);
        RegisterTypeMapper(Boolean.class, (Function<String, Boolean>) Boolean::valueOf);

        RegisterTypeMapper(Byte.class, (Function<String, Byte>) Byte::valueOf);
        RegisterTypeMapper(Short.class, (Function<String, Short>) Short::valueOf);
        RegisterTypeMapper(Integer.class, (Function<String, Integer>) Integer::valueOf);
        RegisterTypeMapper(Long.class, (Function<String, Long>) Long::valueOf);

        RegisterTypeMapper(Float.class, (Function<String, Float>) Float::valueOf);
        RegisterTypeMapper(Double.class, (Function<String, Double>) Double::valueOf);

        RegisterTypeMapper(Character.class, s -> s.toCharArray()[0]);
    }

    public <T> void RegisterTypeMapper(Class<T> type, Function<String, T> mapper) {
        RegisterTypeMapper(type, (s, c) -> mapper.apply(s));
    }

    public <T> void RegisterTypeMapper(Class<T> type, BiFunction<String, Class, T> mapper) {
        typeMappers.put(type, mapper);
    }

    public Function<String, ?> GetTypeMapper(Class type) {
        Function<String, ?> mapper = null;

        Class<?> cls = type;

        while(mapper == null && cls != null) {
            mapper = _GetTypeMapper(cls);
            cls = cls.getSuperclass();
        }

        return mapper;
    }

    @SuppressWarnings("unchecked")
    public <T> Function<String, T> _GetTypeMapper(Class<T> type) {
        return (Function<String, T>) typeMappers.get(type);
    }

    public void Load() {
        try {
            LoadImpl();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void LoadImpl() throws IOException {

        File file = new File(this.configFile);
        if(!file.exists()) {
            file.createNewFile();
            return;
        }

        List<String> lines = Files.readAllLines(file.toPath());
        lines.stream()
                .map(ConfigEntrySplit::Create)
                .filter(Objects::nonNull)
                .filter(ConfigEntrySplit::IsValid)
                .forEach(e -> loadedConfigEntries.put(e.key, e.value));
    }

    public Optional<String> Get(String key) {
        return Optional.ofNullable(loadedConfigEntries.get(key));
    }

    public <T> Optional<?> Get(String key, Class<T> type) {

        if(type.isEnum()) {
            Class<Enum> enumType = (Class<Enum>) type;
            return Get(key).map(str -> Enum.valueOf(enumType, str));
        }

        Function<String, ?> mapper = GetTypeMapper(type);
        if(mapper == null)
            return Optional.empty();
        return Get(key, mapper);
    }

    public <T> Optional<T> Get(String key, Function<String, T> mapper) {
        return Get(key).map(mapper);
    }

    public <T> void Set(Object owner, String key, Class<T> type, Field field) {
        Get(key, type).ifPresent(v -> {
            field.setAccessible(true);
            try {
                field.set(owner, v);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    public void _Inject(Object obj) {
        GetFields(obj.getClass()).forEach(f -> Set(obj, f.metadata.Name(), f.metadata.Type(), f.field));
    }

    protected List<ConfigField> GetFields(Class cls) {
        List<ConfigField> fields = new ArrayList<>();
        GetFields(cls, fields);
        return fields;
    }
    protected void GetFields(Class cls, List<ConfigField> fields) {
        Field[] declaredFields = cls.getDeclaredFields();

        for (Field field : declaredFields) {
            if(field.isAnnotationPresent(Config.class)) {
                Config cfg = field.getAnnotation(Config.class);
                fields.add(new ConfigField(field, cfg));
            }
        }

        Class superclass = cls.getSuperclass();
        if(superclass != null)
            GetFields(superclass, fields);
    }

    // Static
    public static void Inject(Object object) {
        instance()._Inject(object);
    }

    public static class ConfigField {
        public final Field field;
        public final Config metadata;

        public ConfigField(Field field, Config metadata) {
            this.field = field;
            this.metadata = metadata;
        }
    }

    public static class ConfigEntrySplit {
        public final String fullLine;
        public final String key;
        public final String value;

        public boolean IsValid() {
            return IsValid(key) && IsValid(value);
        }
        public boolean IsValid(String str) {
            return str != null && !str.isEmpty();
        }

        public ConfigEntrySplit(String fullLine) throws InvalidPropertiesFormatException {
            this.fullLine = fullLine;
            String[] split = fullLine.split("=");
            if (split.length != 2)
                throw new InvalidPropertiesFormatException("Invalid property format: " + fullLine);
            this.key = split[0].trim();
            this.value = split[1].trim();
        }

        public static ConfigEntrySplit Create(String fullLine) {
            try {
                return new ConfigEntrySplit(fullLine);
            } catch (InvalidPropertiesFormatException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
