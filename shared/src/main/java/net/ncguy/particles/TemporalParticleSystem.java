package net.ncguy.particles;

import net.ncguy.profile.ProfilerHost;

public class TemporalParticleSystem extends AbstractParticleSystem {

    float spawnOverTime;
    int amtSpawned = 0;

    public TemporalParticleSystem(int desiredAmount, float spawnOverTime) {
        super(desiredAmount);
        this.spawnOverTime = spawnOverTime;
    }

    @Override
    public void Update(float delta) {
        ProfilerHost.Start("TemporalParticleSystem::Update");

        float factor = life / spawnOverTime;

        int toSpawn = Math.round(desiredAmount * factor);
        toSpawn -= amtSpawned;

        if (toSpawn > 0) {
            Spawn(amtSpawned, toSpawn);
            amtSpawned += toSpawn;
        }else {
//            if(amtSpawned < desiredAmount) {
//                System.out.println("None to spawn this frame, To spawn in future: " + (desiredAmount - amtSpawned));
//            }
        }
        ProfilerHost.End("TemporalParticleSystem::Update");
        super.Update(delta);
    }

    @Override
    public void Reset() {
        super.Reset();
        amtSpawned = 0;
    }
}
