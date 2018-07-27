package net.ncguy.tools.debug.view.component;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import net.ncguy.entity.component.FieldPropertyDescriptorLite;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;

import java.lang.reflect.Field;
import java.util.Optional;

public class FieldPropertyDescriptor<T> implements PropertySheet.Item {

    private final Class<T> type;
    public final Field field;
    public final Object owner;
    public final String category;
    public final String name;
    public final String description;

    public boolean editable = true;

    ObservableValue<T> value;

    public FieldPropertyDescriptor(Class<T> type, Field field, Object owner, String category, String name, String description) {
        this.type = type;
        this.field = field;
        this.owner = owner;
        this.category = category;
        this.name = name;
        this.description = description;

        value = new SimpleObjectProperty<>((T) getValue());
    }

    public FieldPropertyDescriptor(FieldPropertyDescriptorLite<T> lite) {
        this.type = lite.type;
        this.field = lite.field;
        this.owner = lite.owner;
        this.category = lite.category;
        this.name = lite.name;
        this.description = lite.description;
        this.editable = lite.editable;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Object getValue() {
        try {
            field.setAccessible(true);
            return field.get(owner);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setValue(Object value) {
        try {
            field.setAccessible(true);
            field.set(owner, value);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unhandled exception");
            e.printStackTrace();
        }
    }

    @Override
    public Optional<ObservableValue<? extends Object>> getObservableValue() {
        return Optional.ofNullable(value);
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass() {
        Optional<Class<? extends PropertyEditor<?>>> aClass = FieldPropertyFactory.GetEditorClass(getType());
        return aClass;
    }
}
