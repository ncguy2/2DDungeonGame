package net.ncguy.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import net.ncguy.world.MainEngine;

public abstract class AbstractRenderer {

    protected final MainEngine engine;
    protected final SpriteBatch batch;

    public AbstractRenderer(MainEngine engine, SpriteBatch batch) {
        this.engine = engine;
        this.batch = batch;
    }

    public abstract void Render(PostProcessingCamera camera, float delta);

}
