/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SecondGame;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import javax.imageio.ImageIO;

/**
 *
 * @author Brandon
 */
public class ImageLoader {
    
    HashMap <ID, BufferedImage> images;
    
    public ImageLoader() {
        images = new HashMap();
    }
    
    public void loader() {

        //IMPORTANT: CURRENT DIRECTORY MUST BE SET TO THE PACKAGE SecondGame BEFORE GAME RUN
       
        try {
            images.put(ID.Player, ImageIO.read(new File("./images/spaceship_sprite.png"))); //Items in subdirectory
            images.put(ID.Backg, ImageIO.read(new File("./images/Space1.jpg")));
            images.put(ID.Bullet, ImageIO.read(new File("./images/Shell.png")));
            images.put(ID.Rocket, ImageIO.read(new File("./images/spr_rocket.png")));
            images.put(ID.HealthPU, ImageIO.read(new File("./images/healthpack.png")));
            images.put(ID.tripleshotPU, ImageIO.read(new File("./images/tripleshot.png")));
            images.put(ID.fastPU, ImageIO.read(new File("./images/fast.png")));
            images.put(ID.rocketPU, ImageIO.read(new File("./images/rocket_icon.png")));
            images.put(ID.EnemyBasic, ImageIO.read(new File ("./images/simple_ufo.png")));
            images.put(ID.EnemyFast, ImageIO.read(new File ("./images/speeder.png")));
            images.put(ID.EnemyTank, ImageIO.read(new File ("./images/heavy.png")));
            images.put(ID.Boss, ImageIO.read(new File ("./images/mothership.png")));
        } catch (IOException e) {
            System.out.println("excpetion " + e);      
        }
        
    }
    
    public BufferedImage grabImage(ID id) {
        return images.get(id);
    }
    
    
}
