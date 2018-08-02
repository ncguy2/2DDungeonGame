package net.ncguy.tools.debug.view.component.editors;

import javafx.scene.Node;
import net.ncguy.tools.debug.view.component.editors.nodes.GLColourCurveNode;
import net.ncguy.util.curve.GLColourCurve;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;

public class GLColourCurveEditor implements PropertyEditor<GLColourCurve> {

    private final PropertySheet.Item item;

    public GLColourCurveEditor(PropertySheet.Item item) {
        this.item = item;
    }

    @Override
    public Node getEditor() {
        return new GLColourCurveNode(getValue());
    }

    @Override
    public GLColourCurve getValue() {
        return (GLColourCurve) item.getValue();
    }

    @Override
    public void setValue(GLColourCurve value) {

    }
}
