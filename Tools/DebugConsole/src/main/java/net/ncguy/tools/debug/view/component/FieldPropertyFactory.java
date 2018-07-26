package net.ncguy.tools.debug.view.component;

import net.ncguy.entity.component.EntityComponent;
import net.ncguy.entity.component.EntityProperty;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FieldPropertyFactory {

    public static List<FieldPropertyDescriptor> GetDescriptors(EntityComponent component) {
        List<FieldPropertyDescriptor> propertyDescriptors = new ArrayList<>();
        GetDescriptors(component, propertyDescriptors);
        return propertyDescriptors;
    }

    public static void GetDescriptors(EntityComponent component, Collection<FieldPropertyDescriptor> propertyDescriptors) {
        GetDescriptors(component, component.getClass(), propertyDescriptors);
    }

    public static void GetDescriptors(EntityComponent component, Class<?> cls, Collection<FieldPropertyDescriptor> propertyDescriptors) {

        Field[] declaredFields = cls.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if(declaredField.isAnnotationPresent(EntityProperty.class)) {
                EntityProperty meta = declaredField.getAnnotation(EntityProperty.class);
                //noinspection unchecked
                FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(meta.Type(), declaredField, component, meta.Category(), meta.Name(), meta.Description());
                propertyDescriptors.add(descriptor);
            }
        }

        if(!cls.equals(EntityComponent.class))
            GetDescriptors(component, cls.getSuperclass(), propertyDescriptors);
    }

}
