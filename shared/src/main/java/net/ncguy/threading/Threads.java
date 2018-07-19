package net.ncguy.threading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Threads {

    public static void SpinThreads(Runnable... tasks) throws InterruptedException {
        SpinThreads(0, true, tasks);
    }
    public static void SpinThreads(boolean daemon, Runnable... tasks) throws InterruptedException {
        SpinThreads(0, daemon, tasks);
    }
    public static void SpinThreads(long millisTimeout, Runnable... tasks) throws InterruptedException {
        SpinThreads(millisTimeout, true, tasks);
    }
    public static void SpinThreads(long millisTimeout, boolean daemon, Runnable... tasks) throws InterruptedException {
        Thread[] threads = SpinThreadsNoJoin(daemon, tasks);

        for (Thread thread : threads)
            thread.join(millisTimeout);
    }

    public static Thread[] SpinThreadsNoJoin(boolean daemon, Runnable... tasks) {
        Thread[] threads = new Thread[tasks.length];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(tasks[i]);
            threads[i].setDaemon(daemon);
            threads[i].start();
        }

        return threads;
    }

    ExecutorService service;
    List<Future<?>> futures;

    public Threads(int nThreads) {
        service = Executors.newFixedThreadPool(nThreads);
        futures = new ArrayList<>();
    }

    public void Invoke(Runnable... tasks) {
        for (Runnable task : tasks)
            futures.add(service.submit(task));
    }

    public void Wait() {
        while(!futures.isEmpty()) {
            futures.removeIf(Future::isDone);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        futures.clear();
    }

}
