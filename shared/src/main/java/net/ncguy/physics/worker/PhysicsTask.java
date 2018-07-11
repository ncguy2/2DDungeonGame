package net.ncguy.physics.worker;

import com.badlogic.gdx.physics.box2d.World;
import net.ncguy.physics.PhysicsService;
import net.ncguy.threading.ThreadTask;

public abstract class PhysicsTask<T> extends ThreadTask<T, PhysicsTask> {

    public transient World world;
    public transient PhysicsService service;

    @Override
    public PhysicsTask TakeMeta(PhysicsTask target) {
        this.world = target.world;
        this.service = target.service;
        return super.TakeMeta(target);
    }

    public static abstract class VoidPhysicsTask extends PhysicsTask<Void> {

        public abstract void Task();

        @Override
        public Void Run() {
            Task();
            return null;
        }
    }

}
