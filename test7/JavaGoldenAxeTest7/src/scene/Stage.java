package scene;

import entity.EnemiesManager;
import entity.Enemy1;
import entity.Player1;
import entity.Player2;
import entity.Water;
import infra.Actor;
import infra.Camera;
import infra.Collider;
import infra.Direction;
import static infra.Direction.LEFT;
import static infra.Direction.RIGHT;
import infra.Entity;
import infra.Go;
import infra.Input;
import infra.PlayerControl;
import infra.Resource;
import infra.Scene;
import static infra.Settings.SHOW_COLLIDERS;
import infra.Sprite;
import infra.StateManager;
import infra.Terrain;
import infra.Text;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stage (Scene) class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Stage extends Scene {

    private final List<Entity> entities = new ArrayList<>();
    private final List<Entity> entitiesAdd = new ArrayList<>();
    
    private Player1 player1;
    private Player2 player2;
    
    private BufferedImage hud;
    
    public Stage(StateManager<Scene> scenes) {
        super(scenes, "stage", null);
    }

    public List<Entity> getEntities() {
        return entities;
    }
    
    public void addEntity(Entity entity) {
        entitiesAdd.add(entity);
        entity.init();
    }
    
    @Override
    public void init() {
        hud = Resource.getImage("hud");
        entities.add(new Water(this));
        entities.add(player1 = new Player1(this, new PlayerControl(1)));
        //entities.add(player2 = new Player2(this, new PlayerControl(2)));
        //entities.add(new Enemy1(this, new PlayerControl(3), player1, 220, 242));
        //entities.add(new Enemy1(this, new PlayerControl(3), player1, 250, 252));
        //entities.add(new Enemy3(this, new PlayerControl(3), player1, 230, 270));
        //entities.add(new Enemy3(this, new PlayerControl(3), player1, 240, 260));
        entities.add(new EnemiesManager(this, player1));
        entities.forEach(entity -> entity.init());
        Terrain.load(3);
        Camera.setPlayer1(player1);
        Camera.setPlayer2(player1);
    }
    
    @Override
    public void onEnter() {
    }

    @Override
    public void update() {
        Go.update();
        
        // debug
        if (Input.isKeyJustPressed(KeyEvent.VK_1)) {
            SHOW_COLLIDERS = !SHOW_COLLIDERS;
        }
        
        if (Input.isKeyJustPressed(KeyEvent.VK_2)) {
            Go.show();
        }
        
        // ---
        
        if (!entitiesAdd.isEmpty()) {
            entities.addAll(entitiesAdd);
            entitiesAdd.clear();
        }
        
        entities.forEach(entity -> {
            if (!entity.isDestroyed()) {
                entity.update();
                
                if (entity instanceof Player1) {
                    ((Player1) entity).clearForcedAttacks();
                }
            }
        });        
        
        Camera.update();
        
        // collision check
        for (Entity e1 : entities) {
            next_entity:
            for (Entity e2 : entities) {
                if (e1 != e2 && !e1.isDestroyed() && !e2.isDestroyed() 
                        && e1 instanceof Player1 && e2 instanceof Player1
                        && !(e1 instanceof Enemy1 && e2 instanceof Enemy1)) {
                    
//                    Player1 p1 = (Player1) e1;
//                    Player1 p2 = (Player1) e2;
//                    
//                    Sprite frame1 = p1.getAnimationPlayer().getCurrentAnimationFrame(); // body sprite
//                    Sprite frame2 = p2.getAnimationPlayer().getCurrentAnimationFrame(); // attack sprite
//                    
//                    boolean hit = frame1.collides((int) p1.getX(), (int) (p1.getZ() - p1.getY()), frame2, (int) p2.getX(), (int) (p2.getZ() - p2.getY()), colliderResult, p1.getAnimationPlayer().isFlip());
//                    if (hit) {
//                        Direction attackDirection = p2.getAnimationPlayer().isFlip() ? LEFT : RIGHT;
//                        p1.hit(attackDirection, colliderResult.isKnockDown());
//                    }

                    Player1 p1 = (Player1) e1;
                    Player1 p2 = (Player1) e2;

                    Sprite frame1 = p1.getAnimationPlayer().getCurrentAnimationFrame(); // attack sprite
                    Sprite frame2 = p2.getAnimationPlayer().getCurrentAnimationFrame(); // body sprite

                    for (int i = 0; i < frame1.getColliders().size(); i++) {
                        Collider xxx = frame1.getWorldSpaceCollider(i, (int) p1.getX(), (int) (p1.getZ() - p1.getY()), p1.getAnimationPlayer().isFlip());
                        if (xxx.getType() == Collider.Type.ATTACK || xxx.getType() == Collider.Type.FORCE_ATTACK) {
                            for (int i2 = 0; i2 < frame2.getColliders().size(); i2++) {
                                Collider yyy = frame2.getWorldSpaceCollider(i2, (int) p2.getX(), (int) (p2.getZ() - p2.getY()), p2.getAnimationPlayer().isFlip());
                                if (yyy.getType() == Collider.Type.BODY) {
                                    if (yyy.intersects(xxx) && Math.abs(p1.getZ() - p2.getZ()) < 16) {
                                        
                                        if (xxx.getType() == Collider.Type.ATTACK) {
                                            // hit
                                            //Direction attackDirection = p1.getAnimationPlayer().isFlip() ? LEFT : RIGHT;
                                            Direction attackDirection = p2.getX() < p1.getX() ? LEFT : RIGHT;
                                            p2.hit(attackDirection, xxx.isKnockDown(), xxx.getAttackType(), xxx);
                                            p1.setLastHitDistance((int) Math.abs(p2.getX() - p1.getX()));
                                            // TODO ver uma outra forma melhor de parar quando da cabecada
                                            p1.setVx(0);
                                        }
                                        else if (xxx.getType() == Collider.Type.FORCE_ATTACK) {
                                            //yyy.getForceAttackId()
                                            p2.addForcedAttack(xxx.getForceAttackId());
                                            p2.setThrowOpponent(p1);
                                        }
                                        
                                        continue next_entity; // TODO verificar se realmente precisa sair aqui ?
                                    }
                                }
                            }
                        }
                    }

        
                }   
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        AffineTransform ot = g.getTransform();
        
        g.translate(-Camera.getX(), -Camera.getY()); // camera transformation
        
        Terrain.draw(g);
        
        Collections.sort(entities);

        entities.forEach(entity -> {
            if (!entity.isDestroyed() && entity instanceof Actor) {
                Actor actor = (Actor) entity;
                Terrain.drawShadow(g, (int) actor.getX(), (int) (actor.getZ()));
            }
        });   
        
        entities.forEach(entity -> {
            if (!entity.isDestroyed()) {
                entity.draw(g);
            }
        });   
        
// DEGUG collision tests      
//        Player1 p1 = (Player1) player1;
//        Player1 p2 = (Player1) player2;
//
//        Sprite frame1 = p1.getAnimationPlayer().getCurrentAnimationFrame(); // body sprite
//        Sprite frame2 = p2.getAnimationPlayer().getCurrentAnimationFrame(); // attack sprite
//        
//        for (int i = 0; i < frame1.getColliders().size(); i++) {
//            Collider xxx = frame1.getWorldSpaceCollider(i, (int) p1.getX(), (int) (p1.getZ() - p1.getY()), p1.getAnimationPlayer().isFlip());
//            g.setColor(Color.ORANGE);
//            g.fillRect(xxx.x, xxx.y, xxx.width, xxx.height);
//        }
//
//        for (int i = 0; i < frame2.getColliders().size(); i++) {
//            Collider xxx = frame2.getWorldSpaceCollider(i, (int) p2.getX(), (int) (p2.getZ() - p2.getY()), p2.getAnimationPlayer().isFlip());
//            g.setColor(Color.CYAN);
//            g.fillRect(xxx.x, xxx.y, xxx.width, xxx.height);
//        }
        g.setTransform(ot);
        
        Go.draw(g);
        
        //Text.draw(g, "GOLDEN-AXE FONT TEST ABCDEFG", 1, 1, Color.RED, Color.ORANGE);
        
        g.drawImage(hud, 0, 0, 276, 207, null);
    }
    
}
