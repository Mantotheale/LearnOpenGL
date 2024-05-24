package org.example;

import org.example.renderer.Renderer;
import org.example.renderer.buffer.BufferLayout;
import org.example.renderer.buffer.IndexBuffer;
import org.example.renderer.buffer.VertexArray;
import org.example.renderer.buffer.VertexBuffer;
import org.example.renderer.shader.FragmentShader;
import org.example.renderer.shader.ShaderProgram;
import org.example.renderer.shader.VertexShader;
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
        float vertices[] = {
                -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
                0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
                0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
                0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
                -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
                -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,

                -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
                0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
                -0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
                -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,

                -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
                -0.5f,  0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
                -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
                -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
                -0.5f, -0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
                -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,

                0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
                0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
                0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
                0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
                0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
                0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,

                -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
                0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
                0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
                0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
                -0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,

                -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
                0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
                -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
                -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f
        };

        BufferLayout layout = new BufferLayout.Builder().addFloats(3).addFloats(3).build();
        VertexBuffer vertexBuffer = new VertexBuffer.Builder().add(vertices).build();
        objectArray = new VertexArray(vertexBuffer, layout);

        String vertexShaderPath = "src/main/resources/shaders/vertexShader.glsl";
        String fragmentShaderPath = "src/main/resources/shaders/fragmentShader.glsl";
        VertexShader vertexShader = new VertexShader(vertexShaderPath);
        FragmentShader fragmentShader = new FragmentShader(fragmentShaderPath);
        objectShader = new ShaderProgram(vertexShader, fragmentShader);

        BufferLayout lightLayout = new BufferLayout.Builder().addFloats(3).addFloats(3).build();
        VertexBuffer lightBuffer = new VertexBuffer.Builder().add(vertices).build();
        lightArray = new VertexArray(lightBuffer, lightLayout);

        vertexShaderPath = "src/main/resources/shaders/vertexShader.glsl";
        fragmentShaderPath = "src/main/resources/shaders/lightSourceFragmentShader.glsl";
        vertexShader = new VertexShader(vertexShaderPath);
        fragmentShader = new FragmentShader(fragmentShaderPath);
        lightShader = new ShaderProgram(vertexShader, fragmentShader);

        //lightPosition = new Vector3f(1.2f, 1.0f, 2.0f);

        objectShader.setUniform("objectColor", 1, 0.5f, 0.31f);
        objectShader.setUniform("lightColor", 1, 1, 1);

        Renderer.setClearColor(0, 0, 0, 1);
    }

    private float lightRadius = 1.5f;

    private void loop() {
        double currentFrame = glfwGetTime();
        deltaTime = currentFrame - lastFrame;
        lastFrame = currentFrame;

        processInput();

        Renderer.clearColor();
        Renderer.clearDepth();

        Matrix4f projection = new Matrix4f().perspective(Math.toRadians(camera.fov()), (float)width / (float)height, 0.1f, 100.0f);


        Vector3f lightPosition = new Vector3f(2 * (float) Math.cos(glfwGetTime()), 0, 2 * (float) Math.sin(glfwGetTime()));
        Matrix4f model = new Matrix4f().translate(lightPosition).scale(0.2f);
        lightShader.setUniform("model", model);
        lightShader.setUniform("view", camera.viewMatrix());
        lightShader.setUniform("projection", projection);
        Renderer.draw(lightArray, lightShader);

        model = new Matrix4f();
        objectShader.setUniform("model", model);
        objectShader.setUniform("view", camera.viewMatrix());
        objectShader.setUniform("projection", projection);
        objectShader.setUniform("lightPosition", lightPosition);
        objectShader.setUniform("viewerPosition", camera.position());
        Renderer.draw(objectArray, objectShader);

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