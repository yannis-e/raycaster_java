package com.engine.graphics;

import com.engine.world.Map;
import com.engine.world.Player;
import com.engine.util.Vector2D;

import java.io.File;

public class Renderer {

    private int width;
    private int height;
    private int[] pixels;

    private int hitWallType;
    private int hitWallSide;
    private Vector2D currentTile;

    private final int texWidth = 64;
    private final int texHeight = 64;

    private Texture[] textures;

    private static final int AIR = 0;
    private static final int TEXTURE_START = 2;
    private static final int CEILING_TEXTURE = 2;

    public Renderer(int width, int height, String[] textureNames) {

        this.width = width;
        this.height = height;
        this.pixels = new int[width * height];

        textures = new Texture[textureNames.length + TEXTURE_START];

        loadTextures(textureNames);
    }

    private void loadTextures(String[] textureNames) {

        for (int i = 0; i < textureNames.length; i++) {
    
            int texIndex = i + TEXTURE_START;
    
            String filePath = "com/assets/textures/" + textureNames[i];
            File file = new File(filePath);
    
            if (!file.exists() || file.isDirectory()) {
                System.out.println("Texture not found: " + filePath);
    
                textures[texIndex] = new Texture(
                    new File[]{ new File("missing") },
                    texWidth,
                    texHeight
                );
    
                continue;
            }
    
            textures[texIndex] = new Texture(
                new File[]{ file },
                texWidth,
                texHeight
            );
        }
    }

    private float getLight(int tileX, int tileY, Player player) {

        double dx = tileX + 0.5 - player.Position.x;
        double dy = tileY + 0.5 - player.Position.y;

        double dist = dx * dx + dy * dy;
        
        return (float)Math.max(0.05, 1.0 / (1.0 + dist * 0.15));
    }

    private int applyLight(int color, float light) {
        int r = (int)(((color >> 16) & 0xFF) * light);
        int g = (int)(((color >> 8) & 0xFF) * light);
        int b = (int)((color & 0xFF) * light);

        return (r << 16) | (g << 8) | b;
    }

    public double calculateDDA(Vector2D rayDir, Player player, Map map) {

        Vector2D start = player.Position;

        int mapX = (int) start.x;
        int mapY = (int) start.y;

        double deltaX = (rayDir.x == 0) ? 1e30 : Math.abs(1 / rayDir.x);
        double deltaY = (rayDir.y == 0) ? 1e30 : Math.abs(1 / rayDir.y);

        double sideX;
        double sideY;

        int stepX;
        int stepY;

        if (rayDir.x < 0) {
            stepX = -1;
            sideX = (start.x - mapX) * deltaX;
        } else {
            stepX = 1;
            sideX = (mapX + 1.0 - start.x) * deltaX;
        }

        if (rayDir.y < 0) {
            stepY = -1;
            sideY = (start.y - mapY) * deltaY;
        } else {
            stepY = 1;
            sideY = (mapY + 1.0 - start.y) * deltaY;
        }

        boolean hit = false;
        int side = 0;

        while (!hit) {

            if (sideX < sideY) {
                sideX += deltaX;
                mapX += stepX;
                side = 0;
            } else {
                sideY += deltaY;
                mapY += stepY;
                side = 1;
            }

            currentTile = new Vector2D(mapX, mapY);

            if (map.isWall(currentTile)) {
                hit = true;
                hitWallType = map.getWallType(currentTile);
                hitWallSide = side;
            }
        }

        double dist;

        if (side == 0) {
            dist = (mapX - start.x + (1 - stepX) / 2.0) / rayDir.x;
        } else {
            dist = (mapY - start.y + (1 - stepY) / 2.0) / rayDir.y;
        }

        return Math.max(dist, 0.01);
    }

    public void render(Player player, Map map) {

        for (int x = 0; x < width; x++) {

            double cameraX = 2 * x / (double) width - 1;

            Vector2D rayDir = new Vector2D(
                    player.Direction.x + player.Plane.x * cameraX,
                    player.Direction.y + player.Plane.y * cameraX
            );

            double wallDist = calculateDDA(rayDir, player, map);

            int lineHeight = (int) (height / wallDist);

            int drawStart = Math.max(0, -lineHeight / 2 + height / 2);
            int drawEnd = Math.min(height - 1, lineHeight / 2 + height / 2);

            double wallX;

            if (hitWallSide == 0) {
                wallX = player.Position.y + wallDist * rayDir.y;
            } else {
                wallX = player.Position.x + wallDist * rayDir.x;
            }

            wallX -= Math.floor(wallX);

            int texX = (int) (wallX * texWidth);

            if (hitWallSide == 0 && rayDir.x > 0) texX = texWidth - texX - 1;
            if (hitWallSide == 1 && rayDir.y < 0) texX = texWidth - texX - 1;

            double step = 1.0 * texHeight / lineHeight;
            double texPos = (drawStart - height / 2.0 + lineHeight / 2.0) * step;

            float wallLight = getLight((int)currentTile.x, (int)currentTile.y, player);

            for (int y = drawStart; y < drawEnd; y++) {

                int texY = (int) texPos & (texHeight - 1);
                texPos += step;

                int texId = hitWallType;
                if (texId < TEXTURE_START || texId >= textures.length || textures[texId] == null) {
                    texId = TEXTURE_START;
                }

                int color = textures[texId].get(texX, texY);

                color = applyLight(color, wallLight);

                if (hitWallSide == 1) {
                    color = (color >> 1) & 0x7F7F7F;
                }

                pixels[x + y * width] = color;
            }

            double floorXWall, floorYWall;

            int mapX = (int) currentTile.x;
            int mapY = (int) currentTile.y;

            if (hitWallSide == 0 && rayDir.x > 0) {
                floorXWall = mapX;
                floorYWall = mapY + wallX;
            } else if (hitWallSide == 0 && rayDir.x < 0) {
                floorXWall = mapX + 1.0;
                floorYWall = mapY + wallX;
            } else if (hitWallSide == 1 && rayDir.y > 0) {
                floorXWall = mapX + wallX;
                floorYWall = mapY;
            } else {
                floorXWall = mapX + wallX;
                floorYWall = mapY + 1.0;
            }

            for (int y = drawEnd + 1; y < height; y++) {

                double currentDist = height / (2.0 * y - height);
                double weight = currentDist / wallDist;

                double fx = weight * floorXWall + (1 - weight) * player.Position.x;
                double fy = weight * floorYWall + (1 - weight) * player.Position.y;

                int tx = (int) (fx * texWidth) & (texWidth - 1);
                int ty = (int) (fy * texHeight) & (texHeight - 1);

                int tileX = (int) Math.floor(fx);
                int tileY = (int) Math.floor(fy);

                int floorTex =
                        TEXTURE_START +
                        Math.abs(tileX * 374761393 + tileY * 668265263)
                        % (textures.length - TEXTURE_START);

                int floorColor = textures[floorTex].get(tx, ty);
                int ceilingColor = textures[CEILING_TEXTURE].get(tx, ty);

                float light = getLight(tileX, tileY, player);

                floorColor = applyLight(floorColor, light);
                ceilingColor = applyLight(ceilingColor, light);

                pixels[x + y * width] = floorColor;
                pixels[x + (height - y) * width] = ceilingColor;
            }
        }

        drawMinimap(player, map);
    }

    public void drawMinimap(Player player, Map map) {

        int tileSize = 10;

        for (int y = 0; y < map.height; y++) {
            for (int x = 0; x < map.width; x++) {

                int color = 0x222222;

                if (map.getWallType(new Vector2D(x, y)) != AIR) {
                    color = 0x888888;
                }

                int sx = x * tileSize;
                int sy = y * tileSize;

                for (int ty = 0; ty < tileSize; ty++) {
                    for (int tx = 0; tx < tileSize; tx++) {

                        int px = sx + tx;
                        int py = sy + ty;

                        if (px >= 0 && px < width && py >= 0 && py < height) {
                            pixels[px + py * width] = color;
                        }
                    }
                }
            }
        }

        int px = (int) (player.Position.x * tileSize);
        int py = (int) (player.Position.y * tileSize);

        pixels[px + py * width] = 0xFF0000;
    }

    public int[] getPixels() {
        return pixels;
    }
}