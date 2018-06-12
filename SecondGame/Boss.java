/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SecondGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 *
 * @author Brandon
 */
public class Boss extends GameObject{
    
    int interval = 0; //interval of doing action()
    int health = 50;
    HUD hud;
    
    boolean isDead = false;
    int phase;
    
    public Boss(int x, int y, ID id, Handler handler, HUD hud, ImageLoader imgl, int xMod, int yMod) {
        super(x, y, id, (short) 0, handler, imgl); //gameobject properties
        setVelX(xMod);
        setVelY(yMod);
        setValue(1000);
        this.hud = hud;
        phase = 1;
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, 100, 100);
    }

    public void tick() {
        
        if (health == 0){
            die();
        }
       
        if(doAction2 == true) { //taking damage
            action2();
            resetdoAction2();
        }
        
        x += velX;
        y += velY;
        
        
        
        interval++;
        if (interval == 20) { //at 20 calls of boss.tick()
            action();
            interval = 0;
        }
    
    }
    
     public void action() {
         
         if (phase == 1) {
         
         handler.addObject(new Bullet(x + 50, y + 50 ,ID.Bullet, imgl, angle, this.getID(), handler)); 
            handler.addObject(new Bullet(x + 50, y + 50 ,ID.Bullet, imgl, (short)(angle+10), this.getID(), handler)); 
            handler.addObject(new Bullet(x + 50, y + 50 ,ID.Bullet, imgl, (short)(angle-10), this.getID(), handler)); 
            
             handler.addObject(new Bullet(x + 50, y + 50 ,ID.Bullet, imgl, (short)(angle + 90), this.getID(), handler)); 
            handler.addObject(new Bullet(x + 50, y + 50 ,ID.Bullet, imgl, (short)(angle+10 + 90), this.getID(), handler)); 
            handler.addObject(new Bullet(x + 50, y + 50 ,ID.Bullet, imgl, (short)(angle-10 + 90), this.getID(), handler)); 
            
             handler.addObject(new Bullet(x + 50, y + 50 ,ID.Bullet, imgl, (short)(angle + 180), this.getID(), handler)); 
            handler.addObject(new Bullet(x + 50, y + 50 ,ID.Bullet, imgl, (short)(angle+10 + 180), this.getID(), handler)); 
            handler.addObject(new Bullet(x + 50, y + 50 ,ID.Bullet, imgl, (short)(angle-10 + 180), this.getID(), handler)); 
            
             handler.addObject(new Bullet(x + 50, y + 50 ,ID.Bullet, imgl, (short)(angle + 270), this.getID(), handler)); 
            handler.addObject(new Bullet(x + 50, y + 50 ,ID.Bullet, imgl, (short)(angle+10 + 270), this.getID(), handler)); 
            handler.addObject(new Bullet(x + 50, y + 50 ,ID.Bullet, imgl, (short)(angle-10 + 270), this.getID(), handler)); 
         } else if (phase == 2) {
             handler.addObject(new Rocket(x + 50, y + 50 ,ID.Rocket, imgl, angle, this.getID(), handler)); 
            handler.addObject(new Rocket(x + 50, y + 50 ,ID.Rocket, imgl, (short)(angle+10), this.getID(), handler)); 
            handler.addObject(new Rocket(x + 50, y + 50 ,ID.Rocket, imgl, (short)(angle-10), this.getID(), handler)); 
            
             handler.addObject(new Rocket(x + 50, y + 50 ,ID.Rocket, imgl, (short)(angle + 90), this.getID(), handler)); 
            handler.addObject(new Rocket(x + 50, y + 50 ,ID.Rocket, imgl, (short)(angle+10 + 90), this.getID(), handler)); 
            handler.addObject(new Rocket(x + 50, y + 50 ,ID.Rocket, imgl, (short)(angle-10 + 90), this.getID(), handler)); 
            
             handler.addObject(new Rocket(x + 50, y + 50 ,ID.Rocket, imgl, (short)(angle + 180), this.getID(), handler)); 
            handler.addObject(new Rocket(x + 50, y + 50 ,ID.Rocket, imgl, (short)(angle+10 + 180), this.getID(), handler)); 
            handler.addObject(new Rocket(x + 50, y + 50 ,ID.Rocket, imgl, (short)(angle-10 + 180), this.getID(), handler)); 
            
             handler.addObject(new Rocket(x + 50, y + 50 ,ID.Rocket, imgl, (short)(angle + 270), this.getID(), handler)); 
            handler.addObject(new Rocket(x + 50, y + 50 ,ID.Rocket, imgl, (short)(angle+10 + 270), this.getID(), handler)); 
            handler.addObject(new Rocket(x + 50, y + 50 ,ID.Rocket, imgl, (short)(angle-10 + 270), this.getID(), handler)); 
         } else if (phase == 3) {
            handler.addObject(new Bullet(x + 50, y + 50 ,ID.Bullet, imgl, (short)(angle + 90), this.getID(), handler)); 
            handler.addObject(new Bullet(x + 50, y + 50 ,ID.Bullet, imgl, (short)(angle+10 + 90), this.getID(), handler)); 
            handler.addObject(new Bullet(x + 50, y + 50 ,ID.Bullet, imgl, (short)(angle-10 + 90), this.getID(), handler)); 
            handler.addObject(new Bullet(x + 50, y + 50 ,ID.Bullet, imgl, (short)(angle + 20 + 90), this.getID(), handler)); 
            handler.addObject(new Bullet(x + 50, y + 50 ,ID.Bullet, imgl, (short)(angle- 20 + 90), this.getID(), handler));  
         }
        
    }
     
     public void setPhase(int num) {
         phase = num;
     }
     
    public void action2() {
        health--;
    }

    public void render(Graphics g) {
  
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(img, x, y, null);

    }

    public boolean isDead() {
        return isDead;
    }
    
    
    public void die() {
        hud.addCurrency(value);
        isDead = true;
        handler.removeObject(this);
    }
}