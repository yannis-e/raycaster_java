package com.engine.graphics;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.io.File;

public class Texture {
    public  int width = 0;
    public  int height = 0;
    private int currentFrame = 0;
    private int maxFrames;
    private boolean animated;
    private int[][] pixels;

    public Texture(File[] framePaths, int width, int height) {
        this.width = width;
        this.height = height;
    
        this.pixels = new int[framePaths.length][width * height];
        maxFrames = framePaths.length;
        if(maxFrames>1) {
            animated = true;
        } else {
            animated = false;
        }
    
        if (framePaths == null || framePaths.length == 0 || framePaths[0] == null) {
            createFallbackTexture(width, height);
            return;
        }
    
        try {
            BufferedImage img = ImageIO.read(framePaths[0]);
    
            if (img == null) {
                createFallbackTexture(width, height);
                return;
            }
    
            BufferedImage scaled = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_RGB
            );
    
            Graphics2D g = scaled.createGraphics();
            g.drawImage(img, 0, 0, width, height, null);
            g.dispose();
    
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    pixels[0][x + y * width] = scaled.getRGB(x, y);
                }
            }
    
        } catch (Exception e) {
            createFallbackTexture(width, height);
        }
    }

    private void createFallbackTexture(int texWidth, int texHeight) {
        for (int y = 0; y < texHeight; y++) {
            for (int x = 0; x < texWidth; x++) {

                boolean checker = ((x / 16) + (y / 16)) % 2 == 0;
                int color = checker ? 0xAAAAAA : 0x555555;

                pixels[0][x + y * width] = color;
            }
        }
    }

    public int get(int x, int y) {
        return pixels[currentFrame][(y & (height - 1)) * width + (x & (width - 1))];
    }

    public void nextFrame() {
        if (animated) {
            currentFrame += 1;
        }
    }
}