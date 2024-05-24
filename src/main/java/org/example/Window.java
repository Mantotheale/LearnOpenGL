package org.example;

import org.example.renderer.Renderer;
import org.example.renderer.buffer.BufferLayout;
import org.example.renderer.buffer.IndexBuffer;
import org.example.renderer.buffer.VertexArray;
import org.example.renderer.buffer.VertexBuffer;
import org.example.renderer.shader.FragmentShader;
import org.example.renderer.shader.ShaderProgram;
import org.example.renderer.shader.VertexShader;
import org.example.renderer.texture.Texture;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Window {
    private final long window;
    private int width;
    private int height;

    private VertexArray objectArray;
    private VertexArray lightArray;
    private ShaderProgram objectShader;
    private ShaderProgram lightShader;
    private Vector3f lightPosition;

    private double deltaTime = 0;
    private double lastFrame = 0;
    private double lastX;
    private double lastY;
    private boolean firstMouse = true;
    private final Camera camera = new Camera();
    private Texture texture1;
    private Texture texture2;
    private Vector3f[] cubePositions;

    public Window() {
        if (!glfwInit()) throw new RuntimeException("Couldn't initialize GLFW");

        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        width = 800;
        height = 600;
        window = glfwCreateWindow(width, height, "Learn OpenGL", 0, 0);

        if (window == 0) throw new RuntimeException("Failed to open a window");

        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glfwSetFramebufferSizeCallback(window, this::framebuffer_size_callback);
        glViewport(0, 0, width, height);
        glEnable(GL_DEPTH_TEST);
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glfwSetCursorPosCallback(window, this::mouse_callback);
        glfwSetScrollCallback(window, this::scroll_callback);
        lastX = (double) width / 2;
        lastY = (double) height / 2;
    }

    public void run() {
        init();

        while (!glfwWindowShouldClose(window)) {
            loop();
        }

        terminate();
    }

    private void init() {
        float[] vertices = {
                // positions          // normals           // texture coords
                -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f, 0.0f,
                0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  1.0f, 0.0f,
                0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  1.0f, 1.0f,
                0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  1.0f, 1.0f,
                -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f, 0.0f,

                -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,   0.0f, 0.0f,
                0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,   1.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,   1.0f, 1.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,   1.0f, 1.0f,
                -0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,   0.0f, 1.0f,
                -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,   0.0f, 0.0f,

                -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  1.0f, 0.0f,
                -0.5f,  0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  1.0f, 1.0f,
                -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  0.0f, 1.0f,
                -0.5f, -0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  0.0f, 0.0f,
                -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  1.0f, 0.0f,

                0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  1.0f, 0.0f,
                0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  1.0f, 1.0f,
                0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  0.0f, 1.0f,
                0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  0.0f, 1.0f,
                0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  0.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  1.0f, 0.0f,

                -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  0.0f, 1.0f,
                0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  1.0f, 1.0f,
                0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  1.0f, 0.0f,
                0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  1.0f, 0.0f,
                -0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  0.0f, 0.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  0.0f, 1.0f,

                -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f, 1.0f,
                0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  1.0f, 1.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  1.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  1.0f, 0.0f,
                -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  0.0f, 0.0f,
                -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f, 1.0f
        };

        BufferLayout layout = new BufferLayout.Builder()
                .addFloats(3).addFloats(3).addFloats(2).build();
        VertexBuffer vertexBuffer = new VertexBuffer.Builder().add(vertices).build();
        objectArray = new VertexArray(vertexBuffer, layout);

        String vertexShaderPath = "src/main/resources/shaders/vertexShader.glsl";
        String fragmentShaderPath = "src/main/resources/shaders/fragmentShader.glsl";
        VertexShader vertexShader = new VertexShader(vertexShaderPath);
        FragmentShader fragmentShader = new FragmentShader(fragmentShaderPath);
        objectShader = new ShaderProgram(vertexShader, fragmentShader);

        BufferLayout lightLayout = new BufferLayout.Builder()
                .addFloats(3).addFloats(3).addFloats(2).build();
        VertexBuffer lightBuffer = new VertexBuffer.Builder().add(vertices).build();
        lightArray = new VertexArray(lightBuffer, lightLayout);

        vertexShaderPath = "src/main/resources/shaders/vertexShader.glsl";
        fragmentShaderPath = "src/main/resources/shaders/lightSourceFragmentShader.glsl";
        vertexShader = new VertexShader(vertexShaderPath);
        fragmentShader = new FragmentShader(fragmentShaderPath);
        lightShader = new ShaderProgram(vertexShader, fragmentShader);

        texture1 = new Texture("src/main/resources/images/container2.png");
        texture2 = new Texture("src/main/resources/images/container2_specular.png");

        //lightPosition = new Vector3f(1.2f, 1.0f, 2.0f);

        objectShader.setUniform("material.diffuse", 0);
        objectShader.setUniform("material.specular", 1);
        objectShader.setUniform("material.shininess", 32.0f);

        objectShader.setUniform("light.ambient",  0.2f, 0.2f, 0.2f);
        objectShader.setUniform("light.diffuse",  0.5f, 0.5f, 0.5f);
        objectShader.setUniform("light.specular", 1.0f, 1.0f, 1.0f);

        objectShader.setUniform("light.direction", -0.2f, -1.0f, -0.3f);

        Renderer.setClearColor(0, 0, 0, 1);

        cubePositions = new Vector3f[]{
                new Vector3f( 0.0f,  0.0f,  0.0f),
                new Vector3f( 2.0f,  5.0f, -15.0f),
                new Vector3f(-1.5f, -2.2f, -2.5f),
                new Vector3f(-3.8f, -2.0f, -12.3f),
                new Vector3f( 2.4f, -0.4f, -3.5f),
                new Vector3f(-1.7f,  3.0f, -7.5f),
                new Vector3f( 1.3f, -2.0f, -2.5f),
                new Vector3f( 1.5f,  2.0f, -2.5f),
                new Vector3f( 1.5f,  0.2f, -1.5f),
                new Vector3f(-1.3f,  1.0f, -1.5f)
        };
    }

    private float lightRadius = 1.5f;
    private double last0 = 0;
    private int count = 0;

    private void loop() {
        double currentFrame = glfwGetTime();
        deltaTime = currentFrame - lastFrame;
        lastFrame = currentFrame;

        count++;
        if (currentFrame - last0 > 1) {
            System.out.println("FPS: " + count);
            count = 0;
            last0 = currentFrame;
        }

        processInput();

        Renderer.clearColor();
        Renderer.clearDepth();

        Matrix4f projection = new Matrix4f().perspective(Math.toRadians(camera.fov()), (float)width / (float)height, 0.1f, 100.0f);

        Vector3f lightPosition = new Vector3f(1.7f * (float) Math.cos(glfwGetTime() / 4), 1.5f, 1.7f * (float) Math.sin(glfwGetTime() / 4));
        Matrix4f model = new Matrix4f().translate(lightPosition).scale(0.2f);
        lightShader.setUniform("model", model);
        lightShader.setUniform("view", camera.viewMatrix());
        lightShader.setUniform("projection", projection);
        Renderer.draw(lightArray, lightShader);


        for(int i = 0; i < 10; i++) {
            float angle = 20.0f * i;

            model = new Matrix4f().translate(cubePositions[i])
                    .rotate(Math.toRadians(angle),new Vector3f(1.0f, 0.3f, 0.5f).normalize());
            objectShader.setUniform("model", model);
            objectShader.setUniform("view", camera.viewMatrix());
            objectShader.setUniform("projection", projection);
            objectShader.setUniform("viewerPosition", camera.position());
            Renderer.draw(objectArray, objectShader, texture1, texture2);

            glDrawArrays(GL_TRIANGLES, 0, 36);
        }

        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    private void terminate() {
        glfwTerminate();
    }

    public void framebuffer_size_callback(long window, int width, int height) {
        glViewport(0, 0, width, height);
        this.width = width;
        this.height = height;
    }

    public void mouse_callback(long window, double xPos, double yPos) {
        if (firstMouse) {
            lastX = xPos;
            lastY = yPos;
            firstMouse = false;
        }

        double xOffset = xPos - lastX;
        double yOffset = lastY - yPos;
        lastX = xPos;
        lastY = yPos;

        camera.processMouseMovement(xOffset, yOffset, deltaTime);
    }

    void scroll_callback(long window, double xoffset, double yOffset) {
        camera.processMouseScroll(yOffset, deltaTime);
    }

    public void processInput() {
        if(glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
            glfwSetWindowShouldClose(window, true);

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)
            camera.processKeyboard(Camera.Movement.FORWARD, deltaTime);
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)
            camera.processKeyboard(Camera.Movement.BACKWARDS, deltaTime);
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)
            camera.processKeyboard(Camera.Movement.LEFT, deltaTime);
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)
            camera.processKeyboard(Camera.Movement.RIGHT, deltaTime);
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS)
            camera.processKeyboard(Camera.Movement.UP, deltaTime);
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS)
            camera.processKeyboard(Camera.Movement.DOWN, deltaTime);
    }
}