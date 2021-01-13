package infra;

import java.awt.Graphics2D;
import scene.Stage;

/**
 * Actor class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public abstract class Actor extends Entity<Stage> {

    protected double x;
    protected double y;
    protected double minHeight;
    protected final StateManager<Actor> stateManager;
    protected boolean alive = true;
    
    public Actor(Stage stage, String spriteResource, int spriteSize) {
        super(stage);
        stateManager = new StateManager<>();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(double minHeight) {
        this.minHeight = minHeight;
    }

    public void translate(int dx, int dy, int dz) {
        x += dx;
        y += dy;
        z += dz;
    }

    public StateManager getStateManager() {
        return stateManager;
    }

    public boolean isAlive() {
        return alive;
    }
    
    @Override
    public void update() {
        stateManager.update();
    }

    @Override
    public void draw(Graphics2D g) {
        stateManager.draw(g);
    }    

    /**
     * Idle (State) class.
     */
    protected class Idle extends State<Actor> {
        
        public Idle(StateManager stateManager, Actor owner) {
            super(stateManager, "idle", owner);
        }

        @Override
        public void onEnter() {
        }

        @Override
        public void update() {
        }

        @Override
        public void draw(Graphics2D g) {
        }
        
    }
    
}
