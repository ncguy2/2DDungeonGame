package net.ncguy.entity.component;

import com.badlogic.gdx.graphics.Color;

public class LightComponent extends SceneComponent {

    @EntityProperty(Type = Color.class, Category = "Light", Description = "Light colour", Name = "Light colour")
    public Color colour;
    @EntityProperty(Type = Float.class, Category = "Light", Description = "Light radius", Name = "Light radius")
    public float radius;

    public LightComponent(String name) {
        super(name);
        colour = Color.WHITE.cpy();
        radius = 1000;
    }
}
