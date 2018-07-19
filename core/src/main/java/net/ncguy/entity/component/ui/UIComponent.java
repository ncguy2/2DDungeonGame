package net.ncguy.entity.component.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import net.ncguy.entity.component.EntityComponent;
import net.ncguy.entity.component.SceneComponent;

public class UIComponent<T extends EntityComponent> extends SceneComponent {

    protected final T targetComponent;

    public UIComponent(String name, T targetComponent) {
        super(name);
        this.targetComponent = targetComponent;
    }

    public void Render(SpriteBatch batch) {

    }

}