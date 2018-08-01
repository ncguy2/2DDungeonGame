package net.ncguy.world;

import net.ncguy.system.BaseSystem;
import net.ncguy.util.DeltaCalculator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadedEngine extends Engine {

    public static final List<WeakReference<ThreadedEngine>> registry = new ArrayList<>();

    public boolean alive;
    DeltaCalculator deltaTimer;
    ScheduledThreadPoolExecutor executor;

    public ThreadedEngine() {
        super();
        registry.add(new WeakReference<>(this));
        deltaTimer = new DeltaCalculator();
    }

    public void Schedule() {
        if(alive)
            executor.schedule(this::ScheduleStep, 16, TimeUnit.MILLISECONDS);
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
}
