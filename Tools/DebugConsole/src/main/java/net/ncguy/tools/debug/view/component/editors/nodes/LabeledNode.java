package net.ncguy.tools.debug.view.component.editors.nodes;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class LabeledNode<T extends Node> extends AnchorPane {

    Property<String> labelProp;
    Property<Color> labelBGColour;
    Property<Color> labelBGStrokeColour;

    T node;
    Label label;
    Rectangle bg;
    AnchorPane bgContainer;
    GridPane container;


    public LabeledNode(String labelText, T node) {
        this(new SimpleStringProperty(labelText), node);
    }

    public LabeledNode(Property<String> labelProp, T node) {
        super();
        labelBGStrokeColour = new SimpleObjectProperty<>();
        labelBGColour = new SimpleObjectProperty<>();
        labelBGColour.addListener((observable, oldValue, newValue) -> labelBGStrokeColour.setValue(newValue.darker()));
        SetColour(Color.WHITE);
        this.labelProp = labelProp;
        this.node = node;
        Build();
    }

    void SetColour(Color colour) {
        labelBGColour.setValue(colour);

        if(container != null) {
            String c = labelBGColour.getValue().toString();
            c = c.substring(0, c.length() - 2).replace("0x", "#");
            StringBuilder sb = new StringBuilder();
            sb.append("-fx-background-color: ")
                    .append(c)
                    .append(";");
            sb.append("-fx-background-radius: 5px;");
            sb.append("-fx-background-insets: 1px;");
            sb.append("-fx-border-width: 1px;");
            sb.append("-fx-border-color: #00000066;");
            sb.append("-fx-border-radius: 5px;");
            container.setStyle(sb.toString());
            container.applyCss();
        }
    }

    void Build() {

        container = new GridPane();
        getChildren().add(container);
        AnchorPane.setRightAnchor(container, 0.0);
        AnchorPane.setLeftAnchor(container, 0.0);
        AnchorPane.setBottomAnchor(container, 0.0);
        AnchorPane.setTopAnchor(container, 0.0);


        this.label = new Label();
        this.label.textProperty().bind(labelProp);
//
//        container.minHeightProperty().bind(heightProperty());
//        container.prefHeightProperty().bind(heightProperty());
//        container.maxHeightProperty().bind(heightProperty());
//
//        container.minWidthProperty().bind(widthProperty());
//        container.prefWidthProperty().bind(widthProperty());
//        container.maxWidthProperty().bind(widthProperty());

        container.getRowConstraints().add(new RowConstraints());
        container.getColumnConstraints().add(new ColumnConstraints());
        container.getColumnConstraints().add(new ColumnConstraints());
        container.getColumnConstraints().add(new ColumnConstraints());
        container.getColumnConstraints().add(new ColumnConstraints());

        container.getRowConstraints().get(0).setMaxHeight(32.0);
        container.getRowConstraints().get(0).setVgrow(Priority.NEVER);


        applyCss();
        Text t = new Text(label.getText());
        t.applyCss();
        double padding = 4;
        double width = t.getLayoutBounds().getWidth();
        container.getColumnConstraints().get(0).setPrefWidth(padding);
        container.getColumnConstraints().get(1).setPrefWidth(width);
        container.getColumnConstraints().get(2).setPrefWidth(padding);
        container.getColumnConstraints().get(3).setHgrow(Priority.ALWAYS);

//        bgContainer = new AnchorPane();
//        bgContainer.setMaxHeight(32.0);
//        bg = new Rectangle();
//        bg.widthProperty().bind(widthProperty());
//        bg.heightProperty().bind(heightProperty());
//        bgContainer.getChildren().add(bg);
//        double bgPadding = 1;
//        bg.widthProperty().bind(bgContainer.widthProperty().subtract(bgPadding * 2));
//        bg.heightProperty().bind(bgContainer.heightProperty().subtract(bgPadding * 2));
//        container.add(bgContainer, 0, 0, 4, 1);
//        AnchorPane.setTopAnchor(bg, bgPadding);
//        AnchorPane.setBottomAnchor(bg, bgPadding);
//        AnchorPane.setLeftAnchor(bg, bgPadding);
//        AnchorPane.setRightAnchor(bg, bgPadding);
//        container.add(bg, 0, 0, 5, 1);

//        bg.setArcHeight(5);
//        bg.setArcWidth(5);
//        bg.fillProperty().bind(labelBGColour);
//        bg.strokeProperty().bind(labelBGStrokeColour);

        container.add(label, 1, 0);
        container.add(node, 3, 0);
    }

    public void SetText(String text) {
        labelProp.setValue(text);
    }

}
