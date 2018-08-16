package net.ncguy.particles;

import net.ncguy.profile.ProfilerHost;

public class TemporalParticleSystem extends AbstractParticleSystem {

    float spawnOverTime;
    int amtSpawned = 0;

    public TemporalParticleSystem(ParticleProfile profile) {
        super(profile);
        this.spawnOverTime = profile.spawnOverTime;
    }

//    public TemporalParticleSystem(int particleCount, float spawnOverTime, float duration, String... blockNames) {
//        super(particleCount, duration, blockNames);
//        this.spawnOverTime = spawnOverTime;
//    }

    @Override
    public void Update(float delta) {
        ProfilerHost.Start("TemporalParticleSystem::Update");

        float factor = life / spawnOverTime;

        int toSpawn = (int) Math.ceil(desiredAmount * factor);
        toSpawn -= amtSpawned;

        if (toSpawn > 0) {
            amtSpawned += Spawn(amtSpawned, toSpawn);
        }else {
//            if(amtSpawned < desiredAmount) {
//                System.out.println("None to spawn this frame, To spawn in future: " + (desiredAmount - amtSpawned));
//            }
        }
        ProfilerHost.End("TemporalParticleSystem::Update");
        super.Update(delta);
    }

    @Override
    public void BeginFinish() {
        super.BeginFinish();
        amtSpawned = desiredAmount;
    }

    @Override
    public void Reset() {
        super.Reset();
        amtSpawned = 0;
    }
}
