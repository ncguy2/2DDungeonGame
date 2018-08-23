package net.ncguy.post;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import net.ncguy.entity.component.EntityProperty;
import net.ncguy.entity.component.FieldPropertyDescriptorLite;
import net.ncguy.entity.component.IPropertyProvider;
import net.ncguy.util.ReloadableShaderProgram;
import net.ncguy.viewport.FBO;
import net.ncguy.world.MainEngine;

import java.util.Collection;

public abstract class MultiPostProcessor<T extends MultiPostProcessor> implements IPropertyProvider {

    @EntityProperty(Type = Boolean.class, Name = "Enabled", Description = "Is this post processor enabled", Category = "Post processing")
    protected boolean enabled = true;

    protected FBO frameBuffer;
    protected ReloadableShaderProgram shader;

    protected MainEngine engine;

    public MultiPostProcessor(MainEngine engine) {
        this.engine = engine;
    }

    public void Resize(int width, int height) {
        if(frameBuffer != null)
            frameBuffer.Resize(width, height);
    }

    public abstract T Init();
    public Texture[] Render(Batch batch, Camera camera, Texture[] input, float delta) {
        if(enabled)
            return _Render(batch, camera, input, delta);
        return input;
    }
    protected abstract Texture[] _Render(Batch batch, Camera camera, Texture[] input, float delta);
    public abstract void Shutdown();

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public void Provide(Collection<FieldPropertyDescriptorLite> descriptors) {
        FieldPropertyDescriptorLite.OfClass(this, descriptors);
    }
}
