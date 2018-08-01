package net.ncguy.tools.debug.view.component.editors.nodes;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import net.ncguy.entity.Transform2D;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class TransformNode extends AnchorPane {

    Property<Transform2D> prop;
    Property<Number> rotationProp;
    private Consumer<Transform2D> onChange;

    VectorNode traNode;
    LabeledNode<NumericField> rotNode;
    VectorNode sclNode;

    public TransformNode(Transform2D target) {
        this(new SimpleObjectProperty<>(target));
    }

    public TransformNode(Property<Transform2D> prop) {
        super();
        this.prop = prop;
        Build();
    }

    public TransformNode AddListener(Consumer<Transform2D> onChange) {
        this.onChange = onChange;
        return this;
    }

    void Accept(Transform2D tgt) {
        if(onChange != null)
            onChange.accept(tgt);
    }

    private void Build() {
        GridPane container = new GridPane();
        container.getColumnConstraints().add(new ColumnConstraints());

        container.getRowConstraints().add(new RowConstraints());
        container.getRowConstraints().add(new RowConstraints());
        container.getRowConstraints().add(new RowConstraints());

        traNode = new VectorNode(prop.getValue().translation);
        rotNode = new LabeledNode<>("Degrees", new NumericField(Float.class));
        sclNode = new VectorNode(prop.getValue().scale);

        traNode.SetEditable(false);
        rotNode.node.editableProperty().setValue(false);
        sclNode.SetEditable(false);

        rotNode.SetColour(Color.RED);

        rotationProp = new SimpleObjectProperty<>(prop.getValue().rotationDegrees);

        rotNode.node.valueProperty().bind(rotationProp);

        container.add(traNode, 0, 0);
        container.add(rotNode, 0, 1);
        container.add(sclNode, 0, 2);

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                TransformNode.this.UpdateProperties();
            }
        }, 0, 16);

        getChildren().add(container);

        AnchorPane.setRightAnchor(container, 0.0);
        AnchorPane.setLeftAnchor(container, 0.0);
        AnchorPane.setBottomAnchor(container, 0.0);
        AnchorPane.setTopAnchor(container, 0.0);
    }

    void UpdateProperties() {
        Transform2D val = prop.getValue();
        if (val == null)
            val = new Transform2D();

        traNode.prop.setValue(val.translation);
        if(!rotNode.node.isFocused())
            rotationProp.setValue(val.rotationDegrees);
        sclNode.prop.setValue(val.scale);
    }
}
