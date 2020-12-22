package infra;

import static infra.Settings.*;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;
import scene.Stage;

/**
 * Display class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Display extends Canvas {
    
    private final JFrame frame;
    private BufferedImage backbuffer;
    private StateManager<Scene> scenes;
    private BufferStrategy bs;
    private boolean running;
    private Thread thread;
    
    public Display(JFrame frame) {
        this.frame = frame;
        setBackground(Color.DARK_GRAY);
    }
    
    public void start() {
        createBufferStrategy(2);
        bs = getBufferStrategy();
        //Audio.initialize();
        backbuffer = new BufferedImage(
                CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
        
        initAllScenes();
        
        running = true;
        thread = new Thread(new MainLoop());
        thread.start();
        addKeyListener(new Input());
        
        Audio.start();
        Audio.loadMusic(1, "/res/music/wilderness.mid");
        //Audio.loadMusic(1, "/res/music/battle-field-2-.mid");
        //Audio.loadMusic(1, "/res/music/path-of-the-fiend-2-.mid");
        Audio.playMusic(1);

    }
    
    private void initAllScenes() {
        scenes = new StateManager<>();
        //scenes.addState(new Initializing(scenes));
        //scenes.addState(new OLPresents(scenes));
        //scenes.addState(new Title(scenes));
        //scenes.addState(new Hiscores(scenes));
        scenes.addState(new Stage(scenes));
        //scenes.addState(new HiscoreTop(scenes));
        //scenes.addState(new HiscoreEnterName(scenes));
        scenes.initAll();
        scenes.switchTo("stage");
    }
    
    private class MainLoop implements Runnable {

        @Override
        public void run() {
            long currentTime = System.nanoTime();
            long lastTime = currentTime;
            long delta;
            long unprocessedTime = 0;
            boolean updated = false;
            while (running) {
                currentTime = System.nanoTime();
                delta = currentTime - lastTime;
                unprocessedTime += delta;
                lastTime = currentTime;
                while (unprocessedTime >= Settings.REFRESH_PERIOD) {
                    unprocessedTime -= Settings.REFRESH_PERIOD;
                    update();
                    updated = true;
                }
                if (updated) {
                    Graphics2D g = (Graphics2D) bs.getDrawGraphics();
                    draw((Graphics2D) backbuffer.getGraphics());
                    
                    if (KEEP_ASPECT_RATIO) {
                        g.clearRect(0, 0, getWidth(), getHeight());
                        int left = 0;
                        int width = getWidth();
                        int height = (int) (width / ASPECT_RATIO);
                        int top = (int) ((getHeight() - height) / 2);
                        if (top < 0) {
                            top = 0;
                            height = getHeight();
                            width = (int) (height * ASPECT_RATIO);
                            left = (int) ((getWidth() - width) / 2);
                        }
                        g.drawImage(backbuffer
                                , left, top, width, height, null);
                    }
                    else {
                        g.drawImage(backbuffer
                                , 0, 0, getWidth(), getHeight(), null);
                    }
                    
                    g.dispose();
                    bs.show();
                    updated = false;
                }
                
                try {
                    Thread.sleep(3);
                } catch (InterruptedException ex) {
                }
            }
        }
        
    }
    
    private void update() {
        scenes.update();
        
        if (Input.isKeyJustPressed(KeyEvent.VK_SPACE)) {
            int width = getWidth();
            int height = (int) (width / ASPECT_RATIO);
            int top = (int) ((getHeight() - height) / 2);
            if (top < 0) {
                height = getHeight();
                width = (int) (height * ASPECT_RATIO);
            }
                        
            setPreferredSize(new Dimension(width, height));
            frame.pack();
        }
    }

    private void draw(Graphics2D g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        scenes.draw(g);
    }

}
