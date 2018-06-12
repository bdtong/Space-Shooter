/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SecondGame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Observable;

/**
 *
 * @author Owner
 */
public class PlayerListener extends Observable implements KeyListener{
        
    private Handler handler;
    private boolean[] keyDown = new boolean[4];

    public PlayerListener (Handler handler) {
        this.handler = handler;
        
        keyDown[0] = false;
        keyDown[1] = false;
        keyDown[2] = false;
        keyDown[3] = false;
        
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_W) {
            if (handler.player.getGoFast() == false) {
                handler.player.setVelY(-5);
            } else {
                handler.player.setVelY(-10);
            }
            keyDown[0] = true;
        }
        if (key == KeyEvent.VK_S) {
             if (handler.player.getGoFast() == false) {
                handler.player.setVelY(5);
            } else {
                handler.player.setVelY(10);
            }
            keyDown[1] = true;
        }
        if (key == KeyEvent.VK_A) {
             if (handler.player.getGoFast() == false) {
                handler.player.setVelX(-5);
            } else {
                handler.player.setVelX(-10);
            }
            keyDown[2] = true;
        }
        if (key == KeyEvent.VK_D) {          
             if (handler.player.getGoFast() == false) {
                handler.player.setVelX(5);
            } else {
                handler.player.setVelX(10);
            }
            keyDown[3] = true;
        }
                
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();     
        
        if (key == KeyEvent.VK_W) 
            keyDown[0] = false;
        if (key == KeyEvent.VK_S) 
            keyDown[1] = false;
        if (key == KeyEvent.VK_A) 
            keyDown[2] = false;
        if (key == KeyEvent.VK_D) 
            keyDown[3] = false;
        if (!keyDown[0] && !keyDown[1])
            handler.player.setVelY(0);
        if (!keyDown[2] && !keyDown[3])
            handler.player.setVelX(0);
        if (key == KeyEvent.VK_SPACE)
            handler.player.setdoAction();
        if (key == KeyEvent.VK_B)
            handler.player.setdoAction2();
         
    }
    
}
