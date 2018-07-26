package net.ncguy.profile;

import com.badlogic.gdx.utils.Pool;
import org.lwjgl.opengl.GL15;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import static org.lwjgl.opengl.GL33.GL_TIMESTAMP;
import static org.lwjgl.opengl.GL33.glQueryCounter;

public class GPUProfiler {

    public static boolean PROFILING_ENABLED = true;
    private static boolean profilingEnabled;

    public static Pool<GPUTaskProfile> taskPool;
    public static ArrayList<Integer> queryObjects;
    public static int frameCounter;
    public static GPUTaskProfile currentTask;

    public static ArrayList<GPUTaskProfile> completedFrames;

    static {
        taskPool = new Pool<GPUTaskProfile>() {
            @Override
            protected GPUTaskProfile newObject() {
                return new GPUTaskProfile();
            }
        };
        queryObjects = new ArrayList<>();
        frameCounter = 0;
        completedFrames = new ArrayList<>();
    }

    public static void setFrameCounter(int frame) {
        frameCounter = frame;
    }

    public static void StartFrame() {
        profilingEnabled = PROFILING_ENABLED;
        if(currentTask != null)
            throw new IllegalStateException("Previous frame not ended properly");
        if(profilingEnabled) {
            int i = frameCounter++;
            currentTask = taskPool.obtain().Init(null, "Frame " + i, GetQuery(), i);
        }
    }

    public static void Start(String name) {
        if(profilingEnabled && currentTask != null)
            currentTask = taskPool.obtain().Init(currentTask, name, GetQuery(), frameCounter);
    }

    public static void End(String name) {
        End();
    }
    public static void End() {
        if(profilingEnabled && currentTask != null)
            currentTask = currentTask.end(GetQuery());
    }

    public static void EndFrame() {

        if(!profilingEnabled)
            return;

        if(currentTask.getParent() != null)
            throw new IllegalStateException("Error ending frame, not all tasks finished. Current task name: " + currentTask.name);
        currentTask.end(GetQuery());

        if(completedFrames.size() < 5)
            completedFrames.add(currentTask);
        else Recycle(currentTask);

        currentTask = null;
    }

    public static Queue<GPUTaskProfile> GetFrames() {
        Queue<GPUTaskProfile> frames = new LinkedList<>(completedFrames);
        completedFrames.clear();
        return frames;
    }

    public static GPUTaskProfile GetFrameResults() {
        if(completedFrames.isEmpty())
            return null;

        GPUTaskProfile frame = completedFrames.get(0);
        if(frame.resultsAvailable())
            return completedFrames.remove(0);

        return null;
    }

    public static void Recycle(GPUTaskProfile task) {
        queryObjects.add(task.getStartQuery());
        queryObjects.add(task.getEndQuery());

        task.getChildren().forEach(GPUProfiler::Recycle);

        taskPool.free(task);
    }

    private static int GetQuery() {
        int query;
        if (queryObjects.isEmpty()) query = GL15.glGenQueries();
        else query = queryObjects.remove(queryObjects.size() - 1);

        glQueryCounter(query, GL_TIMESTAMP);

        return query;
    }

}
