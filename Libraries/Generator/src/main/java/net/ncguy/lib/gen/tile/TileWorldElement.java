package net.ncguy.lib.gen.tile;

import net.ncguy.lib.gen.WorldElement;

public class TileWorldElement extends WorldElement {

    public int x, y;
    public boolean solid;

    public String texRef;
    public boolean texConnected;

    public boolean[][] texMap;
}
