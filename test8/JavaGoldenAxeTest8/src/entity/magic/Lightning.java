package entity.magic;

import infra.Camera;
import infra.Entity;
import infra.Resource;
import infra.Scene;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import scene.Stage;

/**
 *
 * @author admin
 */
public class Lightning extends Entity {
    
    private final BufferedImage[] frames = new BufferedImage[2];
    private double frameIndex;
    private double x;
    private Stage stage;
    private List<Dust> dustsCache = new ArrayList<>();
    
    public Lightning(Scene scene) {
        super(scene);
        this.stage = (Stage) scene;
    }

    @Override
    public void init() {
        for (int i = 0; i < frames.length; i++) {
            frames[i]= Resource.getImage("magic_gilius_a_" + (i));
        }
        
        for (int i = 0; i < 30; i++) {
            Dust dust = new Dust(stage);
            stage.addEntity(dust);
            dustsCache.add(dust);
        }
        
        update();
        update();
    }
    
    private void spawnDust(int x, int z, int maxTime) {
        for (Dust dust : dustsCache) {
            if (dust.isFree()) {
                dust.show(x, z, maxTime);
                return;
            }
        }
    }
    private int lastDustShowTime = -1;
    
    @Override
    public void update() {
        
        this.x = Camera.getX() + 138 - frames[0].getWidth() / 2 + 120 * Math.sin(System.nanoTime() * 0.000000001);
        this.z = 250 + 30 * Math.cos(System.nanoTime() * 0.0000000027193);
        frameIndex += 0.5;
        if (frameIndex >= frames.length) {
            frameIndex = 0;
        }
        
        //System.out.println("dust " + (int) (System.nanoTime() * 0.000000002));
        int showTime = (int) (System.nanoTime() * 0.00000002);
        if (lastDustShowTime < 0 || (lastDustShowTime != showTime &&  showTime % 2 == 0)) {
            lastDustShowTime = showTime;
            for (int i = 0; i < 3; i++) {
                spawnDust((int) x + 4, (int) z + 8, i * 50);
                spawnDust((int) x + 60, (int) z + 40, i * 50);
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.drawImage(frames[(int) frameIndex], (int) x, (int) (z - frames[(int) frameIndex].getHeight() + 32), null);
    }
    
    public void show(int x, int z) {
        this.x = x;
        this.z = z;
    }
    
}
