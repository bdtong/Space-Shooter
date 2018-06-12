package SecondGame;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Player extends GameObject{
    
    int homeX; //home position
    int homeY;
    short homeAngle;
    
    HUD hud; //hud
    
    File firingSound; //sound
    
    boolean tripleshot = false;
    boolean rocket = false;
    boolean gofast = false;

    public Player(int x, int y, ID id, Handler handler, HUD hud, ImageLoader imgl , short angle) {
        super(x, y, id, angle, handler, imgl); //gameobject properties       

        this.homeX = x; //player properties
        this.homeY = y;
        this.homeAngle = angle;
        this.hud = hud;
        firingSound = new File("./Audio/Tank Firing.wav");
        
        //In java, for objects, what is passed to the function is a copy of the reference
        //(not a copy of the object). Therefore, we can manipulate the object, although now we have two references
        //pointing to the object. Intuitively, removing or changing the copy reference does not affect the outside
        //reference. 
        //Copy constructors can be used for copying objects.
        //In java, primitives are passed by copy. So changes inside are not reflected outside.
        
    }

    public void tick() {
        
        int lastPositionX = x;
        int lastPositionY = y;
        
        if (hud.health == 0){
            die();
        }
        
        x += velX; //adding increments of position to display movement (velocity)
        y += velY;

        collision(lastPositionX, lastPositionY);
        
        if(doAction == true) {
            action();
            resetdoAction();
        }
        
        if(doAction2 == true) {
            action2();
            resetdoAction2();
        }
        
        checkBorder(); //final boundary check

    }
    
    public void action() {
        
        if (tripleshot == true && rocket == false) {
            handler.addObject(new Bullet(x + 10, y + 10 ,ID.Bullet, imgl, angle, this.getID(), handler)); 
            handler.addObject(new Bullet(x + 10, y + 10 ,ID.Bullet, imgl, (short)(angle+10), this.getID(), handler)); 
            handler.addObject(new Bullet(x + 10, y + 10 ,ID.Bullet, imgl, (short)(angle-10), this.getID(), handler)); 
        } else if (tripleshot == false && rocket == true) {
            handler.addObject(new Rocket(x , y ,ID.Rocket, imgl, angle, this.getID(), handler));  
        } else if (tripleshot == true && rocket == true) {
            handler.addObject(new Rocket(x , y ,ID.Rocket, imgl, angle, this.getID(), handler)); 
            handler.addObject(new Rocket(x , y ,ID.Rocket, imgl, (short)(angle+10), this.getID(), handler)); 
            handler.addObject(new Rocket(x , y ,ID.Rocket, imgl, (short)(angle-10), this.getID(), handler)); 
        } else {
            handler.addObject(new Bullet(x + 10, y + 10 ,ID.Bullet, imgl, angle, this.getID(), handler)); //adjusted bullet position for accurate center
        }
        //playFiringSound();
        
    }
    
    public void setTriple() {
        tripleshot = true;
    }
    
    public void setGoFast() {
        gofast = true;
    }
    
     public boolean getGoFast() {
        return gofast;
    }
     
    public void setRocket() {
        rocket = true;
    }
    
    
    public void action2() {
        //pending functionality. pls do not touch.
        System.out.println("action2 called");
    }

    public void collision(int lastPositionX, int lastPositionY) {
        
        for (int i = 0; i < handler.objects.size(); i++) { //checking collision of player with all other gameobjects 
            
            GameObject temp = handler.objects.get(i);    
            
            if (temp.getID() == ID.EnemyBasic) {
                if(getBounds().intersects(temp.getBounds())) {
                    hud.health -= 20;
                    handler.removeObject(temp);
                }
            }  
            
            if (temp.getID() == ID.EnemyFast) {
                if(getBounds().intersects(temp.getBounds())) {
                    hud.health -= 40;
                    handler.removeObject(temp);
                }
            }  
            
            if (temp.getID() == ID.EnemyTank) {
                if(getBounds().intersects(temp.getBounds())) {
                    hud.health -= 30;
                    handler.removeObject(temp);
                }
            }  
            
            if (temp.getID() == ID.Boss) {
                if(getBounds().intersects(temp.getBounds())) {
                    hud.health -= 2;
                }
            }  
            
            if (temp.getID() == ID.Bullet) {
                if(getBounds().intersects(temp.getBounds()) && temp.getID2() != this.getID()) { //checks if bullet is not from itself  
                    hud.health -= 5;
                    handler.removeObject(temp);
                }
            }  
            
            if (temp.getID() == ID.Rocket) {
                if(getBounds().intersects(temp.getBounds()) && temp.getID2() != this.getID()) { //checks if bullet is not from itself  
                    hud.health -= 5;
                    handler.removeObject(temp);
                }
            } 
        }
        
    }
    
    public Rectangle getBounds() {
        
        //Rotating bounds (BUGGED MIGHT WANT TO FIX)
        /*Rectangle bounds = new Rectangle(x, y, 32, 32);
        AffineTransform transform = new AffineTransform(); //creating AffineTransform
        transform.rotate(Math.toRadians(angle), x + bounds.getWidth() / 2, y + bounds.getHeight() / 2); //rotating based on angle.
        //angle does not need to be adjusted because there is not "front" of hitbox. paramater 2 and 3 represent positions
        //relative to origin.
        Shape rect = transform.createTransformedShape(bounds); //instantiating shape from transform.
        return rect.getBounds();*/
       
        //stationary bounds
        return new Rectangle(x, y, 32, 32);
          
    }

    public void render(Graphics g) {
        
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle + 90), img.getWidth() / 2, img.getHeight() / 2);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(img, rotation, null);
        
        //Rotating bounds render
        /*Rectangle bounds = new Rectangle(x, y, 32, 32);
        Graphics2D g4 = (Graphics2D) g;
        AffineTransform r2 = new AffineTransform();
        r2.rotate(Math.toRadians(angle), x + bounds.getWidth() / 2, y + bounds.getHeight() / 2);
        Shape s2 = r2.createTransformedShape(bounds);
        g4.fill(s2);*/
        
        //Staionary bounds render
        /*Graphics2D g3 = (Graphics2D) g;
        g3.drawRect(x, y, 32, 32);*/
   
    }
    
    public void checkBorder() {
        if (x < 0) {
            x = 0;
        }
        if (x >= 280) {
            x = 280;
        }
        if (y < 130) {
            y = 130;
        }
        if (y >= 740) {
            y = 740;
        }
    }

    private void playFiringSound() {
        Clip firing = null;
        try {
            
            firing = AudioSystem.getClip();
            firing.open(AudioSystem.getAudioInputStream(firingSound));
        } catch (LineUnavailableException ex) {
            System.out.println("Line unavailable");
        } catch (UnsupportedAudioFileException ex) {
            System.out.println("Aunsupported audio file");
        } catch (IOException ex) {
            System.out.println("IO exception");
        }
        FloatControl firingVolume = (FloatControl) firing.getControl(FloatControl.Type.MASTER_GAIN);
        firingVolume.setValue(-15.0f);
        firing.start();
    }

    private void die() {
        
        hud.lives -= 1;
        
        if (hud.lives > 0){
            hud.health = 100;
            x = homeX;
            y = homeY;
            angle = homeAngle;
        } else if (hud.lives == 0) {
            handler.removeObject(this);
        }
           
        
    }
    
}
