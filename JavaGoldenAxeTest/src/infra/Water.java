package infra;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author admin
 */
public class Water extends Entity {
    
    private BufferedImage[] frames = new BufferedImage[3];
    private double frameIndex;
    private double tx;
    private double tx2;
    private double ty;
    
    public Water(View view) {
        super(view);
        
        for (int i = 0; i < 3; i++) {
            frames[i] = loadImage("/res/image/water_" + i + ".png");
        }
    }
    
    private BufferedImage loadImage(String res) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream(res));
        } catch (IOException ex) {
            Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }        
        return image;
    }
    
    @Override
    public void update() {
        frameIndex += 0.1;
        if (frameIndex >= 3) {
            frameIndex = 0;
        }
        tx2 -= 16;
        tx -= 4;
        while (tx <= -48) {
            tx = 0;
        }
        ty = 8 * Math.sin(System.nanoTime() * 0.000000002);
    }

    @Override
    public void draw(Graphics2D g) {
        int[] offset = {16, 32, 0};
        int px = 50 - offset[(int) frameIndex];
        int py = 360;
        for (int i = 0; i < 30; i++) {
            g.drawImage(frames[(int) frameIndex], (int) (48 * i + tx) + px, (int) ty + py, null);
        }
    }
    
}
