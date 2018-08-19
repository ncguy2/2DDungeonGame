package net.ncguy.profile;

import com.badlogic.gdx.utils.Pool;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public abstract class AbstractProfiler<TASK extends TaskProfile<TASK>> {

    public Pool<TASK> taskPool;
    public int frameCounter;
    public TASK currentTask;
    public ArrayList<TASK> completedFrames;

    public boolean profilingEnabled;

    public AbstractProfiler() {
        taskPool = new Pool<TASK>() {
            @Override
            protected TASK newObject() {
                return NewProfile();
            }
        };
        completedFrames = new ArrayList<>();
        frameCounter = 0;
    }

    public abstract TASK NewProfile();
    public abstract TASK StartProfile(String name, TASK task);
    public abstract TASK EndProfile(TASK task);
    public abstract void RecycleProfile(TASK task);

    public void setFrameCounter(int frame) {
        frameCounter = frame;
    }

    public void StartFrame() {
        if (currentTask != null)
            throw new IllegalStateException("Previous frame not ended properly");
        if (profilingEnabled) {
            currentTask = StartProfile("Frame " + frameCounter, taskPool.obtain());
            frameCounter++;
        }
    }

    public void EndFrame() {

        if(!profilingEnabled)
            return;

        if(currentTask.getParent() != null)
            throw new IllegalStateException("Error ending frame, not all tasks finished. Current task name: " + currentTask.name);
        EndProfile(currentTask);
//        currentTask.end(GetQuery());

        if(completedFrames.size() < 5)
            completedFrames.add(currentTask);
        else Recycle(currentTask);

        currentTask = null;
    }

    public void Start(String name) {
        if(profilingEnabled && currentTask != null)
            currentTask = StartProfile(name, taskPool.obtain());
    }

    public void End(String name) {
        End();
    }
    public void End() {
        if(profilingEnabled && currentTask != null)
            currentTask = EndProfile(currentTask);
    }

    public Queue<TASK> GetFrames() {
        Queue<TASK> frames = new LinkedList<>(completedFrames);
        completedFrames.clear();
        return frames;
    }

    public TASK GetFrameResults() {
        if(completedFrames.isEmpty())
            return null;

        TASK frame = completedFrames.get(0);
        if(frame.resultsAvailable())
            return completedFrames.remove(0);

        return null;
    }

    public void Recycle(TASK task) {
        RecycleProfile(task);
        task.getChildren().forEach(this::Recycle);
        taskPool.free(task);
    }

}
