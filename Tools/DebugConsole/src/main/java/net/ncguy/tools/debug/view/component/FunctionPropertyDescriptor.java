package net.ncguy.tools.debug.view.component;

import net.ncguy.entity.component.FieldPropertyDescriptorLite;
import net.ncguy.tools.debug.view.component.editors.FunctionEditor;
import org.controlsfx.property.editor.PropertyEditor;

import java.util.Optional;

public class FunctionPropertyDescriptor extends FieldPropertyDescriptor<Runnable> {

    private final Runnable task;

    public FunctionPropertyDescriptor(Class<Runnable> type, Runnable task, String category, String name, String description) {
        super(type, null, null, category, name, description);
        this.task = task;
    }

    public FunctionPropertyDescriptor(FieldPropertyDescriptorLite<Runnable> lite) {
        super(lite);
        if(lite.value instanceof Runnable)
            this.task = (Runnable) lite.value;
        else this.task = null;
    }

    @Override
    public Object getValue() { return task; }
    @Override
    public void setValue(Object value) {}

    @Override
    public Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass() {
        return Optional.of(FunctionEditor.class);
    }
}
