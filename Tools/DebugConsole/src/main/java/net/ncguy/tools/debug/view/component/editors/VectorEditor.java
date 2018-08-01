package net.ncguy.tools.debug.view.component.editors;

import com.badlogic.gdx.math.Vector2;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import net.ncguy.tools.debug.view.component.editors.nodes.VectorNode;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;

public class VectorEditor implements PropertyEditor<Vector2> {

    private final PropertySheet.Item item;

    public VectorEditor(PropertySheet.Item item) {
        this.item = item;
    }

    @Override
    public Node getEditor() {
        Vector2 value = getValue();
        if(value != null)
            return new VectorNode(value).AddListener(this::setValue);
        TextField textField = new TextField("Null value");
        textField.setEditable(false);
        return textField;
    }

    @Override
    public Vector2 getValue() {
        Object v = item.getValue();
        if(v instanceof Vector2)
            return (Vector2) v;
        return null;
    }

    @Override
    public void setValue(Vector2 value) {
        Object v = item.getValue();
        if(v instanceof Vector2)
            ((Vector2) v).set(value);
        else item.setValue(value);
    }
}
