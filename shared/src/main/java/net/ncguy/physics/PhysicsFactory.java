package net.ncguy.physics;

import com.badlogic.gdx.physics.box2d.*;

public abstract class PhysicsFactory {

    protected World world;

    public PhysicsFactory(World world) {
        this.world = world;
    }

    public abstract Body CreateBody(BodyDef def);
    public abstract Fixture CreateFixture(Body body, FixtureDef def);
    public abstract Fixture[] CreateFixtures(Body body, FixtureDef... def);
    public abstract Joint CreateJoint(JointDef def);

}
