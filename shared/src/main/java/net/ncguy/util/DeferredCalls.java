package net.ncguy.util;

import net.ncguy.script.ScriptHost;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeferredCalls {

    private static DeferredCalls instance;

    public static DeferredCalls Instance() {
        if (instance == null)
            instance = new DeferredCalls();
        return instance;
    }

    List<Call> calls;

    private DeferredCalls() {
        calls = new ArrayList<>();
        ScriptHost.AddGlobalBinding("Defer", this);
    }

    public void Post(float delay, Runnable task) {
        Post(new Call(delay, task));
    }

    public void Post(Call call) {
        calls.add(call);
    }

    public void Update(float delta) {
        if (calls.isEmpty())
            return;

        calls.stream()
                .peek(c -> c.delay -= delta)
                .filter(c -> c.delay <= 0)
                .peek(c -> c.task.run())
                .collect(Collectors.toList())
                .forEach(calls::remove);
    }

    public static class Call {
        public float delay;
        public Runnable task;

        public Call(float delay, Runnable task) {
            this.delay = delay;
            this.task = task;
        }
    }

}
