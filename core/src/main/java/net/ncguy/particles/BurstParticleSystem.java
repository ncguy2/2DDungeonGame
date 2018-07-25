package net.ncguy.particles;

public class BurstParticleSystem extends AbstractParticleSystem {

    boolean hasSpawned = false;

    public BurstParticleSystem(int desiredAmount) {
        super(desiredAmount);
    }

    @Override
    public void Update(float delta) {
        if(!hasSpawned) {
            Spawn(desiredAmount);
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
