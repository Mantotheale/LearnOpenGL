package org.example.renderer.buffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;

public class IndexBuffer {
    private final int id;
    private final int[] indices;

    public IndexBuffer(int[] indexArray) {
        indices = indexArray;

        id = glGenBuffers();

        bind();

        IntBuffer buffer = MemoryUtil.memAllocInt(indices.length).put(indices).rewind();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        unbind();
    }

    public IndexBuffer(List<Integer> indexList) {
        indices = new int[indexList.size()];

        for (int i = 0; i < indexList.size(); i++)
            indices[i] = indexList.get(i);

        id = glGenBuffers();

        bind();

        IntBuffer buffer = MemoryUtil.memAllocInt(indices.length).put(indices).rewind();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        unbind();
    }

    public int count() {
        return indices.length;
    }

    public void bind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
    }

    public void unbind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}