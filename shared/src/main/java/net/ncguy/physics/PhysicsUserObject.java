package net.ncguy.physics;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ContactListener;
import net.ncguy.entity.Entity;

public class PhysicsUserObject {

    public Entity entity;
    public ContactListener listener;
    public BodyDef.BodyType bodyType;

    public PhysicsUserObject() {}

    public static PhysicsUserObject Scriptable() {
        PhysicsUserObject obj = new PhysicsUserObject();
        obj.listener = new ScriptableContactListener();
        return obj;
    }

    public static PhysicsUserObject Scriptable(Body body) {
        PhysicsUserObject obj = Scriptable();
        obj.bodyType = body.getType();
        return obj;
    }
}
