package net.ncguy.entity.component;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class RenderComponent extends SceneComponent {

    public RenderComponent(String name) {
        super(name);
    }

    public abstract void Render(SpriteBatch batch);
    public void RenderShadow(SpriteBatch batch) {
        Render(batch);
    }

}
