package infra;

import java.awt.Graphics2D;

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
