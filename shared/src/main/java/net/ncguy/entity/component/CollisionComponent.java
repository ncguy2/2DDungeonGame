package net.ncguy.entity.component;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import net.ncguy.physics.worker.DestroyBodyTask;
import net.ncguy.system.PhysicsContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the box2D collision data source, and provides access to the generated body instance
 */
public class CollisionComponent extends SceneComponent {

    public transient Body body;
    public PhysicsContainer container;
    public BodyDef bodyDef;
    public List<FixtureDef> fixtureDefs;
    public String bodyRef;
    public boolean useComponentTransform;
    public boolean useOverrideVelocity;
    public Vector2 overrideVelocity = new Vector2();

    public CollisionComponent(String name) {
        super(name);
        fixtureDefs = new ArrayList<>();
    }

    @Override
    public void _OnRemoveFromComponent() {
        super._OnRemoveFromComponent();
        Body body = this.body;
        if(body != null && container != null) {
            container.foreman.Post(new DestroyBodyTask(body));
            this.body = null;
        }
    }
}
