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
    
    String[] textures = {
        "walls/bars_red_4.png",
        "walls/brick_brown_0.png",
        "walls/brick_dark_0.png",
        "walls/brick_gray_0.png",
        "walls/catacombs_0.png",
        "walls/catacombs_12.png",
        "walls/hive_0.png",
        "walls/lab-metal_0.png",
        "walls/lab-rock_0.png",
        "walls/lab-stone_0.png",
        "walls/marble_wall_1.png",
        "walls/mirrored_wall_old.png",
        "walls/snake_0.png",
        "walls/wall_vines_0.png",
        "walls/wall_vines_3.png",
        "walls/zot_blue_0_new.png",
        "floor/cobble_blood_1_new.png",
        "floor/cobble_blood_2_new.png",
        "floor/cobble_blood_3_new.png",
        "floor/cobble_blood_4_new.png",
        "floor/cobble_blood_5_new.png",
        "floor/cobble_blood_6_new.png",
        "floor/cobble_blood_7_new.png",
        "floor/cobble_blood_8_new.png",
        "floor/cobble_blood_9_new.png",
        "floor/cobble_blood_10_new.png"
    };

    private static Engine instance;

    public void initEngine() {
        this.map = new Map("com/assets/level2.txt");
        if (!map.loadSuccessful) {
            throw new IllegalArgumentException("Failed to load file!");
        }

        this.player = new Player(map.findSpawnPosition());
        
        int width = 1920;
        int height = 1080;
        this.renderer = new Renderer(width, height, textures);
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