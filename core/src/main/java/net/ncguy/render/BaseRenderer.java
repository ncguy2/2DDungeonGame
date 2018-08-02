package net.ncguy.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import net.ncguy.viewport.FBO;
import net.ncguy.viewport.FBOBuilder;
import net.ncguy.world.MainEngine;

public abstract class BaseRenderer {

    protected MainEngine engine;
    protected SpriteBatch batch;
    protected Camera camera;
    protected FBO screenBuffer;

    public BaseRenderer(MainEngine engine, SpriteBatch batch, Camera camera) {
        this.engine = engine;
        this.batch = batch;
        this.camera = camera;
        screenBuffer = FBOBuilder.BuildScreenBuffer(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public Texture GetTexture() {
        return screenBuffer.getColorBufferTexture();
    }

    public boolean ShouldFlipTexture() {
        return true;
    }

    public void Resize(int width, int height) {
        screenBuffer.Resize(width, height);
    }

    public abstract void Render(float delta);

}
