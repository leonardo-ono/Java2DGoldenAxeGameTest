package infra;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;


/**
 * Sprite class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Sprite1 implements Comparable<Sprite1> {
    
    private static final Color SELECTED_ATTACK = new Color(255, 0, 0, 128);
    private static final Color SELECTED_BODY = new Color(0, 0, 255, 128);
    private static final Color SELECTED_FORCE_ATTACK = new Color(0, 255, 0, 128);

    private final String id;
    private final BufferedImage image;
    private final Point origin;
    private Direction originalDirection;
    private final Rectangle rectangle;
    
    private int px;
    private int py;
    private int z;
    
    public Sprite1(String id, BufferedImage image, int ox, int oy, Direction originalDirection, Rectangle rectangle) {
        this.id = id;
        this.image = image;
        origin = new Point(ox, oy);
        this.originalDirection = originalDirection;
        this.rectangle = rectangle;
    }

    public BufferedImage getImage() {
        return image;
    }

    public Point getOrigin() {
        return origin;
    }
    
    public Direction getOriginalDirection() {
        return originalDirection;
    }

    public void set(int x, int y) {
        this.px = x;
        this.py = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
    
    public void draw(Graphics2D g, boolean flipHorizontal) {
        draw(g, px, py, flipHorizontal);
    }
    
    public void draw(Graphics2D g, int x, int y, boolean flipHorizontal) {
        //g.drawImage(image, x - origin.x, y - origin.y, image.getWidth(), image.getHeight(), null);
        if (originalDirection == Direction.LEFT) {
            flipHorizontal = !flipHorizontal;
        }
            
        int sx =  x - (image.getWidth() - origin.x);
        int sy =  y - origin.y;
        
        int dx1 = sx + image.getWidth();
        int dy1 = sy; 
        int dx2 = sx;
        int dy2 = dy1 + image.getHeight();
        
        if (!flipHorizontal) {
            dx1 = x - origin.x;
            dx2 = dx1 + image.getWidth();
        }
        
        int sx1 = 0;
        int sy1 = 0;
        int sx2 = image.getWidth();
        int sy2 = image.getHeight();
        g.drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
    }

    @Override
    public String toString() {
        return "sprite " + rectangle.x + " " + rectangle.y 
                + " " + rectangle.width + " " + rectangle.height + " " 
                + origin.x + " " + origin.y;
    }

    @Override
    public int compareTo(Sprite1 o) {
        return z - o.z;
    }
    
}
