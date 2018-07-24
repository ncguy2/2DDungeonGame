package net.ncguy.tools.debug.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;

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

    @Override
    public String toString() {
        return name;
    }
}
