package net.ncguy.lib.gen.tile;

import net.ncguy.lib.gen.BaseWorld;
import net.ncguy.lib.gen.utils.SimplexNoise;

public class TileWorld extends BaseWorld<TileWorldElement> {

    public int width;
    public int height;
    public double solidThreshold = .5;

    protected final SimplexNoise noise;

    public TileWorld() {
        this(0);
    }

    public TileWorld(int noiseSeed) {
        noise = new SimplexNoise(noiseSeed);
    }

    @Override
    public void Generate() {

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double noise = this.noise.noise(x, y);
                TileWorldElement element = new TileWorldElement();
                element.solid = noise > solidThreshold;
                element.x = x;
                element.y = y;
                elements.add(element);
            }
        }
    }
}
