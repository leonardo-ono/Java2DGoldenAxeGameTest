package main;

import infra.Display;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import static infra.Settings.PREFERRED_VIEWPORT_WIDTH;
import static infra.Settings.PREFERRED_VIEWPORT_HEIGHT;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

/**
 * Main class.
 * 
 * Game entry point.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Main {

    private static JFrame frame = new JFrame();
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Display view = new Display(frame);
            view.setPreferredSize(new Dimension(
                    PREFERRED_VIEWPORT_WIDTH, PREFERRED_VIEWPORT_HEIGHT));
            
            frame.setUndecorated(false);

            frame.setTitle("Java Golden Axe Test #8");
            frame.getContentPane().add(view);
            //frame.setResizable(false);
            frame.pack();
            //frame.setLocationRelativeTo(null);
            frame.setLocation(150, 100);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            
            view.requestFocus();
            view.start();

            //fullscreen();
        });
        
    }   
 
    private static void fullscreen() {
        //frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        //  https://stackoverflow.com/questions/875132/how-to-call-setundecorated-after-a-frame-is-made-visible
        frame.dispose();
        frame.setUndecorated(true);
        frame.setVisible(true);
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        System.out.println("full screen supported: " + gd.isFullScreenSupported());
        int i = 0;
        for (DisplayMode dm : gd.getDisplayModes()) {
            System.out.println(i++ + "Diplay mode: " + dm.getWidth() + ", " + dm.getHeight());
        }

        gd.setFullScreenWindow(frame);
        System.out.println("display change supported: " + gd.isDisplayChangeSupported());
        gd.setDisplayMode(gd.getDisplayModes()[5]);
    }
    
}
