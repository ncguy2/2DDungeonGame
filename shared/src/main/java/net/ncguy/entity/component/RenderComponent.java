package net.ncguy.entity.component;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class RenderComponent extends SceneComponent {

    public RenderComponent(String name) {
        super(name);
    }

    public void _Render(SpriteBatch batch) {
        if(enabled)
            Render(batch);
    }
    public abstract void Render(SpriteBatch batch);
    public void RenderShadow(SpriteBatch batch) {
        _Render(batch);
    }

}
