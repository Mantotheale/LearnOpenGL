package org.example.renderer.buffer;

import org.lwjgl.system.MemoryUtil;

import java.nio.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;

public class VertexBuffer {
    private final int id;
    private final int byteSize;

    private VertexBuffer(int id, int size) {
        this.id = id;
        this.byteSize = size;
    }

    public void bind() {
        glBindBuffer(GL_ARRAY_BUFFER, id);
    }

    public void unbind() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public int byteSize() {
        return byteSize;
    }

    public static class Builder {
        private record SubBuffer(Buffer buffer, int offset, int size) { }

        private final List<SubBuffer> buffers;

        public Builder() {
            this.buffers = new ArrayList<>();
        }

        public Builder add(int[] subData) {
            buffers.add(new SubBuffer(MemoryUtil.memAllocInt(subData.length).put(subData).flip(),
                    lastOffset() + lastSize(), subData.length * Integer.BYTES));

            return this;
        }

        public Builder add(float[] subData) {
            buffers.add(new SubBuffer(MemoryUtil.memAllocFloat(subData.length).put(subData).flip(),
                    lastOffset() + lastSize(), subData.length * Float.BYTES));

            return this;
        }

        public Builder add(byte[] subData) {
            buffers.add(new SubBuffer(MemoryUtil.memAlloc(subData.length).put(subData).flip(),
                    lastOffset() + lastSize(), subData.length * Byte.BYTES));

            return this;
        }

        public Builder add(int subData) {
            buffers.add(new SubBuffer(MemoryUtil.memAllocInt(1).put(subData).flip(),
                    lastOffset() + lastSize(), Integer.BYTES));

            return this;
        }

        public Builder add(float subData) {
            buffers.add(new SubBuffer(MemoryUtil.memAllocFloat(1).put(subData).flip(),
                    lastOffset() + lastSize(), Float.BYTES));

            return this;
        }

        public Builder add(byte subData) {
            buffers.add(new SubBuffer(MemoryUtil.memAlloc(1).put(subData).flip(),
                    lastOffset() + lastSize(), Integer.BYTES));

            return this;
        }

        public VertexBuffer build() {
            int size = lastOffset() + lastSize();

            int id = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, id);
            glBufferData(GL_ARRAY_BUFFER, size, GL_STATIC_DRAW);

            for (SubBuffer buf: buffers) {
                if (buf.buffer instanceof IntBuffer) {
                    glBufferSubData(GL_ARRAY_BUFFER, buf.offset, (IntBuffer) buf.buffer);
                } else if (buf.buffer instanceof FloatBuffer) {
                    glBufferSubData(GL_ARRAY_BUFFER, buf.offset, (FloatBuffer) buf.buffer);
                } else {
                    glBufferSubData(GL_ARRAY_BUFFER, buf.offset, (ByteBuffer) buf.buffer);
                }

                MemoryUtil.memFree(buf.buffer);
            }

            glBindBuffer(GL_ARRAY_BUFFER, 0);

            return new VertexBuffer(id, lastOffset() + lastSize());
        }

        private int lastOffset() {
            if (buffers.isEmpty())
                return 0;

            return buffers.get(buffers.size() - 1).offset;
        }

        private int lastSize() {
            if (buffers.isEmpty())
                return 0;

            return buffers.get(buffers.size() - 1).size;
        }
    }
}