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
public class EnemyBasic extends GameObject{
    
    int interval = 0;
    int health = 3;
    HUD hud;
    
    public EnemyBasic(int x, int y, ID id, Handler handler, HUD hud, ImageLoader imgl, int xMod, int yMod) {
        super(x, y, id, (short) 0, handler, imgl); //gameobject properties
        setVelX(xMod);
        setVelY(yMod);
        setValue(10);
        this.hud = hud;
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, 30, 30);
    }

    public void tick() {
        
        if (health == 0){
            die();
        }
       
        if(doAction2 == true) {
            action2();
            resetdoAction2();
        }
        
        x += velX;
        y += velY;
        
        interval++;
        if (interval == 50) {
            action();
            interval = 0;
        }
        
        checkBorder();
    
    }
    
     public void action() {
        
        handler.addObject(new Bullet(x + 10, y + 10 ,ID.Bullet, imgl, (short) 90, this.getID(), handler)); //adjusted bullet position for accurate center
        
    }
     
    public void action2() {
        health--;
    }

    public void render(Graphics g) {
  
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(img, x, y, null);

    }
    
    public void checkBorder() {
        if (x < 0) {
            handler.removeObject(this);
        }
        if (x >= 280) {
            handler.removeObject(this);
        }
        if (y < 0) {
            handler.removeObject(this);
        }
        if (y >= 740) {
            handler.removeObject(this);
        }
    }
    
    public void die() {
        hud.addCurrency(value);
        handler.removeObject(this);
    }
}
