package net.ncguy.entity.component;

import com.badlogic.gdx.graphics.Color;

/**
 * Provides the reference used by the client renderer to identify which texture to use when rendering the component
 */
public class PrimitiveCircleComponent extends SceneComponent {

    public final Color colour;

    public PrimitiveCircleComponent() {
        this("Unnamed Scene component");
    }

    public PrimitiveCircleComponent(String name) {
        super(name);
        colour = Color.WHITE.cpy();
    }
}
