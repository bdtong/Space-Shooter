/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SecondGame;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

/**
 *
 * @author Owner
 */
public class Bullet extends GameObject{
    
    public Bullet(int x, int y, ID id, ImageLoader imgl, short angle, ID id2, Handler handler) {
        super(x, y, id, angle, handler, imgl); //gameobject properties
        setID2(id2);
    }

    @Override
    public void tick() {
        
        this.Forward(15);
        x += velX;
        y += velY;
        
        collision();
        checkBorder();
        
    }
    
    @Override
    public void render(Graphics g) {
        AffineTransform position = AffineTransform.getTranslateInstance(x, y);
        position.rotate(Math.toRadians(angle), img.getWidth() /2, img.getHeight() /2);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(img, position, null);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle (x, y, 4, 4);
    }
    
    public void collision(){
        
        for (int i = 0; i < handler.objects.size(); i++) { //checking collision of bullet with all other gameobjects 
            GameObject temp = handler.objects.get(i); 
            
            if (temp.getID() == ID.EnemyBasic && (this.getID2() != ID.EnemyBasic) && (this.getID2() != ID.EnemyFast) && (this.getID2() != ID.EnemyTank) && (this.getID2() != ID.Boss)) {
                if(getBounds().intersects(temp.getBounds())) {          
                    handler.removeObject(this);
                    temp.setdoAction2();
                }
            }  
            
            if (temp.getID() == ID.EnemyFast && (this.getID2() != ID.EnemyBasic) && (this.getID2() != ID.EnemyFast) && (this.getID2() != ID.EnemyTank) && (this.getID2() != ID.Boss)) {
                if(getBounds().intersects(temp.getBounds())) {          
                    handler.removeObject(this);
                    temp.setdoAction2();
                }
            } 
            
            if (temp.getID() == ID.EnemyTank && (this.getID2() != ID.EnemyBasic) && (this.getID2() != ID.EnemyFast) && (this.getID2() != ID.EnemyTank) && (this.getID2() != ID.Boss) ) {
                if(getBounds().intersects(temp.getBounds())) {          
                    handler.removeObject(this);
                    temp.setdoAction2();
                }
            }  
            
            if (temp.getID() == ID.Boss && (this.getID2() != ID.EnemyBasic) && (this.getID2() != ID.EnemyFast) && (this.getID2() != ID.EnemyTank) && (this.getID2() != ID.Boss)) {
                if(getBounds().intersects(temp.getBounds())) {          
                    handler.removeObject(this);
                    temp.setdoAction2();
                }
            }  
        }
        
    }
    
    public void checkBorder() {
        if (x < 0) {
            handler.removeObject(this);
        }
        if (x >= 300) {
            handler.removeObject(this);
        }
        if (y < 130) {
            handler.removeObject(this);
        }
        if (y >= 760) {
            handler.removeObject(this);
        }
    }
  
}
