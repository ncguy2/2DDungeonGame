package net.ncguy.entity.component;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the box2D collision data source, and provides access to the generated body instance
 */
public class CollisionComponent extends SceneComponent {

    public transient Body body;
    public BodyDef bodyDef;
    public List<FixtureDef> fixtureDefs;
    public String bodyRef;

    public CollisionComponent(String name) {
        super(name);
        fixtureDefs = new ArrayList<>();
    }

}
