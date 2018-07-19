package net.ncguy.viewport;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;

import static com.badlogic.gdx.graphics.GL20.GL_RGB;

public class FBOBuilder {

    public static FBO BuildDefaultGBuffer(int width, int height) {
        FBO.Builder fboBuilder = new FBO.Builder(width, height);

        fboBuilder.addBasicColorTextureAttachment(Pixmap.Format.RGBA8888); // Diffuse
        fboBuilder.addBasicColorTextureAttachment(Pixmap.Format.RGBA8888); // Normal
        fboBuilder.addBasicColorTextureAttachment(Pixmap.Format.RGBA8888); // Emissive
        fboBuilder.addBasicColorTextureAttachment(Pixmap.Format.RGBA8888); // RG: Texture Coordinates, B: Occlusion

//        fboBuilder.addDepthRenderBuffer(GL30.GL_DEPTH_COMPONENT32F);
        fboBuilder.addBasicDepthRenderBuffer();
        return fboBuilder.Build();
    }

    public static FBO BuildLightingBuffer(int width, int height) {
        FBO.Builder fboBuilder = new FBO.Builder(width, height);
        fboBuilder.addColorTextureAttachment(GL30.GL_RGB32F, GL_RGB, GL20.GL_FLOAT); // Lighting
        fboBuilder.addBasicDepthRenderBuffer();
        return fboBuilder.Build();
    }

    public static FBO BuildScreenBuffer(int width, int height) {
        FBO.Builder fboBuilder = new FBO.Builder(width, height);
        fboBuilder.addBasicColorTextureAttachment(Pixmap.Format.RGBA8888);
        fboBuilder.addBasicDepthRenderBuffer();
        return fboBuilder.Build();
    }

}
