package net.ncguy.ui.character;

import com.kotcrab.vis.ui.widget.VisTable;
import net.ncguy.entity.Entity;

public class CharacterUI extends VisTable {

    BaseCharacterUI onPlayer;
    BaseInventoryUI inventory;

    public CharacterUI(Entity owningEntity) {
        onPlayer = new BaseCharacterUI(owningEntity);
        inventory = new BaseInventoryUI(owningEntity);

        defaults().grow().pad(8);

        add(onPlayer);
        add(inventory).row();
    }
}
