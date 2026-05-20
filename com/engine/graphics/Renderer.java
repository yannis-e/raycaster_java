package com.engine.graphics;

import com.engine.world.Map;
import com.engine.world.Player;
import com.engine.util.Vector2D;

public class Renderer {
    private int width;
    private int height;
    private int[] pixels;

    private int hitWallType;
    private int hitWallSide; 
    private double wallXCoord;

    public Renderer(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new int[width * height];
    }

    public void drawMinimap(Player player, Map map) {
        int tileSize = 10;
        int padding = 0;  
        int mapWidth = map.width;
        int mapHeight = map.height;

        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                int color = 0x222222;
                
                Vector2D tileCheck = new Vector2D(x, y);
                if (map.getWallType(tileCheck) > 0) {
                    color = 0x888888;
                }

                int startScreenX = padding + (x * tileSize);
                int startScreenY = padding + (y * tileSize);

                for (int tileY = 0; tileY < tileSize - 1; tileY++) {
                    for (int tileX = 0; tileX < tileSize - 1; tileX++) {
                        int pixelX = startScreenX + tileX;
                        int pixelY = startScreenY + tileY;

                        if (pixelX >= 0 && pixelX < width && pixelY >= 0 && pixelY < height) {
                            pixels[pixelX + pixelY * width] = color;
                        }
                    }
                }
            }
        }

        int playerScreenX = padding + (int) (player.Position.x * tileSize);
        int playerScreenY = padding + (int) (player.Position.y * tileSize);
        int playerColor = 0xFF0000;

        for (int h = -1; h <= 1; h++) {
            for (int w = -1; w <= 1; w++) {
                int pX = playerScreenX + w;
                int pY = playerScreenY + h;
                if (pX >= 0 && pX < width && pY >= 0 && pY < height) {
                    pixels[pX + pY * width] = playerColor;
                }
            }
        }
    }

    public double calculateDDA(Vector2D vRayDir, Player player, Map map) {
        Vector2D vRayStart = new Vector2D(player.Position.x, player.Position.y);

        int mapX = (int) vRayStart.x;
        int mapY = (int) vRayStart.y;

        double deltaDistX = (vRayDir.x == 0) ? 1e30 : Math.abs(1 / vRayDir.x);
        double deltaDistY = (vRayDir.y == 0) ? 1e30 : Math.abs(1 / vRayDir.y);

        double sideDistX;
        double sideDistY;

        int stepX;
        int stepY;

        if (vRayDir.x < 0) {
            stepX = -1;
            sideDistX = (vRayStart.x - mapX) * deltaDistX;
        } else {
            stepX = 1;
            sideDistX = (mapX + 1.0 - vRayStart.x) * deltaDistX;
        }

        if (vRayDir.y < 0) {
            stepY = -1;
            sideDistY = (vRayStart.y - mapY) * deltaDistY;
        } else {
            stepY = 1;
            sideDistY = (mapY + 1.0 - vRayStart.y) * deltaDistY;
        }

        boolean hit = false;
        int side = 0;

        while (!hit) {
            if (sideDistX < sideDistY) {
                sideDistX += deltaDistX;
                mapX += stepX;
                side = 0;
            } else {
                sideDistY += deltaDistY;
                mapY += stepY;
                side = 1;
            }

            Vector2D currentTile = new Vector2D(mapX, mapY);
            if (map.isWall(currentTile)) {
                hit = true;
                this.hitWallType = map.getWallType(currentTile);
                this.hitWallSide = side;
            }
        }

        double perpWallDist;
        if (side == 0) {
            perpWallDist = (mapX - vRayStart.x + (1 - stepX) / 2.0) / vRayDir.x;
            this.wallXCoord = vRayStart.y + perpWallDist * vRayDir.y;
        } else {
            perpWallDist = (mapY - vRayStart.y + (1 - stepY) / 2.0) / vRayDir.y;
            this.wallXCoord = vRayStart.x + perpWallDist * vRayDir.x;
        }
        this.wallXCoord -= Math.floor(this.wallXCoord);

        return perpWallDist <= 0 ? 0.01 : perpWallDist;
    }

    public void render(Player player, Map map) {
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = (i < pixels.length / 2) ? 0x333333 : 0x111111;
        }

        for (int x = 0; x < width; x++) {
            double cameraX = 2 * x / (double) width - 1; 
            
            Vector2D rayDir = new Vector2D(
                player.Direction.x + player.Plane.x * cameraX,
                player.Direction.y + player.Plane.y * cameraX
            );

            double wallDistance = calculateDDA(rayDir, player, map);

            int lineHeight = (int) (height / wallDistance);

            int drawStart = -lineHeight / 2 + height / 2;
            if (drawStart < 0) drawStart = 0;
            int drawEnd = lineHeight / 2 + height / 2;
            if (drawEnd >= height) drawEnd = height - 1;

            int color = 0x00FF00; 
            if (hitWallType == 2) color = 0xFF0000; 
            
            if (hitWallSide == 1) {
                color = (color >> 1) & 0x7F7F7F;
            }

            for (int y = drawStart; y < drawEnd; y++) {
                pixels[x + y * width] = color;
            }
        }
        drawMinimap(player, map);
    }

    public int[] getPixels() {
        return pixels;
    }
}