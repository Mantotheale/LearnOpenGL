package org.example.renderer.buffer;

import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class VertexArray {
    private final int id;
    private final int vertexCount;

    public VertexArray(VertexBuffer vertexBuffer, BufferLayout bufferLayout) {
        id = glGenVertexArrays();

        bind();
        vertexBuffer.bind();

        int i = 0;
        for (BufferLayout.LayoutElement el: bufferLayout) {
            glVertexAttribPointer(i, el.count(), el.type(), el.toNormalize(), bufferLayout.stride(), el.offset());
            glEnableVertexAttribArray(i);
            i++;
        }

        vertexCount = vertexBuffer.byteSize() / bufferLayout.byteSize();

        vertexBuffer.unbind();
        unbind();
    }

    public void bind() {
        glBindVertexArray(id);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    public int count() {
        return vertexCount;
    }

    public void bindIndexBuffer(IndexBuffer indexBuffer) {
        bind();
        indexBuffer.bind();
        unbind();
    }

    public void unbindIndexBuffer(IndexBuffer indexBuffer) {
        bind();
        indexBuffer.unbind();
        unbind();
    }
}