package org.example.renderer.buffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class BufferLayout implements Iterable<BufferLayout.LayoutElement> {
    private final List<LayoutElement> layoutElements;
    private final int stride;
    private final int byteSize;

    private BufferLayout(List<LayoutElement> layoutElements) {
        this.layoutElements = layoutElements;
        this.stride = layoutElements.stream().mapToInt(LayoutElement::size).reduce(0, Integer::sum);
        this.byteSize = stride;
    }

    public final int stride() {
        return stride;
    }

    public final int byteSize() {
        return stride;
    }

    @Override
    public Iterator<LayoutElement> iterator() {
        return layoutElements.iterator();
    }

    public record LayoutElement(int type, int count, boolean toNormalize, int size, int offset) {
        public LayoutElement {
            if (type != GL_INT && type != GL_FLOAT && type != GL_UNSIGNED_BYTE)
                throw new IllegalArgumentException("Type of layout element must be int, float or unsigned byte");

            if (count <= 0)
                throw new IllegalArgumentException("Number of elements in a layout record must be positive");

            if (size != count * sizeOfType(type))
                throw new IllegalArgumentException("Size doesn't match with type and count");

            if (offset < 0)
                throw new IllegalArgumentException("Offset can't be negative");
        }

        public LayoutElement(int type, int count, boolean toNormalize, int offset) {
            this(type, count, toNormalize, count * sizeOfType(type), offset);
        }

        private static int sizeOfType(int type) {
            return switch (type) {
                case GL_INT -> Integer.BYTES;
                case GL_FLOAT -> Float.BYTES;
                case GL_UNSIGNED_BYTE -> Byte.BYTES;
                default -> throw new IllegalArgumentException("Can't pass a type that is not int, float" +
                        "or unsigned byte to layout element");
            };
        }
    }

    public static class Builder {
        private final List<LayoutElement> elements;

        private int lastOffset() {
            if (elements.isEmpty())
                return 0;

            return elements.get(elements.size() - 1).offset;
        }

        private int lastSize() {
            if (elements.isEmpty())
                return 0;

            return elements.get(elements.size() - 1).size;
        }

        public Builder() {
            this.elements = new ArrayList<>();
        }

        public Builder(List<LayoutElement> elements) {
            this.elements = elements;
        }

        public Builder addInts(int count) {
            return add(GL_INT, count, false);
        }

        public Builder addFloats(int count) {
            return add(GL_FLOAT, count, false);
        }

        public Builder addFloats(int count, boolean toNormalize) {
            return add(GL_FLOAT, count, toNormalize);
        }

        public Builder addUBytes(int count) {
            return add(GL_UNSIGNED_BYTE, count, false);
        }

        private Builder add(int type, int count, boolean toNormalize) {
            elements.add(new LayoutElement(type, count, toNormalize, lastOffset() + lastSize()));
            return this;
        }

        public BufferLayout build() {
            return new BufferLayout(elements);
        }
    }
}