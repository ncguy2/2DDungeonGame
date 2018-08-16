package net.ncguy.tools.debug.view.component.editors;

import javafx.scene.Node;
import net.ncguy.tools.debug.view.component.editors.nodes.ProgressViewerNode;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;

import java.util.function.Supplier;

public class ProgressEditor implements PropertyEditor<Supplier<Float>> {

    PropertySheet.Item item;

    public ProgressEditor(PropertySheet.Item item) {
        this.item = item;
    }

    @Override
    public Node getEditor() {
        return new ProgressViewerNode(getValue());
    }

    @Override
    public Supplier<Float> getValue() {
        return (Supplier<Float>) item.getValue();
    }

    @Override
    public void setValue(Supplier<Float> value) {
        // NOOP
    }
}
