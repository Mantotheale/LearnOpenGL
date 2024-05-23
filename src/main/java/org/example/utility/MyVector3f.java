package org.example.utility;

import org.joml.Vector3f;

public interface MyVector3f {
    static Vector3f plus(Vector3f a, Vector3f b) {
        return a.add(b, new Vector3f());
    }

    static Vector3f minus(Vector3f a, Vector3f b) {
        return a.sub(b, new Vector3f());
    }

    static Vector3f cross(Vector3f a, Vector3f b) {
        return a.cross(b, new Vector3f());
    }

    static float dot(Vector3f a, Vector3f b) {
        return a.dot(b);
    }

    static Vector3f mul(Vector3f a, float scale) {
        return a.mul(scale, new Vector3f());
    }

    static Vector3f mul(Vector3f a, float xScale, float yScale, float zScale) {
        return a.mul(xScale, yScale, zScale, new Vector3f());
    }

    static Vector3f mul(Vector3f a, Vector3f scale) {
        return a.mul(scale, new Vector3f());
    }

    static Vector3f normalize(Vector3f v) {
        return v.normalize(new Vector3f());
    }
}