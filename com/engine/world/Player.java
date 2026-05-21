package com.engine.world;

import com.engine.util.Vector2D;

public class Player {
    public Vector2D Position;
    public Vector2D Direction;
    public Vector2D Plane;

    public void rotate(double a) {
        Direction.rotate(a);
        Plane.rotate(a);
    }

    public void move(Vector2D moveDirection, double speed, Map map) {
        double newX = Position.x + moveDirection.x * speed;
        double newY = Position.y + moveDirection.y * speed;

        double paddingX = (moveDirection.x > 0) ? 0.2 : -0.2;
        double paddingY = (moveDirection.y > 0) ? 0.2 : -0.2;

        if (!map.isWall(new Vector2D(newX + paddingX, Position.y))) {
            Position.x = newX;
        }
        if (!map.isWall(new Vector2D(Position.x, newY + paddingY))) {
            Position.y = newY;
        }
    }
    public Player(Vector2D spawn) {
        this.Position = spawn.copy();
        this.Direction = new Vector2D(1.0, 0.0);
        this.Plane = new Vector2D(0.0, 0.88);
    }
}
