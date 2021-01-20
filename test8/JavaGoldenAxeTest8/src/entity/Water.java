package entity;

import infra.Entity;
import infra.Resource;
import infra.Scene;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import scene.Stage;

/**
 *
 * @author admin
 */
public class Water extends Entity<Stage> {
    
    private BufferedImage[] frames = new BufferedImage[3];
    private double frameIndex;
    private double tx;
    private double tx2;
    private double ty;
    
    public Water(Stage stage) {
        super(stage);
        for (int i = 0; i < 3; i++) {
            frames[i] = Resource.getImage("water_" + i);
        }
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
        ty = 8 * Math.sin(System.currentTimeMillis()* 0.000002);
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
