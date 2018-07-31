package net.ncguy.entity.component;

public class InventoryComponent extends EntityComponent {

    public InventoryComponent() {
        this("Unnamed Scene component");
    }

    public InventoryComponent(String name) {
        super(name);
    }
}
