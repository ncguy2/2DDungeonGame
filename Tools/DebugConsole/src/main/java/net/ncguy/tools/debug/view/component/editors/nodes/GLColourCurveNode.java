package net.ncguy.tools.debug.view.component.editors.nodes;

import com.badlogic.gdx.graphics.Color;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import net.ncguy.lib.foundation.utils.Curve;
import net.ncguy.util.curve.GLColourCurve;

public class GLColourCurveNode extends CurveNode<Color, GLColourCurve> {

    XYChart.Series<Float, Float> redSeries;
    XYChart.Series<Float, Float> greenSeries;
    XYChart.Series<Float, Float> blueSeries;
    XYChart.Series<Float, Float> alphaSeries;

    public GLColourCurveNode(GLColourCurve curve) {
        super(curve);
    }

    public void RebuildSeries() {
        redSeries.getData().clear();
        greenSeries.getData().clear();
        blueSeries.getData().clear();
        alphaSeries.getData().clear();

        for (Curve.Item<Color> item : curve.items) {
            XYChart.Data<Float, Float> r = new XYChart.Data<>();
            r.setXValue(item.value);
            r.setYValue(item.item.r);
            redSeries.getData().add(r);

            XYChart.Data<Float, Float> g = new XYChart.Data<>();
            g.setXValue(item.value);
            g.setYValue(item.item.g);
            greenSeries.getData().add(g);

            XYChart.Data<Float, Float> b = new XYChart.Data<>();
            b.setXValue(item.value);
            b.setYValue(item.item.b);
            blueSeries.getData().add(b);

            XYChart.Data<Float, Float> a = new XYChart.Data<>();
            a.setXValue(item.value);
            a.setYValue(item.item.a);
            alphaSeries.getData().add(a);
        }
    }

    @Override
    public void BuildSeries(LineChart chart) {
        redSeries = new XYChart.Series<>();
        greenSeries = new XYChart.Series<>();
        blueSeries = new XYChart.Series<>();
        alphaSeries = new XYChart.Series<>();

        redSeries.setName("Red");
        greenSeries.setName("Green");
        blueSeries.setName("Blue");
        alphaSeries.setName("Alpha");

        RebuildSeries();

        chart.getData().addAll(redSeries, greenSeries, blueSeries, alphaSeries);

    }

    @Override
    public Node BuildContent() {
        GridPane pane = new GridPane();

        pane.getColumnConstraints().add(new ColumnConstraints());

        pane.getRowConstraints().add(new RowConstraints());
        pane.getRowConstraints().add(new RowConstraints());

        pane.getColumnConstraints().get(0).setHgrow(Priority.ALWAYS);
        pane.getRowConstraints().get(0).setVgrow(Priority.ALWAYS);
        pane.getRowConstraints().get(1).setPrefHeight(32);

        ScrollPane scroller = new ScrollPane();
        pane.add(scroller, 0, 0);

        VBox box = new VBox();
        curve.items.stream().map(this::BuildContentRow).forEach(box.getChildren()::add);
        scroller.setContent(box);

        scroller.setFitToHeight(true);
        scroller.setFitToWidth(true);


        ButtonBar buttonBar = new ButtonBar();

        Button newItemBtn = new Button("Add");
        newItemBtn.setOnAction(event -> {
            curve.Add(Color.WHITE.cpy(), curve.max + 1);
            BuildPaneContent();
        });

        buttonBar.getButtons().add(newItemBtn);

        pane.add(buttonBar, 0, 1);

        return pane;
    }

    @Override
    public String GetStylesheet() {
        return "css/colourCurve.css";
    }

    public Node BuildContentRow(Curve.Item<Color> item) {
        HBox box = new HBox();

        LabeledNode<NumericField> key = new LabeledNode<>("Key", new NumericField(Float.class));
        key.node.valueProperty().setValue(item.value);
        key.node.valueProperty().addListener((observable, oldValue, newValue) -> {
            item.value = newValue.floatValue();
            curve.Sort();
            RebuildSeries();
        });

        LabeledNode<ColorPicker> val = new LabeledNode<>("Value", new ColorPicker());
        val.node.setValue(new javafx.scene.paint.Color(item.item.r, item.item.g, item.item.b, item.item.a));
        val.node.valueProperty().addListener((observable, oldValue, newValue) -> {
            item.item.r = (float) newValue.getRed();
            item.item.g = (float) newValue.getGreen();
            item.item.b = (float) newValue.getBlue();
            item.item.a = (float) newValue.getOpacity();
            RebuildSeries();
        });

        Button removeBtn = new Button("X");
        removeBtn.setOnAction(event -> {
            curve.items.remove(item);
            BuildPaneContent();
        });
        box.getChildren().addAll(key, val, removeBtn);

        return box;
    }
}
