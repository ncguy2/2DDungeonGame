package net.ncguy.lib.gen;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseWorld<T extends WorldElement> {

    protected List<T> elements;

    public BaseWorld() {
        elements = new ArrayList<>();
    }

    public abstract void Generate();

    public List<T> Elements() {
        return new ArrayList<>(elements);
    }

}
