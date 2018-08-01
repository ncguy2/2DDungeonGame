package net.ncguy.tools.debug.view.component.editors.nodes;

import com.badlogic.gdx.math.Vector2;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class VectorNode extends AnchorPane {

    public final Property<Vector2> prop;
    SimpleBooleanProperty editable;
    LabeledNode<NumericField> xNode;
    LabeledNode<NumericField> yNode;
    Property<Number> xProp;
    Property<Number> yProp;
    Timer timer;
    GridPane container;
    private Consumer<Vector2> onChange;

    public VectorNode(Vector2 vec) {
        this(new SimpleObjectProperty<>(vec));
    }

    public VectorNode(Property<Vector2> prop) {
        super();
        editable = new SimpleBooleanProperty(true);
        this.prop = prop;
        Build();
    }

    public void SetEditable(boolean editable) {
        this.editable.set(editable);
    }

    public VectorNode AddListener(Consumer<Vector2> onChange) {
        this.onChange = onChange;
        return this;
    }

    void Accept(Vector2 tgt) {
        if(onChange != null)
            onChange.accept(tgt);
    }

    void Build() {

        container = new GridPane();

        ObservableList<ColumnConstraints> columnConstraints = container.getColumnConstraints();
        columnConstraints.add(new ColumnConstraints());
        columnConstraints.add(new ColumnConstraints());
        container.getRowConstraints()
                .add(new RowConstraints());

        columnConstraints.forEach(c -> c.setPercentWidth(50));

        xNode = new LabeledNode<>("X", new NumericField(Float.class));
        yNode = new LabeledNode<>("Y", new NumericField(Float.class));

        xNode.node.editableProperty().bind(editable);
        yNode.node.editableProperty().bind(editable);

        xNode.SetColour(Color.RED);
        yNode.SetColour(Color.GREEN);

        xProp = new SimpleObjectProperty<>();
        yProp = new SimpleObjectProperty<>();

        xProp.addListener((observable, oldValue, newValue) -> {
            prop.getValue().x = newValue.floatValue();
            Accept(prop.getValue());
        });
        yProp.addListener((observable, oldValue, newValue) -> {
            prop.getValue().y = newValue.floatValue();
            Accept(prop.getValue());
        });

        xNode.node.valueProperty()
                .bindBidirectional(xProp);
        yNode.node.valueProperty()
                .bindBidirectional(yProp);

        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                VectorNode.this.UpdateProperties();
            }
        }, 0, 16);

        container.add(xNode, 0, 0);
        container.add(yNode, 1, 0);

        getChildren().add(container);

        AnchorPane.setRightAnchor(container, 0.0);
        AnchorPane.setLeftAnchor(container, 0.0);
        AnchorPane.setBottomAnchor(container, 0.0);
        AnchorPane.setTopAnchor(container, 0.0);

        UpdateProperties();
    }

    void UpdateProperties() {
        Vector2 val = prop.getValue();
        if (val == null)
            val = Vector2.Zero;

        if(!editable.get() || !xNode.node.isFocused())
            xProp.setValue(val.x);
        if(!editable.get() || !yNode.node.isFocused())
            yProp.setValue(val.y);

    }


}
