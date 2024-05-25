package org.example.renderer.buffer;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;

public record Vertex(Vector3f position, Vector3f normal, Vector2f texCoords) {
    public static BufferLayout LAYOUT = new BufferLayout.Builder()
            .addFloats(3).addFloats(3).addFloats(2).build();

    public static VertexBuffer bufferFromList(List<Vertex> vertices) {
        VertexBuffer.Builder builder = new VertexBuffer.Builder();

        for (Vertex v: vertices) {
            builder.add(v.position.x);
            builder.add(v.position.y);
            builder.add(v.position.z);
            builder.add(v.normal.x);
            builder.add(v.normal.y);
            builder.add(v.normal.z);
            builder.add(v.texCoords.x);
            builder.add(v.texCoords.y);
        }

        return builder.build();
    }
}
