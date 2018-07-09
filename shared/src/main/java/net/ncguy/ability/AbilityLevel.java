package net.ncguy.ability;

public enum AbilityLevel {
    Cantrip,
    Level_1,
    Level_2,
    Level_3,
    Level_4,
    Level_5,
    Level_6,
    Level_7,
    Level_8,
    Level_9,
    ;

    public final int cost;

    AbilityLevel() {
        this.cost = ordinal();
    }

    AbilityLevel(int cost) {
        this.cost = cost;
    }
}
