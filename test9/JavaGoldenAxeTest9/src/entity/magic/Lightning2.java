package entity.magic;

import entity.Player1;
import infra.AnimationPlayer;
import infra.Camera;
import infra.Input;
import infra.Scene;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;
import scene.Stage;

/**
 *
 * @author admin
 */
public class Lightning2 extends Player1 {

    //private final BufferedImage[] frames = new BufferedImage[2];
    private double frameIndex;
    //private double x;
    private Stage stage;
    private List<Dust> dustsCache = new ArrayList<>();
    private double dx = 600;

    private Composite additiveComposite = new AdditiveComposite(1);

    private AnimationPlayer animationPlayerb;
    public static int effect = 0;
    
    public Lightning2(Scene scene) {
        super((Stage) scene, null);
        this.stage = (Stage) scene;
    }

    @Override
    public void init() {
        animationPlayer = new AnimationPlayer("lightning_1",
                "/res/animation/gilius/magic/colliders.txt",
                "/res/animation/gilius/magic/sprite_sheet.txt",
                "/res/animation/gilius/magic/points.txt");

        animationPlayerb = new AnimationPlayer("lightning_1b",
                "/res/animation/gilius/magic/colliders.txt",
                "/res/animation/gilius/magic/sprite_sheet.txt",
                "/res/animation/gilius/magic/points.txt");

        animationPlayer.setAnimation("magic");
        animationPlayerb.setAnimation("magic");

        //for (int i = 0; i < frames.length; i++) {
        //    frames[i]= Resource.getImage("magic_gilius_a_" + (i));
        //}
        for (int i = 0; i < 64; i++) {
            Dust dust = new Dust(stage);
            stage.addEntity(dust);
            dustsCache.add(dust);
        }

        showShadow = false;
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
        if (Input.isKeyJustPressed(KeyEvent.VK_4)) {
            effect = 0;
        }
        if (Input.isKeyJustPressed(KeyEvent.VK_5)) {
            effect = 1;
        }
        
        dx = dx * 0.98;
        if (dx < 1) {
            dx = 0;
        }

        this.x = dx + Camera.getX() + 192 - 52 + 120 * Math.sin(System.nanoTime() * 0.000000001);
        this.z = 250 + 30 * Math.cos(System.nanoTime() * 0.0000000027193);
        frameIndex += 0.5;
        animationPlayer.setFrame((int) frameIndex);
        animationPlayerb.setFrame((int) frameIndex);
        //if (frameIndex >= frames.length) {
        //    frameIndex = 0;
        //}

        //System.out.println("dust " + (int) (System.nanoTime() * 0.000000002));
        int showTime = (int) (System.nanoTime() * 0.00000002);
        if (lastDustShowTime < 0 || (lastDustShowTime != showTime && showTime % 2 == 0)) {
            lastDustShowTime = showTime;
            for (int i = 0; i < 3; i++) {
                spawnDust((int) x + 10 - 52, (int) z + 4, i * 50);
                spawnDust((int) x + 60 - 52, (int) z + 34, i * 50);
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        //g.drawImage(frames[(int) frameIndex], (int) x, (int) (z - frames[(int) frameIndex].getHeight() + 32), null);
        Composite oc = g.getComposite();
        
        if (effect == 0) {
            animationPlayerb.draw(g, (int) x, (int) z);
        }
        else {
            g.setComposite(additiveComposite);
            animationPlayer.draw(g, (int) x, (int) z);
        }
        
        g.setComposite(oc);
    }

    public void show(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public static class AdditiveComposite implements Composite {

        private double intensity;

        public AdditiveComposite(double intensity) {
            super();
            this.intensity = intensity;
        }

        @Override
        public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
            return new AdditiveCompositeContext(intensity);
        }

    }

    private static class AdditiveCompositeContext implements CompositeContext {

        private double intensity;

        public AdditiveCompositeContext(double intensity) {
            this.intensity = intensity;
        }

        public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
            int w1 = src.getWidth();
            int h1 = src.getHeight();
            int chan1 = src.getNumBands();
            int w2 = dstIn.getWidth();
            int h2 = dstIn.getHeight();
            int chan2 = dstIn.getNumBands();

            int minw = Math.min(w1, w2);
            int minh = Math.min(h1, h2);
            int minCh = Math.min(chan1, chan2);

            for (int x = 0; x < dstIn.getWidth(); x++) {
                for (int y = 0; y < dstIn.getHeight(); y++) {
                    float[] pxSrc = null;
                    pxSrc = src.getPixel(x, y, pxSrc);
                    float[] pxDst = null;
                    pxDst = dstIn.getPixel(x, y, pxDst);

                    float alpha = 255;
                    if (pxSrc.length > 3) {
                        alpha = pxSrc[3];
                    }

                    for (int i = 0; i < 3 && i < minCh; i++) {
                        pxDst[i] = Math.min(255, ((int) (pxSrc[i] * intensity) * (alpha / 255)) + (pxDst[i]));
                        //pxDst[i] = Math.max(0, pxDst[i] - (pxSrc[i] * (alpha / 255)));
                        dstOut.setPixel(x, y, pxDst);
                    }
                }
            }
        }

        public void dispose() {
        }
    }

}
