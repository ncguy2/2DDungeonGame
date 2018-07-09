package net.ncguy.ability;

import net.ncguy.classes.Classes;

import java.util.ArrayList;

public interface Requirement {

    class ClassRequirement extends ArrayList<Classes> implements Requirement {}
    class LevelRequirement implements Requirement {

        public LevelRequirement() { }

        public LevelRequirement(int level) {
            this.level = level;
        }

        public int level;
    }

}
