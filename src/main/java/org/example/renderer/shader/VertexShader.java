package org.example.renderer.shader;

import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public class VertexShader extends Shader {
    public VertexShader(String pathString) {
        super(GL_VERTEX_SHADER, pathString);
    }
}