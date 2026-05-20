package com.engine;

import com.engine.graphics.Display;
import com.engine.graphics.Renderer;
import com.engine.util.Vector2D;
import com.engine.world.Map;
import com.engine.world.Player;

public class Engine {

    private Player player;
    private Map map;
    private Renderer renderer;
    private Display display;
    private Input input;

    double moveSpeed = 0.05;
    double rotateSpeed = 0.04;
    double strafeSpeed = 0.02;
    
    private static Engine instance;

    public void initEngine() {
        this.map = new Map("com/assets/level1.txt");
        if (!map.loadSuccessful) {
            throw new IllegalArgumentException("Failed to load file!");
        }

        this.player = new Player(7, 7);
        
        int width = 640;
        int height = 480;
        this.renderer = new Renderer(width, height);
        this.display = new Display("Raycasting", width, height);

        this.input = new Input();
        this.display.addKeyListener(input);
    }

    public void run() {
        while (true) {

            input.update();

            if (input.forward) {
                player.move(player.Direction, moveSpeed, map);
            }
            if (input.back) {
                Vector2D backwardDir = new Vector2D(-player.Direction.x, -player.Direction.y);
                player.move(backwardDir, moveSpeed, map);
            }

            if (input.strafeLeft) {
                Vector2D leftDir = new Vector2D(player.Direction.y, -player.Direction.x);
                player.move(leftDir, strafeSpeed, map);
            }
            if (input.strafeRight) {
                Vector2D rightDir = new Vector2D(-player.Direction.y, player.Direction.x);
                player.move(rightDir, strafeSpeed, map);
            }

            if (input.rotateLeft)  player.rotate(-rotateSpeed);
            if (input.rotateRight) player.rotate(rotateSpeed);

            renderer.render(player, map);
            display.render(renderer.getPixels());

            try {
                Thread.sleep(16); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        instance = new Engine();
        instance.initEngine();
        instance.run();
    }
}