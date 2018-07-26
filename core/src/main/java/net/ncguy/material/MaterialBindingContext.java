package net.ncguy.material;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class MaterialBindingContext {

    public final int baseTextureId;
    public int textureId;
    public int subroutineId;

    public MaterialBindingContext(int baseTextureId) {
        this.baseTextureId = baseTextureId;
    }

    public void Reset() {
        this.subroutineId = 0;
        this.textureId = this.baseTextureId;
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
    }

    public int Bind(Texture texture) {
        int id = textureId++;
        texture.bind(id);
        return id;
    }

    public void Bind(ShaderProgram shader) {

    }

}
