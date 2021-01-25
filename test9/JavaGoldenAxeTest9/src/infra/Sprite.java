package infra;


import infra.AnimationPlayer.MyPoint;
import infra.Collider.Type;
import static infra.Settings.SHOW_COLLIDERS;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Sprite class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Sprite {
    
    private static final Color SELECTED_ATTACK = new Color(255, 0, 0, 128);
    private static final Color SELECTED_BODY = new Color(0, 0, 255, 128);
    private static final Color SELECTED_FORCE_ATTACK = new Color(0, 255, 0, 128);

    private final String id;
    private final BufferedImage image;
    private final Point origin;
    private Direction originalDirection;
    private final Rectangle rectangle;
    private final List<Collider> colliders = new ArrayList<>();
    private Map<String, MyPoint> points = new HashMap<>();
    
    public Sprite(String id, BufferedImage image, int ox, int oy, Direction originalDirection, Rectangle rectangle, List<Collider> colliders, List<MyPoint> points) {
        this.id = id;
        this.image = image;
        origin = new Point(ox, oy);
        this.originalDirection = originalDirection;
        this.rectangle = rectangle;
        
        for (Collider collider : colliders) {
            if (collider.intersects(rectangle)) {
                Collider copy = new Collider(collider.x, collider.y, collider.width, collider.height);
                copy.setType(collider.getType());
                copy.setAttackType(collider.getAttackType());
                copy.setKnockDown(collider.isKnockDown());
                copy.setForceAttackId(collider.getForceAttackId());
                
                copy.x -= rectangle.x;
                copy.y -= rectangle.y;
                this.colliders.add(copy);
            }
        }
        for (MyPoint point : points) {
            if (rectangle.contains(point)) {
                MyPoint copy = new MyPoint(point.getId(), point.x, point.y);
                copy.x -= rectangle.x;
                copy.y -= rectangle.y;
                copy.x = copy.x - origin.x;
                copy.y = copy.y - origin.y;
                this.points.put(copy.getId(), copy);
            }
        }
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
    
    public List<Collider> getColliders() {
        return colliders;
    }
    
    public void getPoint(String name, MyPoint myPoint, boolean flipHorizontal) {
        if (originalDirection == Direction.LEFT) {
            flipHorizontal = !flipHorizontal;
        }
        MyPoint pTmp = points.get(name);
        if (pTmp == null) {
            myPoint.setLocation(0, 0);
            return;
        }
        
        int x = pTmp.x;
        int y = pTmp.y;
        
        int dx1 = x;
        int dy1 = y;
        
        if (flipHorizontal) {
            dx1 = -x;
        }
        
        myPoint.setLocation(dx1, dy1);
    }
    
    public void addPoint(String name, int x, int y) {
        points.put(name, new MyPoint(name, x, y));
    }

    public Map<String, MyPoint> getPoints() {
        return points;
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

        if (SHOW_COLLIDERS) {
            for (Collider collider : colliders) {
                if (collider.getType() == Collider.Type.BODY) {
                    g.setColor(SELECTED_BODY);
                }
                else if (collider.getType() == Collider.Type.ATTACK) {
                    g.setColor(SELECTED_ATTACK);
                }
                else {
                    g.setColor(SELECTED_FORCE_ATTACK);
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
        
        if (originalDirection == Direction.LEFT) {
            flipped = !flipped;
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
        colliderTmp.setForceAttackId(collider.getForceAttackId());
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
