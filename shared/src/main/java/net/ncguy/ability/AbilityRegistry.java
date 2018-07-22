package net.ncguy.ability;

import net.ncguy.ability.loader.AbilityXmlLoader;
import net.ncguy.entity.component.AbilitiesComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AbilityRegistry {

    private static AbilityRegistry instance;
    public static AbilityRegistry instance() {
        if (instance == null)
            instance = new AbilityRegistry();
        return instance;
    }

    List<Ability> abilities;

    private AbilityRegistry() {
        abilities = new ArrayList<>();
    }

    public Optional<Ability> Get(String name) {
        return GetAll(name).filter(a -> a.length >= 1).map(a -> a[0]);
    }

    public Optional<Ability[]> GetAll(String name) {
        return Optional.of(abilities.stream().filter(a -> a.name.equalsIgnoreCase(name)).toArray(Ability[]::new));
    }

    public List<Ability> All() {
        return new ArrayList<>(abilities);
    }

    public List<String> Names() {
        return abilities.stream().map(a -> a.name).collect(Collectors.toList());
    }

    public void Load(String xml) {
        AbilityXmlLoader loader = new AbilityXmlLoader(xml);
        abilities.addAll(loader.Parse());
    }

    public void Give(String abilityName, AbilitiesComponent abilities) {
        Get(abilityName).ifPresent(abilities::GrantAbility);
    }

    public void GiveAll(AbilitiesComponent abilities) {
        this.abilities.forEach(abilities::GrantAbility);
    }

}
