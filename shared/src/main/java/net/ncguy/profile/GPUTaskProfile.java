package net.ncguy.profile;

import org.lwjgl.opengl.GL15;

import static com.badlogic.gdx.graphics.GL20.GL_TRUE;
import static org.lwjgl.opengl.GL15.GL_QUERY_RESULT;
import static org.lwjgl.opengl.GL15.glGetQueryObjectui;
import static org.lwjgl.opengl.GL33.glGetQueryObjectui64;

public class GPUTaskProfile extends TaskProfile<GPUTaskProfile> {

    public int startQuery, endQuery;

    public GPUTaskProfile() {
        super();
    }

    public GPUTaskProfile Init(GPUTaskProfile parent, String name, int startQuery, int frameId) {
        GPUTaskProfile init = super.Init(parent, name, frameId);
        init.startQuery = startQuery;
        return init;
    }

    public GPUTaskProfile end(int endQuery) {
        this.endQuery = endQuery;
        return super.end();
    }

    @Override
    public boolean resultsAvailable() {
        return glGetQueryObjectui(endQuery, GL15.GL_QUERY_RESULT_AVAILABLE) == GL_TRUE;
    }

    @Override
    @Deprecated
    public GPUTaskProfile end() {
        return super.end();
    }

    public int getStartQuery(){
        return startQuery;
    }

    public int getEndQuery(){
        return endQuery;
    }

    @Override
    public long getStartTime(){
        return glGetQueryObjectui64(startQuery, GL_QUERY_RESULT);
    }

    @Override
    public long getEndTime(){
        return glGetQueryObjectui64(endQuery, GL_QUERY_RESULT);
    }
    @Override
    public long getTimeTaken(){
        return getEndTime() - getStartTime();
    }

    @Override
    public void reset() {
        super.reset();
        startQuery = -1;
        endQuery = -1;
    }

}
