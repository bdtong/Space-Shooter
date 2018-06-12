/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SecondGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Brandon
 */
public class HUD{
    
    public int health = 100;
    public int lives = 3;
    public String currency = "0";
    
    ImageLoader imgl;
    BufferedImage img;
    BufferedImage img2;
    BufferedImage img3;
    BufferedImage img4;
    
    public boolean speedBought = false;
    public boolean tripleBought = false;
    public boolean rocketBought = false;
    
    public HUD (ImageLoader imgl) {
        this.imgl = imgl;
        img = imgl.grabImage(ID.Player); //grabbing tankImage
        img2 = imgl.grabImage(ID.fastPU);
        img3 = imgl.grabImage(ID.tripleshotPU);
        img4 = imgl.grabImage(ID.rocketPU);
    }
    
    public void tick () {
        
        health = checkborder(health, 0, 100);
         
    }
    
    public void render (Graphics g) {
        
        Graphics2D g2 = (Graphics2D) g;
        
        g.setColor(Color.black);
        g.fillRect(0,0, 320, 130);
        g.setColor(Color.white);
        g.drawRect(0,0, 312, 130);

        g.setColor(Color.gray);
        g.fillRect(15,15, 200, 32);
        g.setColor(Color.green);
        g.fillRect(15, 15, health * 2, 32);
        g.setColor(Color.white);
        g.drawRect(15, 15, 200, 32);
            
        if (lives == 3 || lives == 2 || lives == 1) 
            g2.drawImage(img, 15, 60, 20, 20, null);
        if (lives == 3 || lives == 2)
            g2.drawImage(img, 45, 60, 20, 20, null);
        if (lives == 3)
            g2.drawImage(img, 75, 60, 20, 20, null);
        
        g2.drawString("Currency: ", 15, 105);
        g2.drawString(currency, 70, 105);
        
        g.drawRect(275,85, 20,20);
        if (speedBought == true) 
            g2.drawImage(img2,205,85, 20,20, null);
        
        g.drawRect(240,85, 20, 20);
        if (tripleBought == true) 
            g2.drawImage(img3, 240,85, 20,20, null);
        
        g.drawRect(205,85, 20, 20);
        if (rocketBought == true) 
            g2.drawImage(img4, 275,85, 20,20, null);
        
        
    }
    
    public void speedBought() {
        speedBought = true;
    }
    
    public void tripleBought() {
        tripleBought = true;
    }
    
    public void rocketBought() {
        rocketBought = true;
    }
    
    public void addCurrency(int value) {
        
        int currencyInt = Integer.parseInt(currency);
        currencyInt = currencyInt + value;
        currency = String.valueOf(currencyInt);
        
    }
    
    public int getCurrency() {
        return Integer.parseInt(currency);
    }
    
    public void removeCurrency(int value) {
        
        int currencyInt = Integer.parseInt(currency);
        currencyInt = currencyInt - value;
        currency = String.valueOf(currencyInt);
        
    }
    
    public void restoreHealth() {
        health = 100;
    }
    
    public int checkborder(int var, int min, int max) {
        
        //health bar wont exceed its borders
        if (var >= max) 
            return max;
        else if (var <= min)
            return min;
        else 
            return var;
        
    }
}
