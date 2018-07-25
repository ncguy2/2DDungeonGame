package net.ncguy.util;

import com.badlogic.gdx.files.FileHandle;
import net.ncguy.shaders.ComputeShader;

public class ReloadableComputeShader extends ReloadableShader<ComputeShader> {

    public ReloadableComputeShader(String name, FileHandle handle) {
        super(name);
        program = new ComputeShader(handle);
    }

    @Override
    public void ReloadImmediate() {
        program.Recompile();
    }

    @Override
    public String getLog() {
        return program.GetLog();
    }

    @Override
    public void Shutdown() {
        if(program != null) {
            program.dispose();
            program = null;
        }
    }
}
