package net.ncguy.ui.character.widgets;

import net.ncguy.ability.Ability;
import net.ncguy.entity.Entity;
import net.ncguy.entity.component.AbilityComponent;
import net.ncguy.ui.dnd.DnDManager;
import net.ncguy.ui.dnd.FunctionalTarget;

import java.util.List;

public class AbilityEquipWidget extends AbilityWidget {

    private final int slotIdx;

    public AbilityEquipWidget(Entity owningEntity, int slotIdx) {
        super(owningEntity);
        this.slotIdx = slotIdx;
    }

    @Override
    public void SetAbility(Ability ability) {
        super.SetAbility(ability);
        List<AbilityComponent> abilityComponents = owningEntity.GetComponents(AbilityComponent.class, true);
        while(abilityComponents.size() <= slotIdx) {
            AbilityComponent abilityComponent = owningEntity.AddComponent(new AbilityComponent("Ability/" + abilityComponents.size()))
                    .SlotIdx(abilityComponents.size());
            abilityComponents.add(abilityComponent);
        }
        abilityComponents.get(slotIdx).SetAbility(ability);
    }

    @Override
    public void InitDnD() {
        FunctionalTarget tgt = DnDManager.instance()
                .AddTarget(this, DnDManager.ConditionType.All, DnDManager.Tags.Ability);
        tgt.drag = (source, payload, x, y, pointer) -> true;
        tgt.drop = (source, payload, x, y, pointer) -> {
            Object object = payload.getObject();
            if(object instanceof Ability)
                SetAbility((Ability) object);
        };
    }

}
