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
    
    public void draw(Graphics2D g, int x, int y) {
        g.drawImage(image, x - origin.x, y - origin.y, null);
    }

    @Override
    public String toString() {
        return "sprite " + rectangle.x + " " + rectangle.y 
                + " " + rectangle.width + " " + rectangle.height + " " 
                + origin.x + " " + origin.y;
    }
    
}
