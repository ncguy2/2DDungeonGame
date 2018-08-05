package net.ncguy.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import net.ncguy.buffer.ShaderStorageBufferObject;
import net.ncguy.profile.ProfilerHost;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GL43;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import static com.badlogic.gdx.graphics.GL20.GL_LINK_STATUS;
import static com.badlogic.gdx.graphics.GL20.GL_TRUE;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BARRIER_BIT;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

public class ComputeShader implements Disposable {

    protected FileHandle scriptHandle;
    protected String script;
    protected int programHandle;

    protected Map<String, Integer> uniformLocationCache;
    public final Map<String, String> macroParams;

    public ComputeShader(FileHandle scriptHandle) {
        this(scriptHandle, new HashMap<>());
    }

    public ComputeShader(FileHandle scriptHandle, Map<String, String> macroParams) {
        this.scriptHandle = scriptHandle;
        this.macroParams = macroParams;
        uniformLocationCache = new TreeMap<>();
        Compile();
    }

    public int GetUniformLocation(String uniform) {
        if(programHandle <= 0)
            return -1;

        if(uniformLocationCache.containsKey(uniform))
            return uniformLocationCache.get(uniform);

        int loc = Gdx.gl.glGetUniformLocation(programHandle, uniform);
        uniformLocationCache.put(uniform, loc);
        return loc;
    }

    public void SetUniform(String uniform, Consumer<Integer> setter) {
        ProfilerHost.Start("ComputeShader::SetUniform [" + uniform + "]");
        setter.accept(GetUniformLocation(uniform));
        ProfilerHost.End("ComputeShader::SetUniform");
    }

    public void BindSSBO(int bindingPoint, ShaderStorageBufferObject ssbo) {
        ProfilerHost.Start("ComputeShader::BindSSBO [" + bindingPoint + "]");
        ssbo.Bind(bindingPoint);
        ProfilerHost.End("ComputeShader::BindSSBO");
    }

    public void Dispatch() {
        Dispatch(1);
    }
    public void Dispatch(int x) {
        Dispatch(x, 1);
    }
    public void Dispatch(int x, int y) {
        Dispatch(x, y, 1);
    }
    public void Dispatch(int x, int y, int z) {
        GL43.glDispatchCompute(x, y, z);
    }

    public void Compile() {
        ProfilerHost.Start("ComputeShader::Compile [" + scriptHandle.nameWithoutExtension() + "]");
        ProfilerHost.Start("Preamble");
        script = ShaderPreprocessor.ReadShader(scriptHandle, macroParams);
        ProfilerHost.End("Preamble");

        ProfilerHost.Start("Compilation");
        IntBuffer outBuffer = BufferUtils.newIntBuffer(8);
        int cs = Gdx.gl30.glCreateShader(GL43.GL_COMPUTE_SHADER);
        Gdx.gl30.glShaderSource(cs, script);
        Gdx.gl.glCompileShader(cs);
        Gdx.gl.glGetShaderiv(cs, GL20.GL_COMPILE_STATUS, outBuffer);
        if(outBuffer.get() != GL_TRUE) {
            System.err.println("Error in compiling compute shader");
            String log = Gdx.gl.glGetShaderInfoLog(cs);
            System.out.println(log);
            ProfilerHost.End("Compilation");
            ProfilerHost.End("ComputeShader::Compile");
            return;
        }
        ProfilerHost.End("Compilation");

        ProfilerHost.Start("Linking");
        outBuffer.position(0);
        programHandle = Gdx.gl30.glCreateProgram();
        Gdx.gl.glAttachShader(programHandle, cs);
        Gdx.gl.glLinkProgram(programHandle);
        Gdx.gl.glGetProgramiv(programHandle, GL_LINK_STATUS, outBuffer);

        if(outBuffer.get() != GL_TRUE) {
            System.err.println("Error in linking compute shader");
            String s = Gdx.gl.glGetProgramInfoLog(programHandle);
            System.out.println(s);
            ProfilerHost.End("Linking");
            ProfilerHost.End("ComputeShader::Compile");
            return;
        }
        ProfilerHost.End("Linking");

        ProfilerHost.Start("Cleanup");
        Gdx.gl.glDeleteShader(cs);
        ProfilerHost.End("Cleanup");
        ProfilerHost.End("ComputeShader::Compile");

    }

    public void Recompile() {
        dispose();
        Compile();
    }

    public String GetLog() {
        return Gdx.gl20.glGetProgramInfoLog(programHandle);
    }

    public void Bind() {
        Gdx.gl.glUseProgram(programHandle);
    }
    public void Unbind() {
        Gdx.gl.glUseProgram(0);
    }

    public void WaitForCompletion() {
        ProfilerHost.Start("ComputeShader::WaitForCompletion");
        GL42.glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, 0);
        ProfilerHost.End("ComputeShader::WaitForCompletion");
    }

    @Override
    public void dispose() {
        ProfilerHost.Start("ComputeShader::dispose");
        uniformLocationCache.clear();
        Gdx.gl.glUseProgram(0);
        Gdx.gl.glDeleteProgram(programHandle);
        ProfilerHost.End("ComputeShader::dispose");
    }
}
