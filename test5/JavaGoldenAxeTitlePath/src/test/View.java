package test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author leonardo
 */
public class View extends JPanel implements MouseListener, KeyListener {
    
    private boolean isOn = true;
    private BufferedImage frame;
    private List<Point> lastPoints = new ArrayList<Point>();
    
    private List<Point> firstPath;
    
    private List<Arrow> arrows = new ArrayList<>();
    
    private int frameIndex = 0;
    
    private boolean started = false;
    
    private BufferedImage kanji;
    
    private String[] generatedArray = {"", "", "", "", "", "", "", "", ""};
    
    public View() {
        try {
            frame = new BufferedImage(934, 662, BufferedImage.TYPE_INT_RGB);
            
            kanji = ImageIO.read(getClass().getResourceAsStream("kanji.png"));
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        
        addMouseListener(this);
        addKeyListener(this);
        
        createLastPoints();
        
        for (int i = 0; i < lastPoints.size(); i++) {
            Point lastPoint = lastPoints.get(i);
            List<Point> path = new ArrayList<>();
            createPath2(path, lastPoint);
            if (firstPath == null) {
                firstPath = path;
            }
            arrows.add(new Arrow(path, Color.RED, i * 31, 2));
        }
        
        //createArrows2();

//        for (int i = 0; i< 1000; i++) {
//            for (Arrow arrow : arrows) {
//                arrow.update();
//            }
//        }
        
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    update();
                }
                
                if (started) {
                    for (int arrowIndex = 0; arrowIndex < 9; arrowIndex++) {
                        if (!arrows.get(arrowIndex).isFinished()) {
                            generatedArray[arrowIndex] += "" + (int) arrows.get(arrowIndex).getPosition().x + "," + (int) arrows.get(arrowIndex).getPosition().y + ",";
                        }
                    }
                    
                    // exit if all arrow was finished
                    boolean allFinished = true;
                    for (int arrowIndex = 0; arrowIndex < 9; arrowIndex++) {
                        if (!arrows.get(arrowIndex).isFinished()) {
                            allFinished = false;
                            break;
                        }
                    }
                    
                    if (allFinished) {
                        System.out.println("int[][] letters = { ");
                        for (int i = 0; i < generatedArray.length; i++) {
                            System.out.println("{ " + generatedArray[i] + lastPoints.get(i).x + "," + (lastPoints.get(i).y - 1) + "}, ");
                        }
                        System.out.println("};");
                        System.exit(0);
                    }
                }
                
                repaint();
            }
        }, 100, 1000 / 60);
    }

    private void createLastPoints() {
        int dx = 365;
        int dy = 185;
        lastPoints.add(new Point(16 + dx, 176 + dy));
        lastPoints.add(new Point(40 + dx, 176 + dy));
        lastPoints.add(new Point(64 + dx, 176 + dy));
        lastPoints.add(new Point(88 + dx, 176 + dy));
        lastPoints.add(new Point(112 + dx, 176 + dy));
        lastPoints.add(new Point(136 + dx, 176 + dy));
        lastPoints.add(new Point(172 + dx, 176 + dy));
        lastPoints.add(new Point(196 + dx, 176 + dy));
        lastPoints.add(new Point(220 + dx, 176 + dy));
        //sprite letter RIGHT 16 176 21 42 0 0
        //sprite letter RIGHT 40 176 21 42 0 0
        //sprite letter RIGHT 64 176 21 42 0 0
        //sprite letter RIGHT 88 176 21 42 0 0
        //sprite letter RIGHT 112 176 21 42 0 0
        //sprite letter RIGHT 136 176 21 42 0 0
        //sprite letter RIGHT 172 176 22 42 0 0
        //sprite letter RIGHT 196 176 23 42 0 0
        //sprite letter RIGHT 220 176 21 42 0 0
        
        for (int i = 0; i < lastPoints.size(); i++) {
            System.out.println("lastPoints " + i + " = " + lastPoints.get(i));
        }
    }
    
    
    private void createPath2(List<Point> path2, Point lastPoint) {

        path2.add(new Point(667, 456));
        path2.add(new Point(516, 443));
        path2.add(new Point(413, 421));
        path2.add(new Point(350, 397));
        path2.add(new Point(323, 380));
        path2.add(new Point(307, 361));
        path2.add(new Point(305, 343));
        path2.add(new Point(314, 324));
        
        double d = 360 / 16;
        for (double angle = 210; angle < 765; angle += d) {
            double x = (150 + 0) * Math.cos(Math.toRadians(angle));
            double y = 28 * Math.sin(Math.toRadians(angle));
            double aay = 0;
            
            if (angle < 360) {
                aay = 0;
            }
            else if (angle < 540) {
                aay = 10;
            } 
            else if (angle < 720) {
                aay = 0;
            }
            else {
                aay = 20;
            }
            path2.add(new Point((int) (480 + x), (int) (311 + y + aay)));
        }
        
        path2.add(lastPoint);
    }
    
//    private void createArrows2() {
//        for (int i = 0; i < 8; i++) {
//            arrows.add(new Arrow(path2, Color.RED, i * 100, 3));
//        }
//    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) frame.getGraphics();
        draw(g2d);
        g.drawImage(frame, 0, 0, null);
    }
    
    public void update() {
        if (!started) return;
        
        for (Arrow arrow : arrows) {
            arrow.update();
        }
    }
    
    public void draw(Graphics2D g) {
        g.clearRect(0, 0, frame.getWidth(), frame.getHeight());
        
        g.setColor(Color.WHITE);
        g.drawRect(350, 250, 276, 207);

        g.drawImage(kanji, 380, 260, null);

        g.setColor(Color.GREEN);
        for (Point p : firstPath) {
            g.fillOval(p.x - 5, p.y - 5, 10, 10);
        }


        if (isOn) {
            for (Arrow arrow : arrows) {
                arrow.draw(g);
            }
        }
        
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                View view = new View();
                JFrame frame = new JFrame();
                frame.setTitle("Golden Axe Title Path Generator");
                frame.getContentPane().add(view);
                frame.setSize(934, 662);
                //frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setResizable(false);
                frame.setVisible(true);
                view.requestFocus();
            }
        });
    }    

    @Override
    public void mouseClicked(MouseEvent e) {
        //path2.add(new Point(e.getX(), e.getY()));
        //System.out.println("path2.add(new Point(" + e.getX() + ", " + e.getY() + "));");
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

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
//        if (e.getKeyCode() == KeyEvent.VK_2) {
//            createArrows2();
//        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            started = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
    
}
