package org.example.utility;

import org.joml.Vector3f;

public class ImmutableVector3f {
    private final Vector3f vector;

    private ImmutableVector3f(Vector3f v) {
        vector = v;
    }

    public ImmutableVector3f plus(Vector3f b) {
        return new ImmutableVector3f(MyVector3f.plus(vector, b));
    }

    public ImmutableVector3f plus(ImmutableVector3f b) {
        return plus(b.vector);
    }

    public ImmutableVector3f minus(Vector3f b) {
        return new ImmutableVector3f(MyVector3f.minus(vector, b));
    }

    public ImmutableVector3f minus(ImmutableVector3f b) {
        return minus(b.vector);
    }

    public ImmutableVector3f cross(Vector3f b) {
        return new ImmutableVector3f(MyVector3f.cross(vector, b));
    }

    public ImmutableVector3f cross(ImmutableVector3f b) {
        return cross(b.vector);
    }

    public float dot(Vector3f b) {
        return vector.dot(b);
    }

    public float dot(ImmutableVector3f b) {
        return dot(b.vector);
    }

    public ImmutableVector3f normalize() {
        return new ImmutableVector3f(MyVector3f.normalize(vector));
    }

    public Vector3f value() {
        return new Vector3f(vector);
    }

    public static ImmutableVector3f of(Vector3f v) {
        return new ImmutableVector3f(new Vector3f(v));
    }

    public static ImmutableVector3f of(float x, float y, float z) {
        return new ImmutableVector3f(new Vector3f(x, y, z));
    }

    public static ImmutableVector3f ZERO = new ImmutableVector3f(new Vector3f());
}