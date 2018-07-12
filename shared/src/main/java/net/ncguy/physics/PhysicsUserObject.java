package net.ncguy.physics;

import com.badlogic.gdx.physics.box2d.ContactListener;
import net.ncguy.entity.Entity;

public class PhysicsUserObject {

    public Entity entity;
    public ContactListener listener;

    public PhysicsUserObject() {}

    public static PhysicsUserObject Scriptable() {
        PhysicsUserObject obj = new PhysicsUserObject();
        obj.listener = new ScriptableContactListener();
        return obj;
    }
}
