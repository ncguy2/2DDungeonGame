package net.ncguy.physics;

import com.badlogic.gdx.physics.box2d.*;

import java.util.Arrays;

public class PhysicsFactoryImpl extends PhysicsFactory {
    public PhysicsFactoryImpl(World world) {
        super(world);
    }

    @Override
    public Body CreateBody(BodyDef def) {
        Body body = world.createBody(def);
//        body.setUserData(new PhysicsUserObject());
        body.setUserData(PhysicsUserObject.Scriptable());
        return body;
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
