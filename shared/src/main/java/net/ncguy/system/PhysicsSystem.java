package net.ncguy.system;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import net.ncguy.entity.Entity;
import net.ncguy.entity.component.CollisionComponent;
import net.ncguy.entity.component.MovementComponent;
import net.ncguy.world.EntityWorld;

import java.util.List;

public class PhysicsSystem extends BaseSystem {

    public static float screenToPhysics = .01f;
    public static float physicsToScreen = 100f;

    World collisionWorld;

    public PhysicsSystem(EntityWorld operatingWorld) {
        super(operatingWorld);
    }

    @Override
    public void Startup() {
        collisionWorld = new World(Vector2.Zero, true);
    }

    @Override
    public void Update(float delta) {

        // Pre-Step
        List<Entity> entities = operatingWorld.GetFlattenedEntitiesWithComponents(CollisionComponent.class, MovementComponent.class);
        for (Entity entity : entities) {
            CollisionComponent collision = entity.GetComponent(CollisionComponent.class, true);
            MovementComponent movement = entity.GetComponent(MovementComponent.class, true);

            collision.body.setLinearVelocity(movement.velocity.scl(screenToPhysics));

            movement.velocity.setZero();
        }

        DoPhysicsStep(delta);
    }

    @Override
    public void Shutdown() {
        collisionWorld.dispose();
        collisionWorld = null;
    }

    private float accumulator = 0;

    private void DoPhysicsStep(float deltaTime) {
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        float timeStep = 1f / 45f;
        while (accumulator >= timeStep) {
            collisionWorld.step(timeStep, 6, 2);
            accumulator -= timeStep;
        }
    }

    public World World() {
        return collisionWorld;
    }
}
