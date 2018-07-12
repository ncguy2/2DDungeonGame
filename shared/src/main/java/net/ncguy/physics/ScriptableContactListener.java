package net.ncguy.physics;

import com.badlogic.gdx.physics.box2d.*;

import javax.script.ScriptException;
import javax.swing.tree.DefaultTreeCellEditor;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ScriptableContactListener implements ContactListener {

    public Consumer<Contact> BeginContact;
    public Consumer<Contact> EndContact;
    public BiConsumer<Contact, Manifold> PreSolve;
    public BiConsumer<Contact, ContactImpulse> PostSolve;

    @Override
    public void beginContact(Contact contact) {
        try {
            CallBeginContact(contact);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void endContact(Contact contact) {
        try {
            CallEndContact(contact);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        try {
            CallPreSolve(contact, oldManifold);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        try {
            CallPostSolve(contact, impulse);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }
    
    void CallBeginContact(Contact contact) throws ScriptException {
        if(BeginContact != null)
            BeginContact.accept(contact);
    }

    void CallEndContact(Contact contact) throws ScriptException {
        if(EndContact != null)
            EndContact.accept(contact);
    }

    void CallPreSolve(Contact contact, Manifold manifold) throws ScriptException {
        if(PreSolve != null)
            PreSolve.accept(contact, manifold);
    }

    void CallPostSolve(Contact contact, ContactImpulse impulse) throws ScriptException {
        if(PostSolve != null)
            PostSolve.accept(contact, impulse);
    }
    
}
