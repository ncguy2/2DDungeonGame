package net.ncguy.physics;

import com.badlogic.gdx.physics.box2d.*;

import java.util.Optional;

public class PhysicsContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        GetUserObject(contact.getFixtureA()).map(o -> o.listener).ifPresent(l -> l.beginContact(contact));
        GetUserObject(contact.getFixtureB()).map(o -> o.listener).ifPresent(l -> l.beginContact(contact));
    }

    @Override
    public void endContact(Contact contact) {
        GetUserObject(contact.getFixtureA()).map(o -> o.listener).ifPresent(l -> l.endContact(contact));
        GetUserObject(contact.getFixtureB()).map(o -> o.listener).ifPresent(l -> l.endContact(contact));
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        GetUserObject(contact.getFixtureA()).map(o -> o.listener).ifPresent(l -> l.preSolve(contact, oldManifold));
        GetUserObject(contact.getFixtureB()).map(o -> o.listener).ifPresent(l -> l.preSolve(contact, oldManifold));
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        GetUserObject(contact.getFixtureA()).map(o -> o.listener).ifPresent(l -> l.postSolve(contact, impulse));
        GetUserObject(contact.getFixtureB()).map(o -> o.listener).ifPresent(l -> l.postSolve(contact, impulse));
    }

    Optional<PhysicsUserObject> GetUserObject(Fixture fixture) {
        if(fixture == null)
            return Optional.empty();

        Body body = fixture.getBody();
        if(body == null)
            return Optional.empty();

        Object userData = body.getUserData();

        if(userData instanceof PhysicsUserObject)
            return Optional.of((PhysicsUserObject) userData);

        return Optional.empty();
    }
}
