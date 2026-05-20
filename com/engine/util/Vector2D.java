package com.engine.util;

public class Vector2D {
    public double x;
    public double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void add(Vector2D other) {
        this.x += other.x;
        this.y += other.y;
    }

    public Vector2D multiply(double scalar) {
        return new Vector2D(this.x * scalar, this.y * scalar);
    }

    public void rotate(double angle) {
        double oldX = this.x;
        this.x = oldX * Math.cos(angle) - this.y * Math.sin(angle);
        this.y = oldX * Math.sin(angle) + this.y * Math.cos(angle);
    }

    public Vector2D copy() {
        return new Vector2D(this.x, this.y);
    }
}