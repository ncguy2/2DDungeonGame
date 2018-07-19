package net.ncguy.system;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;
import net.ncguy.entity.Entity;
import net.ncguy.entity.component.CollisionComponent;
import net.ncguy.entity.component.MovementComponent;
import net.ncguy.physics.PhysicsService;
import net.ncguy.physics.worker.PhysicsForeman;
import net.ncguy.physics.worker.SpawnEntityTask;
import net.ncguy.world.EntityWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PhysicsSystem extends BaseSystem {

    public static float screenToPhysics = .01f;
    public static float physicsToScreen = 100f;

//    World collisionWorld;
//    PhysicsService service;
//    PhysicsForeman foreman;
//    PhysicsContactListener listener;

    @Deprecated
    transient PhysicsContainer overworldContainer;

    List<PhysicsContainer> physicsContainers;

    public PhysicsSystem(EntityWorld operatingWorld) {
        super(operatingWorld);
    }

    @Override
    public void Startup() {
        physicsContainers = new ArrayList<>();
//        collisionWorld = new World(Vector2.Zero, true);
//        listener = new PhysicsContactListener();
//        collisionWorld.setContactListener(listener);
//        service = new PhysicsServiceImpl(collisionWorld);
//        foreman = new PhysicsForeman(collisionWorld, service);

        PhysicsContainer overworldContainer = new PhysicsContainer().Name("Overworld");
        this.overworldContainer = overworldContainer;
        overworldContainer.preStep = this::PreStep;
        overworldContainer.postStep = this::PostStep;
        overworldContainer.Startup();
        physicsContainers.add(overworldContainer);
    }

    void PreStep(PhysicsContainer container) {
        // Pre-Step
        List<Entity> entities = operatingWorld.GetFlattenedEntitiesWithComponents(CollisionComponent.class, MovementComponent.class);
        for (Entity entity : entities) {
            CollisionComponent collision = entity.GetComponent(CollisionComponent.class, true);

            if (!container.WorldContains(collision))
                continue;

            MovementComponent movement = entity.GetComponent(MovementComponent.class, true);

            if(collision.body != null)
                collision.body.setLinearVelocity(movement.velocity.scl(screenToPhysics));

            movement.velocity.setZero();
        }
    }

    void PostStep(PhysicsContainer container) {
        List<Entity> entities = operatingWorld.GetFlattenedEntitiesWithComponents(CollisionComponent.class);
        for (Entity entity : entities) {
            CollisionComponent collision = entity.GetComponent(CollisionComponent.class, true);
            if (!container.WorldContains(collision))
                continue;
            Transform transform = collision.body.getTransform();
            collision.transform.translation.set(transform.getPosition())
                    .scl(physicsToScreen);
            collision.transform.rotationDegrees = (float) Math.toDegrees(transform.getRotation());
        }
    }

    public Optional<PhysicsContainer> GetContainer(String name) {
        return physicsContainers.stream().filter(c -> c.name.equalsIgnoreCase(name)).findFirst();
    }
    public Optional<PhysicsContainer> GetContainer(UUID uuid) {
        return physicsContainers.stream().filter(c -> c.uuid.equals(uuid)).findFirst();
    }

    public Optional<PhysicsContainer> GetContainer(Body body) {
        return physicsContainers.stream().filter(c -> c.WorldContains(body)).findFirst();
    }

    public void Migrate(Entity entity, PhysicsContainer to) {
        List<CollisionComponent> cols = entity.GetComponents(CollisionComponent.class, true);
        for (CollisionComponent col : cols) {
            for (int i = 0; i < col.fixtureDefs.size(); i++)
                col.fixtureDefs.get(i).shape = col.body.getFixtureList().get(i).getShape();
            GetContainer(col.body).ifPresent(c -> c.service.QueueRemoveBody(col.body));
            col.body = null;
            to.foreman.Post(new SpawnEntityTask(col.bodyDef, col.fixtureDefs.toArray(new FixtureDef[0])));
        }
    }

    @Override
    public void Update(float delta) {
        Runnable[] tasks = physicsContainers.stream()
                .map(c -> (Runnable) () -> c.Update(delta))
                .toArray(Runnable[]::new);

        for (Runnable task : tasks) {
            task.run();
        }

//        try {
//            SpinThreads(tasks);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void Shutdown() {
        physicsContainers.forEach(PhysicsContainer::Shutdown);
        physicsContainers.clear();
    }

    @Deprecated
    public World World() {
        return overworldContainer.world;
    }

    @Deprecated
    public PhysicsService Service() {
        return overworldContainer.service;
    }

    @Deprecated
    public PhysicsForeman Foreman() {
        return overworldContainer.foreman;
    }

}