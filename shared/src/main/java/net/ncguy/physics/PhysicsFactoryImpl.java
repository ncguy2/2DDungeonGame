package net.ncguy.physics;

import com.badlogic.gdx.physics.box2d.*;

import java.util.Arrays;

public class PhysicsFactoryImpl extends PhysicsFactory {
    public PhysicsFactoryImpl(World world) {
        super(world);
    }

    @Override
    public Body CreateBody(BodyDef def) {
        return world.createBody(def);
    }

    @Override
    public Fixture CreateFixture(Body body, FixtureDef def) {
        return body.createFixture(def);
    }

    @Override
    public Fixture[] CreateFixtures(Body body, FixtureDef... def) {
        return Arrays.stream(def)
                .map(body::createFixture)
                .toArray(Fixture[]::new);
    }

    @Override
    public Joint CreateJoint(JointDef def) {
        return world.createJoint(def);
    }
}
