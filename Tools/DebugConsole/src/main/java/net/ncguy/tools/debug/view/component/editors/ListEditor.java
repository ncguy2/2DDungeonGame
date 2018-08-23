package net.ncguy.tools.debug.view.component.editors;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import net.ncguy.post.MultiPostProcessor;
import net.ncguy.tools.debug.view.component.editors.nodes.CollectionEditor;
import net.ncguy.tools.debug.view.component.editors.nodes.LabeledNode;
import net.ncguy.tools.debug.view.component.editors.nodes.MultiPostProcessorItem;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;

import java.util.List;

public class ListEditor implements PropertyEditor<List> {

    private final PropertySheet.Item item;

    public ListEditor(PropertySheet.Item item) {
        this.item = item;
    }

    @Override
    public Node getEditor() {
        List list = getValue();
        if(list.isEmpty())
            return new Label("Empty list, cannot discern type");

        // TOOD abstract
        Object item = list.get(0);
        if(item instanceof MultiPostProcessor) {
            CollectionEditor<MultiPostProcessor> editor = new CollectionEditor<MultiPostProcessor>(getValue()) {
                @Override
                protected Node BuildNode(MultiPostProcessor item) {
                    return new MultiPostProcessorItem(item);
                }
            };

            AnchorPane p = new AnchorPane();
            p.getChildren().add(editor);
            AnchorPane.setTopAnchor(p, 0.0);
            AnchorPane.setBottomAnchor(p, 0.0);
            AnchorPane.setLeftAnchor(p, 0.0);
            AnchorPane.setRightAnchor(p, 0.0);
            LabeledNode<Node> acc = new LabeledNode<>("Stuff", p);
            acc.SetColour(Color.CYAN);
            return p;
        }

        return new Label("Unrecognised type: " + item.getClass().getCanonicalName());
    }

    @Override
    public List getValue() {
        return (List) item.getValue();
    }

    @Override
    public void setValue(List value) {

    }
}
