package net.ncguy.util;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

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
        ShaderProgram program = new ShaderProgram(vertexShader, fragmentShader);
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
}
