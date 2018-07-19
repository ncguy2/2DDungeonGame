package net.ncguy.tools.debug.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RootController implements Initializable {

    ObservableList<Item> items;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        items = FXCollections.observableArrayList();

        Items.setItems(items);

        AddItem("Shaders", "/fxml/pages/shaders.fxml");
        AddItem("Entities", "/fxml/pages/entities.fxml");
    }

    void AddItem(String name, String fxmlPath) {
        items.add(new Item(name, fxmlPath));
    }

    @FXML
    public ListView<Item> Items;
    @FXML
    public AnchorPane Content;

    public void ItemsClicked(MouseEvent mouseEvent) {
        ObservableList<Node> children = Content.getChildren();
        children.clear();

        Item item = Items.getSelectionModel()
                .getSelectedItem();
        if(item == null)
            return;

        try {
            Node node = FXMLLoader.load(getClass().getResource(item.fxmlPath));
            children.add(node);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Item {
        public String name;
        public String fxmlPath;

        public Item(String name, String fxmlPath) {
            this.name = name;
            this.fxmlPath = fxmlPath;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
