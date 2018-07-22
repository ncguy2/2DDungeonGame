package net.ncguy.ui.character;

import com.kotcrab.vis.ui.widget.VisTable;
import net.ncguy.entity.Entity;
import net.ncguy.ui.character.widgets.AbilityEquipWidget;

public class AbilitiesEquippedUI extends VisTable {

    private final Entity owningEntity;

    public AbilitiesEquippedUI(Entity owningEntity) {
        this.owningEntity = owningEntity;
        Init();
    }

    void Init() {
        defaults().pad(16);

        add(new AbilityEquipWidget(owningEntity, 0));
        add(new AbilityEquipWidget(owningEntity, 1)).row();
        add(new AbilityEquipWidget(owningEntity, 2));
        add(new AbilityEquipWidget(owningEntity, 3)).row();
    }

}
