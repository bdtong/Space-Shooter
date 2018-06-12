package SecondGame;

import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;

public class Window extends Canvas{
  private static final long serialVersionUID = 1L;
  
  public Window(int width, int height, String title, GameWorld gameworld){
    JFrame frame = new JFrame(title);
    
    frame.setPreferredSize(new Dimension(width, height));
    frame.setMaximumSize(new Dimension(width, height));
    frame.setMinimumSize(new Dimension(width, height));
    
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.setLocationRelativeTo(null);
    frame.add(gameworld);
    frame.setVisible(true);
    gameworld.start();
  }

}
