package net.ncguy.tools.debug.view.component.editors;

import javafx.scene.Node;
import javafx.scene.control.Button;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;

public class FunctionEditor implements PropertyEditor<Runnable> {

    PropertySheet.Item item;

    public FunctionEditor(PropertySheet.Item item) {
        this.item = item;

    }

    @Override
    public Node getEditor() {
        Button btn = new Button();
        btn.setText(item.getName());
        btn.setOnAction(e -> {
            Object obj = item.getValue();
            if(obj instanceof Runnable)
                ((Runnable) obj).run();
        });
        return btn;
    }

    @Override
    public Runnable getValue() {
        return null;
    }

    @Override
    public void setValue(Runnable value) {

    }
}
