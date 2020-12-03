package infra;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Golden Axe - Select Player (Scene) Test
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com);
 */
public class View extends JPanel {
    
    private BufferedImage background;
    private BufferedImage backgroundSkeleton;
    private List<Sprite> sprites = new ArrayList<>();
    
    private int angle;
    private boolean nextCharacter;
    private int direction;
    
    public View() {
        try {
            background = ImageIO.read(getClass().getResourceAsStream("/res/image/background.png"));
            backgroundSkeleton = ImageIO.read(getClass().getResourceAsStream("/res/image/select_player_skeleton.png"));
            
            BufferedImage i0 = ImageIO.read(getClass().getResourceAsStream("/res/image/select_player_0.png"));
            BufferedImage i1 = ImageIO.read(getClass().getResourceAsStream("/res/image/select_player_1.png"));
            BufferedImage i2 = ImageIO.read(getClass().getResourceAsStream("/res/image/select_player_2.png"));
            sprites.add(new Sprite(i0, 20, 82, Math.toRadians(0)));
            sprites.add(new Sprite(i1, 23, 82, Math.toRadians(120)));
            sprites.add(new Sprite(i2, 20, 82, Math.toRadians(240)));
        } catch (IOException ex) {
            Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
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
        if (Input.isKeyPressed(KeyEvent.VK_LEFT)) {
            nextCharacter = true;
            direction = 1;
        }
        else if (Input.isKeyPressed(KeyEvent.VK_RIGHT)) {
            nextCharacter = true;
            direction = -1;
        }
        
        if (nextCharacter) {
            angle += direction * 4;
            while (angle < 0) {
                angle += 360;
            }
            if (angle % 120 == 0) {
                nextCharacter = false;
            }
        }
        sprites.forEach(sprite -> sprite.update(Math.toRadians(angle - 15)));
    }
    
    private void draw(Graphics2D g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        g.drawLine(0, 0, getWidth(), getHeight());
        
        g.scale(2.5, 2.5);
        
        g.drawImage(background, 0, 0, null);
        g.drawImage(backgroundSkeleton, 0, 0, null);
        
        Collections.sort(sprites);
        for (Sprite sprite : sprites) {
            sprite.draw(g, 70, 172, true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            View view = new View();
            view.setPreferredSize(new Dimension((int) (276 * 2.5), (int) (207 * 2.5)));
            JFrame frame = new JFrame();
            frame.setTitle("Java Golden Axe - Select Player (Scene) / Test #2");
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
