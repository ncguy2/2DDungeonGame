package net.ncguy.shaders;

import com.badlogic.gdx.files.FileHandle;

import java.util.Map;

public class MutableComputeShader extends ComputeShader {



    public MutableComputeShader(FileHandle scriptHandle) {
        super(scriptHandle);
    }

    public MutableComputeShader(FileHandle scriptHandle, Map<String, String> macroParams) {
        super(scriptHandle, macroParams);
    }

    @Override
    public void Compile() {
        super.Compile();
    }
}
