package net.ncguy.tools.debug.view.component.editors.nodes;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import net.ncguy.lib.foundation.utils.Curve;

public abstract class CurveNode<T, U extends Curve<T>> extends AnchorPane {

    public final U curve;
    LineChart chart;
    SplitPane splitPane;

    public CurveNode(U curve) {
        super();
        this.curve = curve;
        Build();
    }

    private void Build() {

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        chart = new LineChart(xAxis, yAxis);
        chart.setAnimated(false);
        String s = GetStylesheet();
        if(s != null && !s.isEmpty())
            chart.getStylesheets().add(s);
        BuildSeries(chart);

        splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);

        BuildPaneContent();

        getChildren().add(splitPane);
        AnchorPane.setTopAnchor(splitPane, 0.0);
        AnchorPane.setBottomAnchor(splitPane, 0.0);
        AnchorPane.setLeftAnchor(splitPane, 0.0);
        AnchorPane.setRightAnchor(splitPane, 0.0);
    }

    public void BuildPaneContent() {
        boolean retain = !splitPane.getItems().isEmpty();
        double[] dividerPositions;
        if(retain) {
            dividerPositions = splitPane.getDividerPositions();
            splitPane.getItems()
                    .clear();
        }else dividerPositions = null;

        splitPane.getItems().add(chart);
        splitPane.getItems().add(BuildContent());

        if(retain)
            splitPane.setDividerPositions(dividerPositions);

    }

    public abstract void BuildSeries(LineChart chart);
    public abstract Node BuildContent();
    public abstract String GetStylesheet();

}
