package net.ncguy.entity.component.modifiers;

import net.ncguy.entity.component.EntityComponent;

import java.util.List;
import java.util.stream.Collectors;

public class ModifierComponent extends EntityComponent {

    public ModifierComponent() {
    }

    public ModifierComponent(String name) {
        super(name);
    }

    public int GetSelfIndex() {
        if(owningComponent == null)
            return -1;

        List<ModifierComponent> list = owningComponent.childrenComponents.stream().filter(getClass()::isInstance).map(getClass()::cast).collect(Collectors.toList());

        return list.indexOf(this);
    }

}
