package net.ncguy.lib.gen.tile;

import net.ncguy.lib.gen.WorldGenerator;

import java.util.Collection;

public class CaveTileWorldGenerator extends WorldGenerator<TileWorldElement, CaveTileWorld> {

    public int seed = 8;
    public int width = 8;
    public int height = 8;
    public int iterations = 8;

    public float chanceToStartAlive = 0.45f;
    public int deathLimit = 6;
    public int birthLimit = 3;

    @Override
    public CaveTileWorld GetWorld() {
        return new CaveTileWorld(seed);
    }

    @Override
    public Collection<TileWorldElement> GetElements() {
        CaveTileWorld tileWorld = GetWorld();
        tileWorld.width = width;
        tileWorld.height = height;
        tileWorld.iterations = iterations;
        tileWorld.chanceToStartAlive = chanceToStartAlive;
        tileWorld.deathLimit = deathLimit;
        tileWorld.birthLimit = birthLimit;
        tileWorld.Generate();
        return tileWorld.Elements();
    }

}
