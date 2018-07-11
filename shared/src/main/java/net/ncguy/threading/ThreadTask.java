package net.ncguy.threading;

import java.util.function.Consumer;

public abstract class ThreadTask<T, SELF extends ThreadTask> {

    int totalSteps;
    int currentStep;

    State state;

    public transient ThreadForeman foreman;

    public Consumer<T> onFinish;

    public ThreadTask() {
        this(-1);
    }

    public ThreadTask(int totalSteps) {
        this.totalSteps = totalSteps;
        this.currentStep = 0;
        this.state = State.Pending;
    }

    public float Progress() {
        if(totalSteps <= 0)
            return -1f;

        return ((float) currentStep) / ((float) totalSteps);
    }

    public void Sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public SELF TakeMeta(SELF target) {
        this.foreman = target.foreman;
        return (SELF) this;
    }

    public ThreadTask OnFinish(Consumer<T> onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    public void _OnFinish(T data) {
        if(this.onFinish != null)
            this.onFinish.accept(data);
    }

    public State State() {
        return state;
    }

    public void Execute() {
        Start();
        T data = Run();
        Finish();
        _OnFinish(data);
    }

    public void Start() {
        this.state = State.Running;
    }

    public void Finish() {
        this.state = State.Completed;
    }

    public abstract T Run();

    public static enum State {
        Pending,
        Running,
        Completed
    }

    public static abstract class VoidThreadTask extends ThreadTask<Void, VoidThreadTask> {

        public abstract void Task();

        @Override
        public Void Run() {
            Task();
            return null;
        }
    }

}
