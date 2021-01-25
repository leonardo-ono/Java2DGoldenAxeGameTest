package entity.magic;

import infra.Entity;
import infra.Resource;
import infra.Scene;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author admin
 */
public class Dust extends Entity {
    
    private final BufferedImage[] frames = new BufferedImage[5];
    private double frameIndex;
    private int x;
    private boolean free;
    private long waitTime;

    private Composite additiveComposite = new Lightning2.AdditiveComposite(0.3);
    
    public Dust(Scene scene) {
        super(scene);
    }

    public boolean isFree() {
        return free;
    }

    @Override
    public void init() {
        for (int i = 0; i < frames.length; i++) {
            frames[i]= Resource.getImage("magic_gilius_a_" + (i + 2));
        }
    }
    
    @Override
    public void update() {
        if (free || System.currentTimeMillis() < waitTime) {
            return;
        }
        
        frameIndex += 0.2;
        if (frameIndex >= frames.length) {
            frameIndex = frames.length - 1;
            free = true;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (free || System.currentTimeMillis() < waitTime) {
            return;
        }
        Composite oc = g.getComposite();
        
        if (Lightning2.effect == 1) {
            g.setComposite(additiveComposite);
        }
        
        g.drawImage(frames[(int) frameIndex], x, (int) (z - frames[0].getHeight()), null);
        g.setComposite(oc);
    }
    
    public void show(int x, int z, int maxTime) {
        this.x = x + 16 - (int) (32 * Math.random());
        this.z = z + (int) (16 * Math.random());;
        frameIndex = 0;
        free = false;
        waitTime = System.currentTimeMillis() + maxTime;
    }
    
}
