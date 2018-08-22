package net.ncguy;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import net.ncguy.post.PostProcessor;

import java.util.Arrays;
import java.util.List;

public class PostProcessingCamera {

    public final Camera camera;
    public final List<PostProcessor> processors;
    public Texture postProcessedTexture;

    public PostProcessingCamera(Camera camera, PostProcessor... processors) {
        this.camera = camera;
        this.processors = Arrays.asList(processors);
    }

    public Camera Camera() {
        return camera;
    }

}
