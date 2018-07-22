package net.ncguy.ui.character;

import com.kotcrab.vis.ui.widget.VisTable;
import net.ncguy.entity.Entity;

public class BaseCharacterUI extends VisTable {

    CharacterEquippedUI equipped;

    public BaseCharacterUI(Entity owningEntity) {
        equipped = new CharacterEquippedUI(owningEntity);

        add(equipped).grow();

    }
}
