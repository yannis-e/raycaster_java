package com.engine.world;

import com.engine.util.Vector2D;

public class Player {
    Vector2D Position;
    Vector2D Direction;
    Vector2D Plane;

    public Player(double spawnX, double spawnY) {
        this.Position = new Vector2D(spawnX, spawnY);
        this.Direction = new Vector2D(1.0, 0.0);
        this.Plane = new Vector2D(0.0, 0.66);
    }
}
