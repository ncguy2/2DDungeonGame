package net.ncguy.ability;

public class Ability {

    public String name;
    public String icon;
    public AbilityLevel level;
    public String scriptPath;
    public AbilityComponent[] components;
    public Requirement[] requirements;

}


/*
<Ability>
            <Name>Heal</Name>
            <Icon></Icon>
            <Level>Cantrip</Level>
            <Script>scripts/abilities/heal.js</Script>
            <Cost>
                <Component type="Verbal"/>
                <Component type="Somatic"/>
            </Cost>
            <Requirements>
                <ClassRequirement>
                    <Class>Bard</Class>
                    <Class>Cleric</Class>
                    <Class>Druid</Class>
                    <Class>Paladin</Class>
                    <Class>Wizard</Class>
                </ClassRequirement>
                <LevelRequirement>1</LevelRequirement>
            </Requirements>
        </Ability>
 */