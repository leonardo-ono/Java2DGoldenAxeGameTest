package infra;


import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;


/**
 *
 * @author admin
 */
public class Sprite implements Comparable<Sprite> {

    private final BufferedImage image;
    private final Point origin;
    private double x;
    private double z;
    private final double initialAngle;
    private final AffineTransform transform;
    
    public Sprite(BufferedImage image, int ox, int oy, double initialAngle) {
        this.image = image;
        origin = new Point(ox, oy);
        this.initialAngle = initialAngle;
        transform = new AffineTransform();
    }
    
    public void update(double angle) {
        z = 0.9 + 0.1 * Math.sin(angle + initialAngle);
        x = 30 * Math.cos(angle + initialAngle);
    }
    
    public void draw(Graphics2D g, int tx, int ty, boolean flipHorizontal) {
        transform.setToIdentity();
        transform.scale(z, z);
        transform.translate(x + tx - origin.x, ty - origin.y);
        g.drawImage(image, transform, null);
    }

    @Override
    public int compareTo(Sprite o) {
        return (int) Math.signum(z - o.z);
    }
    
}
