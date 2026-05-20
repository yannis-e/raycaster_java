package com.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Input implements KeyListener {

    private boolean[] keys = new boolean[256];
    
    public boolean forward, back, strafeLeft, strafeRight, rotateLeft, rotateRight;

    public void update() {
        forward     = keys[KeyEvent.VK_W] || keys[KeyEvent.VK_UP];
        back        = keys[KeyEvent.VK_S] || keys[KeyEvent.VK_DOWN];
        strafeLeft  = keys[KeyEvent.VK_A];
        strafeRight = keys[KeyEvent.VK_D];
        rotateLeft  = keys[KeyEvent.VK_LEFT];
        rotateRight = keys[KeyEvent.VK_RIGHT];
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode >= 0 && keyCode < keys.length) {
            keys[keyCode] = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode >= 0 && keyCode < keys.length) {
            keys[keyCode] = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
}