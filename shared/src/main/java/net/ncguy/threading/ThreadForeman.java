package net.ncguy.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadForeman<T extends ThreadTask<?, ?>> {

    ExecutorService executor;

    public ThreadForeman() {
        this(Runtime.getRuntime().availableProcessors());
    }

    public ThreadForeman(int parallelism) {
        executor = Executors.newWorkStealingPool(parallelism);
    }

    public Future<?> Post(T task) {
        task.foreman = this;
        return executor.submit(task::Execute);
    }

}
