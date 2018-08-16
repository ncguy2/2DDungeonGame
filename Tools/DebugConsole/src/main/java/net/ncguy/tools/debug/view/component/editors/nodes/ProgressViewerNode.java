package net.ncguy.tools.debug.view.component.editors.nodes;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

public class ProgressViewerNode extends AnchorPane {

    Supplier<Float> progressSupplier;
    Timer timer;
    ProgressBar bar;
    Label label;

    public ProgressViewerNode(Supplier<Float> progressSupplier) {
        this.progressSupplier = progressSupplier;

        label = new Label();

        bar = new ProgressBar();
        getChildren().addAll(label, bar);

        AnchorPane.setBottomAnchor(bar, 0.0);
        AnchorPane.setTopAnchor(bar, 0.0);
        AnchorPane.setRightAnchor(bar, 0.0);

        AnchorPane.setBottomAnchor(label, 0.0);
        AnchorPane.setTopAnchor(label, 0.0);
        AnchorPane.setLeftAnchor(label, 0.0);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(progressSupplier == null)
                    return;

                final Float f = progressSupplier.get();
                Platform.runLater(() -> {
                    bar.setProgress(f);
                    label.setText(Math.round(f * 100) + "%");
                });
            }
        }, 0, 16);
    }

    @Override
    protected void finalize() throws Throwable {
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        super.finalize();
    }
}