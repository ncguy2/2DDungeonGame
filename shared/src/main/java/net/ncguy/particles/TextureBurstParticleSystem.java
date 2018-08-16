package net.ncguy.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import net.ncguy.shaders.ComputeShader;

import java.util.Random;

public class TextureBurstParticleSystem extends AbstractParticleSystem {

    public Vector2 size;
    boolean hasSpawned = false;

    public TextureBurstParticleSystem(ParticleProfile profile) {
        super(profile);
    }

    @Override
    public int Spawn(int offset, int amount) {
        ComputeShader program = spawnScript.Program();
        program.Bind();
        program.SetUniform("u_startId", loc -> Gdx.gl.glUniform1i(loc, offset));
        program.SetUniform("u_workload", loc -> Gdx.gl.glUniform1i(loc, 1));
        program.SetUniform("iTime", loc -> Gdx.gl.glUniform1f(loc, life));
        program.SetUniform("u_rngBaseSeed", loc -> Gdx.gl.glUniform1i(loc, new Random().nextInt()));
        program.SetUniform("gTime", loc -> Gdx.gl.glUniform1f(loc, GlobalLife));
        program.SetUniform("u_sampleColour", loc -> {
            if(colourTexture != null) {
                colourTexture.bind(4);
                Gdx.gl.glUniform1i(loc, 4);
            }
        });
        program.SetUniform("u_sampleMask", loc -> {
            if(maskTexture != null) {
                maskTexture.bind(5);
                Gdx.gl.glUniform1i(loc, 5);
            }
        });
        program.SetUniform("u_sampleChannel", loc -> Gdx.gl.glUniform1i(loc, maskChannel));
        program.SetUniform("u_sampleSize", loc -> Gdx.gl.glUniform2f(loc, size.x, size.y));
        uniformSetters.forEach(program::SetUniform);

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

        int amtSpawned = round(amount, 256);
        program.Dispatch(amtSpawned);
        program.Unbind();
        return amtSpawned;
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

    @Deprecated
    public static TextureBurstParticleSystem Build(Texture colourTexture, Texture maskTexture, int maskChannel, Vector2 size, ParticleProfile profile) {
        int amt = Math.round(size.x * size.y);
        TextureBurstParticleSystem sys = new TextureBurstParticleSystem(profile);
        sys.size = size;
        sys.colourTexture = colourTexture;
        sys.maskTexture = maskTexture;
        sys.maskChannel = maskChannel;
        return sys;
    }

}

