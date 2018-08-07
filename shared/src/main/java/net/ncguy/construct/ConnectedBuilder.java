package net.ncguy.construct;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

// TODO port to GLSL shader
public class ConnectedBuilder {

    public Texture airTex;
    public CTSprite ctSprite;

    private boolean[] n;

    public ConnectedBuilder() {
        Pixmap map = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        map.setColor(0, 0, .74f, 1);
        map.fillRectangle(0, 0, 1, 1);
        airTex = new Texture(map);
        map.dispose();
    }

    public CTSprite build(Texture tex, int x, int y, boolean[][] map) {
        ctSprite = new CTSprite();

        ctSprite.tr.set(new Sprite(tex));
        ctSprite.bl.set(new Sprite(tex));
        ctSprite.tl.set(new Sprite(tex));
        ctSprite.br.set(new Sprite(tex));
//        currentTile.print(x, y);

        n = new boolean[9];
        for(int i = 0; i < n.length; i++)
            n[i] = false;

        n[0] = isMatchingNeighbour(map[x][y], x-1, y+1, map);
        n[1] = isMatchingNeighbour(map[x][y], x, y+1, map);
        n[2] = isMatchingNeighbour(map[x][y], x+1, y+1, map);
        n[3] = isMatchingNeighbour(map[x][y], x-1, y, map);
        n[4] = isMatchingNeighbour(map[x][y], x, y, map);
        n[5] = isMatchingNeighbour(map[x][y], x+1, y, map);
        n[6] = isMatchingNeighbour(map[x][y], x-1, y-1, map);
        n[7] = isMatchingNeighbour(map[x][y], x, y-1, map);
        n[8] = isMatchingNeighbour(map[x][y], x+1, y-1, map);


        if(n[4]){
//            ctSprite.setTexture(airTex);
            ctSprite.tl.setRegion(0, .5f, .5f, 1);
            ctSprite.tr.setRegion(.5f, 1, .5f, 1);
            ctSprite.bl.setRegion(0, .5f, 0, .5f);
            ctSprite.br.setRegion(.5f, 1, 0, .5f);
//            return ctSprite;
        }

        //n[0] n[1] n[2]
        //n[3] n[4] n[5]
        //n[6] n[7] n[8]

        //Top Left
        if(n[0] && n[1] && n[3]){
            ctSprite.tl.setRegion(0, 0, .5f, .1f);
        }else if(n[0] && n[1]) {
            ctSprite.tl.setRegion(0, .2f, .5f, .3f);
        }else if(n[0] && n[3]) {
            ctSprite.tl.setRegion(0, .4f, .5f, .5f);
        }else if(n[1] && n[3]) {
            ctSprite.tl.setRegion(0, 0, .5f, .1f);
        }else if(n[0]){
            ctSprite.tl.setRegion(0, .6f, .5f, .7f);
        }else if(n[1]) {
            ctSprite.tl.setRegion(0, .2f, .5f, .3f);
        }else if(n[3]) {
            ctSprite.tl.setRegion(0, .4f, .5f, .5f);
        }else{
            ctSprite.tl.setRegion(0, .8f, .5f, .9f);
        }
        //Top Right
        if(n[1] && n[2] && n[5]){
            ctSprite.tr.setRegion(.5f, 0, 1, .1f);
        }else if(n[1] && n[2]) {
            ctSprite.tr.setRegion(.5f, .2f, 1, .3f);
        }else if(n[1] && n[5]) {
            ctSprite.tr.setRegion(.5f, 0, 1, .1f);
        }else if(n[2] && n[5]) {
            ctSprite.tr.setRegion(.5f, .4f, 1, .5f);
        }else if(n[1]) {
            ctSprite.tr.setRegion(.5f, .2f, 1, .3f);
        }else if(n[2]) {
            ctSprite.tr.setRegion(.5f, .6f, 1, .7f);
        }else if(n[5]) {
            ctSprite.tr.setRegion(.5f, .4f, 1, .5f);
        }else{
            ctSprite.tr.setRegion(.5f, .8f, 1, .9f);
        }
        //Bottom Left
        if(n[3] && n[6] && n[7]) {
            ctSprite.bl.setRegion(0, .1f, .5f, .2f);
        }else if(n[3] && n[6]) {
            ctSprite.bl.setRegion(0, .5f, .5f, .6f);
        }else if(n[3] && n[7]) {
            ctSprite.bl.setRegion(0, .1f, .5f, .2f);
        }else if(n[6] && n[7]) {
            ctSprite.bl.setRegion(0, .3f, .5f, .4f);
        }else if(n[3]) {
            ctSprite.bl.setRegion(0, .5f, .5f, .6f);
        }else if(n[6]) {
            ctSprite.bl.setRegion(0, .7f, .5f, .8f);
        }else if(n[7]) {
            ctSprite.bl.setRegion(0, .3f, .5f, .4f);
        }else{
            ctSprite.bl.setRegion(0, .9f, .5f, 1);
        }
        //Bottom Right
        if(n[5] && n[7] && n[8]) {
            ctSprite.br.setRegion(.5f, .1f, 1, .2f);
        }else if(n[5] && n[7]) {
            ctSprite.br.setRegion(.5f, .1f, 1, .2f);
        }else if(n[5] && n[8]) {
            ctSprite.br.setRegion(.5f, .5f, 1, .6f);
        }else if(n[7] && n[8]) {
            ctSprite.br.setRegion(.5f, .3f, 1, .4f);
        }else if(n[5]) {
            ctSprite.br.setRegion(.5f, .5f, 1, .6f);
        }else if(n[7]) {
            ctSprite.br.setRegion(.5f, .3f, 1, .4f);
        }else if(n[8]) {
            ctSprite.br.setRegion(.5f, .7f, 1, .8f);
        }else{
            ctSprite.br.setRegion(.5f, .9f, 1, 1);
        }
        return ctSprite;
    }

    public boolean isAirNeighbour(int x, int y, boolean[][] map) {
//        if(x < 0 || x >= map.length) return true;
//        if(y < 0 || y >= map[x].length) return true;
        try {
            return !map[x][y];
        }catch (Exception e) { return true; }
    }

    public boolean isMatchingNeighbour(boolean type, int x, int y, boolean[][] map) {
        try{
            return map[x][y] != type;
        }catch(Exception e) { return false; }
    }

}

