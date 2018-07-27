package net.ncguy.tools.debug.view.component;

import com.badlogic.gdx.graphics.Color;
import net.ncguy.entity.component.EntityComponent;
import net.ncguy.entity.component.EntityFunction;
import net.ncguy.entity.component.EntityProperty;
import net.ncguy.entity.component.IPropertyProvider;
import net.ncguy.tools.debug.view.component.editors.ColourEditor;
import net.ncguy.tools.debug.view.component.editors.FunctionEditor;
import org.controlsfx.property.editor.PropertyEditor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class FieldPropertyFactory {

    public static List<FieldPropertyDescriptor> GetDescriptors(EntityComponent component) {
        List<FieldPropertyDescriptor> propertyDescriptors = new ArrayList<>();
        GetDescriptors(component, propertyDescriptors);

        if (component instanceof IPropertyProvider)
            ((IPropertyProvider) component).Provide()
                    .stream()
                    .map(c -> new FieldPropertyDescriptor(c))
                    .forEach(propertyDescriptors::add);

        return propertyDescriptors;
    }

    public static void GetDescriptors(EntityComponent component, Collection<FieldPropertyDescriptor> propertyDescriptors) {
        GetDescriptors(component, component.getClass(), propertyDescriptors);
    }

    public static void GetDescriptors(EntityComponent component, Class<?> cls, Collection<FieldPropertyDescriptor> propertyDescriptors) {

        Field[] declaredFields = cls.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(EntityProperty.class)) {
                EntityProperty meta = declaredField.getAnnotation(EntityProperty.class);
                //noinspection unchecked
                FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(meta.Type(), declaredField, component, meta.Category(), meta.Name(), meta.Description());
                propertyDescriptors.add(descriptor);
            }
        }

        Method[] declaredMethods = cls.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (method.isAnnotationPresent(EntityFunction.class)) {
                EntityFunction meta = method.getAnnotation(EntityFunction.class);
                //noinspection unchecked
                FieldPropertyDescriptor descriptor = new FunctionPropertyDescriptor(Runnable.class, () -> {
                    try {
                        method.invoke(component);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }, meta.Category(), meta.Name(), meta.Description());
                propertyDescriptors.add(descriptor);
            }
        }

        if (!cls.equals(EntityComponent.class))
            GetDescriptors(component, cls.getSuperclass(), propertyDescriptors);
    }


    static Map<Class, Class<? extends PropertyEditor<?>>> editorClasses = new HashMap<>();
    static {
        RegisterDefaults();
    }
    public static void RegisterDefaults() {
        Register(Color.class, ColourEditor.class);
        Register(Runnable.class, FunctionEditor.class);
    }
    public static void Register(Class type, Class<? extends PropertyEditor<?>> editorClass) {
        editorClasses.put(type, editorClass);
    }
    public static Optional<Class<? extends PropertyEditor<?>>> GetEditorClass(Class type) {
        return Optional.ofNullable(editorClasses.get(type));
    }

}
