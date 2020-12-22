
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * @author Leonardo Ono (ono.leo80@gmail.com);
 */
public class View2 extends JPanel implements KeyListener, MouseListener {

    private static String file = "attack.txt";
    
    private SpriteSheet spriteSheet;
    private double x;
    private double y;
    private int frame;

    
    public View2() {
        spriteSheet = new SpriteSheet("gilius.png", file);
        x = spriteSheet.getSprites().get(frame).getOrigin().x;
        y = spriteSheet.getSprites().get(frame).getOrigin().y;
    }

    public void start() {
        addKeyListener(new Input());
        addKeyListener(this);
        addMouseListener(this);
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
        double speed = 0.25;
        if (Input.isKeyPressed(KeyEvent.VK_LEFT)) {
            x -= speed;
        } else if (Input.isKeyPressed(KeyEvent.VK_RIGHT)) {
            x += speed;
        }

        if (Input.isKeyPressed(KeyEvent.VK_UP)) {
            y -= speed;
        } else if (Input.isKeyPressed(KeyEvent.VK_DOWN)) {
            y += speed;
        }

        spriteSheet.getSprites().get(frame).getOrigin().x = (int) x;
        spriteSheet.getSprites().get(frame).getOrigin().y = (int) y;

    }

    private void draw(Graphics2D g) {
        g.clearRect(0, 0, getWidth(), getHeight());

        g.scale(2.5, 2.5);
        
        g.drawString("frame: " + frame, 10, 10);
        
        g.translate(276 / 2, 207 / 2);
        
        spriteSheet.getSprites().get(frame).draw(g, 0, 0);
        
        g.drawLine(-276 / 2, 0, 276, 0);
        g.drawLine(0, -207 / 2, 0, 207);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            View2 view = new View2();
            view.setPreferredSize(new Dimension((int) (276 * 2.5), (int) (207 * 2.5)));
            JFrame frame = new JFrame();
            frame.setTitle("Java 3D Mesh vs Sphere Collision Test");
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

    // --- mouse ---
    
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    // --- KeyListener ---
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
        if (e.getKeyCode() == KeyEvent.VK_1) {
            if (frame > 0) {
                frame--;
                x = spriteSheet.getSprites().get(frame).getOrigin().x;
                y = spriteSheet.getSprites().get(frame).getOrigin().y;
            }
        } 
        else if (e.getKeyCode() == KeyEvent.VK_2) {
            if (frame < spriteSheet.getSprites().size() - 1) {
                frame++;
                x = spriteSheet.getSprites().get(frame).getOrigin().x;
                y = spriteSheet.getSprites().get(frame).getOrigin().y;
            }
        }        

        if (e.getKeyCode() == KeyEvent.VK_S) {
            saveSpriteSheet();
        } 
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private void saveSpriteSheet() {
        try {
            PrintWriter pw = new PrintWriter("D:\\golden_axe\\JavaGoldenAxeAnimation\\src\\" + file);
            for (Sprite sprite : spriteSheet.getSprites()) {
                pw.println(sprite);
            }
            pw.close();
        }
        catch (Exception ex) {
            Logger.getLogger(View2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
