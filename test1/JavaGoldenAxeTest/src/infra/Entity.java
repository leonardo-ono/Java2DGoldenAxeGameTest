package infra;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author admin
 */
public class Entity {

    protected View view;

    public Entity(View view) {
        this.view = view;
    }

    public void update() {
    }
    
    public void draw(Graphics2D g) {
    }
    
}
