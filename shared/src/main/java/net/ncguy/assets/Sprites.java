package net.ncguy.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Sprites {

    protected static Sprite pixel;
    public static Sprite Pixel() {
        if (pixel == null) {
            Pixmap map = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            map.setColor(Color.WHITE);
            map.drawPixel(0, 0);
            pixel = new Sprite(new Texture(map));
            map.dispose();
        }
        return pixel;
    }

    protected static Sprite defTex;
    public static Sprite Default() {
        if (defTex == null) {
            Pixmap map = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
            map.setColor(Color.BLACK);
            map.drawRectangle(0, 0, 2, 2);
            map.setColor(Color.MAGENTA);
            map.drawPixel(1, 0);
            map.drawPixel(0, 1);
            defTex = new Sprite(new Texture(map));
            map.dispose();
        }
        return defTex;
    }

    protected static Sprite ball;
    public static Sprite Ball() {
        if (ball == null) {
            Pixmap map = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
            map.setColor(Color.WHITE);
            map.fillCircle(31, 31, 32);
            ball = new Sprite(new Texture(map));
            map.dispose();
        }
        return ball;
    }

    public static void Dispose() {
        if(pixel != null) {
            pixel.getTexture().dispose();
            pixel = null;
        }
        if(defTex != null) {
            defTex.getTexture().dispose();
            defTex = null;
        }
        if(ball != null) {
            ball.getTexture().dispose();
            ball = null;
        }
    }

}
