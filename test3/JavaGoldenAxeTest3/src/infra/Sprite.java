package infra;


import infra.Collider.Type;
import static infra.Settings.SHOW_COLLIDERS;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


/**
 * Sprite class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Sprite {
    
    private static final Color SELECTED_ATTACK = new Color(255, 0, 0, 128);
    private static final Color SELECTED_BODY = new Color(0, 0, 255, 128);
    
    private final BufferedImage image;
    private final Point origin;
    private final Rectangle rectangle;
    private final List<Collider> colliders = new ArrayList<>();
    
    public Sprite(BufferedImage image, int ox, int oy, Rectangle rectangle, List<Collider> colliders) {
        this.image = image;
        origin = new Point(ox, oy);
        this.rectangle = rectangle;
        
        for (Collider collider : colliders) {
            if (collider.intersects(rectangle)) {
                Collider copy = new Collider(collider.x, collider.y, collider.width, collider.height);
                copy.setType(collider.getType());
                copy.setAttackType(collider.getAttackType());
                copy.setKnockDown(collider.isKnockDown());
                
                copy.x -= rectangle.x;
                copy.y -= rectangle.y;
                this.colliders.add(copy);
            }
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public Point getOrigin() {
        return origin;
    }

    public List<Collider> getColliders() {
        return colliders;
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

        if (SHOW_COLLIDERS) {
            for (Collider collider : colliders) {
                if (collider.getType() == Collider.Type.BODY) {
                    g.setColor(SELECTED_BODY);
                }
                else {
                    g.setColor(SELECTED_ATTACK);
                }

                if (flipHorizontal) {
                    int flippedColliderX = image.getWidth() - (collider.x + collider.width);
                    g.fillRect(sx + flippedColliderX, sy + collider.y, collider.width, collider.height);
                }
                else {
                    g.fillRect(collider.x + dx1, collider.y + dy1, collider.width, collider.height);
                }

            }
        }
        
    }
    
    private final Collider colliderTmp = new Collider();

    public Collider getWorldSpaceCollider(int colliderIndex, int x, int y, boolean flipped) {
        if (colliders.isEmpty()) {
            colliderTmp.setBounds(-999, -999, 0, 0);
            return colliderTmp;
        }
        
        Collider collider = colliders.get(colliderIndex);
        if (!flipped) {
            //int sx =  x - origin.x;
            //int sy =  y - origin.y;
            int sy =  y - origin.y;
            int dx1 = x - origin.x;
            int dy1 = sy; 
            
            //colliderTmp.setBounds(sx + collider.x, sy + collider.y, collider.width, collider.height);
            colliderTmp.setBounds(collider.x + dx1, collider.y + dy1, collider.width, collider.height);
        }
        else {
            int sx =  x - (image.getWidth() - origin.x);
            int sy =  y - origin.y;
            int flippedColliderX = image.getWidth() - (collider.x + collider.width);
            colliderTmp.setBounds(sx + flippedColliderX, sy + collider.y, collider.width, collider.height);
        
            //int sx =  x - (image.getWidth() - origin.x);
            //int sy =  y - origin.y;
            //int flippedColliderX = image.getWidth() - (collider.x + collider.width);
            //colliderTmp.setBounds(sx + flippedColliderX, sy + collider.y, collider.width, collider.height);
        }
        colliderTmp.setType(collider.getType());
        colliderTmp.setAttackType(collider.getAttackType());
        colliderTmp.setKnockDown(collider.isKnockDown());
        return colliderTmp;
    }
    
    public boolean collides(int bx, int by, Sprite attackSprite, int ax, int ay, Collider colliderResult, boolean flipped) {
        for (int aci = 0; aci < attackSprite.colliders.size(); aci++) {
            Collider attack = attackSprite.getWorldSpaceCollider(aci, ax, ay, flipped);
            if (attack.getType() == Type.ATTACK) {
                for (int bci = 0; bci < colliders.size(); bci++) {
                    Collider body = getWorldSpaceCollider(bci, bx, by, flipped);
                    if (body.getType() == Type.BODY) {
                        if (attack.intersects(body)) {
                            colliderResult.setBounds(attack);
                            colliderResult.setType(Type.ATTACK);
                            colliderResult.setKnockDown(attack.isKnockDown());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "sprite " + rectangle.x + " " + rectangle.y 
                + " " + rectangle.width + " " + rectangle.height + " " 
                + origin.x + " " + origin.y;
    }
    
}
