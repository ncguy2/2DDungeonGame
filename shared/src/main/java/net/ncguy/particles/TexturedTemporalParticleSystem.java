package net.ncguy.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import net.ncguy.assets.TextureResolver;
import net.ncguy.shaders.ComputeShader;

import java.util.Random;

public class TexturedTemporalParticleSystem extends TemporalParticleSystem {

    public Vector2 size;
    public transient Texture texture;
    public String texturePath;
    public int maskChannel;
    protected transient boolean isLoadingTexture = false;

    public TexturedTemporalParticleSystem(ParticleProfile profile) {
        super(profile);
        size = profile.size;
        texturePath = profile.texturePath;
        maskChannel = profile.maskChannel;
    }

    @Override
    public int Spawn(int offset, int amount) {
        ComputeShader program = spawnScript.Program();
        program.Bind();

        program.SetUniform("u_spawnPoint", loc -> {
            Vector2 p;
            if(spawnPointSupplier == null)
                p = Vector2.Zero;
            else p = spawnPointSupplier.get();
            Gdx.gl.glUniform2f(loc, p.x, p.y);
        });

        program.SetUniform("u_spawnMatrix", loc -> {
            Matrix3 p;
            if(spawnMatrixSupplier == null)
                p = new Matrix3();
            else p = spawnMatrixSupplier.get();
            Gdx.gl.glUniformMatrix3fv(loc, 1, false, p.val, 0);
        });

        program.SetUniform("u_startId", loc -> Gdx.gl.glUniform1i(loc, offset));
        program.SetUniform("iTime", loc -> Gdx.gl.glUniform1f(loc, life));
        program.SetUniform("u_rngBaseSeed", loc -> Gdx.gl.glUniform1i(loc, new Random().nextInt()));
        program.SetUniform("imaxParticleCount", loc -> Gdx.gl.glUniform1i(loc, desiredAmount));
        program.SetUniform("gTime", loc -> Gdx.gl.glUniform1f(loc, GlobalLife));
        program.SetUniform("u_spawnTexture", loc -> {
            if(texture != null) {
                texture.bind(4);
                Gdx.gl.glUniform1i(loc, 4);
            }
        });
        program.SetUniform("u_sampleChannel", loc -> Gdx.gl.glUniform1i(loc, maskChannel));
        program.SetUniform("u_size", loc -> {
            if(size != null)
                Gdx.gl.glUniform2f(loc, size.x, size.y);
        });
        uniformSetters.forEach(program::SetUniform);

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

        int amtSpawned = round(amount, INVOCATIONS_PER_WORKGROUP);
        program.Dispatch(amtSpawned);
        program.Unbind();
        return amtSpawned;
    }

    @Override
    public void Update(float delta) {

        if(!isLoadingTexture && texture == null && (texturePath != null && !texturePath.isEmpty())) {
            isLoadingTexture = true;
            TextureResolver.GetTextureAsync(texturePath, t -> {
                texture = t;
                if(size == null)
                    size = new Vector2(texture.getWidth(), texture.getHeight());
                isLoadingTexture = false;
            });
        }

        super.Update(delta);
    }
}

