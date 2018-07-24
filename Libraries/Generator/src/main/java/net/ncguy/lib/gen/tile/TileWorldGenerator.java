package net.ncguy.lib.gen.tile;

import net.ncguy.lib.gen.WorldGenerator;

import java.util.Collection;

public class TileWorldGenerator extends WorldGenerator<TileWorldElement, TileWorld> {

    @Override
    public TileWorld GetWorld() {
        return new TileWorld();
    }

    @Override
    public Collection<TileWorldElement> GetElements() {
        TileWorld tileWorld = GetWorld();
        tileWorld.width = 8;
        tileWorld.height = 8;
        tileWorld.Generate();
        return tileWorld.Elements();
    }

}
