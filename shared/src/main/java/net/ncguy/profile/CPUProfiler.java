package net.ncguy.profile;

import com.badlogic.gdx.utils.Pool;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class CPUProfiler {

    public static final boolean PROFILING_ENABLED = false;

    public static Pool<CPUTaskProfile> taskPool;
    public static int frameCounter;
    public static CPUTaskProfile currentTask;

    public static ArrayList<CPUTaskProfile> completedFrames;

    static {
        taskPool = new Pool<CPUTaskProfile>() {
            @Override
            protected CPUTaskProfile newObject() {
                return new CPUTaskProfile();
            }
        };
        frameCounter = 0;
        completedFrames = new ArrayList<>();
    }


    public static void StartFrame() {
        if(currentTask != null)
            throw new IllegalStateException("Previous frame not ended properly");
        if(PROFILING_ENABLED) {
            int i = frameCounter++;
            currentTask = taskPool.obtain().Init(null, "Frame " + i, i);
        }
    }

    public static void Start(String name) {
        if(PROFILING_ENABLED && currentTask != null)
            currentTask = taskPool.obtain().Init(currentTask, name, frameCounter);
    }

    public static void End(String name) {
        End();
    }
    public static void End() {
        if(PROFILING_ENABLED && currentTask != null)
            currentTask = currentTask.end();
    }

    public static void EndFrame() {

        if(!PROFILING_ENABLED)
            return;

        if(currentTask.getParent() != null)
            throw new IllegalStateException("Error ending frame, not all tasks finished. Current task name: " + currentTask.name);
        currentTask.end();

        if(completedFrames.size() < 5)
            completedFrames.add(currentTask);
        else Recycle(currentTask);

        currentTask = null;
    }

    public static Queue<CPUTaskProfile> GetFrames() {
        Queue<CPUTaskProfile> frames = new LinkedList<>(completedFrames);
        completedFrames.clear();
        return frames;
    }

    public static CPUTaskProfile GetFrameResults() {
        if(completedFrames.isEmpty())
            return null;

        CPUTaskProfile frame = completedFrames.get(0);
        if(frame.resultsAvailable())
            return completedFrames.remove(0);

        return null;
    }

    public static void Recycle(CPUTaskProfile task) {
        task.getChildren().forEach(CPUProfiler::Recycle);

        taskPool.free(task);
    }
}
