package org.example.renderer;

import org.example.renderer.buffer.IndexBuffer;
import org.example.renderer.buffer.VertexArray;
import org.example.renderer.shader.ShaderProgram;
import org.example.renderer.texture.Texture;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    public static void draw(VertexArray vertexArray, ShaderProgram shaderProgram) {
        shaderProgram.bind();
        vertexArray.bind();

        glDrawArrays(GL_TRIANGLES, 0, vertexArray.count());

        vertexArray.unbind();
        shaderProgram.unbind();
    }

    public static void draw(VertexArray vertexArray, ShaderProgram shaderProgram, Texture texture) {
        shaderProgram.bind();
        vertexArray.bind();
        texture.bind();

        glDrawArrays(GL_TRIANGLES, 0, vertexArray.count());

        texture.unbind();
        vertexArray.unbind();
        shaderProgram.unbind();
    }

    public static void draw(VertexArray vertexArray, ShaderProgram shaderProgram, Texture texture1, Texture texture2) {
        shaderProgram.bind();
        vertexArray.bind();
        texture1.bind(0);
        texture2.bind(1);

        glDrawArrays(GL_TRIANGLES, 0, vertexArray.count());

        texture1.unbind();
        texture2.unbind();
        vertexArray.unbind();
        shaderProgram.unbind();
    }

    public static void draw(VertexArray vertexArray, IndexBuffer indexBuffer, ShaderProgram shaderProgram, Texture texture1, Texture texture2) {
        shaderProgram.bind();
        vertexArray.bind();
        texture1.bind(0);
        texture2.bind(1);

        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

        texture1.unbind();
        texture2.unbind();
        vertexArray.unbind();
        shaderProgram.unbind();
    }

    public static void drawWithOutline(VertexArray vertexArray, ShaderProgram shaderProgram, Texture texture, ShaderProgram outlineShader, Matrix4f model) {
        glStencilFunc(GL_ALWAYS, 1, 0xFF);
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
        glStencilMask(0xFF);

        shaderProgram.setUniform("model", model);
        Renderer.draw(vertexArray, shaderProgram, texture);

        glStencilFunc(GL_NOTEQUAL, 1, 0xFF);

        outlineShader.setUniform("model", model.scale(1.1f));
        Renderer.draw(vertexArray, outlineShader);

        glStencilFunc(GL_ALWAYS, 1, 0xFF);

        clearStencil();
        glStencilMask(0x00);
    }

    public static void clearColor() {
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public static void clearDepth() {
        glClear(GL_DEPTH_BUFFER_BIT);
    }

    public static void clearStencil() {
        glClear(GL_STENCIL_BUFFER_BIT);
    }


    public static void setClearColor(float red, float green, float blue, float alpha) {
        glClearColor(red, green, blue, alpha);
    }

}