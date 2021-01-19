package infra;

import static infra.Collider.AttackType.NONE;
import static infra.Collider.Type.BODY;
import java.awt.Rectangle;


/**
 *
 * @author admin
 */
public class Collider extends Rectangle {
    
    public enum Type { ATTACK, BODY, FORCE_ATTACK }
    
    private Type type = BODY;
    
    public enum AttackType { NONE, SWING, RAP, THROW }
    private AttackType attackType = NONE;
    
    private boolean knockDown; // will attack collider knock down ?
    private String forceAttackId;
    
    // TODO
    private int damage;
    private int hitLimit = 1; // max number of enemies this collides
            
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

    @Override
    public String toString() {
        String forceAttackIdStr = "";
        if (forceAttackId != null) {
            forceAttackIdStr = ":" + forceAttackId;
        }
        return "collider " + type + forceAttackIdStr + " " + attackType + " " + knockDown + " " + x + " " + y + " " + width + " " + height;
    }
    
}
