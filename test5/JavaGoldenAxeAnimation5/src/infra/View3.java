package infra;


import infra.Collider.AttackType;
import infra.Collider.Type;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Colliders
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com);
 */
public class View3 extends JPanel implements KeyListener, MouseListener {

    
    //public static final String FILE = "D:\\golden_axe\\JavaGoldenAxeAnimation5\\src\\enemy\\longmoan\\colliders.txt";
    //public static final String IMAGE_FILE = "/enemy/longmoan/longmoan.png";
    
    public static final String FILE = "D:\\golden_axe\\JavaGoldenAxeAnimation5\\src\\player\\gilius\\colliders.txt";
    public static final String IMAGE_FILE = "/player/gilius/gilius.png";
    
    public static final double SCALE = 2.5;
    
    private BufferedImage image;
    private int x;
    private int y;
    
    private List<Collider> colliders = new ArrayList<>();
    private Collider currentCollider;
    
    private static final Color SELECTED_ATTACK = new Color(255, 0, 0, 128);
    private static final Color SELECTED_BODY = new Color(0, 0, 255, 128);
    private static final Color SELECTED_FORCE_ATTACK = new Color(0, 255, 0, 128);
    
    public View3() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream(IMAGE_FILE));
        } catch (IOException ex) {
            Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }

    public void start() {
        addKeyListener(new Input());
        addKeyListener(this);
        addMouseListener(this);
        loadColliders();
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
        double speed = 4;
        
        // scroll screen
        if (Input.isKeyPressed(KeyEvent.VK_LEFT)) {
            x -= speed;
        } 
        if (Input.isKeyPressed(KeyEvent.VK_RIGHT)) {
            x += speed;
        }

        if (Input.isKeyPressed(KeyEvent.VK_UP)) {
            y -= speed;
        } 
        if (Input.isKeyPressed(KeyEvent.VK_DOWN)) {
            y += speed;
        }

        speed = 1;
        
        if (Input.isKeyJustPressed(KeyEvent.VK_G)) {
            currentCollider.x -= speed;
        } 
        if (Input.isKeyJustPressed(KeyEvent.VK_J)) {
            currentCollider.x += speed;
        }

        if (Input.isKeyJustPressed(KeyEvent.VK_Y)) {
            currentCollider.y -= speed;
        } 
        if (Input.isKeyJustPressed(KeyEvent.VK_H)) {
            currentCollider.y += speed;
        }

        if (Input.isKeyJustPressed(KeyEvent.VK_S)) {
            currentCollider.width -= speed;
        } 
        if (Input.isKeyJustPressed(KeyEvent.VK_F)) {
            currentCollider.width += speed;
        }

        if (Input.isKeyJustPressed(KeyEvent.VK_E)) {
            currentCollider.height -= speed;
        } 
        if (Input.isKeyJustPressed(KeyEvent.VK_D)) {
            currentCollider.height += speed;
        }
        
    }

    private void draw(Graphics2D g) {
        g.clearRect(0, 0, getWidth(), getHeight());

        g.scale(SCALE, SCALE);
        
        //g.translate(276 / 2, 207 / 2);
        //g.drawLine(-276 / 2, 0, 276, 0);
        //g.drawLine(0, -207 / 2, 0, 207);

        g.translate(-x, -y);
        
        g.drawImage(image, 0, 0, null);
        
        for (Collider collider : colliders) {

            if (currentCollider != null && currentCollider == collider) {
                if (collider.getType() == Collider.Type.BODY) {
                    g.setColor(SELECTED_BODY);
                }
                else if (collider.getType() == Collider.Type.ATTACK) {
                    g.setColor(SELECTED_ATTACK);
                }
                else {
                    g.setColor(SELECTED_FORCE_ATTACK);
                }
                g.fillRect(collider.x, collider.y, collider.width, collider.height);
            }
            else {
                if (collider.getType() == Collider.Type.BODY) {
                    g.setColor(Color.BLUE);
                }
                else if (collider.getType() == Collider.Type.ATTACK) {
                    g.setColor(Color.RED);
                }
                else {
                    g.setColor(Color.GREEN);
                }
                g.drawRect(collider.x, collider.y, collider.width, collider.height);
            }
            
            if (collider.getType() == Type.ATTACK) {
                if (collider.isKnockDown()) {
                    g.drawString("K", collider.x, collider.y);
                }

                g.drawString("" + collider.getAttackType(), collider.x + 16, collider.y);
            }
            else if (collider.getType() == Type.FORCE_ATTACK) {
                g.drawString("" + collider.getForceAttackId(), collider.x, collider.y);
            }
        }
        
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            View3 view = new View3();
            view.setPreferredSize(new Dimension((int) (276 * SCALE), (int) (207 * SCALE)));
            JFrame frame = new JFrame();
            frame.setTitle("Java");
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
        int cx = (int) ((e.getX()) / SCALE) + x;
        int cy = (int) ((e.getY()) / SCALE) + y;
        
        // select existent collider
        for (Collider collider : colliders) {
            if (collider.contains(cx, cy)) {
                currentCollider = collider;
                return;
            }
        }
        
        // new collider
        int cwidth = (int) (50 / 2.5);
        int cheight = (int) (50 / 2.5);
        colliders.add(currentCollider = new Collider(cx, cy, cwidth, cheight));
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
        } 
        else if (e.getKeyCode() == KeyEvent.VK_2) {
        }        

        // save file
        if (e.getKeyCode() == KeyEvent.VK_0) {
            saveColliders();
        } 

        // set attack type
        if (e.getKeyCode() == KeyEvent.VK_1 && currentCollider != null) {
            currentCollider.setAttackType(Collider.AttackType.NONE);
        }
        if (e.getKeyCode() == KeyEvent.VK_2 && currentCollider != null) {
            currentCollider.setAttackType(Collider.AttackType.SWING);
        }
        if (e.getKeyCode() == KeyEvent.VK_3 && currentCollider != null) {
            currentCollider.setAttackType(Collider.AttackType.RAP);
        }
        if (e.getKeyCode() == KeyEvent.VK_4 && currentCollider != null) {
            currentCollider.setAttackType(Collider.AttackType.THROW);
        }

        
        // set attack collider type
        if (e.getKeyCode() == KeyEvent.VK_A && currentCollider != null) {
            currentCollider.setType(Collider.Type.ATTACK);
        }
        
        // set body collider type
        if (e.getKeyCode() == KeyEvent.VK_B && currentCollider != null) {
            currentCollider.setType(Collider.Type.BODY);
        }

        // set force_attack collider type
        if (e.getKeyCode() == KeyEvent.VK_C && currentCollider != null) {
            currentCollider.setType(Collider.Type.FORCE_ATTACK);
        }

        // set knock down
        if (e.getKeyCode() == KeyEvent.VK_K && currentCollider != null) {
            currentCollider.setKnockDown(!currentCollider.isKnockDown());
        }
        
        // delete
        if (e.getKeyCode() == KeyEvent.VK_DELETE && currentCollider != null) {
            colliders.remove(currentCollider);
            currentCollider = null;
        } 
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
 
    private void loadColliders() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(FILE));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] data = line.split("\\ ");
                if (data[0].equals("collider")) {
                    String[] attackData = data[1].split(":");
                    String attackTypeStr = attackData[0];
                    String forceAttackIdStr = null;
                    if (attackData.length > 1) {
                        forceAttackIdStr = attackData[1];
                    }
                    Type type = Type.valueOf(attackTypeStr);
                    AttackType attackType = AttackType.valueOf(data[2]);
                    boolean knockDown = Boolean.parseBoolean(data[3]);
                    int x = Integer.parseInt(data[4]);
                    int y = Integer.parseInt(data[5]);
                    int width = Integer.parseInt(data[6]);
                    int height = Integer.parseInt(data[7]);
                    colliders.add(currentCollider = new Collider(x, y, width, height));
                    currentCollider.setType(type);
                    currentCollider.setAttackType(attackType);
                    currentCollider.setKnockDown(knockDown);
                    currentCollider.setForceAttackId(forceAttackIdStr);
                }
            }
            br.close();
        }
        catch (Exception ex) {
            Logger.getLogger(View2.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    private void saveColliders() {
        try {
            PrintWriter pw = new PrintWriter(FILE);
            for (Collider collider : colliders) {
                pw.println(collider);
            }
            pw.close();
        }
        catch (Exception ex) {
            Logger.getLogger(View2.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
}
