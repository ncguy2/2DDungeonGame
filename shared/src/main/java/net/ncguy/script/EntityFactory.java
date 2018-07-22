package net.ncguy.script;


import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import net.ncguy.entity.Entity;
import net.ncguy.entity.component.CollisionComponent;
import net.ncguy.entity.component.DistortionComponent;
import net.ncguy.entity.component.SpriteComponent;
import net.ncguy.system.PhysicsSystem;
import net.ncguy.world.Engine;

/**
 * Defines a set of functions to quickly create simple entities, useful for script access
 * <br>
 * Unless specified otherwise, these functions do not add the created entity to the world
 */
public class EntityFactory {

    public final ScriptUtils host;

    public EntityFactory(ScriptUtils host) {
        this.host = host;
    }

    public ScriptUtils Host() {
        return host;
    }

    public Engine Engine() {
        return Host().Engine();
    }

    public World World() {
        return Host().World();
    }

    public PhysicsSystem PhysicsSystem() {
        return Host().PhysicsSystem();
    }

    public Entity CreateEntity() {
        return new Entity();
    }

    public Entity CreateSpriteEntity(String spriteRef) {
        Entity entity = CreateEntity();
        entity.SetRootComponent(new SpriteComponent("Root")).spriteRef = spriteRef;
        return entity;
    }

    public Entity CreateDistortionEntity(String spriteRef) {
        Entity entity = CreateEntity();
        entity.SetRootComponent(new DistortionComponent("Root")).spriteRef = spriteRef;
        return entity;
    }

    public Entity CreateCollisionEntity(Body body) {
        Entity entity = CreateEntity();
        entity.SetRootComponent(new CollisionComponent("Root")).body = body;
        return entity;
    }


}
