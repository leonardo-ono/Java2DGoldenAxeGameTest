package infra;

import static infra.Collider.Type.BODY;
import java.awt.Rectangle;


/**
 *
 * @author admin
 */
public class Collider extends Rectangle {
    
    public enum Type { ATTACK, BODY }
    
    private Type type = BODY;
    
    public enum AttackType { NONE, SWING, RAP, THROW }
    private AttackType attackType;
    
    private boolean knockDown; // will attack collider knock down ?
    
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

    @Override
    public String toString() {
        return "collider " + type + " " + attackType + " " + knockDown + " " + x + " " + y + " " + width + " " + height;
    }
    
}
