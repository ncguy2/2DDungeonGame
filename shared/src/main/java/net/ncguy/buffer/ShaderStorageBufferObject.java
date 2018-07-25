package net.ncguy.buffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.utils.Disposable;
import net.ncguy.profile.ProfilerHost;
import org.lwjgl.opengl.GL15;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

public class ShaderStorageBufferObject implements Disposable {

    private final Buffer data;
    public final int size;
    protected int bufferId;

    public ShaderStorageBufferObject(Buffer data) {
        this(data, 0);
    }
    public ShaderStorageBufferObject(Buffer data, int location) {
        this(data, data.capacity(), location);
    }
    public ShaderStorageBufferObject(Buffer data, int size, int location) {
        this.data = data;
        this.size = size;
        Init(location);
    }

    public ShaderStorageBufferObject(int size) {
        this(null, size, 0);
    }

    public void Init(int location) {
        if(data != null)
            data.position(0);

        ProfilerHost.Start("ShaderStorageBufferObject::Init");
        bufferId = Gdx.gl.glGenBuffer();
        Gdx.gl30.glBindBuffer(GL_SHADER_STORAGE_BUFFER, bufferId);
        Gdx.gl30.glBufferData(GL_SHADER_STORAGE_BUFFER, size, data, GL30.GL_DYNAMIC_COPY);
        Gdx.gl30.glBindBufferBase(GL_SHADER_STORAGE_BUFFER, location, bufferId);

        Gdx.gl30.glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0); // unbind
        ProfilerHost.End("ShaderStorageBufferObject::Init");
    }

    public void Bind(int location) {
        ProfilerHost.Start("ShaderStorageBufferObject::Bind");
        Gdx.gl30.glBindBuffer(GL_SHADER_STORAGE_BUFFER, bufferId);
        Gdx.gl30.glBindBufferBase(GL_SHADER_STORAGE_BUFFER, location, bufferId);
        ProfilerHost.End("ShaderStorageBufferObject::Bind");
    }

    public ByteBuffer Map(int access) {
        ProfilerHost.Start("ShaderStorageBufferObject::Map");
        ProfilerHost.Start("Bind");
        Gdx.gl30.glBindBuffer(GL_SHADER_STORAGE_BUFFER, bufferId);
        ProfilerHost.End("Bind");
        ProfilerHost.Start("Map");
        ByteBuffer buffer = GL15.glMapBuffer(GL_SHADER_STORAGE_BUFFER, access);
        ProfilerHost.End("Map");
        ProfilerHost.End("ShaderStorageBufferObject::Map");
        return buffer;
    }

    public void Unmap() {
        ProfilerHost.Start("ShaderStorageBufferObject::Unmap");
        ProfilerHost.Start("Unmap");
        GL15.glUnmapBuffer(GL_SHADER_STORAGE_BUFFER);
        ProfilerHost.End("Unmap");
        ProfilerHost.Start("Unbind");
        Gdx.gl30.glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
        ProfilerHost.End("Unbind");
        ProfilerHost.End("ShaderStorageBufferObject::Unmap");
    }

    public ByteBuffer GetData() {
        ProfilerHost.Start("ShaderStorageBufferObject::GetData");
        ByteBuffer buffer = Map(GL15.GL_READ_ONLY);
        Unmap();
        ProfilerHost.End("ShaderStorageBufferObject::GetData");
        return buffer;
    }

    public void Unbind() {
        ProfilerHost.Start("ShaderStorageBufferObject::Unbind");
        Gdx.gl30.glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
        ProfilerHost.End("ShaderStorageBufferObject::Unbind");
    }

    @Override
    public void dispose() {
        Gdx.gl.glDeleteBuffer(bufferId);
        bufferId = 0;
    }
}
