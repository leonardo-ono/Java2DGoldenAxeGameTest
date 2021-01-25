package entity;

import infra.Actor;
import infra.Camera;
import infra.Input;
import infra.AnimationPlayer;
import infra.AnimationPlayer.MyPoint;
import infra.Audio;
import infra.Collider;
import infra.Collider.AttackType;
import infra.Direction;
import static infra.Direction.IDLE;
import static infra.Direction.LEFT;
import static infra.Direction.RIGHT;
import infra.PlayerControl;
import infra.Sprite;
import infra.State;
import infra.Terrain;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.Set;
import scene.Stage;

/**
 * Beast1 class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Beast1 extends Player1 {
    
    protected PlayerControl control;
    
    protected double vx = 0;
    protected double vy = 0;
    protected double vz = 0;    

    protected AnimationPlayer animationPlayer;
    
    protected double frame;
    protected long idleTime;
    
    protected Set<String> forcedAttacks = new HashSet<>();
    protected Beast1 throwOpponent;
    
    // TODO test: used to check which combo will be selected from attack2 to attack3 or attack4
    protected int lastHitDistance;
    
    private Player1 mountedBy;
    
    private Player1 player;
    protected PlayerControl aiControl;

    public Beast1(Stage stage, PlayerControl control, Player1 player, int x, int z, int y) {
        super(stage, control);
        this.aiControl = control;
        this.player = player;
        
        int keyId = Actor.getKeyId();
        aiControl.up = keyId + 0;
        aiControl.down = keyId + 1;
        aiControl.left = keyId + 2;
        aiControl.right = keyId + 3;
        aiControl.attack = keyId + 4;
        aiControl.jump = keyId + 5;
        aiControl.runLeft = keyId + 6;
        aiControl.runRight = keyId + 7;
        
        
        this.x = x; // x = 720;
        this.z = z; // z = 272;
        this.y = y;
        
        Audio.loadSound(11, "/res/sound/01.wav"); // attack 1 SWING

        Audio.loadSound(13, "/res/sound/03.wav"); // hit player ?

        Audio.loadSound(12, "/res/sound/02.wav"); // hit enemy 1 SWING
        Audio.loadSound(14, "/res/sound/04.wav"); // hit enemy 2 SWING ?
        Audio.loadSound(15, "/res/sound/05.wav"); // hit enemy 2 RAP
        
        Audio.loadSound(16, "/res/sound/06.wav"); // knock down
        Audio.loadSound(18, "/res/sound/08.wav"); // hit floor
        
        Audio.loadSound(20, "/res/sound/36.wav"); // dead
    }

    public Player1 getMountedBy() {
        return mountedBy;
    }

    public void mount(Player1 mountedBy) {
        this.mountedBy = mountedBy;
        stateManager.switchTo("walking");

        if (mountedBy instanceof Enemy1 || mountedBy instanceof Enemy3) {
            this.control = aiControl;
            EnemiesManager.setEnemyKilled(true);
        }
        else {
            this.control = player.getControl();
        }
    }

    public void unmount() {
        this.mountedBy = null;
        stateManager.switchTo("resting");
    }
    
    public void clearForcedAttacks() {
        forcedAttacks.clear();
    }
    
    public void addForcedAttack(String forcedAttackId) {
        forcedAttacks.add(forcedAttackId);
    }

    public void setThrowOpponent(Beast1 throwOpponent) {
        this.throwOpponent = throwOpponent;
    }
    
    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public void setLastHitDistance(int lastHitDistance) {
        this.lastHitDistance = lastHitDistance;
    }

    @Override
    public void init() {
        //x = 50;
        //z = 270;
        
        animationPlayer = new AnimationPlayer("chicken_leg"
                , "/res/animation/chicken_leg/colliders.txt"
                , "/res/animation/chicken_leg/sprite_sheet.txt"
                , "/res/animation/chicken_leg/points.txt");
        
        animationPlayer.setAnimation("walking_down");
        
        stateManager.addState(new Walking());
        stateManager.addState(new Jumping());
        stateManager.addState(new AttackSpecial());
        stateManager.addState(new AttackSpecialReturn());
        stateManager.addState(new Attack1());
        stateManager.addState(new Attack2());
        stateManager.addState(new Attack3());
        stateManager.addState(new Attack4());
        stateManager.addState(new Kicking());
        stateManager.addState(new Throwing());
        stateManager.addState(new Thrown());
        stateManager.addState(new Running());
        stateManager.addState(new RunningAttack());
        stateManager.addState(new Stunned());
        stateManager.addState(new StunnedRap());
        stateManager.addState(new KnockDown());
        stateManager.addState(new StandUp());
        stateManager.addState(new Resting());
        
        stateManager.switchTo("resting");
        //stateManager.switchTo("walking");
        
    }

//    public void updateControlDirectional() {
//        if (Input.isKeyPressed(control.left)) {
//            vx = -1;
//        }
//        else if (Input.isKeyPressed(control.right)) {
//            vx = 1;
//            runningStartTime = System.currentTimeMillis();
//        }
//
//        if (Input.isKeyPressed(control.up)) {
//            vz = -1;
//        }
//        else if (Input.isKeyPressed(control.down)) {
//            vz = 1;
//        }        
//    }
    
//    public void updateControlJump() {
//        // jump
//        if (Input.isKeyJustPressed(control.jump) && y == minHeight) {
//            vy = 6;
//            stateManager.switchTo("jumping");
//        }        
//        
//        // TODO caiu no precipicio
//        if (y < -150) {
//            destroy();
//            Camera.setPlayer1(Camera.getPlayer2());
//        }           
//    }

    public AnimationPlayer getAnimationPlayer() {
        return animationPlayer;
    }
    
    
    // cada ataque tem detector de acerto, se acertar e tiver continuidade para combo, no final da animacao
    // o ultimo frame fica visivel para mais tempo para facilitar o jogador a continuar a sequencia de combos.
    
    // enemy + distance = combo
    protected void createCombo(String name, String ... attackNames) {
        // TODO
    }
    
    protected void setCombo(String name) {
        // TODO
    }

    @Override
    public void update() {
        if (mountedBy instanceof Enemy1 || mountedBy instanceof Enemy3) {
            processAI();
        }
        
        stateManager.update();
    }

    private double rz = 0;
    private int rx = 0;
    
    private boolean running;
    private int runningTargetX;
    private int runningInitialDistance;
    private long nextJumpTime;
    private Direction runningDirection = Direction.IDLE;
    
    private Direction attackPosition = IDLE;
    private boolean positioningForAttack;
    private int attackPositionX;
    
    private boolean idleWalking = false;
    private int idleTargetX = 0;
    private int idleTargetZ = 0;
    private long idleEndTime;
    
    private boolean dead;

    public boolean isDead() {
        return dead;
    }
    
    
    public void setAIMode(Direction aimode) {
        attackPosition = aimode;
        idleWalking = false;
        idleTargetX = 0;
        idleTargetZ = 0;
    }
    
    private void processAI() {
        if (stateManager.getCurrentState().getName().equals("dying") 
                || stateManager.getCurrentState().getName().equals("dead")) {
            
            return;
        }
        
        int tmpX = (int) (player.getX() - x);
        int tmpZ = (int) (player.getZ() - z);
        
        if (attackPosition == LEFT) {
            tmpX = (int) ((player.getX() - 40) - x);
        }
        else if (attackPosition == RIGHT) {
            tmpX = (int) ((player.getX() + 40) - x);
        }
        else if (attackPosition == Direction.IDLE) {
            if (!idleWalking) {
                //int sign = Math.random() > 0.5 ? 1 : - 1;
                //int value = (int) (sign * (50 + 50 * Math.random()));
                //idleTargetX = (int) (player.getX() + value);
                idleTargetX = (int) (Camera.getMaxX() - 276 * Math.random());
                idleTargetZ = (int) (z + 200 * Math.random() - 100);
                idleWalking = true;
                idleEndTime = System.currentTimeMillis() + 2000 + (int) (1000 * Math.random());
            }
            else {
                tmpX = (int) (idleTargetX - x);
                tmpZ = (int) (idleTargetZ - z);
                if (Math.abs(tmpX) < 8 || System.currentTimeMillis() > idleEndTime) {
                    idleWalking = false;
                }
            }
        }

        
//        
//        if (positioningForAttack) {
//            if (attackPosition == LEFT && tmpX > 64) {
//                positioningForAttack = false;
//                return;
//            }
//            else if (attackPosition == LEFT) {
//                tmpX = (int) (player.getX() - 164 - x);
//            }
//            else if (attackPosition == RIGHT && tmpX < -64) {
//                positioningForAttack = false;
//                return;
//            }
//            else if (attackPosition == RIGHT) {
//                tmpX = (int) (player.getX() + 164 - x);
//            }
//        }
//        else if (!positioningForAttack && tmpX > 0 && attackPosition == RIGHT) {
//            attackPositionX = (int) (player.getX() + 75 + 75 * Math.random());
//            positioningForAttack = true;
//            return;
//        }
//        else if (!positioningForAttack && tmpX < 0 && attackPosition == LEFT) {
//            attackPositionX = (int) (player.getX() -75 - 75 * Math.random());
//            positioningForAttack = true;
//            return;
//        }

        if (!Terrain.isWalkable((int) (player.getX()), (int) (player.getZ() + rz))) {
            rz = 0;
        }
        if (!Terrain.isWalkable((int) (player.getX() + rx), (int) (player.getZ()))) {
            rx = 0;
        }
        if (!Terrain.isWalkable((int) (player.getX() - rx), (int) (player.getZ()))) {
            rx = 0;
        }
        
        // System.out.println("enemy position: " + x + " " + z + " dx = " + dx);
        
        //Input.simulateKeyReleased(control.right);
        //Input.simulateKeyReleased(control.left);
        //Input.simulateKeyReleased(control.down);
        //Input.simulateKeyReleased(control.up);
        Input.simulateKeyReleased(control.attack);
        
        
        if (running || stateManager.getCurrentState().getName().equals("running")) {
            String playerState = player.getStateManager().getCurrentState().getName();
            if (!playerState.equals("knock_down") && //!playerState.equals("stand_up") &&
                Math.abs(player.getX() - x) < 64 && Math.abs(player.getZ() - z) < 24) {
                
                Input.simulateKeyPressed(control.attack);
                Input.simulateKeyReleased(control.runLeft);
                Input.simulateKeyReleased(control.runRight);
                running = false;
            }
            // for some reason "running" state was cancelled, hit different height stage, etc
            else if (!stateManager.getCurrentState().getName().equals("running")) {
                //Input.simulateKeyReleased(control.runLeft);
                //Input.simulateKeyReleased(control.runRight);
                //Input.simulateKeyReleased(control.left);
                //Input.simulateKeyReleased(control.right);
                running = false;
            }
            else if (//Math.abs(runningTargetX - x) < 8 || Math.abs(tmpX) > runningInitialDistance ||
                       (((Running) stateManager.getCurrentState()).getDirection() == LEFT && (player.getX() - x) > 80) 
                    || (((Running) stateManager.getCurrentState()).getDirection() == RIGHT && (player.getX() - x) < -80)) {
                Input.simulateKeyReleased(control.runLeft);
                Input.simulateKeyReleased(control.runRight);
                Input.simulateKeyReleased(control.left);
                Input.simulateKeyReleased(control.right);
                running = false;
            }
            return;
        }
        else if (!running && Math.abs(tmpX) > 100 && Math.random() > 0.97 && attackPosition != IDLE) {
            running = true;
            runningTargetX = (int) player.getX();
            runningInitialDistance = Math.abs(tmpX);
            if ((player.getX() - x) > 0) {
                Input.simulateKeyPressed(control.runRight);
                runningDirection = RIGHT;
                
            }
            else {
                Input.simulateKeyPressed(control.runLeft);
                runningDirection = LEFT;
            }
            return;
        }
        
        int dx = 0;
        int dz = (int) (tmpZ + rz);

        dx = tmpX - rx;
        //if (tmpX >= 0) dx = tmpX - 40 - rx;
        //if (tmpX < 0) dx = tmpX + 40 + rx;
        //if (attackPosition == LEFT) dx = tmpX - 40 - rx;
        //if (attackPosition == RIGHT) dx = tmpX + 40 + rx;
        

        if (Math.abs(tmpX) > 250) {
            return;
        }
        
        if ((player.getX() - x) > 0 && animationPlayer.isFlip()) {
            animationPlayer.setFlip(false);
        }
        else if ((player.getX() - x) < 0 && !animationPlayer.isFlip()) {
            animationPlayer.setFlip(true);
        }
        
        if (dx > 1) {
            Input.simulateKeyPressed(control.right);
            Input.simulateKeyReleased(control.left);
            
            if (!stateManager.getCurrentState().getName().equals("jumping") 
                    && Terrain.isWalkable((int) (x + 1), (int) (z)) 
                    && (Terrain.getHeight((int) (x + 1), (int) (z)) - y) > 5
                    && System.currentTimeMillis() > nextJumpTime) {
                Input.simulateKeyReleased(control.jump);
                Input.simulateKeyPressed(control.jump);
                nextJumpTime = System.currentTimeMillis() + 1000 + (int) (1000 * Math.random());
            }
            
        }
        else if (dx < -1) {
            Input.simulateKeyPressed(control.left);
            Input.simulateKeyReleased(control.right);

            if (!stateManager.getCurrentState().getName().equals("jumping") 
                    && Terrain.isWalkable((int) (x - 1), (int) (z)) 
                    && (Terrain.getHeight((int) (x - 1), (int) (z)) - y) > 5
                    && System.currentTimeMillis() > nextJumpTime) {
                Input.simulateKeyReleased(control.jump);
                Input.simulateKeyPressed(control.jump);
                nextJumpTime = System.currentTimeMillis() + 1000 + (int) (1000 * Math.random());
            }
        }
        else {
                Input.simulateKeyReleased(control.right);
                Input.simulateKeyReleased(control.left);
        }
        
        if (dz > 0) {
            Input.simulateKeyPressed(control.down);
            Input.simulateKeyReleased(control.up);
        }
        else if (dz < 0) {
            Input.simulateKeyPressed(control.up);
            Input.simulateKeyReleased(control.down);
        }
        else {
            Input.simulateKeyReleased(control.up);
            Input.simulateKeyReleased(control.down);
            if (dx != 0) {
                rz = 60 * Math.random() - 30;
            }
            else {
                rz = 0;
            }
            //rx = (int) (50 * Math.random());
        }

        String playerState = player.getStateManager().getCurrentState().getName();
        if (!playerState.equals("knock_down") && !playerState.equals("stand_up")) {
            if (Math.abs(player.getZ() - z) < 16 && Math.abs(player.getX() - x) < 42
                    && ((attackPosition == RIGHT && player.getX() - x > -42) 
                        || (attackPosition == LEFT && player.getX() - x < 42)) 
                    && Math.random() > 0.95) { // TODO low propability for attacking ?
                Input.simulateKeyPressed(control.attack);
            }
        }

    }
    
    private final MyPoint pTmp = new MyPoint("tmp", 0, 0);
    
    @Override
    public void draw(Graphics2D g) {
        //super.draw(g);
        
        // g.drawLine(0, 0, 276, 207);
        animationPlayer.draw(g, (int) x, (int) (z - y));
        
        // mounted by
        if (mountedBy != null && mountedBy.animationPlayer != null) {
            String key = null;
            for (String k : animationPlayer.getCurrentAnimationFrame().getPoints().keySet()) {
                key = k;
                break;
            }
            if (key != null) {
                mountedBy.animationPlayer.setAnimation(key);
                
                Sprite currentFrame = animationPlayer.getCurrentAnimationFrame();
                System.out.println("beast = " + currentFrame.getOriginalDirection());
                
                // TODO very confusing, don't know what's happening here anymore xD ...
                boolean flip = false;
                if (currentFrame.getOriginalDirection() == mountedBy.animationPlayer.getCurrentAnimationFrame().getOriginalDirection()) {
                    flip = animationPlayer.isFlip();
                    
                }
                else {
                    //if (mountedBy.animationPlayer.getCurrentAnimationFrame().getOriginalDirection() == RIGHT) {
                        flip = !animationPlayer.isFlip();
                    //}
                    //else {
                    //    flip = animationPlayer.isFlip();
                    //}
                }
                
                currentFrame.getPoint(key, pTmp, flip);
                //mountedBy.animationPlayer.setAnimation("mounted_down");
                mountedBy.animationPlayer.setFlip(flip);
                mountedBy.animationPlayer.draw(g, (int) (x + pTmp.x), (int) (z - y + pTmp.y));
                
                // to align beast and player position
                // pTmp.y is subtracted to y
                // it is better now
                mountedBy.setX(x + pTmp.x);
                mountedBy.setZ(z);
                mountedBy.setY(y - pTmp.y);
            }
        }
        
        // floor + height
        g.setColor(Color.GREEN);
        g.fillOval((int) (x - 4), (int) (z - 4), 8, 8);
        // floor
        g.setColor(Color.BLUE);
        g.fillOval((int) (x - 4), (int) (z - y - 4), 8, 8);
    }
     
    // --- States ---
    
    private class Walking extends State {
        
        private long runningLeftStartTime;
        private long runningRightStartTime;
        private double speed = 1.5;
        
        public Walking() {
            super(stateManager, "walking", Beast1.this);
        }
        
        @Override
        public void onEnter() {
            animationPlayer.setAnimation("walking_down");
        }
        
        @Override
        public void update() {
            
            // TODO something like this ?
            if (control == aiControl) {
                speed = 1;
            }
            else {
                speed = 1;
            }
            
            vx = 0;
            vz = 0;
            vy -= 0.3;
            
            // running right
            long difTimeRight = System.currentTimeMillis() - runningRightStartTime;
            boolean rightJustPressed = Input.isKeyJustPressed(control.right);
            if (Input.isKeyJustPressed(control.runRight) || (rightJustPressed && difTimeRight <= 250)) {
                ((Running) stateManager.switchTo("running")).setDirection(RIGHT);
            }
            else if (rightJustPressed) {
                runningRightStartTime = System.currentTimeMillis();
            }

            // running left
            long difTimeLeft = System.currentTimeMillis() - runningLeftStartTime;
            boolean leftJustPressed = Input.isKeyJustPressed(control.left);
            if (Input.isKeyJustPressed(control.runLeft) || leftJustPressed && difTimeLeft <= 250) {
                ((Running) stateManager.switchTo("running")).setDirection(LEFT);
            }
            else if (leftJustPressed) {
                runningLeftStartTime = System.currentTimeMillis();
            }
            
            if (Input.isKeyPressed(control.left)) {
                vx = -speed;
            }
            else if (Input.isKeyPressed(control.right)) {
                vx = speed;
            }

            if (Input.isKeyPressed(control.up)) {
                vz = -speed;
            }
            else if (Input.isKeyPressed(control.down)) {
                vz = speed;
            }                   

            // attack
            boolean attackKey = Input.isKeyJustPressed(control.attack);
            boolean jumpKey = Input.isKeyJustPressed(control.jump);
            
            if (attackKey && jumpKey) {
                ((AttackSpecial) stateManager.switchTo("attack_special")).setDirection(animationPlayer.isFlip() ? RIGHT : LEFT);
                return;
            }
            else if (attackKey) {
                
                System.out.println("forcedAttacks: " + forcedAttacks.isEmpty());
                
                // check colliding with FORCE_ATTACK:
                if (!forcedAttacks.isEmpty()) { // .contains("grab_and_throw")) {
                    stateManager.switchTo("throwing");
                }
                else {
                    stateManager.switchTo("attack1");
                }
            }

            if (Input.isKeyJustPressed(control.kick)) {
                //stateManager.switchTo("kicking");
                stateManager.switchTo("throwing");
            }

            if (Input.isKeyJustPressed(control.hit)) {
                stun();
            }

            if (Input.isKeyJustPressed(control.knockDown)) {
                knockDown(LEFT);
            }
            
            if (vz != 0 && Terrain.isWalkable((int) x, (int) (z + vz)) 
                    && y >= Terrain.getHeight((int) x, (int) (z + vz))) {

                if ((z + vz - minHeight) > Camera.getMinY() 
                        && (z + vz - minHeight) < Camera.getMaxY()) {

                    z += vz;
                }
            }
            if (vx != 0 && Terrain.isWalkable((int) (x + vx), (int) z) 
                    && y >= Terrain.getHeight((int) (x + vx), (int) z)) {

                x += vx;
                if (x < Camera.getMinX()) {
                    x = Camera.getMinX();
                }
                else if (x > Camera.getMaxX()) {
                    x = Camera.getMaxX();
                }
            } 

            minHeight = Terrain.getHeight((int) x, (int) z);
            y += vy;
            if (y < minHeight) {
                y = minHeight;
                vy = 0;
            }

            // jump
            if (jumpKey && y == minHeight) {
                jump(false);
            }        

            // TODO caiu no precipicio
            if (y < -150) {
                destroy();
                Camera.setPlayer2(Camera.getPlayer1());
            }   

            if (vx != 0 || vz != 0) {
                frame += 0.125;
                animationPlayer.setFrame((int) frame);
                idleTime = System.currentTimeMillis() + 500;
                
                if (mountedBy instanceof Enemy1 || mountedBy instanceof Enemy3) {
                    // enemy ai: points to player location
                }
                else {
                    // player walking direction
                    if (vx != 0) {
                        animationPlayer.setFlip(vx < 0);
                    }
                }
                
                animationPlayer.setAnimation(
                        vz >= 0 ? "walking_down" : "walking_up");
            }

            //if (System.currentTimeMillis() >= idleTime) {
            //    animationPlayer.setAnimation("idle");
            //}            
            
            // System.out.println("player1 position: (" + x + "," + z + ") cameraX=" + Camera.getX());
        }

    }
    
    private class Jumping extends State {
        
        private boolean attacking;
        private long attackWaitTime;
        private long attackEndTime;
        private boolean running;
        
        public Jumping() {
            super(stateManager, "jumping", Beast1.this);
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void onEnter() {
            animationPlayer.setAnimation("jumping");
            animationPlayer.setFrame(1);
            attacking = false;
            attackWaitTime = System.currentTimeMillis() + 90;
        }
        
        @Override
        public void update() {
            vx = 0;
            vy -= 0.3;
            vz = 0;
            
            if (Input.isKeyPressed(control.left)) {
                vx = -1;
            }
            else if (Input.isKeyPressed(control.right)) {
                vx = 1;
            }

            if (Input.isKeyPressed(control.up)) {
                vz = -1;
            }
            else if (Input.isKeyPressed(control.down)) {
                vz = 1;
            }
            
            if (!attacking && Input.isKeyJustPressed(control.attack) 
                    && System.currentTimeMillis() >= attackWaitTime) {
                
                attacking = true;
                attackEndTime = System.currentTimeMillis() + 90;
                animationPlayer.setFrame(0);
                Audio.playSound(1); // attack swing
            }
            else if (attacking && System.currentTimeMillis() >= attackEndTime && running) {
                // Death Stab. Ref: https://strategywiki.org/wiki/Golden_Axe/Moves_and_Controls
                animationPlayer.setFrame(3);
            }
            else if (attacking && System.currentTimeMillis() >= attackEndTime && !running) {
                attacking = false;
                animationPlayer.setFrame(2);
                attackWaitTime = System.currentTimeMillis() + 90;
            }
            else if (!attacking && vy < 0) {
                animationPlayer.setFrame(2);
            }
            
            if (vx != 0 && Terrain.isWalkable((int) (x + vx), (int) z) 
                    && y >= Terrain.getHeight((int) (x + vx), (int) z)) {

                x += vx;
                if (x < Camera.getMinX()) {
                    x = Camera.getMinX();
                }
                else if (x > Camera.getMaxX()) {
                    x = Camera.getMaxX();
                }
            } 

            if (vx != 0) {
                animationPlayer.setFlip(vx < 0);
            }

            minHeight = Terrain.getHeight((int) x, (int) z);
            y += vy;
            if (y < minHeight) {
                y = minHeight;
                vy = 0;
                stateManager.switchTo("walking");
            } 
            
        }
        
    }
    
    private class AttackSpecial extends State {
        
        private double frame;
        private Direction direction;
        
        public AttackSpecial() {
            super(stateManager, "attack_special", Beast1.this);
        }
        
        public void setDirection(Direction direction) {
            this.direction = direction;
        }        

        @Override
        public void onEnter() {
            animationPlayer.setAnimation("attack_special");
            frame = 0;
            Audio.playSound(1);
            vx = direction.getDx() * 3;
            animationPlayer.setFrame((int) frame);
        }
        
        @Override
        public void update() {
            vy -= 0.3;
            
            if (vx != 0 && Terrain.isWalkable((int) (x + vx), (int) z) 
                    && y >= Terrain.getHeight((int) (x + vx), (int) z)) {

                x += vx;
                if (x < Camera.getMinX()) {
                    x = Camera.getMinX();
                }
                else if (x > Camera.getMaxX()) {
                    x = Camera.getMaxX();
                }
            }

            minHeight = Terrain.getHeight((int) x, (int) z);
            y += vy;
            if (y < minHeight) {
                y = minHeight;
                vy = 0;
            }
            
            frame += 0.15;
            if (frame >= 5) {
                frame = 4;
                ((AttackSpecialReturn) stateManager.switchTo("attack_special_return")).setDirection(animationPlayer.isFlip() ? LEFT : RIGHT);
            }
            else if (frame >= 4) {
                vx = 0;
            }
            animationPlayer.setFrame((int) frame);
        }
        
    }

    private class AttackSpecialReturn extends State {
        
        private double frame;
        private Direction direction;
        
        public AttackSpecialReturn() {
            super(stateManager, "attack_special_return", Beast1.this);
        }
        
        public void setDirection(Direction direction) {
            this.direction = direction;
        }        

        @Override
        public void onEnter() {
            animationPlayer.setFlip(!animationPlayer.isFlip());
            animationPlayer.setAnimation("attack_special_return");
            frame = 0;
            Audio.playSound(1);
            vx = direction.getDx() * 3;
            animationPlayer.setFrame((int) frame);
        }
        
        @Override
        public void update() {
            vy -= 0.3;
            
            if (vx != 0 && Terrain.isWalkable((int) (x + vx), (int) z) 
                    && y >= Terrain.getHeight((int) (x + vx), (int) z)) {

                x += vx;
                if (x < Camera.getMinX()) {
                    x = Camera.getMinX();
                }
                else if (x > Camera.getMaxX()) {
                    x = Camera.getMaxX();
                }
            }

            minHeight = Terrain.getHeight((int) x, (int) z);
            y += vy;
            if (y < minHeight) {
                y = minHeight;
                vy = 0;
            }
            
            frame += 0.15;
            if (frame >= 3) {
                frame = 2;
                vx = 0;
                stateManager.switchTo("walking");
            }
            animationPlayer.setFrame((int) frame);
        }
        
    }
    
    private class Attack1 extends State {
        
        private double frame;
        private double vx;
        
        public Attack1() {
            super(stateManager, "attack1", Beast1.this);
        }
        
        @Override
        public void onEnter() {
            animationPlayer.setAnimation("attack1");
            frame = 0;
            animationPlayer.setFrame((int) frame);
            Audio.playSound(1);
            vx = animationPlayer.isFlip() ? -0.5 : 0.5;
        }
        
        @Override
        public void update() {
            frame += 0.25;
            if (frame >= 5) {
                frame = 0;
                stateManager.switchTo("walking");
                animationPlayer.setFlip(!animationPlayer.isFlip());
            }
            //else if (frame >= 4 && Input.isKeyJustPressed(control.attack)) {
            //    stateManager.switchTo("attack2");
            //}
            else if (frame >= 3 && Input.isKeyJustPressed(control.jump)) {
                jump(false);
            }
            animationPlayer.setFrame((int) frame);
            
            x += vx;
        }
        
    }

    private class Attack2 extends State {
        
        private double frame;
        
        public Attack2() {
            super(stateManager, "attack2", Beast1.this);
        }
        
        @Override
        public void onEnter() {
            animationPlayer.setAnimation("attack2");
            frame = 0;
            Audio.playSound(1);
        }
        
        @Override
        public void update() {
            frame += 0.2;
            if (frame >= 5) {
                frame = 4;
                stateManager.switchTo("walking");
            }
            else if (frame >= 4 && Input.isKeyJustPressed(control.attack)) {
                
                // TODO combo
                // idea keep the last hit information available
                //if (Math.random() > 0.5) {
                System.out.println("lastHitDistance: " + lastHitDistance);
                if (lastHitDistance < 36) {
                    stateManager.switchTo("attack4");
                }
                else {
                    stateManager.switchTo("attack3");
                }
                
            }
            else if (frame >= 3 && Input.isKeyJustPressed(control.jump)) {
                jump(false);
            }
            animationPlayer.setFrame((int) frame);
        }
        
    }

    private class Attack3 extends State {
        
        private double frame;
        
        public Attack3() {
            super(stateManager, "attack3", Beast1.this);
        }
        
        @Override
        public void onEnter() {
            animationPlayer.setAnimation("attack3");
            frame = 0;
            Audio.playSound(1); // attack swing
        }
        
        @Override
        public void update() {
            frame += 0.2;
            if (frame >= 5) {
                frame = 4;
                stateManager.switchTo("walking");
            }
            else if (frame >= 3 && Input.isKeyJustPressed(control.jump)) {
                jump(false);
            }
            animationPlayer.setFrame((int) frame);
        }
        
    }

    private class Attack4 extends State {
        
        private double frame;
        
        public Attack4() {
            super(stateManager, "attack4", Beast1.this);
        }
        
        @Override
        public void onEnter() {
            animationPlayer.setAnimation("attack4");
            frame = 0;
        }
        
        @Override
        public void update() {
            frame += 0.2;
            if (frame >= 11) {
                frame = 10;
                stateManager.switchTo("walking");
            }
            else if (frame >= 10 && Input.isKeyJustPressed(control.attack)) {
                stateManager.switchTo("kicking");
            }
            animationPlayer.setFrame((int) frame);
        }
        
    }
    
    private class Kicking extends State {
        
        private double frame;
        
        public Kicking() {
            super(stateManager, "kicking", Beast1.this);
        }
        
        @Override
        public void onEnter() {
            animationPlayer.setAnimation("kicking");
            frame = 0;
        }
        
        @Override
        public void update() {
            frame += 0.2;
            if (frame >= 6) {
                frame = 5;
                stateManager.switchTo("walking");
            }
            animationPlayer.setFrame((int) frame);
        }
        
    }

    private class Throwing extends State {
        
        private double frame;
        private boolean hurl;
        
        public Throwing() {
            super(stateManager, "throwing", Beast1.this);
        }
        
        @Override
        public void onEnter() {
            throwOpponent.thrown(Beast1.this);
            throwOpponent.setZ(z - 1);
            animationPlayer.setAnimation("throwing");
            frame = 0;
            animationPlayer.setFrame((int) frame);
            hurl = false;
        }
        
        
        @Override
        public void update() {
            int direction = animationPlayer.isFlip() ? -1 : 1;
            frame += 0.04;
            if (frame < 1) {
                throwOpponent.animationPlayer.setFrame(0);
                animationPlayer.getCurrentAnimationFrame().getPoint("grab", pTmp, animationPlayer.isFlip());
                throwOpponent.setX(x + direction * pTmp.x);
                throwOpponent.setY(y - pTmp.y);
            }
            else if (frame < 2) {
                throwOpponent.animationPlayer.setFrame(1);
                animationPlayer.getCurrentAnimationFrame().getPoint("grab", pTmp, animationPlayer.isFlip());
                throwOpponent.setX(x + direction * pTmp.x);
                throwOpponent.setY(y - pTmp.y);
            }
            else if (frame < 3 && !hurl) {
                hurl = true;
                throwOpponent.knockDown(direction >= 0 ? RIGHT : LEFT);
                //throwOpponent.setVx(direction >= 0 ? throwOpponent.getVx() + 0.5 : throwOpponent.getVx() - 0.5);
            }
            else if (frame >= 4) {
                frame = 3;
                stateManager.switchTo("walking");
            }
            animationPlayer.setFrame((int) frame);
        }
        
    }

    private class Thrown extends State {
        
        private double frame;
        private Beast1 pitcher;
        
        public Thrown() {
            super(stateManager, "thrown", Beast1.this);
        }

        public void setPitcher(Beast1 pitcher) {
            this.pitcher = pitcher;
        }
        
        @Override
        public void onEnter() {
            animationPlayer.setAnimation("thrown");
            frame = 0;
            animationPlayer.setFrame((int) frame);
        }
        
        @Override
        public void update() {
        }
        
    }
    
    private class Running extends State {
        
        private Direction direction;
        private double frame;
        
        public Running() {
            super(stateManager, "running", Beast1.this);
        }

        public Direction getDirection() {
            return direction;
        }

        private void setDirection(Direction direction) {
            this.direction = direction;
        }
                
        @Override
        public void onEnter() {
            animationPlayer.setAnimation("running");
            frame = 0;
        }
        
        @Override
        public void update() {
            frame += 0.2;
            animationPlayer.setFrame((int) frame);
            
            vy -= 0.3;
            
            if (direction == RIGHT && (Input.isKeyPressed(control.runRight) || Input.isKeyPressed(control.right))) {
                vx = 3;
                animationPlayer.setFlip(false);
            }
            else if (direction == LEFT && (Input.isKeyPressed(control.runLeft) || Input.isKeyPressed(control.left))) {
                vx = -3;
                animationPlayer.setFlip(true);
            }
            else {
                vx = 0;
            }

            if (Input.isKeyPressed(control.attack)) {
                ((RunningAttack) stateManager.switchTo("running_attack"))
                        .setDirection(direction);
            }
            else if (Input.isKeyJustPressed(control.jump)) {
                jump(true);
            }
            
            if (vx != 0 && Terrain.isWalkable((int) (x + vx), (int) z) 
                    && y >= Terrain.getHeight((int) (x + vx), (int) z)) {

                x += vx;
                if (x < Camera.getMinX()) {
                    x = Camera.getMinX();
                    stateManager.switchTo("walking");
                }
                else if (x > Camera.getMaxX()) {
                    x = Camera.getMaxX();
                    stateManager.switchTo("walking");
                }
            }
            else {
                stateManager.switchTo("walking");
            }

            minHeight = Terrain.getHeight((int) x, (int) z);
            y += vy;
            if (y < minHeight) {
                y = minHeight;
                vy = 0;
            }
            
        }

    }

    private class RunningAttack extends State {
        
        private Direction direction;
        
        public RunningAttack() {
            super(stateManager, "running_attack", Beast1.this);
        }

        public void setDirection(Direction direction) {
            this.direction = direction;
        }
        
        @Override
        public void onEnter() {
            animationPlayer.setAnimation("running_attack");
            animationPlayer.setFrame(0);
            vy = 3;
            vx = direction.getDx() * 3.5;
        }
        
        @Override
        public void update() {
            vy -= 0.3;
            
            if (vx != 0 && Terrain.isWalkable((int) (x + vx), (int) z) 
                    && y >= Terrain.getHeight((int) (x + vx), (int) z)) {

                x += vx;
                if (x < Camera.getMinX()) {
                    x = Camera.getMinX();
                }
                else if (x > Camera.getMaxX()) {
                    x = Camera.getMaxX();
                }
            }

            minHeight = Terrain.getHeight((int) x, (int) z);
            y += vy;
            if (y < minHeight) {
                y = minHeight;
                vy = 0;
                stateManager.switchTo("walking");
            }
            
        }
        
    }

    private class Stunned extends State {
        
        private double frame;
        //private long endTime;
        
        public Stunned() {
            super(stateManager, "stunned", Beast1.this);
        }

        @Override
        public void onEnter() {
            animationPlayer.setAnimation("hit");
            frame = 0;
            //endTime = System.currentTimeMillis() + 500;
        }
        
        @Override
        public void update() {
            animationPlayer.setFrame((int) frame);
            frame += 0.1;
            if (frame >= 5) {
                lastAttackCollider.setBounds(0, 0, 0, 0);
                stateManager.switchTo("walking");
            }
        }
        
    }

    private class StunnedRap extends State {
        
        private double frame;
        //private long endTime;
        
        public StunnedRap() {
            super(stateManager, "stunned_rap", Beast1.this);
        }

        @Override
        public void onEnter() {
            animationPlayer.setAnimation("hit_rap");
            frame = 0;
            //endTime = System.currentTimeMillis() + 500;
        }
        
        @Override
        public void update() {
            animationPlayer.setFrame((int) frame);
            frame += 0.075;
            if (frame >= 3) {
                lastAttackCollider.setBounds(0, 0, 0, 0);
                stateManager.switchTo("walking");
            }
        }
        
    }

    private class KnockDown extends State {
        
        private Direction direction;
        private long endTime;
        
        public KnockDown() {
            super(stateManager, "knock_down", Beast1.this);
        }

        public void setDirection(Direction direction) {
            this.direction = direction;
        }
        
        @Override
        public void onEnter() {
            animationPlayer.setAnimation("falling");
            animationPlayer.setFrame(0);
            endTime = -1;
            vy = 5;
            vx = direction.getDx() * 2.5;
            Audio.playSound(6);
        }
        
        @Override
        public void update() {
            vy -= 0.3;
            
            if (vx != 0 && Terrain.isWalkable((int) (x + vx), (int) z) 
                    && y >= Terrain.getHeight((int) (x + vx), (int) z)) {

                x += vx;
                if (x < Camera.getMinX()) {
                    x = Camera.getMinX();
                }
                else if (x > Camera.getMaxX()) {
                    x = Camera.getMaxX();
                }
            }
            
            minHeight = Terrain.getHeight((int) x, (int) z);
            y += vy;
            if (y < minHeight) {
                y = minHeight;
                vy *= -0.5;
                vx *= 0.5;
                if (endTime < 0 && Math.abs(vy) < 0.1) {
                    endTime = System.currentTimeMillis() + 500;
                    vx = 0;
                    vy = 0;
                }

                if (animationPlayer.getFrame() == 0) {
                    animationPlayer.setFrame(1);
                    Audio.playSound(8); // hit floor
                }
            }
            
            if (endTime >= 0 && System.currentTimeMillis() >= endTime) {
                lastAttackCollider.setBounds(0, 0, 0, 0);
                stateManager.switchTo("stand_up");
            }
        }
        
    }

    private class StandUp extends State {
        
        private double frame;
        
        public StandUp() {
            super(stateManager, "stand_up", Beast1.this);
        }
        
        @Override
        public void onEnter() {
            animationPlayer.setAnimation("stand_up");
            frame = 0;
        }
        
        @Override
        public void update() {
            frame += 0.15;
            if (frame >= 3) {
                frame = 2;
                stateManager.switchTo("walking");
            }
            animationPlayer.setFrame((int) frame);
        }
        
    }

    private class Resting extends State {
        
        private double frame;
        
        public Resting() {
            super(stateManager, "resting", Beast1.this);
        }
        
        @Override
        public void onEnter() {
            animationPlayer.setAnimation("resting");
            frame = 0;
        }
        
        @Override
        public void update() {
            vx = 0;
            vz = 0;
            vy -= 0.3;

            minHeight = Terrain.getHeight((int) x, (int) z);
            y += vy;
            if (y < minHeight) {
                y = minHeight;
                vy = 0;
            }

            // TODO caiu no precipicio
            if (y < -150) {
                destroy();
                Camera.setPlayer2(Camera.getPlayer1());
            }   

            //frame += 0.15;
            //if (frame >= 3) {
            //    frame = 2;
            //    stateManager.switchTo("walking");
            //}
            animationPlayer.setFrame((int) frame);
        }
        
    }
    
    // ---

    public void jump(boolean running) {
        vy = running ? 8 : 6;
        ((Jumping) stateManager.switchTo("jumping")).setRunning(running);
    }
    
    public void stun() {
        stateManager.switchTo("stunned");
    }
    
    public void stunRap() {
        stateManager.switchTo("stunned_rap");
    }
    
    public void knockDown(Direction direction) {
        ((KnockDown) stateManager.switchTo("knock_down")).setDirection(direction);
    }
    
    public void thrown(Beast1 pitcher) {
        ((Thrown) stateManager.switchTo("thrown")).setPitcher(pitcher);
    }

    private Collider lastAttackCollider = new Collider();
    // TODO resetar lastAttackCollider no final de cada ataque
        public void hit(Direction direction, boolean forceKnockDown, AttackType attackType, Collider attackCollider) {
        //if (attackCollider.equals(lastAttackCollider)) {
        //    return;
        //}
        //lastAttackCollider.set(attackCollider);
        
        if (forceKnockDown || stateManager.getCurrentState().getName().equals("jumping")) {
            knockDown(direction);
        }
        else {
            if (attackType == AttackType.SWING) {
                stun();
            }
            else if (attackType == AttackType.RAP) {
                stunRap();
            }
        }
        
        if (attackType == AttackType.SWING) {
            Audio.playSound(12);
        }
        else if (attackType == AttackType.RAP) {
            Audio.playSound(15);
        }
    }
    
    
        
}
