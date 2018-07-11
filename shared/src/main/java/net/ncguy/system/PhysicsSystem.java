package net.ncguy.system;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;
import net.ncguy.entity.Entity;
import net.ncguy.entity.component.CollisionComponent;
import net.ncguy.entity.component.MovementComponent;
import net.ncguy.physics.PhysicsService;
import net.ncguy.physics.PhysicsServiceImpl;
import net.ncguy.physics.worker.PhysicsForeman;
import net.ncguy.world.EntityWorld;

import java.util.List;

public class PhysicsSystem extends BaseSystem {

    public static float screenToPhysics = .01f;
    public static float physicsToScreen = 100f;

    World collisionWorld;
    PhysicsService service;
    PhysicsForeman foreman;

    public PhysicsSystem(EntityWorld operatingWorld) {
        super(operatingWorld);
    }

    @Override
    public void Startup() {
        collisionWorld = new World(Vector2.Zero, true);
        service = new PhysicsServiceImpl(collisionWorld);
        foreman = new PhysicsForeman(collisionWorld, service);
    }

    @Override
    public void Update(float delta) {
        service.Produce();

        // Pre-Step
        List<Entity> entities = operatingWorld.GetFlattenedEntitiesWithComponents(CollisionComponent.class, MovementComponent.class);
        for (Entity entity : entities) {
            CollisionComponent collision = entity.GetComponent(CollisionComponent.class, true);
            MovementComponent movement = entity.GetComponent(MovementComponent.class, true);

            if(collision.body != null)
                collision.body.setLinearVelocity(movement.velocity.scl(screenToPhysics));

            movement.velocity.setZero();
        }

        DoPhysicsStep(delta);

        // Post-Step
        for (Entity entity : entities) {
            CollisionComponent collision = entity.GetComponent(CollisionComponent.class, true);
            if(collision.body == null) continue;
            Transform transform = collision.body.getTransform();
            collision.transform.translation.set(transform.getPosition()).scl(physicsToScreen);
            collision.transform.rotationDegrees = (float) Math.toDegrees(transform.getRotation());
        }

        service.Remove();
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

    public PhysicsService Service() {
        return service;
    }

    public PhysicsForeman Foreman() {
        return foreman;
    }

}
