package net.ncguy.lib.gen;

import java.util.Collection;

public abstract class WorldGenerator<E extends WorldElement, W extends BaseWorld<E>> {

    public abstract W GetWorld();
    public abstract Collection<E> GetElements();

}
