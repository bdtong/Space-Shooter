/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SecondGame;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import sun.util.logging.PlatformLogger;

/**
 *
 * @author Owner
 */
public class GameWorld extends Canvas implements Runnable{
    
    private static final long serialVersionUID = 2L;

    private Thread thread;
    private boolean isRunning = false;
    
    private BufferedImage background;
    static final int SCREEN_WIDTH = 320;
    static final int SCREEN_HEIGHT = 800;
    
    private Handler handler;
    
    private HUD hud;
    
    private ImageLoader imgloader;
    
    private File soundtrack;

    private int currentTime = 0;
    
    private WaveScreen wavescreen;
    private Shop shop;
    
    private boolean[] spawnSet = new boolean[120]; //flag for when a wave is spawned
    
    private boolean paused = false;  
    private boolean mlON = false; //check when mouse input is being received. mouse
    //listener already attached.
    
    private Boss boss;

    public GameWorld() {
        
        soundtrack = new File("./Audio/space_arcade.mid"); //loading music (Star Wolf, Star-Fox)
        sun.util.logging.PlatformLogger platformLogger = PlatformLogger.getLogger("java.util.prefs"); 
        platformLogger.setLevel(PlatformLogger.Level.OFF); //hiding potentially bugged warning from playSoundtrack()
        //warning deals with accessing java preferences system tree or java incorrectly touching the Windows Registry
        //more info found here https://stackoverflow.com/questions/5354838/java-java-util-preferences-failing
        
        imgloader = new ImageLoader(); //loads images into hashmap
        imgloader.loader(); //init function
        background = imgloader.grabImage(ID.Backg); //loading background
        
        playSoundtrack();
        
        for (int i = 0; i < spawnSet.length; i++) {
          spawnSet[i] = false;
        }
        
        wavescreen = new WaveScreen();
        
        handler = new Handler(); 
        
        this.addKeyListener(new PlayerListener(handler));//registering component to our game world object
        //adding our keyListener PlayerListener, passing in the handler to the constructor
        //our keyListener PlayerListener modifies the velocities of objects within our handler 
        //in our thread, tick() will continue to execute repeatedly portraying movement
        
        new Window(SCREEN_WIDTH, SCREEN_HEIGHT, "Space Shooter", this);   
        
        //[HERE] after exiting from start(). a new thread has begun
        hudBuilder(); //old thread will add our players and etc  
        shop = new Shop(hud, handler, imgloader, this);
        boss = new Boss(120, 1, ID.Boss, handler, hud, imgloader, 0, 5);
        playerBuilder(); 

    }
    
    public void unpause() {
        paused = false;
        mlON = false;
        this.removeMouseListener(shop);
    }
    
    public synchronized void start(){
        thread = new Thread(this);
        thread.start(); //creating new thread calling run()
        isRunning = true;
    }
    
    public synchronized void stop(){
        try{
            thread.join();
            isRunning = false;
        } catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    private void tick() {
             
        if (paused) { 
        } else {
            handler.tick();       
            hud.tick();
        }
        
    }
    
     private void render() {
      
        //creates a place for the image to be rendered behind the screen. then it is shown
        BufferStrategy bs = this.getBufferStrategy(); //gets reference to current bufferstrategy object.
        if (bs == null) {
            this.createBufferStrategy(3); //adding to the Jframe. buffered 3 "screens"
            return;
        }
                   
        Graphics g = bs.getDrawGraphics();
        g.drawImage(background, 0, 0, null);
        handler.render(g);
        hud.render(g);
        
        waveScreenBuilder(g, currentTime);
        enemyBuilder(currentTime);
        
        if (paused && hud.lives != 0 && boss.isDead == false) {
            if (mlON == false) {
                this.addMouseListener(shop);
                mlON = true;
            }
            shopBuilder(g);
        }
        
        if (hud.lives == 0) {
            paused = true;
             wavescreen.render(g, "You Died");
        }
        
        if (boss.isDead() == true) {
            paused = true;
            wavescreen.render(g, "You Win");
        }
        
        g.dispose();
        bs.show();
        
     }
     
    private void shopBuilder(Graphics g) {
        
        shop.render(g);
    }
     
    private void waveScreenBuilder(Graphics g, int time) {
        if (time > 0 && time < 5) {
            wavescreen.render(g, "Wave 1");
        }
        if (time > 43 && time < 47) {
            wavescreen.render(g, "Wave 2");
        }
        if (time > 91 && time < 96) {
            wavescreen.render(g, "Wave 3");
        }
        if (time > 148 && time < 153) {
            wavescreen.render(g, " BOSS ");
        }
        if (time >= 233) {
            wavescreen.render(g, "Time's Up");
        }
    }
    
    private void hudBuilder() {
        
        hud = new HUD(imgloader);
        
    }
    
    private void playerBuilder() {
        
        handler.addObject(new Player(140, 700, ID.Player, handler, hud, imgloader, (short) 270)); 
        
    }
    
    private void enemyBuilder(int time) {
        
        
        //------------------WAVE 1-----------------------
        if (time == 6 && spawnSet[0] == false) {
            handler.addObject(new EnemyBasic(120, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(70, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(20, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[0] = true;
        }
        
        if (time == 8 && spawnSet[1] == false) {
            handler.addObject(new EnemyBasic(270, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(220, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(170, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[1] = true;
        }
        
        if (time == 10 && spawnSet[2] == false) {
            handler.addObject(new EnemyBasic(70, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(120, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(170, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(220, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[2] = true;
        }
        
        if (time == 12 && spawnSet[3] == false) {
            handler.addObject(new EnemyBasic(70, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[3] = true;
        }
        if (time == 13 && spawnSet[4] == false) {
            handler.addObject(new EnemyBasic(120, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[4] = true;
        }
        if (time == 14 && spawnSet[5] == false){
            handler.addObject(new EnemyBasic(170, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[5] = true;
        }
        if (time == 15 && spawnSet[6] == false){
            handler.addObject(new EnemyBasic(220, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[6] = true;
        }
        
        if (time == 16 && spawnSet[7] == false) {
            handler.addObject(new EnemyBasic(220, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[7] = true;
        }
        if (time == 17 && spawnSet[8] == false) {
            handler.addObject(new EnemyBasic(170, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[8] = true;
        }
        if (time == 18 && spawnSet[9] == false){
            handler.addObject(new EnemyBasic(120, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[9] = true;
        }
        if (time == 19 && spawnSet[10] == false){
            handler.addObject(new EnemyBasic(70, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[10] = true;
        }
            
        if (time == 20 && spawnSet[11] == false) {
            handler.addObject(new EnemyBasic(20, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(270, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[11] = true;
        }
        if (time == 21 && spawnSet[12] == false) {
            handler.addObject(new EnemyBasic(120, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(170, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[12] = true;
        }
        
        if (time == 22 && spawnSet[13] == false) {
            handler.addObject(new EnemyBasic(20, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(70, 1, ID.EnemyBasic, handler, hud,  imgloader, 0, 5));
            handler.addObject(new EnemyBasic(220, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(270, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[13] = true;
        }
        
        if (time == 24 && spawnSet[14] == false) {
            handler.addObject(new EnemyBasic(220, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[14] = true;
        }
        if (time == 25 && spawnSet[15] == false) {
            handler.addObject(new EnemyBasic(170, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[15] = true;
        }
        if (time == 26 && spawnSet[16] == false){
            handler.addObject(new EnemyBasic(120, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[16] = true;
        }
        if (time == 27 && spawnSet[17] == false){
            handler.addObject(new EnemyBasic(70, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(20, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(120, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[17] = true;
        }
            
        if (time == 28 && spawnSet[18] == false) {
            handler.addObject(new EnemyBasic(70, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[18] = true;
        }
        if (time == 29 && spawnSet[19] == false) {
            handler.addObject(new EnemyBasic(120, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[19] = true;
        }
        if (time == 30 && spawnSet[20] == false){
            handler.addObject(new EnemyBasic(170, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[20] = true;
        }
        if (time == 31 && spawnSet[21] == false){
            handler.addObject(new EnemyBasic(220, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(270, 1, ID.EnemyBasic, handler, hud,  imgloader, 0, 5));
            handler.addObject(new EnemyBasic(170, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[21] = true;
        }
        
        if (time == 32 && spawnSet[22] == false) {
            handler.addObject(new EnemyBasic(70, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(170, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[22] = true;
        }
        if (time == 33 && spawnSet[23] == false) {
            handler.addObject(new EnemyBasic(120, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(220, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[23] = true;
        }
        
        if (time == 34 && spawnSet[24] == false) {
            handler.addObject(new EnemyBasic(20, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(70, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(220, 20, ID.EnemyBasic, handler, hud,  imgloader, 0, 5));
            handler.addObject(new EnemyBasic(270, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[24] = true;
        }
        
        if (time == 35 && spawnSet[25] == false) {
            handler.addObject(new EnemyBasic(20, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(70, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(120, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(170, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(220, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(270, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[25] = true;
        }
        
        if (time == 40) {
            paused = true;
            currentTime = 41;
        }
        
        //----------------WAVE 2------------------------
        
        if (time == 48 && spawnSet[26] == false) {
            handler.addObject(new EnemyBasic(70, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(220, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[26] = true;
        }
        if (time == 49 && spawnSet[27] == false) {
            handler.addObject(new EnemyBasic(120, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(170, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[27] = true;
        }
        if (time == 50 && spawnSet[28] == false){
            handler.addObject(new EnemyBasic(170, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(120, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[28] = true;
        }
        if (time == 51 && spawnSet[29] == false){
            handler.addObject(new EnemyBasic(220, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(70, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[29] = true;
        }
        
        if (time == 54 && spawnSet[30] == false) {
            handler.addObject(new EnemyFast(120, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(170, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[30] = true;
        }
        
        if (time == 56 && spawnSet[31] == false) {
            handler.addObject(new EnemyBasic(70, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[31] = true;
        }
        if (time == 57 && spawnSet[32] == false) {
            handler.addObject(new EnemyBasic(120, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[32] = true;
        }
        if (time == 58 && spawnSet[33] == false){
            handler.addObject(new EnemyFast(120, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyBasic(170, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(220, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[33] = true;
        }
        if (time == 59 && spawnSet[34] == false){
            handler.addObject(new EnemyFast(170, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyBasic(220, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(270, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[34] = true;
        }
        
        if (time == 60 && spawnSet[35] == false) {
            handler.addObject(new EnemyBasic(220, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[35] = true;
        }
        if (time == 61 && spawnSet[36] == false) {
            handler.addObject(new EnemyBasic(170, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[36] = true;
        }
        if (time == 62 && spawnSet[37] == false){
            handler.addObject(new EnemyFast(170, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyBasic(120, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(70, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[37] = true;
        }
        if (time == 63 && spawnSet[38] == false){
            handler.addObject(new EnemyFast(120, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyBasic(70, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(20, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[38] = true;
        }
        
        if (time == 65 && spawnSet[39] == false) {
            handler.addObject(new EnemyFast(20, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyBasic(70, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(220, 20, ID.EnemyBasic, handler, hud,  imgloader, 0, 5));
            handler.addObject(new EnemyFast(270, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[39] = true;
        }
        if (time == 66 && spawnSet[40] == false) {
            handler.addObject(new EnemyFast(70, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyBasic(120, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(170, 20, ID.EnemyBasic, handler, hud,  imgloader, 0, 5));
            handler.addObject(new EnemyFast(220, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[40] = true;
        }
        
        if (time == 67 && spawnSet[41] == false) {
            handler.addObject(new EnemyFast(70, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(270, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[41] = true;
        }
        
        if (time == 68 && spawnSet[42] == false) {
            handler.addObject(new EnemyFast(20, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(220, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[42] = true;
        }
        
        if (time == 69 && spawnSet[43] == false) {
            handler.addObject(new EnemyFast(270, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(20, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[43] = true;
        }
        
        if (time == 70 && spawnSet[44] == false) {
            handler.addObject(new EnemyFast(70, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(220, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[44] = true;
        }
        
        if (time == 71 && spawnSet[45] == false) {
            handler.addObject(new EnemyFast(120, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(170, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[45] = true;
        }
        
        if (time == 72 && spawnSet[46] == false) {
            handler.addObject(new EnemyFast(220, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(70, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[46] = true;
        }
        if (time == 73 && spawnSet[47] == false) {
            handler.addObject(new EnemyFast(20, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(270, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[47] = true;
        }
        
        if (time == 74 && spawnSet[48] == false){
            handler.addObject(new EnemyFast(170, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyBasic(120, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(70, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[48] = true;
        }
        if (time == 75 && spawnSet[49] == false){
            handler.addObject(new EnemyFast(120, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyBasic(70, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(20, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[49] = true;
        }
        
        if (time == 76 && spawnSet[50] == false) {
            handler.addObject(new EnemyFast(20, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyBasic(70, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(220, 20, ID.EnemyBasic, handler, hud,  imgloader, 0, 5));
            handler.addObject(new EnemyFast(270, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[50] = true;
        }
        if (time == 77 && spawnSet[51] == false) {
            handler.addObject(new EnemyFast(70, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyBasic(120, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(170, 20, ID.EnemyBasic, handler, hud,  imgloader, 0, 5));
            handler.addObject(new EnemyFast(220, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[51] = true;
        }
        
        if (time == 78 && spawnSet[52] == false){
            handler.addObject(new EnemyBasic(70, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(120, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(170, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(220, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[52] = true;
        }
        if (time == 79 && spawnSet[53] == false){
            handler.addObject(new EnemyBasic(170, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(220, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(270, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(120, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(70, 1,ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(20, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[53] = true;
        }
        
        if (time == 80 && spawnSet[54] == false) {
            handler.addObject(new EnemyBasic(120, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(70, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(20, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(170, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(270, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(220, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[54] = true;
        }
        if (time == 81 && spawnSet[55] == false) {
            handler.addObject(new EnemyFast(20, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(120, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(170, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(220, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(270, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(70, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[55] = true;
        }
        if (time == 82 && spawnSet[56] == false) {
            handler.addObject(new EnemyFast(120, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(170, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(220, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(70, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[56] = true;
        }
        if (time == 83 && spawnSet[57] == false) {
            handler.addObject(new EnemyFast(270, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(120, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(170, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(20, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[57] = true;
        }
        
        if (time == 88) {
            paused = true;
            currentTime = 89;
        }
        
        //-----------------------WAVE 3---------------------------------
        
        if (time == 97 && spawnSet[58] == false) {
            handler.addObject(new EnemyBasic(20, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(70, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(120, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(170, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(220, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(270, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[58] = true;
        }
        
        if (time == 98 && spawnSet[59] == false) {
            handler.addObject(new EnemyFast(20, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyBasic(70, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(220, 20, ID.EnemyBasic, handler, hud,  imgloader, 0, 5));
            handler.addObject(new EnemyFast(270, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[59] = true;
        }
        if (time == 99 && spawnSet[60] == false) {
            handler.addObject(new EnemyFast(70, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyBasic(120, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(170, 20, ID.EnemyBasic, handler, hud,  imgloader, 0, 5));
            handler.addObject(new EnemyFast(220, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[60] = true;
        }
        
        if (time == 100 && spawnSet[61] == false) {
            handler.addObject(new EnemyFast(70, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(270, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[61] = true;
        }
        
        if (time == 101 && spawnSet[62] == false) {
            handler.addObject(new EnemyFast(20, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(220, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[62] = true;
        }   
        
         if (time == 102 && spawnSet[63] == false) {
            handler.addObject(new EnemyFast(70, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(270, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyBasic(70, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[63] = true;
        }
        
        if (time == 103 && spawnSet[64] == false) {
            handler.addObject(new EnemyFast(120, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(220, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyBasic(120, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[64] = true;
        } 
        
         if (time == 104 && spawnSet[65] == false) {
            handler.addObject(new EnemyFast(20, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(270, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyBasic(270, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[65] = true;
        }
        
        if (time == 105 && spawnSet[66] == false) {
            handler.addObject(new EnemyFast(120, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(70, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyBasic(120, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[66] = true;
        } 
        
        if (time == 106 && spawnSet[67] == false) {
            handler.addObject(new EnemyFast(20, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(170, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyBasic(170, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[67] = true;
        } 
        
        if (time == 107 && spawnSet[68] == false) {
            handler.addObject(new EnemyFast(220, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(170, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyBasic(220, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[68] = true;
        } 
        
        if (time == 110 && spawnSet[69] == false) {
            handler.addObject(new EnemyTank(170, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[69] = true;
        } 
        
        if (time == 111 && spawnSet[70] == false) {
            handler.addObject(new EnemyTank(270, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[70] = true;
        } 
        
        if (time == 112 && spawnSet[71] == false) {
            handler.addObject(new EnemyTank(20, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[71] = true;
        } 
        
        if (time == 113 && spawnSet[72] == false){
            handler.addObject(new EnemyFast(170, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyBasic(120, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(70, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(220, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(20, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[72] = true;
        }
        if (time == 114 && spawnSet[73] == false){
            handler.addObject(new EnemyFast(220, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyBasic(170, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(120, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(270, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(70, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[73] = true;
        }
        
        if (time == 115 && spawnSet[74] == false) {
            handler.addObject(new EnemyFast(70, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(170, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(120, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(20, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(220, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[74] = true;
        }
        if (time == 116 && spawnSet[75] == false) {
            handler.addObject(new EnemyFast(120, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(220, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(170, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(70, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(270, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[75] = true;
        }
            
        if (time == 117 && spawnSet[76] == false) {
            handler.addObject(new EnemyBasic(70, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(120, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(120, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(70, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(170, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[76] = true;
        }
        if (time == 118 && spawnSet[77] == false) {
            handler.addObject(new EnemyBasic(20, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(70, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(20, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(70, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(120, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[77] = true;
        }
        if (time == 119 && spawnSet[78] == false){
            handler.addObject(new EnemyBasic(270, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(220, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(220, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(270, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(170, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[78] = true;
        }
        if (time == 120 && spawnSet[79] == false){
            handler.addObject(new EnemyBasic(220, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(170, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(220, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(170, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(120, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[79] = true;
        }
        
        if (time == 121 && spawnSet[80] == false) {
            handler.addObject(new EnemyFast(20, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(120, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(70, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(70, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[80] = true;
        }
        if (time == 122 && spawnSet[81] == false) {
            handler.addObject(new EnemyFast(20, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(120, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(70, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(120, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[81] = true;
        }
        if (time == 123 && spawnSet[82] == false){
            handler.addObject(new EnemyFast(120, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(170, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(220, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(170, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[82] = true;
        }
        if (time == 124 && spawnSet[83] == false){
            handler.addObject(new EnemyFast(170, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(270, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(220, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(220, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[83] = true;
        }
        
        if (time == 125 && spawnSet[84] == false) {
            handler.addObject(new EnemyBasic(20, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(70, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(120, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(170, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(220, 20, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(270, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            spawnSet[84] = true;
        }
        
        if (time == 126 && spawnSet[85] == false) {
            handler.addObject(new EnemyFast(20, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(120, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(170, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(220, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(270, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(70, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[85] = true;
        }
        
        if (time == 127 && spawnSet[86] == false){
            handler.addObject(new EnemyBasic(220, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(120, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(20, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(70, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(170, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(120, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[86] = true;
        }
        
        if (time == 128 && spawnSet[87] == false) {
            handler.addObject(new EnemyBasic(270, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(170, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(70, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(120, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(220, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(170, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[87] = true;
        }
        if (time == 129 && spawnSet[88] == false) {
            handler.addObject(new EnemyBasic(220, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(120, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(20, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyTank(170, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(120, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(70, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[88] = true;
        }
        if (time == 130 && spawnSet[89] == false){
            handler.addObject(new EnemyBasic(270, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(170, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(70, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyTank(220, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(170, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(120, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[89] = true;
        }
        
        if (time == 131 && spawnSet[90] == false){
            handler.addObject(new EnemyBasic(270, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(220, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(70, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(72, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyTank(270, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(220, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(20, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(70, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[90] = true;
        }
        
        if (time == 132 && spawnSet[91] == false){
            handler.addObject(new EnemyBasic(270, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(220, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(70, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyBasic(20, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyTank(170, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(120, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(20, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(270, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyFast(70, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(220, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(120, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyFast(170, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            spawnSet[91] = true;
        }
        
        if (time == 133 && spawnSet[92] == false){
            handler.addObject(new EnemyBasic(70, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(70, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(70, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyBasic(220, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(220, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(220, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[92] = true;
        }
        
        if (time == 134 && spawnSet[93] == false){
            handler.addObject(new EnemyBasic(120, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(120, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(120, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[93] = true;
        }
        
        if (time == 135 && spawnSet[94] == false){
            handler.addObject(new EnemyBasic(20, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(20, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(20, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyBasic(270, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(270, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(270, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[94] = true;
        }
        
        if (time == 136 && spawnSet[95] == false){
            handler.addObject(new EnemyBasic(120, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(120, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(120, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyBasic(170, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(170, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(170, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyBasic(70, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(70, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(70, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[95] = true;
        }
        
        if (time == 137 && spawnSet[96] == false){
            handler.addObject(new EnemyBasic(70, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(70, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(70, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[96] = true;
        }
        
        if (time == 138 && spawnSet[97] == false){
            handler.addObject(new EnemyBasic(220, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(220, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(220, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyBasic(170, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(170, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(170, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyBasic(20, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(20, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(20, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyBasic(270, 1, ID.EnemyBasic, handler, hud, imgloader, 0, 5));
            handler.addObject(new EnemyFast(270, 1, ID.EnemyFast, handler, hud, imgloader, 0, 12));
            handler.addObject(new EnemyTank(270, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[97] = true;
        }
        
        if (time == 139 && spawnSet[98] == false){
            handler.addObject(new EnemyTank(220, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(270, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(170, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(120, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(70, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(20, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[98] = true;
        }
        if (time == 140 && spawnSet[99] == false){
            handler.addObject(new EnemyTank(220, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(270, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(170, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(120, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(70, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            handler.addObject(new EnemyTank(20, 1, ID.EnemyTank, handler, hud, imgloader, 0, 3));
            spawnSet[99] = true;
        }
        
        if (time == 145) {
            paused = true;
            currentTime = 146;
        }
        //-------------------------BOSS--------------------------------
        if (time == 154 && spawnSet[100] == false) {
            handler.addObject(boss);
            spawnSet[100] = true;
        }
        if (time == 158 && spawnSet[101] == false) {
            boss.setX(30);
            boss.setY(1);
            spawnSet[101] = true;
        }
        
        if (time == 162 && spawnSet[102] == false) {
            boss.setX(190);
            boss.setY(1);
            spawnSet[102] = true;
        }
        
        if (time == 166 && spawnSet[103] == false) {
            boss.setX(400);
            boss.setY(500);
            boss.setVelX(-5);
            boss.setVelY(0);
            spawnSet[103] = true;
        }
        
        if (time == 169 && spawnSet[104] == false) {
            boss.setX(-1);
            boss.setY(300);
            boss.setVelX(5);
            boss.setVelY(0);
            spawnSet[104] = true;
        }
        
        if (time == 172 && spawnSet[105] == false) {
            boss.setX(120);
            boss.setY(900);
            boss.setVelX(0);
            boss.setVelY(-5);
            spawnSet[105] = true;
        } 
        
        if (time == 177) {
            boss.setPhase(2);
        }
        
        if (time == 178 && spawnSet[106] == false) {
            boss.setX(30);
            boss.setY(1);
            boss.setVelY(5);
            spawnSet[106] = true;
        }
         if (time == 182 && spawnSet[107] == false) {
            boss.setX(190);
            boss.setY(1);
            spawnSet[107] = true;
        }
        
        if (time == 186 && spawnSet[108] == false) {
            boss.setX(400);
            boss.setY(500);
            boss.setVelX(-5);
            boss.setVelY(0);
            spawnSet[108] = true;
        }
        
        if (time == 190 && spawnSet[109] == false) {
            boss.setX(-10);
            boss.setY(300);
            boss.setVelX(5);
            boss.setVelY(0);
            spawnSet[109] = true;
        }
        
        if (time == 194 && spawnSet[110] == false) {
            boss.setX(-1);
            boss.setY(300);
            boss.setVelX(5);
            boss.setVelY(0);
            spawnSet[110] = true;
        }
        
        if (time == 198 && spawnSet[111] == false) {
            boss.setX(120);
            boss.setY(900);
            boss.setVelX(0);
            boss.setVelY(-5);
            spawnSet[111] = true;
        } 
        
        if (time == 203) {
            boss.setPhase(3);
        }
        
        if (time == 204 && spawnSet[112] == false) {
            boss.setX(120);
            boss.setY(1);
            boss.setVelY(3);
            spawnSet[112] = true;
        }
        
        if (time == 210 && spawnSet[113] == false) {
            boss.setX(120);
            boss.setY(1);
            boss.setVelY(2);
            spawnSet[113] = true;
        }
        
        if (time == 218 && spawnSet[114] == false) {
            boss.setX(120);
            boss.setY(1);
            boss.setVelY(1);
            spawnSet[114] = true;
        }
        
    }
    
    public void run()
    {
        long lastTime = System.nanoTime(); // get current time to the nanosecond
        double amountOfTicks = 60.0; // set the number of ticks 
        double ns = 1000000000 / amountOfTicks;
        double delta = 0; // change in time
        long timer = System.currentTimeMillis(); // get current time
        int frames = 0; 
        while(isRunning)
        {
                    long now = System.nanoTime();  // get current time in nonoseconds durring current loop
                    delta += (now - lastTime) / ns; // add the amount of change since the last loop
                    lastTime = now; // set lastTime to now to prepare for next loop
                    while(delta >=1)
                            {
                                tick();
                                delta--;  // lower our delta back to 0 to start our next frame wait
                            }
                            if(isRunning)
                                render();
                            frames++; // note that a frame has passed
                            
                            if(System.currentTimeMillis() - timer > 1000) // if one second has passed
                            {
                                if (paused == false) {
                                    currentTime++;
                                }
                                timer += 1000;
                                frames = 0;
                            }
        }
                stop(); // no longer running stop the thread
    }
    
     private void playSoundtrack() {
        Clip mainSoundClip = null;
        try {
            mainSoundClip = AudioSystem.getClip();
        } catch (LineUnavailableException ex) {
            System.out.println("AudioSystem.getClip failed");
        }
        
        try {
            mainSoundClip.open(AudioSystem.getAudioInputStream(soundtrack));
        } catch (UnsupportedAudioFileException ex) {
            System.out.println("Unsupported Audio File");
        } catch (IOException ex) {
            System.out.println("IO exception");
        } catch (LineUnavailableException ex) {
            System.out.println("Line Unavailable");
        }
        
        FloatControl gainControl = (FloatControl) mainSoundClip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(-20.0f); //Reduce Soundtrack volume 20 decibels.
        mainSoundClip.loop(5);
        
    }
    
    public static void main(String args[]){
        new GameWorld();
    }

}
