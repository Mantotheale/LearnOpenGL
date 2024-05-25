package org.example.renderer.shader;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
    private final int id;

    public ShaderProgram(VertexShader vertexShader, FragmentShader fragmentShader) {
        id = glCreateProgram();

        attachShader(vertexShader);
        attachShader(fragmentShader);

        link();

        detachShader(vertexShader);
        detachShader(fragmentShader);

        vertexShader.delete();
        fragmentShader.delete();
    }

    public ShaderProgram(String vertexShaderPath, String fragmentShaderPath) {
        this(new VertexShader(vertexShaderPath), new FragmentShader(fragmentShaderPath));
    }

    public void bind() {
        glUseProgram(id);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void delete() {
        glDeleteProgram(id);
    }

    public void setUniform(String name, boolean value) {
        bind();
        glUniform1i(getUniformLocation(name), Boolean.compare(value, false));
        unbind();
    }

    public void setUniform(String name, int value) {
        bind();
        glUniform1i(getUniformLocation(name), value);
        unbind();
    }

    public void setUniform(String name, float value) {
        bind();
        glUniform1f(getUniformLocation(name), value);
        unbind();
    }

    public void setUniform(String name, float x, float y, float z) {
        bind();
        glUniform3f(getUniformLocation(name), x, y, z);
        unbind();
    }

    public void setUniform(String name, Vector3f value) {
        bind();
        glUniform3f(getUniformLocation(name), value.x, value.y, value.z);
        unbind();
    }

    public void setUniform(String name, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer =  value.get(stack.mallocFloat(16));

            bind();
            glUniformMatrix4fv(getUniformLocation(name), false, buffer);
            unbind();
        }
    }

    public void setUniform(String name, int index, float value) {
        setUniform(generateName(name, index), value);
    }

    public void setUniform(String name, String field, float value) {
        setUniform(generateName(name, field), value);
    }

    public void setUniform(String name, int index, String field, float value) {
        setUniform(generateName(name, index, field), value);
    }

    public void setUniform(String name, int index, Vector3f value) {
        setUniform(generateName(name, index), value);
    }

    public void setUniform(String name, String field, Vector3f value) {
        setUniform(generateName(name, field), value);
    }

    public void setUniform(String name, int index, String field, Vector3f value) {
        setUniform(generateName(name, index, field), value);
    }

    public void setUniform(String name, int index, float x, float y, float z) {
        setUniform(generateName(name, index), x, y, z);
    }

    public void setUniform(String name, String field, float x, float y, float z) {
        setUniform(generateName(name, field), x, y, z);
    }

    public void setUniform(String name, int index, String field, float x, float y, float z) {
        setUniform(generateName(name, index, field), x, y, z);
    }

    public void setUniform(String name, int index, String field, String innerField, float value) {
        setUniform(generateName(name, index, field, innerField), value);
    }

    public void setUniform(String name, int index, String field, String innerField, Vector3f value) {
        setUniform(generateName(name, index, field, innerField), value);
    }

    public void setUniform(String name, int index, String field, String innerField, float x, float y, float z) {
        setUniform(generateName(name, index, field, innerField), x, y, z);
    }

    public void setUniform(String name, String field, String innerField, float value) {
        setUniform(generateName(name, field, innerField), value);
    }

    public void setUniform(String name, String field, String innerField, Vector3f value) {
        setUniform(generateName(name, field, innerField), value);
    }

    public void setUniform(String name, String field, String innerField, float x, float y, float z) {
        setUniform(generateName(name, field, innerField), x, y, z);
    }


    public void attachShader(Shader shader) {
        glAttachShader(id, shader.getId());
    }

    public void detachShader(Shader shader) {
        glDetachShader(id, shader.getId());
    }

    public void link() {
        glLinkProgram(id);

        if(glGetProgrami(id, GL_LINK_STATUS) == GL_FALSE)
            throw new RuntimeException(glGetProgramInfoLog(id));
    }

    private int getUniformLocation(String name) {
        int location = glGetUniformLocation(id, name);

        if (location == -1)
            throw new IllegalArgumentException("An uniform with name " + name + " doesn't exist in shader " + id);

        return location;
    }

    private String generateName(String name, int index) {
        return name + "[" + index + "]";
    }

    private String generateName(String name, String field) {
        return name + "." + field;
    }

    private String generateName(String name, int index, String field) {
        return generateName(generateName(name, index), field);
    }

    private String generateName(String name, String field, String innerField) {
        return generateName(generateName(name, field), innerField);
    }

    private String generateName(String name, int index, String field, String innerField) {
        return generateName(generateName(name, index, field), innerField);
    }
}