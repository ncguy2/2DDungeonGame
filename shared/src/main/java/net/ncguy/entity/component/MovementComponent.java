package net.ncguy.entity.component;

import com.badlogic.gdx.math.Vector2;

/**
 * Controls the movement offset of the attached transform, is reset between each update
 */
public class MovementComponent extends EntityComponent {

    public final Vector2 velocity = new Vector2();
    public float movementSpeed = 370;

    public MovementComponent(String name) {
        super(name);
    }

}
