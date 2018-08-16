package net.ncguy.particles;


import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
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

    // Textured only
    public String texturePath;
    public int maskChannel;
    public Vector2 size;

    @Deprecated
    public FileHandle spawnHandle;
    @Deprecated
    public FileHandle updateHandle;

    public AbstractParticleSystem Create() {
        return type.Create(this);
    }

}
