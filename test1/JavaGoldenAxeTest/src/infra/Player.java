package infra;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;


/**
 *
 * @author admin
 */
public class Player extends Entity {
    
    private SpriteSheet spriteSheet;
    
    private SpriteSheet spriteSheetWalkingDown;
    private SpriteSheet spriteSheetWalkingUp;
    private SpriteSheet spriteSheetIdle;
    private SpriteSheet spriteSheetJump;
    private SpriteSheet spriteSheetAttack;
    
    private Sprite2 sprite;
    
    private double x = 70; //650;
    private double jumpX = 270; //650;
    private double y = 48;
    private double floorHeight = 0;
    private double z = 150;
    
    private double vy = 0;
    private double vx = 0;
    private double vz = 0;
    
    private double frame;
    
    private boolean flipHorizontal;
    
    private long idleTime;
    private long jumpAttackEndTime;
    
    public Player(View view) {
        super(view);
        sprite = new Sprite2("gilius", 25, 55);
        spriteSheetWalkingDown = new SpriteSheet("/infra/gilius.png", "/infra/walking.txt");
        spriteSheetWalkingUp = new SpriteSheet("/infra/gilius.png", "/infra/walking_up.txt");
        spriteSheetIdle = new SpriteSheet("/infra/gilius.png", "/infra/idle.txt");
        spriteSheetJump = new SpriteSheet("/infra/gilius.png", "/infra/jump.txt");
        spriteSheetAttack = new SpriteSheet("/infra/gilius.png", "/infra/attack.txt");
        
        spriteSheet = spriteSheetWalkingDown;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getFloorHeight() {
        return floorHeight;
    }

    @Override
    public void update() {
        if (spriteSheet == spriteSheetAttack) {
            frame += 0.25;
            if (frame >= 4) {
                spriteSheet = spriteSheetIdle;
            }
            else {
                spriteSheet.setFrame((int) frame);
            }
            return;
        }
        
        boolean moving = Input.isKeyPressed(KeyEvent.VK_LEFT) || Input.isKeyPressed(KeyEvent.VK_RIGHT) 
                || Input.isKeyPressed(KeyEvent.VK_UP) || Input.isKeyPressed(KeyEvent.VK_DOWN);
        
        vx = 0;
        vz = 0;
        
        if (Input.isKeyPressed(KeyEvent.VK_LEFT) && (vy != 0 || !view.isBlocked(x - 1, z - y)) && !view.isBlocked2(x - 1, z - y)) {
            vx = -1;
            sprite.setFlipped(true);
            flipHorizontal = true;
            idleTime = System.currentTimeMillis() + 500;
            if (spriteSheet == spriteSheetIdle) {
                spriteSheet = spriteSheetWalkingDown;
            }
        }
        else if (Input.isKeyPressed(KeyEvent.VK_RIGHT) && (vy != 0 || !view.isBlocked(x + 1, z - y)) && !view.isBlocked2(x + 1, z - y)) {
            vx = 1;
            sprite.setFlipped(false);
            flipHorizontal = false;
            idleTime = System.currentTimeMillis() + 500;
            if (spriteSheet == spriteSheetIdle) {
                spriteSheet = spriteSheetWalkingDown;
            }
        }

        if (Input.isKeyPressed(KeyEvent.VK_UP) && vy == 0 && !view.isBlocked(x, z - 1 - y)
                && view.getCollisionMask().getRGB((int) x, (int) (z - 1 - y)) != Color.GREEN.getRGB()
                && view.getCollisionMask().getRGB((int) x, (int) (z - 1 - y)) != Color.YELLOW.getRGB() 
                && !view.isBlocked2(x, z - 1 - y)) {
            vz = -1;
            spriteSheet = spriteSheetWalkingUp;
            idleTime = System.currentTimeMillis() + 500;
        }
        else if (Input.isKeyPressed(KeyEvent.VK_DOWN) && vy == 0 && !view.isBlocked(x, z + 1 - y)
                && !view.isBlocked2(x, z + 1 - y)) {
            
            vz = 1;
            spriteSheet = spriteSheetWalkingDown;
            idleTime = System.currentTimeMillis() + 500;
        }

        if (!moving && System.currentTimeMillis() >= idleTime && spriteSheet != spriteSheetJump) {
            spriteSheet = spriteSheetIdle;
        }

        if ((int) y == floorHeight) {
            jumpX = x;
            
            if (spriteSheet == spriteSheetJump) {
                spriteSheet = spriteSheetWalkingDown;
                idleTime = System.currentTimeMillis() + 250;
            }
        }
        
        if (Input.isKeyPressed(KeyEvent.VK_X) && (int) y == floorHeight) {
            vy = 7;
            spriteSheet = spriteSheetJump;
            frame = 1;
            spriteSheet.setFrame((int) frame);
        }
        
        if (Input.isKeyPressed(KeyEvent.VK_Z) && spriteSheet == spriteSheetJump) {
            frame = 0;
            spriteSheet.setFrame((int) frame);
            jumpAttackEndTime = System.currentTimeMillis() + 16;
        }
        else if (Input.isKeyPressed(KeyEvent.VK_Z)) {
            spriteSheet = spriteSheetAttack;
            frame = 0;
            spriteSheet.setFrame((int) frame);
            return;
        }

        if (spriteSheet == spriteSheetJump && frame == 0 && System.currentTimeMillis() >= jumpAttackEndTime) {
            frame = 1;
        }
        
        vy -= 0.4;

        if (spriteSheet == spriteSheetJump && frame == 1 && vy < 0) {
            frame = 2;
            spriteSheet.setFrame((int) frame);
        }

        x += vx;
        y += vy;
        z += vz;
        
        int c = view.getCollisionMask().getRGB((int) x, (int) (z - y));

        if (c == Color.GREEN.getRGB() && vy < 0 && vx >= 0 && ((int) x != (int) jumpX || floorHeight > 0)) {
            vx = -1;
            x += vx;
        }
        if (c == Color.YELLOW.getRGB() && vy < 0 && vx <= 0 && ((int) x != (int) jumpX || floorHeight > 0)) {
            vx = 1;
            x += vx;
        }
        
        if (c == Color.RED.getRGB() && vy < 0) {
            floorHeight = 48;
        }
        else if (c != Color.BLACK.getRGB() && c != Color.RED.getRGB()) {
            floorHeight = 0;
        }
        
        if (y < floorHeight) {
            y = floorHeight;
            vy = 0;
        }

        if (c == Color.BLUE.getRGB() && y == floorHeight) {
            z += 1;
        }
        
        if (spriteSheet != spriteSheetJump) {

            
            //if (vx != 0 || vz != 0) {
            if (moving) {
                frame += 0.15;
                spriteSheet.setFrame((int) frame);
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        //g.drawImage(sprite, (int) (x - 25), (int) (z - 55 - y), null);
        
        //sprite.draw(g, (int) x, (int) (z - y));
        
        spriteSheet.draw(g, (int) x, (int) (z - y), flipHorizontal);
        
        g.setColor(Color.ORANGE);
        g.fillOval((int) (x - 3), (int) (z - y - 3), 6, 6);
    }
    
    
}
