package net.ncguy.tools.debug.view;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.ncguy.tools.debug.items.ShaderItemController;
import net.ncguy.util.ReloadableShader;

import java.io.IOException;
import java.lang.ref.Reference;
import java.net.URL;
import java.util.Comparator;
import java.util.Objects;
import java.util.ResourceBundle;

public class ShaderController implements Initializable {

    ObservableList<ReloadableShader> shaderList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        shaderList = FXCollections.observableArrayList();
        shaderList.addListener((ListChangeListener<? super ReloadableShader>) c -> ReloadList());
        UpdateList();
    }

    private void ReloadList() {
        long start = System.nanoTime();
        Container.getChildren()
                .clear();
        shaderList.stream()
                .map(this::Make)
                .filter(Objects::nonNull)
                .peek(n -> VBox.setVgrow(n, Priority.NEVER))
                .forEach(Container.getChildren()::add);

        long mid = System.nanoTime();

        Node spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Container.getChildren().add(spacer);

        long end = System.nanoTime();

        System.out.println("Time taken: ");
        System.out.printf("\tMid: %fms\n", (mid - start) / 1000000f);
        System.out.printf("\tEnd: %fms\n", (end - start) / 1000000f);

    }

    private Node Make(ReloadableShader shader) {
        try {
            return MakeImpl(shader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Label("Error building node for " + shader.Name());
    }

    private Node MakeImpl(ReloadableShader shader) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Node node = loader.load(getClass().getResource("/fxml/items/shaderItem.fxml").openStream());
        Object ctrlr = loader.getController();
        if (ctrlr instanceof ShaderItemController)
            ((ShaderItemController) ctrlr).SetShader(shader);
        return node;
    }

    void UpdateList() {
        ReloadableShader.shaders.stream()
                .filter(Objects::nonNull)
                .map(Reference::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(ReloadableShader::Name))
                .forEach(shader -> shaderList.add(shader));
    }

    @FXML
    protected VBox Container;

}
