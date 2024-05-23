package org.example.renderer.shader;

import org.example.utility.Utility;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public abstract class Shader {
    private final int id;
    private final int type;
    private boolean isDeleted;

    protected Shader(int type, String pathString) {
        this.type = type;

        id = createShader(Utility.fileToString(pathString));
        isDeleted = false;
    }

    public int getId() {
        return id;
    }

    public void delete() {
        glDeleteShader(id);
        isDeleted = true;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    private int createShader(String shaderSource) {
        int shader = glCreateShader(type);
        glShaderSource(shader, shaderSource);
        glCompileShader(shader);

        if(glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE)
            throw new RuntimeException(glGetShaderInfoLog(shader));

        return shader;
    }
}