package net.ncguy.entity.component;

import com.badlogic.gdx.math.Vector2;

/**
 * Controls the movement offset of the attached transform, is reset between each update
 */
public class MovementComponent extends EntityComponent {

    public final Vector2 velocity = new Vector2();
    @EntityProperty(Type = Float.class, Category = "Movement", Description = "Movement speed of the component", Name = "Movement speed")
    public float movementSpeed = 370;
    @EntityProperty(Type = Boolean.class, Category = "Movement", Description = "Should the velocity be reset after being used", Name = "Reset after check")
    public boolean resetAfterCheck = false;

    public MovementComponent() {
        this("Unnamed Scene component");
    }

    public MovementComponent(String name) {
        super(name);
    }

}
