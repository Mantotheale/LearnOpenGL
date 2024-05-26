package org.example;

import org.example.renderer.Model;
import org.example.renderer.Renderer;
import org.example.renderer.buffer.BufferLayout;
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

import java.util.Collections;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Window {
    private final long window;
    private int width;
    private int height;

    private VertexArray cubeVAO;
    private VertexArray planeVAO;
    private VertexArray transparentVAO;
    private ShaderProgram shader;
    private ShaderProgram lightShader;
    private Vector3f lightPosition;

    private double deltaTime = 0;
    private double lastFrame = 0;
    private double lastX;
    private double lastY;
    private double last0 = 0;
    private int count = 0;
    private boolean firstMouse = true;
    private final Camera camera = new Camera();
    private Texture cubeTexture;
    private Texture planeTexture;
    private Texture windowTexture;
    private Vector3f[] vegetation;

    private ShaderProgram shaderSingleColor;
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
        float[] cubeVertices = {
                // positions          // texture Coords
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

        float[] planeVertices = {
                // positions          // texture Coords (note we set these higher than 1 (together with GL_REPEAT as texture wrapping mode). this will cause the floor texture to repeat)
                5.0f, -0.5f,  5.0f,  2.0f, 0.0f,
                -5.0f, -0.5f,  5.0f,  0.0f, 0.0f,
                -5.0f, -0.5f, -5.0f,  0.0f, 2.0f,

                5.0f, -0.5f,  5.0f,  2.0f, 0.0f,
                -5.0f, -0.5f, -5.0f,  0.0f, 2.0f,
                5.0f, -0.5f, -5.0f,  2.0f, 2.0f
        };

        float[] transparentVertices = {
                // positions         // texture Coords (swapped y coordinates because texture is flipped upside down)
                0.0f,  0.5f,  0.0f,  0.0f,  1.0f,
                0.0f, -0.5f,  0.0f,  0.0f,  0.0f,
                1.0f, -0.5f,  0.0f,  1.0f,  0.0f,

                0.0f,  0.5f,  0.0f,  0.0f,  1.0f,
                1.0f, -0.5f,  0.0f,  1.0f,  0.0f,
                1.0f,  0.5f,  0.0f,  1.0f,  1.0f
        };

        BufferLayout cubeLayout = new BufferLayout.Builder().addFloats(3).addFloats(2).build();
        VertexBuffer cubeVBO = new VertexBuffer.Builder().add(cubeVertices).build();
        cubeVAO = new VertexArray(cubeVBO, cubeLayout);

        BufferLayout planeLayout = new BufferLayout.Builder().addFloats(3).addFloats(2).build();
        VertexBuffer planeVBO = new VertexBuffer.Builder().add(planeVertices).build();
        planeVAO = new VertexArray(planeVBO, planeLayout);

        BufferLayout transparentLayout = new BufferLayout.Builder().addFloats(3).addFloats(2).build();
        VertexBuffer transparentVBO = new VertexBuffer.Builder().add(transparentVertices).build();
        transparentVAO = new VertexArray(transparentVBO, transparentLayout);

        String vertexShaderPath = "src/main/resources/shaders/vertexShader.glsl";
        String fragmentShaderPath = "src/main/resources/shaders/fragmentShader.glsl";
        VertexShader vertexShader = new VertexShader(vertexShaderPath);
        FragmentShader fragmentShader = new FragmentShader(fragmentShaderPath);
        shader = new ShaderProgram(vertexShader, fragmentShader);

        vertexShaderPath = "src/main/resources/shaders/vertexShader.glsl";
        fragmentShaderPath = "src/main/resources/shaders/shaderSingleColor.glsl";
        vertexShader = new VertexShader(vertexShaderPath);
        fragmentShader = new FragmentShader(fragmentShaderPath);
        shaderSingleColor = new ShaderProgram(vertexShader, fragmentShader);

        cubeTexture = new Texture("src/main/resources/images/marble.jpg", "a");
        planeTexture = new Texture("src/main/resources/images/metal.png", "b");
        windowTexture = new Texture("src/main/resources/images/blending_transparent_window.png", "c");

        shader.setUniform("texture1", 0);

        vegetation = new Vector3f[]{
                new Vector3f(-1.5f,  0.0f, -0.48f),
                new Vector3f( 1.5f,  0.0f,  0.51f),
                new Vector3f( 0.0f,  0.0f,  0.7f),
                new Vector3f(-0.3f,  0.0f, -2.3f),
                new Vector3f(0.5f,  0.0f, -0.6f)
        };

        Renderer.setClearColor(0, 0, 0, 1);

        glEnable(GL_STENCIL_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private Model backpack;
    private ShaderProgram backpackShader;

    private record VegetationDistance(float distance, Vector3f position) implements Comparable<VegetationDistance> {
        @Override
        public int compareTo(VegetationDistance o) {
            return Float.compare(this.distance, o.distance);
        }
    }

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
        Renderer.clearStencil();

        Matrix4f view = camera.viewMatrix();
        Matrix4f projection = new Matrix4f()
                .perspective(Math.toRadians(camera.fov()), (float)width / (float)height, 0.1f, 100.0f);

        shader.setUniform("view", view);
        shader.setUniform("projection", projection);
        shaderSingleColor.setUniform("view", view);
        shaderSingleColor.setUniform("projection", projection);

        Matrix4f model = new Matrix4f().translate(0, -0.01f, 0);
        shader.setUniform("model", model);
        Renderer.draw(planeVAO, shader, planeTexture);

        model = new Matrix4f().translate(-1.0f, 0.0f, -1.0f);
        shader.setUniform("model", model);
        Renderer.draw(cubeVAO, shader, cubeTexture);
        //Renderer.drawWithOutline(cubeVAO, shader, cubeTexture, shaderSingleColor, model);

        model = new Matrix4f().translate(2.0f, 0.0f, 0f);
        shader.setUniform("model", model);
        Renderer.draw(cubeVAO, shader, cubeTexture);
        //Renderer.drawWithOutline(cubeVAO, shader, cubeTexture, shaderSingleColor, model);

        PriorityQueue<VegetationDistance> orderedVegetation = new PriorityQueue<>(Collections.reverseOrder());
        for (Vector3f v : vegetation) {
            orderedVegetation.add(new VegetationDistance(camera.position().sub(v, new Vector3f()).length(), v));
        }

        while (!orderedVegetation.isEmpty()) {
            model = new Matrix4f().translate(orderedVegetation.poll().position);
            shader.setUniform("model", model);
            Renderer.draw(transparentVAO, shader, windowTexture);
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

    void scroll_callback(long window, double xOffset, double yOffset) {
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