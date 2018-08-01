package net.ncguy.tools.debug.view.component.editors;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import net.ncguy.entity.Transform2D;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class TransformEditor implements PropertyEditor<Transform2D> {

    private final PropertySheet.Item item;

    public TransformEditor(PropertySheet.Item item) {
        this.item = item;
    }

    @Override
    public Node getEditor() {
        Transform2D value = getValue();
        TextField textField = new TextField(Objects.toString(value));
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                textField.setText(Objects.toString(getValue()));
            }
        }, 0, 50);
        textField.setEditable(false);
        return textField;
    }

    @Override
    public Transform2D getValue() {
        Object v = item.getValue();
        if (v instanceof Transform2D)
            return (Transform2D) v;
        return null;
    }

    @Override
    public void setValue(Transform2D value) {
    }
}
