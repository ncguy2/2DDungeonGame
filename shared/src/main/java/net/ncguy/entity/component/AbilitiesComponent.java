package net.ncguy.entity.component;

import net.ncguy.ability.Ability;

import java.util.ArrayList;
import java.util.List;

public class AbilitiesComponent extends EntityComponent {

    public List<Ability> abilities;

    public AbilitiesComponent(String name) {
        super(name);
        abilities = new ArrayList<>();
    }

    public void GrantAbility(Ability ability) {
        if(ability == null)
            return;
        for (Ability a : abilities) {
            if(a.scriptPath.equalsIgnoreCase(ability.scriptPath)) {
                System.out.printf("Duplicate ability script path for %s and %s\n", a.name, ability.name);
                return;
            }
        }
        abilities.add(ability);
    }
}