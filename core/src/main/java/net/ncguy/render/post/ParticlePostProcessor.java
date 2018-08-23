package net.ncguy.render.post;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import net.ncguy.post.MultiPostProcessor;
import net.ncguy.world.MainEngine;

public class ParticlePostProcessor extends MultiPostProcessor<ParticlePostProcessor> {

    ParticlePostRenderer renderer;

    public ParticlePostProcessor(MainEngine engine) {
        super(engine);
    }

    @Override
    public ParticlePostProcessor Init() {
        renderer = new ParticlePostRenderer();
        return this;
    }

    @Override
    public Texture[] _Render(Batch batch, Camera camera, Texture[] input, float delta) {
        Texture[] output = new Texture[input.length + 1];
        System.arraycopy(input, 0, output, 0, input.length);
        output[output.length - 1] = renderer.RenderToTexture(camera.combined);
        return output;
    }

    @Override
    public void Shutdown() {

    }
}
