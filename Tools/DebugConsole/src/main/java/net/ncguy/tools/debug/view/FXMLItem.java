package net.ncguy.tools.debug.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.util.Optional;

public class FXMLItem {
    public String name;
    public String fxmlPath;
    public transient Node node;
    public boolean loadOnce;

    public FXMLItem(String name, String fxmlPath) {
        this(name, fxmlPath, false);
    }

    public FXMLItem(String name, String fxmlPath, boolean loadOnce) {
        this.name = name;
        this.fxmlPath = fxmlPath;
        this.loadOnce = loadOnce;
    }

    public Node GetNode() {
        if(node != null) {
            return node;
        }

        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlPath));
            if(loadOnce)
                this.node = node;
            return node;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Optional<ItemInfo> Build() {
        try {
            return Optional.of(BuildImpl());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    public ItemInfo BuildImpl() throws IOException {
        FXMLLoader loader = new FXMLLoader();
//        Node node = loader.load(getClass().getResource("/fxml/items/shaderItem.fxml").openStream());
        Node node = loader.load(getClass().getResource(fxmlPath).openStream());
        Object ctrlr = loader.getController();
        return new ItemInfo(node, ctrlr);
    }

    @Override
    public String toString() {
        return name;
    }

    public static class ItemInfo {
        public Node node;
        public Object controller;

        public ItemInfo(Node node, Object controller) {
            this.node = node;
            this.controller = controller;
        }
    }

}
