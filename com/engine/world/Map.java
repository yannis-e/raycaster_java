package com.engine.world;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.engine.util.Vector2D;

public class Map {
    public int[][] grid;
    public int width;
    public int height;

    public boolean loadSuccessful;

    public Map(String name) {
        loadSuccessful = loadMap(name);
    }

    public boolean loadMap(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            int index = 0;

            String sizeLine = br.readLine();
            if (sizeLine == null) return false;
            
            String[] tokens = sizeLine.trim().split("\\s+");
            this.width = Integer.parseInt(tokens[0]);
            this.height = Integer.parseInt(tokens[1]);

            this.grid = new int[height][width];

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] splitLine = line.split("\\s+");

                for (int i = 0; i < Math.min(splitLine.length, width); i++) {
                    grid[index][i] = Integer.parseInt(splitLine[i]);
                }

                index++;
                
                if (index >= height) break;
            }
            return true;
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error parsing map file: " + e.getMessage());
            return false;
        }
    }

    public boolean isWall(Vector2D tile) {
        if (tile.x < 0 || tile.x >= width || tile.y < 0 || tile.y >= height) return true; 

        return grid[(int)tile.y][(int)tile.x] > 0;
    }

    public int getWallType(Vector2D tile) {
        if (tile.x < 0 || tile.x >= width || tile.y < 0 || tile.y >= height) return 1;
        return grid[(int)tile.y][(int)tile.x];
    }

    public Vector2D findSpawnPosition() {
    for (int y = 0; y < this.height; y++) {
        for (int x = 0; x < this.width; x++) {
            if (grid[y][x] == 1) {
                grid[y][x] = 0;
                return new Vector2D(x + 0.5, y + 0.5);
            }
        }
    }
    return new Vector2D(2.5, 2.5);
}
}