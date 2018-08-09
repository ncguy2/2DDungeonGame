package net.ncguy.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.IndexData;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexData;
import net.ncguy.profile.ProfilerHost;

import java.nio.ShortBuffer;

public class InstancedMesh extends Mesh {

    public boolean bIsVertexArray;

    protected InstancedMesh(VertexData vertices, IndexData indices, boolean isVertexArray) {
        super(vertices, indices, isVertexArray);
        bIsVertexArray = isVertexArray;
    }

    public InstancedMesh(boolean isStatic, int maxVertices, int maxIndices, VertexAttribute... attributes) {
        super(isStatic, maxVertices, maxIndices, attributes);
        bIsVertexArray = false;
    }

    public InstancedMesh(boolean isStatic, int maxVertices, int maxIndices, VertexAttributes attributes) {
        super(isStatic, maxVertices, maxIndices, attributes);
        bIsVertexArray = false;
    }

    public InstancedMesh(boolean staticVertices, boolean staticIndices, int maxVertices, int maxIndices, VertexAttributes attributes) {
        super(staticVertices, staticIndices, maxVertices, maxIndices, attributes);
        bIsVertexArray = false;
    }

    public InstancedMesh(VertexDataType type, boolean isStatic, int maxVertices, int maxIndices, VertexAttribute... attributes) {
        this(type, isStatic, maxVertices, maxIndices, new VertexAttributes(attributes));
    }

    public InstancedMesh(VertexDataType type, boolean isStatic, int maxVertices, int maxIndices, VertexAttributes attributes) {
        super(type, isStatic, maxVertices, maxIndices, attributes);
        switch (type) {
            case VertexBufferObject:
            case VertexBufferObjectSubData:
            case VertexBufferObjectWithVAO:
                bIsVertexArray = false;
                break;
            case VertexArray:
            default:
                bIsVertexArray = true;
                break;
        }
    }

    public int instanceCount = 1;

    @Override
    public Mesh setIndices(short[] indices, int offset, int count) {
        return super.setIndices(indices, offset, count);
    }

    @Override
    public void render(ShaderProgram shader, int primitiveType, int offset, int count, boolean autoBind) {
        if (count == 0) return;
        ProfilerHost.Start("InstancedMesh::render");

        if (autoBind) {
            ProfilerHost.Start("Autobind shader");
            bind(shader);
            ProfilerHost.End("Autobind shader");
        }

        if (bIsVertexArray) {
            ProfilerHost.Start("Vertex Array");
            if (getNumIndices() > 0) {
                ProfilerHost.Start("Draw elements");
                ShortBuffer buffer = getIndicesBuffer();
                int oldPosition = buffer.position();
                int oldLimit = buffer.limit();
                buffer.position(offset);
                buffer.limit(offset + count);
                ProfilerHost.Start("Draw");
                Gdx.gl20.glDrawElements(primitiveType, count, GL20.GL_UNSIGNED_SHORT, buffer);
                ProfilerHost.End("Draw");
                buffer.position(oldPosition);
                buffer.limit(oldLimit);
                ProfilerHost.End("Draw elements");
            } else {
                ProfilerHost.Start("Draw arrays");
                Gdx.gl20.glDrawArrays(primitiveType, offset, count);
                ProfilerHost.End("Draw arrays");
            }
            ProfilerHost.End("Vertex Array");
        } else {
            if (getNumIndices() > 0) {
                ProfilerHost.Start("Draw elements instanced");
                Gdx.gl30.glDrawElementsInstanced(primitiveType, count, GL20.GL_UNSIGNED_SHORT, offset * 2, instanceCount);
                ProfilerHost.End("Draw elements instanced");
            } else {
                ProfilerHost.Start("Draw arrays instanced");
                Gdx.gl30.glDrawArraysInstanced(primitiveType, offset, count, instanceCount);
                ProfilerHost.End("Draw arrays instanced");
            }
        }

        if (autoBind) {
            ProfilerHost.Start("Autobind shader - unbind");
            unbind(shader);
            ProfilerHost.End("Autobind shader - unbind");
        }
        ProfilerHost.End("InstancedMesh::render");
    }
}
