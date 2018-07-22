package net.ncguy.ui.character;

import com.kotcrab.vis.ui.widget.VisTable;
import net.ncguy.entity.Entity;

public class CharacterEquippedUI extends VisTable {

    private final Entity owningEntity;
    EquipmentUI equipment;
    AbilitiesEquippedUI abilitiesEquipped;

    public CharacterEquippedUI(Entity owningEntity) {
        this.owningEntity = owningEntity;
        equipment = new EquipmentUI(owningEntity);
        abilitiesEquipped = new AbilitiesEquippedUI(owningEntity);

        defaults().pad(8).growY();

        add(equipment);
        add(abilitiesEquipped).row();

    }
}
