package net.ncguy.entity.component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FieldPropertyDescriptorLite<T> {

    public Object value;

    public Class<T> type;
    public Field field;
    public Object owner;
    public String category;
    public String name;
    public String description;
    public boolean editable;

    public static List<FieldPropertyDescriptorLite> OfClass(Object owner) {
        return OfClass(owner, owner.getClass());
    }
    public static List<FieldPropertyDescriptorLite> OfClass(Object owner, Class<?> cls) {
        List<FieldPropertyDescriptorLite> descriptors = new ArrayList<>();
        OfClass(owner, cls, descriptors);
        return descriptors;
    }

    public static void OfClass(Object owner, Collection<FieldPropertyDescriptorLite> descriptors) {
        OfClass(owner, owner.getClass(), descriptors);
    }
    public static void OfClass(Object owner, Class<?> cls, Collection<FieldPropertyDescriptorLite> descriptors) {

        Field[] declaredFields = cls.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(EntityProperty.class)) {
                EntityProperty meta = declaredField.getAnnotation(EntityProperty.class);
                //noinspection unchecked
                FieldPropertyDescriptorLite descriptor = new FieldPropertyDescriptorLite();
                descriptor.name = meta.Name();
                descriptor.description = meta.Description();
                descriptor.category = meta.Category();
                descriptor.field = declaredField;
                descriptor.owner = owner;
                descriptor.type = meta.Type();
                descriptor.editable = meta.Editable();
                descriptors.add(descriptor);
            }
        }

        if(cls.getSuperclass() != null)
            OfClass(owner, cls.getSuperclass(), descriptors);
    }

}
