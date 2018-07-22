package net.ncguy.ui.character;

import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.kotcrab.vis.ui.widget.VisTable;
import net.ncguy.entity.Entity;

public class BaseInventoryUI extends VisTable {

    InventoryUI inventory;
    AbilitiesUI abilities;

    public BaseInventoryUI(Entity owningEntity) {
        inventory = new InventoryUI(owningEntity);
        abilities = new AbilitiesUI(owningEntity);

        add(inventory).growX().height(Value.percentHeight(.6f, this)).row();
        add(abilities).growX().height(Value.percentHeight(.4f, this)).row();
    }
}
