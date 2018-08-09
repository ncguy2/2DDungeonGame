package net.ncguy.particles;


import com.badlogic.gdx.files.FileHandle;
import net.ncguy.util.curve.GLColourCurve;

public class ParticleProfile {

    public String name;

    public AbstractParticleSystem.SystemType type;

    public String[] blocks;

    public GLColourCurve curve;
    public float duration;
    public int particleCount;

    public AbstractParticleSystem.LoopingBehaviour loopingBehaviour = AbstractParticleSystem.LoopingBehaviour.None;
    public int loopingAmount = 1;

    // Temporal only
    public float spawnOverTime;

    @Deprecated
    public FileHandle spawnHandle;
    @Deprecated
    public FileHandle updateHandle;

}
