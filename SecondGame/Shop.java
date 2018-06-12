/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SecondGame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 *
 * @author Brandon
 */
public class Shop extends MouseAdapter {
    
    HUD hud; 
    Handler handler;
    GameWorld game;
    ImageLoader imgl;
    BufferedImage img1;
    BufferedImage img2;
    BufferedImage img3;
    BufferedImage img4;   
    
    public Shop(HUD hud, Handler handler, ImageLoader imgl, GameWorld game) {
        this.hud = hud;
        this.handler = handler;
        this.game = game;
        this.imgl = imgl;
        
        img1 = imgl.grabImage(ID.HealthPU);
        img2 = imgl.grabImage(ID.fastPU);
        img3 = imgl.grabImage(ID.rocketPU);
        img4 = imgl.grabImage(ID.tripleshotPU);
        
    }
    
    public void render(Graphics g) {
       
        g.setColor(Color.black);
        g.fillRect(50,200, 220, 500);
        g.setColor(Color.white);
        g.drawRect(50,200, 220, 500);
        
        Font font = new Font("Vernanda", Font.BOLD, 20);
        g.setFont(font);
        g.setColor(Color.white);
        
        g.drawString("-Shop-", 135, 240);
        Font font2 = new Font("Vernanda", Font.BOLD, 15);
        g.setFont(font2);
        g.drawString("click to select", 115, 270);
        
        g.drawImage(img1, 110, 308, null);
        g.drawString("Full HP", 145, 315);
        g.drawString("Cost: 50", 145, 335);
        g.drawRect(90, 285, 150, 70);
        
        g.drawImage(img2, 110, 405, null);
        g.drawString("Speed Up", 145, 410);
        g.drawString("Cost: 200", 145, 430);
        g.drawRect(90, 380, 150, 70);
        
        g.drawImage(img4, 110, 502, null);
        g.drawString("Triple Shot", 145, 510);
        g.drawString("Cost: 500", 145, 530);
        g.drawRect(90, 480, 150, 70);
        
        g.drawImage(img3, 110, 603, null);
        g.drawString("Rocket", 145, 610);
        g.drawString("Cost: 400", 145, 630);
        g.drawRect(90, 580, 150, 70);
        
        g.drawString("close", 145, 680);     
        
    }
    
    public void mousePressed(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();
        
        if (mx >= 90 && mx <= 240) {
            if (my >= 285 && my <= 355) {
                if(hud.getCurrency() >= 50) {
                    hud.removeCurrency(50);
                    hud.restoreHealth();
                }
            }
        }
        if (mx >= 90 && mx <= 240) {
            if (my >= 380 && my <= 450) {
                if(hud.getCurrency() >= 200) {
                    hud.removeCurrency(200);
                    hud.speedBought();
                    handler.player.setGoFast();
                }
            }
        }
        if (mx >= 90 && mx <= 240) {
            if (my >= 480 && my <= 550) {
                if(hud.getCurrency() >= 500) {
                    hud.removeCurrency(500);
                    hud.tripleBought();
                    handler.player.setTriple();
                }
            }
        }
        if (mx >= 90 && mx <= 240) {
            if (my >= 580 && my <= 650) {
                if(hud.getCurrency() >= 400) {
                    hud.removeCurrency(400);
                    hud.rocketBought();
                    handler.player.setRocket();
                }
            }
        }
        if (mx >= 138 && mx <= 188) {
            if (my >= 665 && my <= 685) {
                game.unpause();
            }
        }
                

    }
}
