package net.ncguy.viewport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.badlogic.gdx.graphics.glutils.FrameBuffer.FrameBufferBuilder;

public class FBO implements Disposable, StackableFBO {

    private final Builder bufferBuilder;
    private FrameBuffer buffer;
    protected String name = UUID.randomUUID().toString();

    public FBO(Builder bufferBuilder) {
        this.bufferBuilder = bufferBuilder;
        Build();
    }

    public FBO(Pixmap.Format format, int width, int height, boolean hasDepth) {
        this(format, width, height, hasDepth, false);
    }

    public FBO(Pixmap.Format format, int width, int height, boolean hasDepth, boolean hasStencil) {
        Builder frameBufferBuilder = new Builder(width, height);
        frameBufferBuilder.addBasicColorTextureAttachment(format);
        if (hasDepth) frameBufferBuilder.addBasicDepthRenderBuffer();
        if (hasStencil) frameBufferBuilder.addBasicStencilRenderBuffer();
        this.bufferBuilder = frameBufferBuilder;
        Build();
    }

    public FBO Name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String Name() {
        return this.name;
    }

    public FBO Resize(int width, int height) {

        if(width == 0 || height == 0) return this;


        if(buffer != null) {

            if(width == buffer.getWidth() && height == buffer.getHeight()) return this;

            buffer.dispose();
            buffer = null;
        }

        bufferBuilder.Resize(width, height);
        buffer = bufferBuilder.build();
        return this;
    }

    /** To allow for drop-in replacements */
    public void begin() {
        Begin();
    }
    /** To allow for drop-in replacements */
    public void end() {
        End();
    }

    /** To allow for drop-in replacements */
    public void beginImpl() {
        Buffer().ifPresent(GLFrameBuffer::begin);
    }
    /** To allow for drop-in replacements */
    public void endImpl() {
        Buffer().ifPresent(GLFrameBuffer::end);
    }

    public int getWidth() {
        return bufferBuilder.Width();
    }
    public int getHeight() {
        return bufferBuilder.Height();
    }

    public int getFramebufferHandle() {
        return Buffer().map(FrameBuffer::getFramebufferHandle).orElse(0);
    }

    public FBO Build() {
        return Resize(bufferBuilder.Width(), bufferBuilder.Height());
    }

    public Optional<FrameBuffer> Buffer() {
        return Optional.ofNullable(buffer);
    }

    public Texture getColorBufferTexture() {
        return GetColourBufferTexture().orElse(null);
    }

    public Optional<Texture> GetColourBufferTexture() {
        return Buffer().map(GLFrameBuffer::getColorBufferTexture);
    }

    public Optional<Texture[]> GetTextures() {
        return Buffer().map(FrameBuffer::getTextureAttachments).map(o -> o.toArray(Texture.class));
    }

    public List<Texture> getTextureAttachments() {

        ArrayList<Texture> texArr = new ArrayList<>();
        if(!GetTextures().isPresent())
            return texArr;

        Texture[] textures = GetTextures().get();
        for (Texture texture : textures) {
            if(texture != null)
                texArr.add(texture);
        }

        return texArr;
    }

    public void Begin() {
        FBOStack.Push(this);
    }

    public void End() {
        FBOStack.Pop();
    }

    public int getDepthBufferHandle() {
        return buffer.getDepthBufferHandle();
    }

    @Override
    public void dispose() {
        if(buffer != null) {
            buffer.dispose();
            buffer = null;
        }
    }

    @Override
    public void BeginFBO() {
        beginImpl();
    }

    @Override
    public void EndFBO() {
        endImpl();
    }

    public void clear(float r, float g, float b, float a, boolean clearDepth) {
        Gdx.gl.glClearColor(r, g, b, a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | (clearDepth ? GL20.GL_DEPTH_BUFFER_BIT : 0));
    }

    public static class Builder extends FrameBufferBuilder {
        public Builder(int width, int height) {
            super(width, height);
        }

        public void Resize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int Width() {
            return width;
        }

        public int Height() {
            return height;
        }

        public FBO Build() {
            return new FBO(this);
        }

    }

}
