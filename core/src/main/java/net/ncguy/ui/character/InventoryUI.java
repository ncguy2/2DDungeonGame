package net.ncguy.ui.character;

import com.kotcrab.vis.ui.widget.VisTable;
import net.ncguy.entity.Entity;

public class InventoryUI extends VisTable {

    Entity owningEntity;

    public InventoryUI(Entity owningEntity) {
        this.owningEntity = owningEntity;
    }
}
