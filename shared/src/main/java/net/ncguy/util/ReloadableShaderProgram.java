package net.ncguy.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.shaders.ShaderPreprocessor;

import java.util.HashMap;
import java.util.Map;

public class ReloadableShaderProgram extends ReloadableShader<ShaderProgram> {

    private final FileHandle vertexShader;
    private final FileHandle fragmentShader;

    protected final Map<String, String> macroParams;

    public ReloadableShaderProgram(String name, FileHandle vertexShader, FileHandle fragmentShader) {
        this(name, vertexShader, fragmentShader, new HashMap<>());
    }

    public ReloadableShaderProgram(String name, FileHandle vertexShader, FileHandle fragmentShader, Map<String, String> macroParams) {
        super(name);
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
        this.macroParams = macroParams;
        ReloadImmediate();
    }

    @Override
    public ShaderProgram Create() {
        return new ShaderProgram(ShaderPreprocessor.ReadShader(vertexShader, macroParams), ShaderPreprocessor.ReadShader(fragmentShader, macroParams));
    }

    @Override
    public void ReloadImmediate() {
        ShaderProgram program = Create();
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
