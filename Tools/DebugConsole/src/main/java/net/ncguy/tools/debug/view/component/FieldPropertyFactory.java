package net.ncguy.tools.debug.view.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import net.ncguy.entity.Transform2D;
import net.ncguy.entity.component.*;
import net.ncguy.tools.debug.view.component.editors.*;
import net.ncguy.util.curve.GLColourCurve;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class FieldPropertyFactory {

    public static List<FieldPropertyDescriptor> GetDescriptors(EntityComponent component) {
        List<FieldPropertyDescriptor> propertyDescriptors = new ArrayList<>();
        GetDescriptors(component, propertyDescriptors);

        if (component instanceof IPropertyProvider)
            ((IPropertyProvider) component).Provide()
                    .stream()
                    .map((Function<FieldPropertyDescriptorLite, FieldPropertyDescriptor>) FieldPropertyDescriptor::new)
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
        Register(GLColourCurve.class, GLColourCurveEditor.class);
        Register(Vector2.class, VectorEditor.class);
        Register(Color.class, ColourEditor.class);
        Register(Transform2D.class, TransformEditor.class);
        Register(Runnable.class, FunctionEditor.class);
        Register(Supplier.class, ProgressEditor.class);
        Register(List.class, ListEditor.class);
    }
    public static void Register(Class type, Class<? extends PropertyEditor<?>> editorClass) {
        editorClasses.put(type, editorClass);
    }
    public static Optional<Class<? extends PropertyEditor<?>>> GetEditorClass(Class type) {
        return Optional.ofNullable(editorClasses.get(type));
    }

    public static <T, U extends PropertyEditor<T>> Optional<U> Build(PropertySheet.Item item, Class<T> type) {
        try {
            return Optional.of(BuildImpl(item, type));
        } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static <T, U extends PropertyEditor<T>> U BuildImpl(PropertySheet.Item item, Class<T> type) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<? extends PropertyEditor<?>> aClass = editorClasses.get(type);
        Constructor<? extends PropertyEditor<?>> ctor = aClass.getConstructor(item.getClass());
        return (U) ctor.newInstance(item);
    }

}
