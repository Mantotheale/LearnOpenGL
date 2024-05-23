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
import org.example.utility.ImmutableVector3f;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Window {
    private final long window;
    private int width;
    private int height;

    private VertexArray vertexArray;
    private VertexBuffer vertexBuffer;
    private Texture texture1;
    private Texture texture2;
    private IndexBuffer indexBuffer;
    private ShaderProgram shaderProgram;
    private Vector3f[] cubePositions;
    private float deltaTime = 0;
    private float lastFrame = 0;
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
    }

    public void run() {
        init();

        while (!glfwWindowShouldClose(window)) {
            loop();
        }

        terminate();
    }

    private void init() {
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glfwSetFramebufferSizeCallback(window, this::framebuffer_size_callback);
        glViewport(0, 0, width, height);
        glEnable(GL_DEPTH_TEST);
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glfwSetCursorPosCallback(window, this::mouse_callback);
        glfwSetScrollCallback(window, this::scroll_callback);
        lastX = (float) width / 2;
        lastY = (float) height / 2;

        float[] vertices = {
                -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
                0.5f, -0.5f, -0.5f,  1.0f, 0.0f,
                0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,

                -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
                0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
                -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,
                -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,

                -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
                -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
                -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

                0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
                0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

                -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
                0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
                0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
                -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,

                -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
                0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
                -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,
                -0.5f,  0.5f, -0.5f,  0.0f, 1.0f
        };

        /*int[] indices = {
                0, 1, 3, // first triangle
                1, 2, 3  // second triangle
        };*/

        String image1 = "src/main/resources/images/container.jpg";
        String image2 = "src/main/resources/images/awesomeface.png";

        String vertexShaderPath = "src/main/resources/shaders/vertexShader.glsl";
        String fragmentShaderPath = "src/main/resources/shaders/fragmentShader.glsl";

        BufferLayout layout = new BufferLayout.Builder().
                addFloats(3).addFloats(2).build();

        indexBuffer = new IndexBuffer(new int[] { 0, 1, 3, 1, 2, 3});
        vertexBuffer = new VertexBuffer.Builder()
                .add(vertices)
                .build();
        vertexArray = new VertexArray(vertexBuffer, layout);

        vertexArray.bindIndexBuffer(indexBuffer);

        texture1 = new Texture(image1);
        texture2 = new Texture(image2);

        VertexShader vertexShader = new VertexShader(vertexShaderPath);
        FragmentShader fragmentShader = new FragmentShader(fragmentShaderPath);

        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);

        shaderProgram.setUniform("texture1", 0);
        shaderProgram.setUniform("texture2", 1);

        Renderer.setClearColor(0.2f, 0.3f, 0.3f, 1.0f);


        cubePositions = new Vector3f[] {
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

    private void loop() {
        float currentFrame = (float) glfwGetTime();
        deltaTime = currentFrame - lastFrame;
        lastFrame = currentFrame;

        processInput();

        Renderer.clearColor();
        Renderer.clearDepth();

        Matrix4f projection = new Matrix4f().perspective(Math.toRadians(camera.fov()), (float)width / (float)height, 0.1f, 100.0f);

        shaderProgram.setUniform("view", camera.viewMatrix());
        shaderProgram.setUniform("projection", projection);

        for(int i = 0; i < 10; i++) {
            float angle = 20.0f * i;

            Matrix4f model = new Matrix4f()
                    .translate(cubePositions[i]).
                    rotate(Math.toRadians(angle), new Vector3f(1.0f, 0.3f, 0.5f).normalize());
            shaderProgram.setUniform("model", model);

            Renderer.draw(vertexArray, shaderProgram, texture1, texture2);
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

    public void mouse_callback(long window, double xpos, double ypos) {
        if (firstMouse) {
            lastX = xpos;
            lastY = ypos;
            firstMouse = false;
        }

        double xOffset = xpos - lastX;
        double yOffset = lastY - ypos;
        lastX = xpos;
        lastY = ypos;

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