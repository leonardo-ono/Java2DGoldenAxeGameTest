package infra;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author admin
 */
public class Go {
    
    private static BufferedImage image;
    private static long showEndTime;
    private static boolean visible;
    private static boolean soundPlayed;
    private static int soundIndex;
    
    static {
        image = Resource.getImage("go");
        Audio.loadSound(1000, "/res/sound/29.wav");
        Audio.loadSound(1001, "/res/sound/29.wav");
        Audio.loadSound(1002, "/res/sound/29.wav");
        Audio.loadSound(1003, "/res/sound/29.wav");
    }
    
    
    public static void update() {
        visible = false;
        if (System.currentTimeMillis() < showEndTime) {
            
            int dif = (int) ((showEndTime - System.currentTimeMillis()) * 0.006);
            System.out.println("dif = " + dif);
            
            if (dif % 2 == 0 || soundIndex > 1003) {
                visible = true;
                if (!soundPlayed) {
                    Audio.playSound(soundIndex++);
                    soundPlayed = true;
                }
            }
            else if (dif % 2 == 1 && soundPlayed) {
                visible = false;
                soundPlayed = false;
            }
        } 
    }
    
    public static void draw(Graphics2D g) {
        if (visible) {
            g.drawImage(image, 193, 42, null);
        }
    }

    public static void show() {
        showEndTime = System.currentTimeMillis() + 1300;
        soundIndex = 1000;
    }
    
}
