package net.ncguy.ui.character.widgets;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.ui.widget.VisLabel;
import net.ncguy.ability.Ability;
import net.ncguy.entity.Entity;
import net.ncguy.ui.dnd.DnDManager;
import net.ncguy.ui.dnd.FunctionalSource;

public class AbilityStoreWidget extends AbilityWidget {

    public AbilityStoreWidget(Entity owningEntity) {
        super(owningEntity);
    }

    public AbilityStoreWidget(Entity owningEntity, Ability ability) {
        super(owningEntity);
        this.SetAbility(ability);
    }

    @Override
    public void InitDnD() {
        FunctionalSource src = DnDManager.instance()
                .AddSource(this, DnDManager.Tags.Ability);
        src.payloadFactory = (payload, event, x, y, pointer) -> {

            payload.setDragActor(new VisLabel(ability.name));
            VisLabel validDragActor = new VisLabel(ability.name);
            validDragActor.setColor(Color.GREEN);
            payload.setValidDragActor(validDragActor);
            VisLabel invalidDragActor = new VisLabel(ability.name);
            invalidDragActor.setColor(Color.RED);
            payload.setInvalidDragActor(invalidDragActor);

            payload.setObject(ability);
        };
    }

}
