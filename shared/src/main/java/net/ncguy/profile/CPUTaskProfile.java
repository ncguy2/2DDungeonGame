package net.ncguy.profile;

public class CPUTaskProfile extends TaskProfile<CPUTaskProfile> {

    public long startTime, endTime;

    public CPUTaskProfile() {
        super();
    }

    public CPUTaskProfile Init(CPUTaskProfile parent, String name, int frameId) {
        CPUTaskProfile init = super.Init(parent, name, frameId);
        this.startTime = System.nanoTime();
        return init;
    }

    @Override
    public CPUTaskProfile end() {
        this.endTime = System.nanoTime();
        return super.end();
    }

    @Override
    public boolean resultsAvailable() {
        return endTime > 0;
    }

    @Override
    public long getStartTime(){
        return startTime;
    }

    @Override
    public long getEndTime(){
        return endTime;
    }

    @Override
    public long getTimeTaken(){
        return getEndTime() - getStartTime();
    }

    @Override
    public void reset() {
        super.reset();
        startTime = -1;
        endTime = -1;
    }

}
