package net.ncguy.lib.gen.tile;

import net.ncguy.lib.gen.WorldGenerator;

import java.util.Collection;

public class SimplexTileWorldGenerator extends WorldGenerator<TileWorldElement, SimplexTileWorld> {

    public int seed = 8;
    public int width = 8;
    public int height = 8;

    @Override
    public SimplexTileWorld GetWorld() {
        return new SimplexTileWorld(seed);
    }

    @Override
    public Collection<TileWorldElement> GetElements() {
        SimplexTileWorld tileWorld = GetWorld();
        tileWorld.width = width;
        tileWorld.height = height;
        tileWorld.Generate();
        return tileWorld.Elements();
    }

}
