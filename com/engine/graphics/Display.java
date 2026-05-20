package com.engine.graphics;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.JFrame;

public class Display extends Canvas {
    private static final long serialVersionUID = 1L;

    private JFrame frame;
    private BufferedImage image;
    private int[] pixels;
    private int width;
    private int height;

    public Display(String title, int width, int height) {
        this.width = width;
        this.height = height;

        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        this.pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        Dimension size = new Dimension(width, height);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        this.frame = new JFrame();
        frame.setTitle(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        requestFocus(); 
    }

    public void render(int[] renderPixels) {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        System.arraycopy(renderPixels, 0, this.pixels, 0, renderPixels.length);

        Graphics g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        
        g.dispose();
        bs.show();
    }
}