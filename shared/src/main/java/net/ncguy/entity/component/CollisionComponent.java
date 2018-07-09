package net.ncguy.entity.component;

import com.badlogic.gdx.physics.box2d.Body;

/**
 * Defines the box2D collision data source, and provides access to the generated body instance
 */
public class CollisionComponent extends SceneComponent {

    public transient Body body;
    public String bodyRef;

    public CollisionComponent(String name) {
        super(name);
    }

}
