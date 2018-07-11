package net.ncguy.physics.worker;

import com.badlogic.gdx.physics.box2d.World;
import net.ncguy.physics.PhysicsService;
import net.ncguy.threading.ThreadForeman;

import java.util.concurrent.Future;

public class PhysicsForeman extends ThreadForeman<PhysicsTask<?>> {

    public World world;
    public PhysicsService service;

    public PhysicsForeman(World world, PhysicsService service) {
        this.world = world;
        this.service = service;
    }

    @Override
    public Future<?> Post(PhysicsTask<?> task) {
        task.world = world;
        task.service = service;
        return super.Post(task);
    }
}
