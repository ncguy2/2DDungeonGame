package net.ncguy.particles;


import com.badlogic.gdx.files.FileHandle;
import net.ncguy.util.curve.GLColourCurve;

public class ParticleProfile {

    public String name;

    public AbstractParticleSystem.SystemType type;
    public FileHandle spawnHandle;
    public FileHandle updateHandle;
    public GLColourCurve curve;
    public float duration;
    public int particleCount;

    // Temporal only
    public float spawnOverTime;

}
