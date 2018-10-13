package net.ncguy.tools.debug.view;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import net.ncguy.profile.*;
import net.ncguy.lib.foundation.utils.FXColourCurve;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ProfilerController implements Initializable {

    public Label CPULabel;
    public Label GPULabel;
    public CheckComboBox captureOptions;
    public CheckBox useReflectionForLocationDiscovery;
    FXColourCurve colourCurve;

    //    public LineChart utilizationChart;
    public AreaChart<Integer, Long> utilizationChart;
    public Label frameIdLabel;
    public TreeView<TaskStats> cpuTree;
    public TreeView<TaskStats> gpuTree;
    public CheckBox animationEnabled;
    public Button prevFrameBtn;
    public Button nextFrameBtn;
    public AnchorPane chartPane;
    public Label taskStartPath;
    public Label taskEndPath;

    Series<Integer, Long> cpuUtil;
    Series<Integer, Long> gpuUtil;
    Map<Integer, List<TaskStats>> cpuStats;
    Map<Integer, List<TaskStats>> gpuStats;

    Map<Integer, TreeItem<TaskStats>> statMap;
    Map<Integer, List<HoveredThresholdNode>> nodeMap;

    SimpleObjectProperty<Integer> selectedFrameId;
    private transient List<TaskStats> cpuStatDump;
    private transient List<TaskStats> gpuStatDump;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        useReflectionForLocationDiscovery.setSelected(TaskProfile.useReflectionForLocationDiscovery);
        useReflectionForLocationDiscovery.selectedProperty().addListener((observable, oldValue, newValue) -> TaskProfile.useReflectionForLocationDiscovery = newValue);

        captureOptions.getItems()
                .addAll("Profiling");

        captureOptions.getItemBooleanProperty(0)
                .setValue(ProfilerHost.PROFILER_ENABLED);

        captureOptions.getItemBooleanProperty(0)
                .addListener((observable, oldValue, newValue) -> {
                    ProfilerHost.PROFILER_ENABLED = newValue;
                });
//        captureOptions.getItemBooleanProperty(1)
//                .addListener((observable, oldValue, newValue) -> {
//                    GPUProfiler.PROFILING_ENABLED = newValue;
//                    updateGlobalProfilerState.run();
//                });

        selectedFrameId = new SimpleObjectProperty<>(-1);
        frameIdLabel.textProperty()
                .bind(selectedFrameId.asString("Frame %d"));
        selectedFrameId.addListener((observable, oldValue, newValue) -> SelectFrame(oldValue, newValue));

        utilizationChart.animatedProperty()
                .bind(animationEnabled.selectedProperty());

        colourCurve = new FXColourCurve();
        colourCurve.Add(Color.web("#377D36"), 0);
//        colourCurve.Add(Color.web("#5E8224"), 1);
//        colourCurve.Add(Color.web("#7D6728"), 3);
//        colourCurve.Add(Color.web("#7D4923"), 7.5f);
//        colourCurve.Add(Color.web("#7D2C2C"), 10f);
        colourCurve.Add(Color.web("#7D2C2C"), 16f);
//        colourCurve.Add(Color.web("#71237A"), 16);

//        if (!CPUProfiler.PROFILING_ENABLED)
//            CPULabel.setText("CPU (Profiling disabled)");
//        if (!GPUProfiler.PROFILING_ENABLED)
//            GPULabel.setText("GPU (Profiling disabled)");

        Runnable bindScrollbars = () -> {
            Set<Node> nodes = cpuTree.lookupAll(".scroll-bar");
            List<ScrollBar> cpuScrollbars = nodes.stream()
                    .filter(n -> n instanceof ScrollBar)
                    .map(n -> (ScrollBar) n)
                    .collect(Collectors.toList());

            nodes = gpuTree.lookupAll(".scroll-bar");
            List<ScrollBar> gpuScrollbars = nodes.stream()
                    .filter(n -> n instanceof ScrollBar)
                    .map(n -> (ScrollBar) n)
                    .collect(Collectors.toList());
            for (int i = 0; i < cpuScrollbars.size(); i++) {
                ScrollBar cpuScrollbar = cpuScrollbars.get(i);
                ScrollBar gpuScrollbar = gpuScrollbars.get(i);
                cpuScrollbar.valueProperty()
                        .bindBidirectional(gpuScrollbar.valueProperty());
            }

            SelectTask();
        };

        SimpleBooleanProperty lock = new SimpleBooleanProperty();

        cpuTree.setOnMouseClicked(event -> bindScrollbars.run());
        gpuTree.setOnMouseClicked(event -> bindScrollbars.run());

        cpuTree.getSelectionModel()
                .selectedIndexProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (lock.get())
                        return;
                    lock.set(true);
                    gpuTree.getSelectionModel()
                            .select(newValue.intValue());
                    lock.set(false);
                    SelectTask();
                });
        gpuTree.getSelectionModel()
                .selectedIndexProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (lock.get())
                        return;
                    lock.set(true);
                    cpuTree.getSelectionModel()
                            .select(newValue.intValue());
                    lock.set(false);
                    SelectTask();
                });


        cpuStats = new TreeMap<>();
        gpuStats = new TreeMap<>();
        statMap = new TreeMap<>();
        nodeMap = new TreeMap<>();

        utilizationChart.getStylesheets()
                .add("css/profiler.css");

        Callback<TreeView<TaskStats>, TreeCell<TaskStats>> cellFactory = tv -> {
            Tooltip tp = new Tooltip();
            TreeCell<TaskStats> cell = new TreeCell<TaskStats>() {
                @Override
                protected void updateItem(TaskStats item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setTooltip(null);
                        setStyle(null);
                    } else {
                        setText(item.toString());
                        long ns = item.getTimeTaken();
                        float alpha = ns / 1000000f;
                        Color sample = colourCurve.Sample(alpha);
                        StringBuilder sb = new StringBuilder();
                        sb.append("-fx-background-color: ")
                                .append(sample.toString()
                                        .replace("0x", "#"))
                                .append("; ");
                        sb.append("-fx-text-fill: #ffffff;");
                        String style = sb.toString();
                        setStyle(style);
                        tp.setText("Overhead: " + item.GetOverheadString() + "ms | Location cost: " + item.GetLocationCostString() + "ms | Ratio: " + item.GetOverheadRatioString());
                        setTooltip(tp);
                    }
                }
            };
            return cell;
        };
        cpuTree.setCellFactory(cellFactory);
        gpuTree.setCellFactory(cellFactory);

        BuildChart();
    }

    void SelectTask() {

        TaskStats cpu = null;
        MultipleSelectionModel<TreeItem<TaskStats>> cpuModel = cpuTree.getSelectionModel();
        if (cpuModel != null) {
            TreeItem<TaskStats> cpuItem = cpuModel.getSelectedItem();
            if (cpuItem != null)
                cpu = cpuItem.getValue();
        }

        TaskStats gpu = null;
        MultipleSelectionModel<TreeItem<TaskStats>> gpuModel = gpuTree.getSelectionModel();
        if (gpuModel != null) {
            TreeItem<TaskStats> gpuItem = gpuModel.getSelectedItem();
            if (gpuItem != null)
                gpu = gpuItem.getValue();
        }

        SelectTask(cpu, gpu);
    }

    void SelectTask(TaskStats cpu, TaskStats gpu) {

        TaskStats stat;
        if (cpu == null)
            stat = gpu;
        else stat = cpu;

        if (stat == null) {
            taskStartPath.setText("");
            taskEndPath.setText("");
            return;
        }

        taskStartPath.setText(stat.startLocation.toString());
        taskEndPath.setText(stat.endLocation.toString());
    }

    private void SelectFrame(Integer oldValue, Integer newValue) {

        nodeMap.getOrDefault(oldValue, new ArrayList<>())
                .forEach(n -> n.SetSize(HoveredThresholdNode.DeselectedSize));
        nodeMap.getOrDefault(newValue, new ArrayList<>())
                .forEach(n -> n.SetSize(HoveredThresholdNode.SelectedSize));

        SelectFrame(newValue);
    }

    public void BuildChart() {
        cpuUtil = new Series<>();
        gpuUtil = new Series<>();

        cpuUtil.setName("CPU ms");
        gpuUtil.setName("GPU ms");

        utilizationChart.getData()
                .addAll(cpuUtil, gpuUtil);

        Axis xAxis = utilizationChart.getXAxis();
        if (xAxis instanceof NumberAxis) {
            NumberAxis axis = (NumberAxis) xAxis;
            axis.setForceZeroInRange(false);
        }

//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                if(captureEnabled.isSelected())
//                    Platform.runLater(ProfilerController.this::UpdateChart);
//            }
//        }, 0, 16);

        ProfilerHost.Notify((cStats, gStats) -> {
            Platform.runLater(() -> {
                this.cpuStatDump = cStats;
                this.gpuStatDump = gStats;
                ProfilerController.this.UpdateChart();
            });
        });

    }

    public void UpdateChart() {
        UpdateChart("CPU", cpuUtil);
        UpdateChart("GPU", gpuUtil);
    }

    int GetXValue(Series series, int val) {

//        if (series.getData()
//                .isEmpty())
//            if (series.equals(cpuUtil)) cpuBaseLine = val;
//            else gpuBaseLine = val;
//
//        if (series.equals(cpuUtil))
//            return val - cpuBaseLine;
//        return val - gpuBaseLine;
        return val;
    }

    void UpdateChart(String type, Series series) {
        List<TaskStats> dump;
        if(type.equals("CPU")) {
            dump = cpuStatDump;
        }else if(type.equals("GPU")) {
            dump = gpuStatDump;
        }else{
            return;
        }

        if (dump.isEmpty()) return;
        int frameId = dump.get(0).frame;

        // TODO abstract
        if (type.equalsIgnoreCase("CPU"))
            cpuStats.put(frameId, dump);
        else //if (type.equalsIgnoreCase("GPU"))
            gpuStats.put(frameId, dump);

        long time = dump.stream()
                .mapToLong(TaskStats::getTimeTaken)
                .max()
                .orElse(0L) / 1000000;

        //noinspection unchecked
        Data<Integer, Long> datum = new Data<>(GetXValue(series, frameId), time);
        //noinspection unchecked
        ObservableList<Data<Integer, Long>> data = series.getData();
        HoveredThresholdNode value = new HoveredThresholdNode(this, frameId, Objects.equals(type, "CPU") ? 0 : 1, time + "ms");

        if (!nodeMap.containsKey(frameId))
            nodeMap.put(frameId, new ArrayList<>());
        nodeMap.get(frameId)
                .add(value);

        datum.setNode(value);
        data.add(datum);
    }

    public void SelectFrame(int frameId) {
        SelectFrame(cpuStats.get(frameId), gpuStats.get(frameId));
    }

    public void SelectFrame(List<TaskStats> cpuStats, List<TaskStats> gpuStats) {
        statMap.clear();
        BuildTree(cpuTree, cpuStats);
        BuildTree(gpuTree, gpuStats);
    }

    void BuildTree(TreeView<TaskStats> tree, List<TaskStats> stats) {
        tree.setRoot(null);
        if (stats == null) return;
        if (stats.isEmpty()) return;

        TaskStats stat = stats.get(0);

        TreeItem<TaskStats> root = new TreeItem<>(stat);

        SimpleObjectProperty<Integer> id = new SimpleObjectProperty<>(0);
        for (TaskStats child : stat.children)
            BuildTree(id, root, child);
        root.setExpanded(true);
        tree.setRoot(root);
    }

    void BuildTree(Property<Integer> id, TreeItem<TaskStats> parentItem, TaskStats stats) {
        Integer value = id.getValue();
        id.setValue(value + 1);
        TreeItem<TaskStats> newItem = new TreeItem<>(stats);

        if (statMap.containsKey(value)) {
            TreeItem<TaskStats> item = statMap.get(value);
            item.expandedProperty()
                    .bindBidirectional(newItem.expandedProperty());
            statMap.remove(value);
        } else {
            statMap.put(value, newItem);
        }

        parentItem.getChildren()
                .add(newItem);
        for (TaskStats child : stats.children)
            BuildTree(id, newItem, child);
    }

    public void ClearChart(ActionEvent actionEvent) {
        cpuUtil.getData()
                .clear();
        gpuUtil.getData()
                .clear();
        cpuStats.clear();
        gpuStats.clear();
        nodeMap.clear();
    }

    public void SelectPreviousFrame(ActionEvent actionEvent) {
        Integer integer = selectedFrameId.get();
        selectedFrameId.set(integer - 1);
    }

    public void SelectNextFrame(ActionEvent actionEvent) {
        Integer integer = selectedFrameId.get();
        selectedFrameId.set(integer + 1);
    }

    public static class HoveredThresholdNode extends StackPane {

        public static final double DeselectedSize = 9.0;
        public static final double SelectedSize = 15.0;

        public final ProfilerController ctrlr;
        public final int key;

        public HoveredThresholdNode(ProfilerController ctrlr, int key, int id, String value) {
            this.ctrlr = ctrlr;
            this.key = key;
            SetSize(DeselectedSize);

            final Node node = createDataThresholdLabel(id, value);

            setOnMouseEntered(mouseEvent -> {
                getChildren().setAll(node);
                setCursor(Cursor.HAND);
                toFront();
            });
            setOnMouseExited(mouseEvent -> {
                getChildren().clear();
                setCursor(Cursor.CROSSHAIR);
            });

            setOnMouseClicked(e -> ctrlr.selectedFrameId.setValue(key));
        }

        private Node createDataThresholdLabel(int id, String value) {
            final Label label = new Label(value + "");
            label.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

            label.setTextFill(Color.DARKGRAY);

            label.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

            VBox box = new VBox();

            final Label label2 = new Label(this.key + "");
            label2.setStyle("-fx-font-size: 12;");
            label2.setTextFill(Color.DARKGRAY);
            label2.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);


            box.getStyleClass()
                    .addAll("default-color" + id, "chart-line-symbol", "chart-series-line");
            box.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            box.getChildren()
                    .add(label);
            box.getChildren()
                    .add(label2);

            return box;
        }

        public void SetSize(double size) {

            setMinSize(size, size);
            setPrefSize(size, size);
            setMaxSize(size, size);

        }

    }

}
