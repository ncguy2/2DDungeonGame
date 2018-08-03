package net.ncguy.util;

import com.badlogic.gdx.files.FileHandle;
import net.ncguy.shaders.ComputeShader;

import java.util.HashMap;
import java.util.Map;

public class ReloadableComputeShader extends ReloadableShader<ComputeShader> {

    public ReloadableComputeShader(String name, FileHandle handle) {
        this(name, handle, new HashMap<>());
    }
    public ReloadableComputeShader(String name, FileHandle handle, Map<String, String> macroParams) {
        super(name);
        program = new ComputeShader(handle, macroParams);
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
