package com.engine;

import com.engine.graphics.Display;
import com.engine.world.Map;
import com.engine.world.Player;

public class Engine {

    static Player player;
    static Map map;
    static Display display;
    
    private static void initEngine() {
        map = new Map("com/assets/level1.txt");
        
        if (!map.loadSuccessful) {
            throw new IllegalArgumentException("Failed to load file!"); 
        }

        player = new Player(2.5, 2.5);
        //this.renderer = new Renderer(640, 480);
        display = new Display("Retro Raycast Engine", 640, 480);
    }

    public static void main(String[] args) {
        initEngine();
    }
}
