package net.ncguy.system;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import net.ncguy.entity.component.CollisionComponent;
import net.ncguy.physics.PhysicsContactListener;
import net.ncguy.physics.PhysicsService;
import net.ncguy.physics.PhysicsServiceImpl;
import net.ncguy.physics.worker.PhysicsForeman;

import java.util.UUID;
import java.util.function.Consumer;

public class PhysicsContainer {

    public String name;
    public UUID uuid;
    public World world;
    private final Vector2 gravity;
    public PhysicsService service;
    public PhysicsForeman foreman;

    public Consumer<PhysicsContainer> preStep;
    public Consumer<PhysicsContainer> postStep;

    public PhysicsContainer() {
        this(UUID.randomUUID(), Vector2.Zero);
    }
    public PhysicsContainer(Vector2 gravity) {
        this(UUID.randomUUID(), gravity);
    }

    public PhysicsContainer(UUID uuid) {
        this(uuid, Vector2.Zero);
    }

    public PhysicsContainer(UUID uuid, Vector2 gravity) {
        this.uuid = uuid;
        this.gravity = gravity;
        this.world = new World(gravity, true);
        world.setContactListener(new PhysicsContactListener());
        service = new PhysicsServiceImpl(world);
        foreman = new PhysicsForeman(world, service);
    }

    public String Name() {
        return name;
    }

    public PhysicsContainer Name(String name) {
        this.name = name;
        return this;
    }

    public void Update(float delta) {
        System.out.println("PhysicsContainer.Update 1");
        service.Execute();
        System.out.println("PhysicsContainer.Update 2");
        service.Remove();
        System.out.println("PhysicsContainer.Update 3");
        service.Produce();
        System.out.println("PhysicsContainer.Update 4");

        if(preStep != null)
            preStep.accept(this);
        System.out.println("PhysicsContainer.Update 5");

        DoPhysicsStep(delta);

        System.out.println("PhysicsContainer.Update 9");

        if(postStep != null)
            postStep.accept(this);
        System.out.println("PhysicsContainer.Update 10");
    }

    private float accumulator = 0;
    private void DoPhysicsStep(float deltaTime) {
        System.out.println("PhysicsContainer.Update 6");
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        float timeStep = 1f / 45f;
        System.out.println("PhysicsContainer.Update 7");
        int step = 0;
        while (accumulator >= timeStep) {
            System.out.println("PhysicsContainer.Update 8.1." + (++step));
            try {
                world.step(timeStep, 6, 2);
            }catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("PhysicsContainer.Update 8.2." + (++step));
            accumulator -= timeStep;
        }
    }

    public void Startup() {
        this.world = new World(gravity, true);
        world.setContactListener(new PhysicsContactListener());
        service = new PhysicsServiceImpl(world);
        foreman = new PhysicsForeman(world, service);
    }

    public void Shutdown() {
        world.dispose();
        world = null;
    }

    public boolean WorldContains(Body body) {
        return body.getWorld().equals(this.world);
    }

    // Helper
    public boolean WorldContains(CollisionComponent component) {
        if(component.body == null)
            return false;
        return WorldContains(component.body);
    }

}