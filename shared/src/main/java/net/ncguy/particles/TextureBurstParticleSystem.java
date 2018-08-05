package net.ncguy.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class TextureBurstParticleSystem extends AbstractParticleSystem {

    public Vector2 size;
    boolean hasSpawned = false;

    public TextureBurstParticleSystem(int desiredAmount, float duration) {
        super(desiredAmount, duration);
    }

    public TextureBurstParticleSystem(int desiredAmount, FileHandle spawnHandle, FileHandle updateHandle, float duration) {
        super(desiredAmount, spawnHandle, updateHandle, duration);
    }

    @Override
    public void Spawn(int offset, int amount) {
        compute.Program()
                .Bind();
        compute.Program().SetUniform("u_startId", loc -> Gdx.gl.glUniform1i(loc, offset));
        compute.Program().SetUniform("u_workload", loc -> Gdx.gl.glUniform1i(loc, 1));
        compute.Program().SetUniform("iTime", loc -> Gdx.gl.glUniform1f(loc, life));
        compute.Program().SetUniform("u_rngBaseSeed", loc -> Gdx.gl.glUniform1i(loc, new Random().nextInt()));
        compute.Program().SetUniform("gTime", loc -> Gdx.gl.glUniform1f(loc, GlobalLife));
        compute.Program().SetUniform("u_sampleColour", loc -> {
            if(colourTexture != null) {
                colourTexture.bind(4);
                Gdx.gl.glUniform1i(loc, 4);
            }
        });
        compute.Program().SetUniform("u_sampleMask", loc -> {
            if(maskTexture != null) {
                maskTexture.bind(5);
                Gdx.gl.glUniform1i(loc, 5);
            }
        });
        compute.Program().SetUniform("u_sampleChannel", loc -> Gdx.gl.glUniform1i(loc, maskChannel));
        compute.Program().SetUniform("u_sampleSize", loc -> Gdx.gl.glUniform2f(loc, size.x, size.y));
        uniformSetters.forEach(compute.Program()::SetUniform);

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

//        BindBuffer();
//        BindBuffers(compute.Program());
        compute.Program().Dispatch((int) (size.x / 16f), (int) (size.y / 16f));
        compute.Program()
                .Unbind();
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

    public Texture colourTexture;
    public Texture maskTexture;
    public int maskChannel;

    public static TextureBurstParticleSystem Build(Texture colourTexture, Texture maskTexture, int maskChannel, Vector2 size, ParticleProfile profile) {
        int amt = Math.round(size.x * size.y);
        TextureBurstParticleSystem sys = new TextureBurstParticleSystem(amt, profile.spawnHandle, profile.updateHandle, profile.duration);
        sys.size = size;
        sys.colourTexture = colourTexture;
        sys.maskTexture = maskTexture;
        sys.maskChannel = maskChannel;
        return sys;
    }

}

