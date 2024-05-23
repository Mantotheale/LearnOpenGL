package org.example.utility;

import org.joml.Vector3f;

public class ImmutableVector3f {
    private final Vector3f vector;

    private ImmutableVector3f(Vector3f v) {
        vector = v;
    }

    public ImmutableVector3f plus(Vector3f v) {
        return new ImmutableVector3f(vector.add(v, new Vector3f()));
    }

    public ImmutableVector3f plus(ImmutableVector3f v) {
        return plus(v.vector);
    }

    public ImmutableVector3f minus(Vector3f v) {
        return new ImmutableVector3f(vector.sub(v, new Vector3f()));
    }

    public ImmutableVector3f minus(ImmutableVector3f v) {
        return minus(v.vector);
    }

    public ImmutableVector3f cross(Vector3f v) {
        return new ImmutableVector3f(vector.cross(v, new Vector3f()));
    }

    public ImmutableVector3f cross(ImmutableVector3f v) {
        return cross(v.vector);
    }

    public float dot(Vector3f v) {
        return vector.dot(v);
    }

    public float dot(ImmutableVector3f v) {
        return dot(v.vector);
    }

    public ImmutableVector3f normalize() {
        return new ImmutableVector3f(vector.normalize(new Vector3f()));
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