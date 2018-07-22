package net.ncguy.tools.debug.view;

public class FXMLItem {
    public String name;
    public String fxmlPath;

    public FXMLItem(String name, String fxmlPath) {
        this.name = name;
        this.fxmlPath = fxmlPath;
    }

    @Override
    public String toString() {
        return name;
    }
}
