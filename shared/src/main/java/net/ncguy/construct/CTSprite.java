package net.ncguy.construct;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class CTSprite {

    public Sprite tl, tr, bl, br;
    private Texture texture;

    public CTSprite() {
        tl = new Sprite();
        tr = new Sprite();
        bl = new Sprite();
        br = new Sprite();
    }

    public Vector2 getPosition() {
        return new Vector2(bl.getX(), bl.getY());
    }
    public Vector2 getSize() {
        return new Vector2(bl.getWidth()*2, bl.getHeight()*2);
    }

    public CTSprite setX(float x) {
        tl.setX(x);
        tr.setX(x+tl.getWidth());
        bl.setX(x);
        br.setX(x+bl.getWidth());
        return this;
    }
    public CTSprite setY(float y) {
        tl.setY(y+bl.getHeight());
        tr.setY(y+br.getHeight());
        bl.setY(y);
        br.setY(y);
        return this;
    }
    public CTSprite setPosition(float x, float y) {
        return setX(x).setY(y);
    }
    public CTSprite setWidth(float w) {
        tl.setSize(w/2, tl.getHeight());
        tr.setSize(w/2, tr.getHeight());
        bl.setSize(w/2, bl.getHeight());
        br.setSize(w/2, br.getHeight());
        return this;
    }
    public CTSprite setHeight(float h) {
        tl.setSize(tl.getWidth(), h/2);
        tr.setSize(tr.getWidth(), h/2);
        bl.setSize(bl.getWidth(), h/2);
        br.setSize(br.getWidth(), h/2);
        return this;
    }
    public CTSprite setSize(float w, float h) {
        tl.setSize(w/2, h/2);
        tr.setSize(w/2, h/2);
        bl.setSize(w/2, h/2);
        br.setSize(w/2, h/2);
        return this;
    }
    public CTSprite setBounds(float x, float y, float w, float h) {
        return setSize(w, h).setPosition(x, y);
    }

    public void draw(Batch batch) {
        tl.draw(batch);
        tr.draw(batch);
        bl.draw(batch);
        br.draw(batch);
    }
    public void draw(Batch batch, float alphaModulation) {
        tl.draw(batch, alphaModulation);
        tr.draw(batch, alphaModulation);
        bl.draw(batch, alphaModulation);
        br.draw(batch, alphaModulation);
    }

    public CTSprite setTexture(Texture texture) {
        this.texture = texture;
        tl.set(new Sprite(texture));
        tr.set(new Sprite(texture));
        bl.set(new Sprite(texture));
        br.set(new Sprite(texture));
        return this;
    }

    public CTSprite setColour(Color colour) {
        tl.setColor(colour);
        tr.setColor(colour);
        bl.setColor(colour);
        br.setColor(colour);
        return this;
    }
    public CTSprite setColour(float colour) {
        tl.setColor(colour);
        tr.setColor(colour);
        bl.setColor(colour);
        br.setColor(colour);
        return this;
    }
    public CTSprite setColour(float r, float g, float b, float a) {
        tl.setColor(r, g, b, a);
        tr.setColor(r, g, b, a);
        bl.setColor(r, g, b, a);
        br.setColor(r, g, b, a);
        return this;
    }

    public Texture getTexture() {
        return this.texture;
    }
}

