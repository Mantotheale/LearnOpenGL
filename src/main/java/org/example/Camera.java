package org.example;

import org.example.utility.MyVector3f;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Vector3f position = new Vector3f(0, 0, 0);
    private Vector3f front = new Vector3f(0, 0, -1);
    private Vector3f up;
    private Vector3f right;
    private Vector3f worldUp = new Vector3f(0, 1, 0);
    private float yaw = -90;

    private float pitch = 0;
    private float movementSpeed = 2.5f;
    private float mouseSensitivity = 0.1f;
    private float fov = 45;

    public Camera() {
        updateCameraVectors();
    }

    public Camera(Vector3f position, Vector3f worldUp, float yaw, float pitch) {
        this.position = position;
        this.worldUp = worldUp;
        this.yaw = yaw;
        this.pitch = pitch;

        updateCameraVectors();
    }

    public Matrix4f viewMatrix() {
        return new Matrix4f().lookAt(position, MyVector3f.plus(position, front), up);
    }

    public float fov() {
        return fov;
    }

    public void processKeyboard(Movement direction, float deltaTime) {
        float velocity = movementSpeed * deltaTime;
        if (direction == Movement.FORWARD)
            position.add(MyVector3f.mul(front, velocity));
        if (direction == Movement.BACKWARDS)
            position.sub(MyVector3f.mul(front, velocity));
        if (direction == Movement.LEFT)
            position.sub(MyVector3f.mul(right, velocity));
        if (direction == Movement.RIGHT)
            position.add(MyVector3f.mul(right, velocity));
    }

    public void processMouseMovement(float xoffset, float yoffset) {
        yaw += xoffset * mouseSensitivity;
        pitch += yoffset * mouseSensitivity;

        if (pitch > 89)
            pitch = 89;
        if (pitch < -89)
            pitch = -89;

        updateCameraVectors();
    }

    public void processMouseScroll(float yoffset) {
        fov -= yoffset;

        if (fov < 1)
            fov = 1;
        if (fov > 45)
            fov = 45;
    }


    private void updateCameraVectors()
    {
        front = new Vector3f(
                /*Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians((pitch))),
                        Math.sin(Math.toRadians(pitch)),
                        -Math.cos(Math.toRadians(yaw)) * Math.sin(Math.toRadians(pitch))*/
                Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)),
                Math.sin(Math.toRadians(pitch)),

                Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch))
        )
                .normalize();

        right = MyVector3f.cross(front, worldUp).normalize();
        up = MyVector3f.cross(right, front).normalize();
    }

    public enum Movement {
        FORWARD,
        BACKWARDS,
        LEFT,
        RIGHT;
    };
}