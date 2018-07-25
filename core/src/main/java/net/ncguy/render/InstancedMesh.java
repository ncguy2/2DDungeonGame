package net.ncguy.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.IndexData;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexData;

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

        if (autoBind) bind(shader);

        if (bIsVertexArray) {
            if (getNumIndices() > 0) {
                ShortBuffer buffer = getIndicesBuffer();
                int oldPosition = buffer.position();
                int oldLimit = buffer.limit();
                buffer.position(offset);
                buffer.limit(offset + count);
                Gdx.gl20.glDrawElements(primitiveType, count, GL20.GL_UNSIGNED_SHORT, buffer);
                buffer.position(oldPosition);
                buffer.limit(oldLimit);
            } else {
                Gdx.gl20.glDrawArrays(primitiveType, offset, count);
            }
        } else {
            if (getNumIndices() > 0) {
                Gdx.gl30.glDrawElementsInstanced(primitiveType, count, GL20.GL_UNSIGNED_SHORT, offset * 2, instanceCount);
            } else {
                Gdx.gl30.glDrawArraysInstanced(primitiveType, offset, count, instanceCount);
            }
        }

        if (autoBind) unbind(shader);
    }
}
