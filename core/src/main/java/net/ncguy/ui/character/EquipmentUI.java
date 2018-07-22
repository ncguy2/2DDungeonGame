package net.ncguy.ui.character;

import com.kotcrab.vis.ui.widget.VisTable;
import net.ncguy.entity.Entity;

public class EquipmentUI extends VisTable {

    Entity owningEntity;

    public EquipmentUI(Entity owningEntity) {
        this.owningEntity = owningEntity;
    }
}
