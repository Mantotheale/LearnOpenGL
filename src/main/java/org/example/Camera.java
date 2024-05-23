package org.example;

import org.example.utility.MyVector3f;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private static final Vector3f WORLD_UP = new Vector3f(0, 1, 0);

    private Vector3f position = new Vector3f(0, 0, 0);
    private Vector3f front;
    private Vector3f up;
    private Vector3f right;
    private double yaw = -90;
    private double pitch = 0;

    private float fov = 45;
    private final float movementSpeed = 2.5f;
    private final double mouseSensitivity = 100;
    private final double scrollSensitivity = 1000;

    public Camera() {
        updateView();
    }

    public Camera(Vector3f position, float yaw, float pitch) {
        this.position = position;
        this.yaw = yaw;
        this.pitch = pitch;

        updateView();
    }

    public Matrix4f viewMatrix() {
        return new Matrix4f().lookAt(position, MyVector3f.plus(position, front), up);
    }

    public float fov() {
        return fov;
    }

    public void processKeyboard(Movement direction, double deltaTime) {
        double velocity = movementSpeed * deltaTime;

        switch (direction) {
            case FORWARD -> position.add(MyVector3f.mul(front, (float) velocity));
            case BACKWARDS -> position.sub(MyVector3f.mul(front, (float) velocity));
            case RIGHT -> position.add(MyVector3f.mul(right, (float) velocity));
            case LEFT -> position.sub(MyVector3f.mul(right, (float) velocity));
            case UP -> position.add(MyVector3f.mul(up, (float) velocity));
            case DOWN -> position.sub(MyVector3f.mul(up, (float) velocity));
        }
    }

    public void processMouseMovement(double xOffset, double yOffset, double deltaTime) {
        yaw += xOffset * mouseSensitivity * deltaTime;
        pitch += yOffset * mouseSensitivity * deltaTime;

        if (pitch > 89)
            pitch = 89;
        if (pitch < -89)
            pitch = -89;

        updateView();
    }

    public void processMouseScroll(double yOffset, double deltaTime) {
        fov -= yOffset * scrollSensitivity * deltaTime;

        if (fov < 1)
            fov = 1;
        if (fov > 45)
            fov = 45;
    }


    private void updateView() {
        front = MyVector3f.fromEuler(yaw, pitch);
        right = MyVector3f.cross(front, WORLD_UP).normalize();
        up = MyVector3f.cross(right, front).normalize();
    }

    public enum Movement {
        FORWARD,
        BACKWARDS,
        LEFT,
        RIGHT,
        UP,
        DOWN
    }
}