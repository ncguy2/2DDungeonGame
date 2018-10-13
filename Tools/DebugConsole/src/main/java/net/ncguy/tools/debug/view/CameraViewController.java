package net.ncguy.tools.debug.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import net.ncguy.render.PostProcessingCamera;
import net.ncguy.util.TextureUtils;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.ResourceBundle;

public class CameraViewController implements Initializable {
    public ListView<PostProcessingCamera> CameraContainer;
    public TabPane CameraViewContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CameraContainer.getSelectionModel().selectedItemProperty().addListener(this::accept);
        ReloadCameras(null);
    }

    private void accept(Observable observable) {
        CameraViewContainer.getTabs().clear();
        PostProcessingCamera camera = CameraContainer.getSelectionModel().getSelectedItem();
        if(camera == null)
            return;

        Texture[] t = camera.postProcessedTextures;
        CameraViewContainer.getTabs().clear();
        for (int i = 0; i < t.length; i++) {
            CameraViewContainer.getTabs().add(Tab(camera, i));
        }
    }

    public void ReloadCameras(ActionEvent actionEvent) {
        List<PostProcessingCamera> cameras = PostProcessingCamera.GetCameras();
        CameraContainer.getItems().setAll(cameras);
    }

    public Tab Tab(PostProcessingCamera camera, int texId) {
        Tab tab = new Tab("Texture " + texId);
        tab.setContent(ImageViewContents(camera, texId));
        tab.setClosable(false);
        return tab;
    }

    public Node ImageViewContents(PostProcessingCamera camera, int texId) {
        AnchorPane pane = new AnchorPane();

        final ImageView view = new ImageView();
        pane.getChildren().add(view);

        Button loadBtn = new Button("Click to load");
        loadBtn.setOnAction(e -> {
            LoadImage(camera, texId, view);
        });

        loadBtn.setMaxHeight(32.0);
        loadBtn.setPrefHeight(32.0);
        loadBtn.setMinHeight(32.0);

        pane.getChildren().add(loadBtn);

        AnchorPane.setBottomAnchor(loadBtn, 0.0);
        AnchorPane.setLeftAnchor(loadBtn, 0.0);
        AnchorPane.setRightAnchor(loadBtn, 0.0);

        AnchorPane.setTopAnchor(view, 0.0);
        AnchorPane.setBottomAnchor(view, 32.0);
        AnchorPane.setLeftAnchor(view, 0.0);
        AnchorPane.setRightAnchor(view, 0.0);

        return pane;
    }

    void LoadImage(PostProcessingCamera camera, int texId, ImageView view) {
        Gdx.app.postRunnable(() -> {
            Texture tex = camera.postProcessedTextures[texId];
            byte[] buffer = TextureUtils.ToArray(tex);
            Platform.runLater(() -> {
//                    Image i = new Image(new ByteArrayInputStream(buffer), tex.getWidth(), tex.getHeight(), true, true);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int width = tex.getWidth();
                int height = tex.getHeight();
                try {

                    byte[] r = new byte[256];
                    byte[] g = new byte[256];
                    byte[] b = new byte[256];
                    for(int i=0; i<256; i++) {
                        r[i]=(byte)i;
                        g[i]=(byte)i;
                        b[i]=(byte)i;
                    }
                    ColorModel defaultColorModel = new DirectColorModel(32, 0xff000000,
                            0x00ff0000,
                            0x0000ff00,
                            0x000000ff
                    );

                    WritableRaster wr = defaultColorModel.createCompatibleWritableRaster(1, 1);
                    SampleModel sampleModel = wr.getSampleModel();
                    sampleModel = sampleModel.createCompatibleSampleModel(width, height);

                    ByteBuffer bb = ByteBuffer.wrap(buffer);
                    int size = width * height;
                    int[] intBuffer = new int[size];
                    for (int i = 0; i < size; i++) {
                        intBuffer[i] = bb.getInt();
                    }

                    DataBuffer db = new DataBufferInt(intBuffer, size, 0);
                    WritableRaster raster = Raster.createWritableRaster(sampleModel, db, null);
                    BufferedImage image = new BufferedImage(defaultColorModel, raster, false, null);

                    ImageIO.write(image, "png", out);
                    out.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
                Image i = new Image(in);

                view.setImage(i);
                view.getParent().layout();
            });
        });
    }

}
