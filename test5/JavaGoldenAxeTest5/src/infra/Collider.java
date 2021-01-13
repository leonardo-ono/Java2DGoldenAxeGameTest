package infra;

import static infra.Collider.Type.BODY;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 *
 * @author admin
 */
public class Collider extends Rectangle {
    
    public enum Type { ATTACK, BODY, FORCE_ATTACK }
    
    private Type type = BODY;
    
    public enum AttackType { NONE, SWING, RAP, THROW }
    private AttackType attackType;
    
    private boolean knockDown; // will attack collider knock down ?
    private String forceAttackId;

    public Collider() {
    }
    
    public Collider(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public AttackType getAttackType() {
        return attackType;
    }

    public void setAttackType(AttackType attackType) {
        this.attackType = attackType;
    }

    public boolean isKnockDown() {
        return knockDown;
    }

    public void setKnockDown(boolean knockDown) {
        this.knockDown = knockDown;
    }

    public String getForceAttackId() {
        return forceAttackId;
    }

    public void setForceAttackId(String forceAttackId) {
        this.forceAttackId = forceAttackId;
    }
    
    public void set(Collider c) {
        setRect(c.x, c.y, c.width, c.height);
        setType(c.type);
        setAttackType(c.attackType);
        setKnockDown(c.knockDown);
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.type);
        hash = 29 * hash + Objects.hashCode(this.attackType);
        hash = 29 * hash + (this.knockDown ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Collider other = (Collider) obj;
        
        return x == other.x && y == other.y && width == other.width && height == other.height
                && type == other.type && attackType == other.attackType && knockDown == other.knockDown;
    }

    @Override
    public String toString() {
        return "collider " + type + " " + knockDown + " " + x + " " + y + " " + width + " " + height;
    }
    
}
