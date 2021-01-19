package entity;

import infra.Camera;
import infra.Direction;
import static infra.Direction.IDLE;
import static infra.Direction.LEFT;
import static infra.Direction.RIGHT;
import infra.Entity;
import infra.Go;
import infra.PlayerControl;
import infra.Scene;
import java.util.Collections;
import scene.Stage;

/**
 *
 * @author admin
 */
public class EnemiesManager extends Entity {
    
    private final Stage stage;
    private final Player1 player1;
    private long strategyChangeTime = -1;
    
    private boolean cameraLocked;
    
    private int[] lockPositions = { 276, 668, 1100, 1520 };
    private int lockPositionIndex;
    
    private static boolean enemyKilled;
    
    public EnemiesManager(Scene scene, Player1 player1) {
        super(scene);
        this.stage = (Stage) scene;
        this.player1 = player1;
    }

    public static void setEnemyKilled(boolean enemyKilled) {
        EnemiesManager.enemyKilled = enemyKilled;
    }

    @Override
    public void init() {
        Camera.setMaxX(lockPositions[lockPositionIndex++]);
    }
    
    
    @Override
    public void update() {
        if (!cameraLocked && Camera.getX() >= Camera.getLockedMaxX() - 276) {
            Camera.setLocked(true);
            cameraLocked = true;
            strategyChangeTime = -1;
            spawnEnemies();
            return;
        }
        
        if (cameraLocked) {
            updateEnemiesStrategy();
        }
    }

    private void updateEnemiesStrategy() {
        if (strategyChangeTime < 0) {
            strategyChangeTime = System.currentTimeMillis() + 1000 + (int) (2000 * Math.random());
        }
        Direction[] aiModes = { LEFT, RIGHT, IDLE, IDLE, IDLE };
        if (System.currentTimeMillis() > strategyChangeTime || enemyKilled) {
            enemyKilled = false;
            Collections.shuffle(stage.getEntities());
            int enemyIndex = 0;
            for (Entity entity : stage.getEntities()) {
                if (entity instanceof Enemy3) {
                    Enemy3 enemy = (Enemy3) entity;
                    if (!enemy.isDead()) {
                        enemy.setAIMode(aiModes[enemyIndex++]);
                    }
                }
                else if (entity instanceof Enemy1) {
                    Enemy1 enemy = (Enemy1) entity;
                    if (!enemy.isDead()) {
                        enemy.setAIMode(aiModes[enemyIndex++]);
                    }
                }
            }
            strategyChangeTime = System.currentTimeMillis() + 10000 + (int) (5000 * Math.random());
            
            if (enemyIndex == 0 && lockPositionIndex <= lockPositions.length - 1) {
                cameraLocked = false;
                Camera.setLocked(false);
                Camera.setMaxX(lockPositions[lockPositionIndex++]);
                Go.show();
            }
        }
    }

    private void spawnEnemies() {
        switch (lockPositionIndex) {
            case 1:
                stage.addEntity(new Enemy1(stage, new PlayerControl(3), player1, 220, 242, 0));
                stage.addEntity(new Enemy1(stage, new PlayerControl(3), player1, 230, 250, 0));
                stage.addEntity(new Enemy3(stage, new PlayerControl(3), player1, 240, 270, 0));
                stage.addEntity(new Enemy3(stage, new PlayerControl(3), player1, 250, 260, 0));
                break;
            case 2:
                stage.addEntity(new Enemy1(stage, new PlayerControl(3), player1, 360, 210, 0));
                stage.addEntity(new Enemy3(stage, new PlayerControl(3), player1, 650, 226, 100));
                break;
            case 3:
                stage.addEntity(new Enemy1(stage, new PlayerControl(3), player1, 1136, 230, 0));
                stage.addEntity(new Enemy1(stage, new PlayerControl(3), player1, 1156, 240, 0));
                stage.addEntity(new Enemy3(stage, new PlayerControl(3), player1, 1176, 235, 0));
                stage.addEntity(new Enemy3(stage, new PlayerControl(3), player1, 1196, 245, 0));
                break;
            case 4:
                stage.addEntity(new Enemy1(stage, new PlayerControl(3), player1, 1200, 150, 0));
                stage.addEntity(new Enemy1(stage, new PlayerControl(3), player1, 1552, 210, 0));
                stage.addEntity(new Enemy3(stage, new PlayerControl(3), player1, 1200, 155, 0));
                stage.addEntity(new Enemy3(stage, new PlayerControl(3), player1, 1552, 200, 0));
                break;
            default:
                break;
        }
    }
    
}
