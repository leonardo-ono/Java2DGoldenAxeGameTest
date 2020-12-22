package infra;


import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;


/**
 *
 * @author admin
 */
public class Sprite {

    private final BufferedImage image;
    private final Point origin;
    private final Rectangle rectangle;
    
    public Sprite(BufferedImage image, int ox, int oy, Rectangle rectangle) {
        this.image = image;
        origin = new Point(ox, oy);
        this.rectangle = rectangle;
    }

    public BufferedImage getImage() {
        return image;
    }

    public Point getOrigin() {
        return origin;
    }
    
    public void draw(Graphics2D g, int x, int y, boolean flipHorizontal) {
        //g.drawImage(image, x - origin.x, y - origin.y, image.getWidth(), image.getHeight(), null);
        
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
    
}
