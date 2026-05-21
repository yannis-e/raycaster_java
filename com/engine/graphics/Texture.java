package com.engine.graphics;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.io.File;

public class Texture {
    public final int width;
    public final int height;
    private int currentFrame;
    private int maxFrames;
    private boolean animated;
    private int[][] pixels;

    public Texture(File[] framePaths, int width, int height) {
        if(framePaths.length > 1) animated = true;
        try {
            this.width = width;
            this.height = height;
            this.pixels = new int[0][width * height];

            BufferedImage img = ImageIO.read(framePaths[0]);

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
                    pixels[0][x + y * width] = image.getRGB(x, y);
                }
            }
        } catch(Exception e) {

        }
    }

    public int get(int x, int y) {
        return pixels[0][(y & (height - 1)) * width + (x & (width - 1))];
    }
}