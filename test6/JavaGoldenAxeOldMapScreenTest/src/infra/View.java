package infra;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Golden Axe - Title screen test
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com);
 */
public class View extends JPanel {
    
    private BufferedImage pen;
    private BufferedImage mapBackground;
    
    private BufferedImage[] maps = new BufferedImage[14];
    private int mapIndex = -1;
    
    private BufferedImage map;
    
    // how many times it will write in the same frame
    private int[] writeTimes = { 
        2, // #1 
        4, // #2
        2, // #3 
        64,4,4,16,16,64, // #4
        8,8,8,8,1 // # 5 fin
    };
    
    // direction
    // cc  bb  aa  aa=start corner
    // 00__00__00  bb=1st direction
    // 0=r 0=r 0-1 cc=2nd direction
    // 1=l 1=l | |
    // 2=d 2=d 3-2
    // 3=u 3=u
    private int[] directions = { 
        0b10_00_00, // #1
        0b10_00_00, // #2
        0b10_00_00, // #3 
        0b01_10_01, 0b00_11_11, 0b10_00_00, 0b01_10_01, 0b11_00_11, 0b11_01_10, // #4 
        0b00_10_00, 0b10_01_01, 0b10_00_00, 0b00_10_00, 0b10_00_00 // #5 fin
    };
    
    private boolean[] keepPenPosition = { 
        false, // #1
        false, // #2 
        false, // #3 
        true, true, true, true, true, false, // #4
        true, true, true, true, false // #5 fin
    };
    
    private int dataColor = -5435376;
    
    
    public View() {
    }

    public void start() {
        try {
            pen = ImageIO.read(getClass().getResourceAsStream("/res/image/feather_pen.png"));
            mapBackground = ImageIO.read(getClass().getResourceAsStream("/res/image/old_map_background.png"));
            
            for (int i = 0; i < maps.length; i++) {
                maps[i] = ImageIO.read(getClass().getResourceAsStream("/res/image/old_map_" + i + ".png"));
            }
            
            map = new BufferedImage(mapBackground.getWidth(), mapBackground.getHeight(), BufferedImage.TYPE_INT_ARGB);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        
        Audio.start();
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
    
    int col = 0;
    int row = 0;
    int cols = 17;
    int rows = 27;
    boolean finished = true;
    
    double penX = 150;
    double penY = -10;
    double penStartX = 150;
    double penStartY = -10;
    int positioningPen = 0;

    private void nextMap() {
        mapIndex++;
        if (mapIndex > maps.length - 1) {
            finished = true;
            mapIndex = maps.length - 1;
            return;
        }
        
        int startCorner = directions[mapIndex] & 3;
        switch (startCorner) {
            case 0: // top left
                col = 0;
                row = 0;
                break;
            case 1: // top right
                col = rows - 1;
                row = 0;
                break;
            case 2: // bottom right
                col = cols - 1;
                row = rows - 1;
                break;
            case 3: // bottom left
                col = 0;
                row = rows - 1;
                break;
        }
        
        cols = 17;
        rows = 27;
        finished = false;

        //penX = 150;
        //penY = -10;
        //penStartX = 150;
        //penStartY = -10;
        positioningPen = 0;        
    }

    private void update() {
        if (finished && Input.isKeyPressedOnce(KeyEvent.VK_SPACE)) {
            nextMap();
            return;
        }
        
        if (finished || mapIndex < 0) {
            return;
        }

        // removing the pen from the map
        if (positioningPen == 3) {
            
            if (keepPenPosition[mapIndex]) {
                penStartX = penX;
                penStartY = penX;
                finished = true;
                nextMap();
                return;
            }
            
            penStartX = 200;
            penStartY = -10;
            
            double dx = penStartX - penX;
            double dy = penStartY - penY;
            double length = Math.sqrt(dx * dx + dy * dy);
            
            if (length < 1) {
                finished = true;
                return;
            }

            //dx /= length;
            //dy /= length;
            penX = penX + dx * 0.1;
            penY = penY + dy * 0.1;

            
            return;
        }

        // moving the pen to the map start writing position
        if (positioningPen == 1) {
            double dx = penStartX - penX;
            double dy = penStartY - penY;
            double length = Math.sqrt(dx * dx + dy * dy);
            
            if (length < 1) {
                map.setRGB((int) penStartX, (int) penStartY, dataColor);
                positioningPen = 2;
                return;
            }
            
            dx *= 0.1;
            dy *= 0.1;
            if (Math.abs(dx) < 1) dx = Math.signum(dx);
            if (Math.abs(dy) < 1) dy = Math.signum(dy);
            penX = penX + dx;
            penY = penY + dy;
            
            return;
        }
                
        if (copyingData) {
            for (int i = 0; i < writeTimes[mapIndex]; i++) {
                if (copyData(col, row)) {
                    copyingData = false;
                    return;
                }
                if (positioningPen == 1) { // first needs to position pen at starting position
                    return;
                }
            }
            return;
        }
        
        boolean found = false;
        
        while (!found) {
            
            int firstDirection = (directions[mapIndex] >> 2) & 3; 
            boolean firstDirectionFinished = false;
            switch (firstDirection) {
                case 0: // right
                    col = col + 1;
                    if (col > cols - 1) {
                        col = 0;
                        firstDirectionFinished = true;
                    }
                    break;
                case 1: // left
                    col = col - 1;
                    if (col < 0) {
                        col = cols - 1;
                        firstDirectionFinished = true;
                    }
                    break;
                case 2: // down
                    row = row + 1;
                    if (row > rows - 1) {
                        row = 0;
                        firstDirectionFinished = true;
                    }
                    break;
                case 3: // up
                    row = row - 1;
                    if (row < 0) {
                        row = rows - 1;
                        firstDirectionFinished = true;
                    }
                    break;
            }
            
            if (firstDirectionFinished) {
                int secondDirection = (directions[mapIndex] >> 4) & 3; 
                boolean secondDirectionFinished = false;
                switch (secondDirection) {
                    case 0: // right
                        col = col + 1;
                        if (col > cols - 1) {
                            col = 0;
                            secondDirectionFinished = true;
                        }
                        break;
                    case 1: // left
                        col = col - 1;
                        if (col < 0) {
                            col = cols - 1;
                            secondDirectionFinished = true;
                        }
                        break;
                    case 2: // down
                        row = row + 1;
                        if (row > rows - 1) {
                            row = 0;
                            secondDirectionFinished = true;
                        }
                        break;
                    case 3: // up
                        row = row - 1;
                        if (row < 0) {
                            row = rows - 1;
                            secondDirectionFinished = true;
                        }
                        break;
                } 
                
                if (secondDirectionFinished) {
                    positioningPen = 3;
                    return;
                }
            }
            
            System.out.println("col=" + col + " row=" + row);
            if (containsData(col, row)) {
                copyingData = true;
                
                int startCorner = directions[mapIndex] & 3;
                switch (startCorner) {
                    case 0: // top left
                        offsetX = 0;
                        offsetY = 0;
                        break;
                    case 1: // top right
                        offsetX = 7;
                        offsetY = 0;
                        break;
                    case 2: // bottom right
                        offsetX = 7;
                        offsetY = 7;
                        break;
                    case 3: // bottom left
                        offsetX = 0;
                        offsetY = 7;
                        break;
                }
                found = true;
            }
        }
        
        
    }

    boolean copyingData = false;
    int offsetX;
    int offsetY;
    private boolean copyData(int c, int r) {
        boolean exit = false;
        boolean copyFinished = false;
        while (!exit) {
            int x1 = c * 8 + offsetX;
            int y1 = r * 8 + offsetY;
            if (maps[mapIndex].getRGB(x1, y1) == dataColor) {
                
                if (positioningPen == 0) {
                    penStartX = x1;
                    penStartY = y1;
                    positioningPen = 1;
                }
                else {
                    map.setRGB(x1, y1, dataColor);
                    penX = x1;
                    penY = y1;
                }
                exit = true;
            }
            

            int firstDirection = (directions[mapIndex] >> 2) & 3; 
            boolean firstDirectionFinished = false;
            switch (firstDirection) {
                case 0: // right
                    offsetX = offsetX + 1;
                    if (offsetX > 8 - 1) {
                        offsetX = 0;
                        firstDirectionFinished = true;
                    }
                    break;
                case 1: // left
                    offsetX = offsetX - 1;
                    if (offsetX < 0) {
                        offsetX = 8 - 1;
                        firstDirectionFinished = true;
                    }
                    break;
                case 2: // down
                    offsetY = offsetY + 1;
                    if (offsetY > 8 - 1) {
                        offsetY = 0;
                        firstDirectionFinished = true;
                    }
                    break;
                case 3: // up
                    offsetY = offsetY - 1;
                    if (offsetY < 0) {
                        offsetY = 8 - 1;
                        firstDirectionFinished = true;
                    }
                    break;
            }
            
            if (firstDirectionFinished) {
                int secondDirection = (directions[mapIndex] >> 4) & 3; 
                boolean secondDirectionFinished = false;
                switch (secondDirection) {
                    case 0: // right
                        offsetX = offsetX + 1;
                        if (offsetX > 8 - 1) {
                            offsetX = 0;
                            secondDirectionFinished = true;
                        }
                        break;
                    case 1: // left
                        offsetX = offsetX - 1;
                        if (offsetX < 0) {
                            offsetX = 8 - 1;
                            secondDirectionFinished = true;
                        }
                        break;
                    case 2: // down
                        offsetY = offsetY + 1;
                        if (offsetY > 8 - 1) {
                            offsetY = 0;
                            secondDirectionFinished = true;
                        }
                        break;
                    case 3: // up
                        offsetY = offsetY - 1;
                        if (offsetY < 0) {
                            offsetY = 8 - 1;
                            secondDirectionFinished = true;
                        }
                        break;
                } 
                
                if (secondDirectionFinished) {
                    copyFinished = true;
                    exit = true;
                }
            }
                        
//            offsetX++;
//            if (offsetX > 7) {
//                offsetX = 0;
//                offsetY++;
//                if (offsetY > 7) {
//                    copyFinished = true;
//                    exit = true;
//                }
//            }
            
        }
        return copyFinished;
    }
    
    private boolean containsData(int c, int r) {
        int x1 = c * 8;
        int y1 = r * 8;
        for (int y = y1; y < y1 + 8; y++) {
            for (int x = x1; x < x1 + 8; x++) {
                if (maps[mapIndex].getRGB(x, y) == dataColor) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private void draw(Graphics2D g) {
        g.setBackground(Color.BLACK);
        g.clearRect(0, 0, getWidth(), getHeight());
        //g.drawLine(0, 0, getWidth(), getHeight());
        
        g.scale(2.5, 2.3);
        
        Text.draw(g, "PRESS SPACE", 19, 3, Color.WHITE, Color.BLUE);
        Text.draw(g, "FOR NEXT", 19, 4, Color.WHITE, Color.BLUE);
        Text.draw(g, "ANIMATION", 19, 5, Color.WHITE, Color.BLUE);

        g.drawImage(mapBackground, 0, 0, null);
        g.drawImage(map, 0, 0, null);
        g.drawImage(pen, (int) penX, (int) (penY - pen.getHeight()), null);
        
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            View view = new View();
            view.setPreferredSize(new Dimension((int) (276 * 2.5), (int) (207 * 2.5)));
            JFrame frame = new JFrame();
            frame.setTitle("Java Golden Axe - Old Map Screen / Test #6");
            frame.getContentPane().add(view);
            frame.setResizable(false);
            frame.pack();
            //frame.setLocationRelativeTo(null);
            frame.setLocation(150, 100);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            view.requestFocus();
            view.start();
        });
    }

}
