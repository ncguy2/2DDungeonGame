package net.ncguy.profile;

import org.lwjgl.opengl.GL15;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL33.GL_TIMESTAMP;
import static org.lwjgl.opengl.GL33.glQueryCounter;

public class GPUProfiler extends AbstractProfiler<GPUTaskProfile> {

    public static ThreadLocal<GPUProfiler> instance;

    public static GPUProfiler Get() {
        if (instance == null)
            instance = ThreadLocal.withInitial(GPUProfiler::new);
        return instance.get();
    }

    public ArrayList<Integer> queryObjects;

    private GPUProfiler() {
        super();
        queryObjects = new ArrayList<>();
    }

    @Override
    public GPUTaskProfile NewProfile() {
        return new GPUTaskProfile();
    }

    @Override
    public GPUTaskProfile StartProfile(String name, GPUTaskProfile task) {
        return task.Init(currentTask, name, GetQuery(), frameCounter);
    }

    @Override
    public GPUTaskProfile EndProfile(GPUTaskProfile task) {
        return currentTask.end(GetQuery());
    }

    @Override
    public void RecycleProfile(GPUTaskProfile task) {
        queryObjects.add(task.getStartQuery());
        queryObjects.add(task.getEndQuery());
    }

    private int GetQuery() {
        int query;
        if (queryObjects.isEmpty()) query = GL15.glGenQueries();
        else query = queryObjects.remove(queryObjects.size() - 1);
        glQueryCounter(query, GL_TIMESTAMP);
        return query;
    }

}
