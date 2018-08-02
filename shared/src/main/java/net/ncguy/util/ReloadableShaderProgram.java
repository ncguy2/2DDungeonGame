package net.ncguy.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.shaders.ShaderPreprocessor;

public class ReloadableShaderProgram extends ReloadableShader<ShaderProgram> {

    private final FileHandle vertexShader;
    private final FileHandle fragmentShader;

    public ReloadableShaderProgram(String name, FileHandle vertexShader, FileHandle fragmentShader) {
        super(name);
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
        ReloadImmediate();
    }

    @Override
    public void ReloadImmediate() {
        ShaderProgram program = new ShaderProgram(ShaderPreprocessor.ReadShader(vertexShader), ShaderPreprocessor.ReadShader(fragmentShader));
        System.out.println(program.getLog());
        if(program.isCompiled()) {
            if(this.program != null)
                this.program.dispose();
            this.program = program;
            return;
        }
        System.out.println(name + " could not compile");
    }

    @Override
    public String getLog() {
        if(program != null)
            return program.getLog();
        return "No program";
    }

    @Override
    public void Shutdown() {
        if(program != null) {
            program.dispose();
            program = null;
        }
    }

    public void BindTexture(String loc, Texture texture, int id) {
        if(program != null) {
            texture.bind(id);
            program.setUniformi(loc, id);
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        }
    }
}