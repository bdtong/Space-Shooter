/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SecondGame;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
*
* @author Owner
*/
abstract public class GameObject {
    
    protected int x, y; //positioning
    protected short angle;
    
    protected int velX, velY; //movement
    
    protected ID id; //identification
    protected ID id2;
    protected ID id3;
    
    protected boolean UpPressed; //key inputs
    protected boolean DownPressed;
    protected boolean RightPressed;
    protected boolean LeftPressed;
    
    protected boolean doAction = false; //actions
    protected boolean doAction2 = false;
    
    protected Handler handler; //handler
    
    protected ImageLoader imgl; //images
    protected BufferedImage img;
    protected BufferedImage img2;
    protected BufferedImage img3;
    
    protected int value; //value
    
    public GameObject(int x, int y, ID id, short angle, Handler handler, ImageLoader imgl) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.angle = angle;
        this.handler = handler;
        this.imgl = imgl;
        img = imgl.grabImage(id);
    }
    
    public abstract void tick();
    public abstract void render(Graphics g);
    public abstract Rectangle getBounds();
    
    public void setX(int x) {
        this.x = x;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public void setID(ID id) {
        this.id = id;
    }
    
    public void setVelX(int velX) {
        this.velX = velX;
    }
    
    public void setVelY(int velY) {
        this.velY = velY;
    }
    
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public ID getID() {
        return id;
    }
    
    public int getVelX() {
        return velX;
    }
    
    public int getVelY() {
        return velY;
    }    
    
    public void toggleUp() {
        this.UpPressed = true;
    }

    public void toggleDown() {
        this.DownPressed = true;
    }

    public void toggleRight() {
        this.RightPressed = true;
    }

    public void toggleLeft() {
        this.LeftPressed = true;
    }

    public void unToggleUp() {
        this.UpPressed = false;
    }

    public void unToggleDown() {
        this.DownPressed = false;
    }

    public void unToggleRight() {
        this.RightPressed = false;
    }

    public void unToggleLeft() {
        this.LeftPressed = false;
    }
    public void Forward(int speed){
        velX = (int) Math.round(speed * Math.cos(Math.toRadians(angle)));
        velY = (int) Math.round(speed * Math.sin(Math.toRadians(angle)));
    }
     
    public void Backward(int speed){
        velX = 0 - (int) Math.round(speed * Math.cos(Math.toRadians(angle)));
        velY = 0 - (int) Math.round(speed * Math.sin(Math.toRadians(angle)));
    }
    
    public void StopMoving(){
        velX = 0;
        velY = 0;
    }
    
    public void rotateCounter() {
        this.angle -= 3;
    }
  
    public void rotateClock() {
        this.angle += 3;
    }
    
    public void setdoAction() {
        doAction = true;
    }
    
    public void resetdoAction () {
        doAction = false;
    }
   
    public boolean getdoAction() {
        return doAction;
    }
    
    public void setID2(ID id2) {
        this.id2 = id2;
    }
    
     public ID getID2() {
        return id2;
    }
     
     public void setID3(ID id3) {
        this.id3 = id3;
    }
    
     public ID getID3() {
        return id3;
    }
     
    public void setdoAction2() {
        doAction2 = true;
    }
    
    public void resetdoAction2() {
        doAction2 = false;
    }
   
    public boolean getdoAction2() {
        return doAction2;
    }
    
    public void setValue(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }

}
