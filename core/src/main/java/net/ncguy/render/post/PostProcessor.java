package net.ncguy.render.post;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import net.ncguy.util.ReloadableShader;
import net.ncguy.viewport.FBO;
import net.ncguy.world.Engine;

public abstract class PostProcessor {

    FBO frameBuffer;
    ReloadableShader shader;

    Engine engine;

    public PostProcessor(Engine engine) {
        this.engine = engine;
    }

    public void Resize(int width, int height) {
        if(frameBuffer != null)
            frameBuffer.Resize(width, height);
    }

    public abstract void Init();
    public abstract Texture Render(Batch batch, Camera camera, Texture input, float delta);
    public abstract void Shutdown();

}