package net.ncguy.particles;

public class BurstParticleSystem extends AbstractParticleSystem {

    boolean hasSpawned = false;

    public BurstParticleSystem(int desiredAmount, float duration, String... blockNames) {
        super(desiredAmount, duration, blockNames);
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
