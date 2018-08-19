package net.ncguy.profile;

public class CPUProfiler extends AbstractProfiler<CPUTaskProfile> {

    public static ThreadLocal<CPUProfiler> instance;

    public static CPUProfiler Get() {
        if(instance == null)
            instance = ThreadLocal.withInitial(CPUProfiler::new);
        return instance.get();
    }

    public CPUProfiler() {
        super();
    }

    @Override
    public CPUTaskProfile NewProfile() {
        return new CPUTaskProfile();
    }

    @Override
    public CPUTaskProfile StartProfile(String name, CPUTaskProfile task) {
        return task.Init(currentTask, name, frameCounter);
    }

    @Override
    public CPUTaskProfile EndProfile(CPUTaskProfile task) {
        return task.end();
    }

    @Override
    public void RecycleProfile(CPUTaskProfile task) {
        // NOOP
    }

}
