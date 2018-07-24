package net.ncguy.profile;

import com.badlogic.gdx.utils.Pool;
import net.ncguy.threading.ThreadUtils;

import java.io.PrintStream;
import java.util.ArrayList;

public abstract class TaskProfile<T extends TaskProfile<T>> implements Pool.Poolable {

    public int depth;
    public int frame;
    public T parent;
    public String name;
    public ArrayList<T> children;
    public StackTraceElement startLocation;
    public StackTraceElement endLocation;

    public TaskProfile() {
        children = new ArrayList<>();
    }

    protected StackTraceElement DiscoverLocation() {
        return ThreadUtils.GetFirstElementNotOfType(Thread.currentThread()
                .getStackTrace(), getClass(), ProfilerHost.class, GPUProfiler.class, CPUProfiler.class, TaskProfile.class);
    }

    protected void StartLocation() {
        startLocation = DiscoverLocation();
    }

    protected void EndLocation() {
        endLocation = DiscoverLocation();
    }

    public T Init(T parent, String name, int frameId) {
        this.parent = parent;
        this.name = name;
        this.frame = frameId;
        this.depth = 0;

        StartLocation();

        if(parent != null)
            parent.addChild((T) this);

        return (T) this;
    }

    public void addChild(T profilerTask) {
        children.add(profilerTask);
        profilerTask.depth = this.depth + 1;
    }

    public abstract boolean resultsAvailable();
    public T end() {
        EndLocation();
        return parent;
    }

    public abstract long getStartTime();
    public abstract long getEndTime();
    public abstract long getTimeTaken();

    public String getName(){
        return name;
    }

    public T getParent() {
        return parent;
    }

    public ArrayList<T> getChildren(){
        return children;
    }

    public void dump() {
        dump(System.out);
    }
    public void dump(PrintStream out) {
        dump(out, 0);
    }

    public void dump(PrintStream out, int indentation){
        for(int i = 0; i < indentation; i++){
            out.print("    ");
        }
        out.println(name + " : " + getTimeTaken()/1000 / 1000f + "ms");
        for(int i = 0; i < children.size(); i++){
            children.get(i).dump(out, indentation + 1);
        }
    }

    @Override
    public void reset() {
        children.clear();
    }
}
