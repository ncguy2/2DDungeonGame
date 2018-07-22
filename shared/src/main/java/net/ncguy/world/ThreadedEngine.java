package net.ncguy.world;

import net.ncguy.system.BaseSystem;
import net.ncguy.util.DeltaCalculator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadedEngine {

    public static final List<WeakReference<ThreadedEngine>> registry = new ArrayList<>();

    public final List<BaseSystem> systems;
    public boolean alive;
    DeltaCalculator deltaTimer;
    ScheduledThreadPoolExecutor executor;

    public ThreadedEngine() {
        super();
        registry.add(new WeakReference<>(this));
        systems = new ArrayList<>();
        deltaTimer = new DeltaCalculator();
    }

    public void Schedule() {
        if(alive)
            executor.schedule(this::ScheduleStep, 20, TimeUnit.MILLISECONDS);
    }
    public void ScheduleStep() {
        run();
        Schedule();
    }

    public void start() {
        alive = true;
        executor = new ScheduledThreadPoolExecutor(1);
        Schedule();
    }

    public void run() {
        final float delta = deltaTimer.Step();
        Update(delta);
    }

    public void Shutdown() {
        alive = false;
        executor.shutdownNow();

        systems.forEach(BaseSystem::Shutdown);
        systems.clear();
    }

    public void Update(final float delta) {
        systems.forEach(sys -> sys.Update(delta));
    }

    public void AddSystem(BaseSystem system) {
        system.Startup();
        this.systems.add(system);
    }

    public void RemoveSystem(BaseSystem system) {
        this.systems.remove(system);
        system.Shutdown();
    }
}
