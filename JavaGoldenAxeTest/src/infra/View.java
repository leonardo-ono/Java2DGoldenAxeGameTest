package infra;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Java 3D Mesh vs Sphere Collision Test
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com);
 */
public class View extends JPanel {
    
    private BufferedImage stage;
    private BufferedImage stageBackground;
    private BufferedImage collisionMask;
    private BufferedImage go;
    private Audio audio;

    private double targetMinX = Integer.MAX_VALUE;
    private double targetY;
    
    private double x;
    private double y;
    
    private double backgroundX;
    
    private Player player;
    
    private Water water;
    
    public View() {
        try {
            stage = ImageIO.read(getClass().getResourceAsStream("stage_2.png"));
            stageBackground = ImageIO.read(getClass().getResourceAsStream("stage_1_background.png"));
            collisionMask = ImageIO.read(getClass().getResourceAsStream("stage_2_collision_mask.png"));
            go = ImageIO.read(getClass().getResourceAsStream("/res/image/go.png"));
        } catch (IOException ex) {
            Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        
        player = new Player(this);
        water = new Water(this);
        
        audio = new Audio();
        audio.start();
        //audio.loadMusic(1, "/res/music/wilderness.mid");
        //audio.loadMusic(1, "/res/music/battle-field-2-.mid");
        audio.loadMusic(1, "/res/music/path-of-the-fiend-2-.mid");
        audio.playMusic(1);
        
        audio.loadSound(1, "/res/sound/29.wav");
        audio.playSound(1);
    }

    public BufferedImage getCollisionMask() {
        return collisionMask;
    }
    
    // blocked but not includes jump
    public boolean isBlocked(double x, double y) {
        boolean result = true;
        try {
            int c = collisionMask.getRGB((int) x, (int) y);
            // System.out.println("color = " + new Color(c));
            result = c == Color.BLACK.getRGB();
        }
        catch (Exception e) {
        }
        return result;
    }

    // blocked including jump
    public boolean isBlocked2(double x, double y) {
        boolean result = true;
        try {
            int c = collisionMask.getRGB((int) x, (int) y);
            // System.out.println("color = " + new Color(c));
            result = c == Color.CYAN.getRGB();
        }
        catch (Exception e) {
        }
        return result;
    }
    
    public void start() {
        addKeyListener(new Input());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        update();
        draw((Graphics2D) g);
        try {
            Thread.sleep(1000 / 60);
        } catch (InterruptedException ex) {
        }
        repaint();
    }
    
    private void update() {
        player.update();
        water.update();
        
        double currentX = (-player.getX() + 276 / 2);
        if (currentX < targetMinX) {
            targetMinX = currentX;
        }
        targetY = (int) ((player.getFloorHeight() - player.getZ()) + 207 / 1.5);
        
        x = x + (targetMinX - x) * 0.05;
        y = y + (targetY - y) * 0.05;
        if (x > 0) x = 0;
        if (y > 0) y = 0;
    }
    
    private void draw(Graphics2D g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        g.drawLine(0, 0, getWidth(), getHeight());
        
        g.scale(2.5, 2.5);
        
        
        //g.translate(targetMinX, targetY);
        g.translate((int) x, (int) y);
        g.drawImage(stageBackground, (int) (-x * 0.25), 0, null);
        g.drawImage(stage, 0, 0, null);
        
        //g.drawImage(collisionMask, 0, 0, null);
        
        player.draw(g);
        
        water.draw(g);
        
        long showGo = (System.nanoTime() / 400000000) % 2;
        if (showGo == 0) {
            g.drawImage(go, (int) -x + 193, (int) (-y + 42), null);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            View view = new View();
            view.setPreferredSize(new Dimension((int) (276 * 2.5), (int) (207 * 2.5)));
            JFrame frame = new JFrame();
            frame.setTitle("Java Golden Axe Test #1");
            frame.getContentPane().add(view);
            frame.setResizable(false);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            view.requestFocus();
            view.start();
        });
    }
    
}
