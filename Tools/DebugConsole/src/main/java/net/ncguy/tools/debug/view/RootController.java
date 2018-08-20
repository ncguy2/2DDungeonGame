package net.ncguy.tools.debug.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class RootController implements Initializable {

    ObservableList<FXMLItem> items;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        items = FXCollections.observableArrayList();

        if(Items != null)
            Items.setItems(items);

        AddItem("Abilities", "/fxml/pages/abilities.fxml");
        AddItem("Entities", "/fxml/pages/entities.fxml");
        AddItem("Shaders", "/fxml/pages/shaders.fxml");
        AddItem("Particles", "/fxml/pages/particles.fxml", false);
        AddItem("Profiler", "/fxml/pages/profiler.fxml", true);

    }

    void AddItem(String name, String fxmlPath) {
        items.add(new FXMLItem(name, fxmlPath));
    }

    void AddItem(String name, String fxmlPath, boolean loadOnce) {
        items.add(new FXMLItem(name, fxmlPath, loadOnce));
    }

    @FXML
    public ListView<FXMLItem> Items;
    @FXML
    public AnchorPane Content;

    public void ItemsClicked(MouseEvent mouseEvent) {
        if(Items == null) return;
        ObservableList<Node> children = Content.getChildren();
        children.clear();

        FXMLItem item = Items.getSelectionModel()
                .getSelectedItem();
        if (item == null)
            return;

        Node node = item.GetNode();
        if(node == null)
            return;
        children.add(node);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
    }

}
