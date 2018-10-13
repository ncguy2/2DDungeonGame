package net.ncguy.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ScreenUtils;

public class TextureUtils {

    public static byte[] ToArray(Texture tex) {
        FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, tex.getWidth(), tex.getHeight(), false);
        SpriteBatch batch = new SpriteBatch();

        fbo.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, tex.getWidth(), tex.getHeight()));
        batch.begin();
        batch.draw(tex, 0, 0, tex.getWidth(), tex.getHeight());
        batch.end();

        byte[] frameBufferPixels = ScreenUtils.getFrameBufferPixels(false);
        fbo.end();

        return frameBufferPixels;
    }

}
