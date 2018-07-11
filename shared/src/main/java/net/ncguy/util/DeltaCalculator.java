package net.ncguy.util;

public class DeltaCalculator {

    long currentTime;
    long prevTime;

    long deltaTime;
    float delta;

    final float scaleFactor = 0.001f;

    public DeltaCalculator() {}

    public float Step() {
        currentTime = System.currentTimeMillis();
        deltaTime = currentTime - prevTime;
        prevTime = currentTime;

        return delta = deltaTime * scaleFactor;
    }

    public float Delta() {
        return this.delta;
    }

    public void Wait(long maxMillis) {
        try {
            _Wait(maxMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void _Wait(long maxMillis) throws InterruptedException {
        long sleepTime = maxMillis - deltaTime;
        long time = Math.max(1, Math.min(sleepTime, maxMillis));
        System.out.println("Sleep time: " + time);
        Thread.sleep(time);
        System.out.println("Sleep finished");
    }

}
