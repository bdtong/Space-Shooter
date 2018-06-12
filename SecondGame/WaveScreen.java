/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SecondGame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
/**
 *
 * @author Owner
 */
public class WaveScreen{
    
    String text;
    
    public WaveScreen() {}
    
    public void render(Graphics g, String textbox) {
        
        text = textbox;
        Font font = new Font("Vernanda", Font.BOLD, 30);

        g.setFont(font);
        g.setColor(Color.white);
        
        g.drawString(text, 110, 400);
        
    }

}
