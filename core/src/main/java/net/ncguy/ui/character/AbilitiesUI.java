package net.ncguy.ui.character;

import com.kotcrab.vis.ui.layout.HorizontalFlowGroup;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import net.ncguy.ability.Ability;
import net.ncguy.entity.Entity;
import net.ncguy.entity.component.AbilitiesComponent;
import net.ncguy.ui.character.widgets.AbilityStoreWidget;

import java.util.ArrayList;
import java.util.List;

public class AbilitiesUI extends VisTable {

    Entity owningEntity;

    HorizontalFlowGroup group;

    public AbilitiesUI(Entity owningEntity) {
        this.owningEntity = owningEntity;
        group = new HorizontalFlowGroup();

        VisScrollPane scroller = new VisScrollPane(group);
        add(scroller).grow().row();

        List<Ability> abilities = new ArrayList<>();

        owningEntity.GetComponents(AbilitiesComponent.class, true)
                .stream()
                .map(e -> e.abilities)
                .forEach(abilities::addAll);

        abilities.stream().map(e -> new AbilityStoreWidget(owningEntity, e)).forEach(group::addActor);
    }
}
