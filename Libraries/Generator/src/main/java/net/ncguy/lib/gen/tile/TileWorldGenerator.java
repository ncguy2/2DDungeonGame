package net.ncguy.lib.gen.tile;

import net.ncguy.lib.gen.WorldGenerator;

import java.util.Collection;

public class TileWorldGenerator extends WorldGenerator<TileWorldElement, TileWorld> {

    public int width = 8;
    public int height = 8;

    @Override
    public TileWorld GetWorld() {
        return new TileWorld();
    }

    @Override
    public Collection<TileWorldElement> GetElements() {
        TileWorld tileWorld = GetWorld();
        tileWorld.width = width;
        tileWorld.height = height;
        tileWorld.Generate();
        return tileWorld.Elements();
    }

}
