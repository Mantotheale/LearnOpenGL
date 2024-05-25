package org.example.renderer;

import org.example.renderer.buffer.IndexBuffer;
import org.example.renderer.buffer.Vertex;
import org.example.renderer.buffer.VertexArray;
import org.example.renderer.shader.ShaderProgram;
import org.example.renderer.texture.Texture;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Mesh {
    private final List<Vertex> vertices;
    private final List<Integer> indices;
    private final List<Texture> textures;

    private final VertexArray vao;

    public Mesh(List<Vertex> vertices, List<Integer> indices, List<Texture> textures) {
        this.vertices = vertices;
        this.indices = indices;
        this.textures = textures;

        vao = new VertexArray(Vertex.bufferFromList(vertices), Vertex.LAYOUT, new IndexBuffer(indices));
    }

    public void draw(ShaderProgram shader) {
        int diffuseNumber = 1;
        int specularNumber = 1;

        for(int i = 0; i < textures.size(); i++) {
            Texture texture = textures.get(i);

            texture.bind(i);

            String number;
            if (texture.type().equals("texture_diffuse")) {
                number = Integer.toString(diffuseNumber);
                diffuseNumber++;
            } else {
                number = Integer.toString(specularNumber);
                specularNumber++;
            }

            shader.setUniform("material." + texture.type() + number, i);
        }

        shader.bind();
        vao.bind();

        glDrawElements(GL_TRIANGLES, vao.indexBufferCount(), GL_UNSIGNED_INT, 0);

        vao.unbind();
        shader.unbind();
    }
}
