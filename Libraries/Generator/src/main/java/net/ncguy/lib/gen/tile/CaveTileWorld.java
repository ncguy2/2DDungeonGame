package net.ncguy.lib.gen.tile;

import net.ncguy.lib.gen.BaseWorld;

import java.util.*;

public class CaveTileWorld extends BaseWorld<TileWorldElement> {

    public int width;
    public int height;
    public int iterations = 8;

    public int seed;

    public float chanceToStartAlive = 0.45f;
    public int deathLimit = 3;
    public int birthLimit = 4;

    Random rand;


    public CaveTileWorld() {
        this(0);
    }

    public CaveTileWorld(int seed) {
        this.seed = seed;
    }

    @Override
    public void Generate() {
        boolean[][] solidsMap = new boolean[width][height];

        long start = System.nanoTime();

        rand = new Random(seed);

        Initialize(solidsMap);
        for (int i = 0; i < iterations; i++) {
            DoSimulationStep(solidsMap);
        }
        Populate(solidsMap);

        long end = System.nanoTime();

        System.out.printf("Generation complete, took %fms\n", (end - start) / 1000000f);
    }

    void Initialize(boolean[][] map) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x][y] = rand.nextFloat() > chanceToStartAlive;
            }
        }
    }

    void DoSimulationStep(boolean[][] map) {
        boolean[][] oldMap = map.clone();

        for (int x = 0; x < oldMap.length; x++)
            for (int y = 0; y < oldMap[x].length; y++) {
                int nbs = CountAliveNeighbours(oldMap, x, y);
                if (oldMap[x][y]) map[x][y] = nbs > deathLimit;
                else map[x][y] = nbs > birthLimit;
            }
    }

    int CountCardinalNeighbours(boolean[][] sampleMap, int x, int y) {
        int count = 0;

        int[][] offsets = new int[4][2];

        offsets[0][0] = -1;
        offsets[0][1] = 0;

        offsets[1][0] = 1;
        offsets[1][1] = 0;

        offsets[2][0] = -1;
        offsets[2][1] = 0;

        offsets[3][0] = 1;
        offsets[3][1] = 0;

        for (int n = 0; n < 4; n++) {

            int i = offsets[n][0];
            int j = offsets[n][1];

            int nX = x + i;
            int nY = y + j;
            if (i == 0 && j == 0)
                continue;

            if (nX < 0 || nY < 0 || nX >= sampleMap.length || nY >= sampleMap[0].length)
                count++;
            else if (sampleMap[nX][nY])
                count++;
        }
        return count;
    }

    int CountAliveNeighbours(boolean[][] sampleMap, int x, int y) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int nX = x + i;
                int nY = y + j;
                if (i == 0 && j == 0)
                    continue;

                if (nX < 0 || nY < 0 || nX >= sampleMap.length || nY >= sampleMap[0].length)
                    count++;
                else if (sampleMap[nX][nY])
                    count++;
            }
        }
        return count;
    }

    int MaskAliveNeighbours(boolean[][] sampleMap, int x, int y) {
        int count = 0;
        int neighbourId = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                boolean alive = false;
                int nX = x + i;
                int nY = y + j;
                if (i == 0 && j == 0)
                    continue;

                if (nX < 0 || nY < 0 || nX >= sampleMap.length || nY >= sampleMap[0].length)
                    alive = true;
                else if (sampleMap[nX][nY])
                    alive = true;

                if(alive)
                    count |= (1 << neighbourId);
                neighbourId++;
            }
        }
        return count;
    }

    void Populate(boolean[][] map) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TileWorldElement element = new TileWorldElement();

                if(map[x][y])
                    element.texRef = "textures/connected/frame.png";
                else element.texRef = "textures/connected/bluepulse.png";
                element.texConnected = true;
                element.texMap = map;

                element.solid = map[x][y];
                element.x = x;
                element.y = y;
                elements.add(element);
            }
        }
    }

}
