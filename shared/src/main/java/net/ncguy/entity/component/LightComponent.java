package net.ncguy.entity.component;

import com.badlogic.gdx.graphics.Color;

public class LightComponent extends SceneComponent {

    public Color colour;
    public float radius;

    public LightComponent(String name) {
        super(name);
        colour = Color.WHITE.cpy();
        radius = 1000;
    }
}
