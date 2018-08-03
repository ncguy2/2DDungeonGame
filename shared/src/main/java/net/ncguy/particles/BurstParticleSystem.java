package net.ncguy.particles;

import com.badlogic.gdx.files.FileHandle;

public class BurstParticleSystem extends AbstractParticleSystem {

    boolean hasSpawned = false;

    public BurstParticleSystem(int desiredAmount, float duration) {
        super(desiredAmount, duration);
    }

    public BurstParticleSystem(int desiredAmount, FileHandle spawnHandle, FileHandle updateHandle, float duration) {
        super(desiredAmount, spawnHandle, updateHandle, duration);
    }

    @Override
    public void Update(float delta) {
        if(!hasSpawned) {
            Spawn(0, desiredAmount);
            hasSpawned = true;
        }
        super.Update(delta);
    }

    @Override
    public void Reset() {
        super.Reset();
        hasSpawned = false;
    }
}
