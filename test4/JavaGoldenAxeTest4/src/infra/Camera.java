package infra;

/**
 * Camera class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Camera {
    private static final int PLAYER_HEIGHT = 48;
    private static final int PLAYER_HALF_WIDTH = 12;
    
    private static Actor player1;
    private static Actor player2;
    
    private static double x;
    private static double y;
    private static double targetMinX = 0;
    private static double targetMaxY = 186;

    public static Actor getPlayer1() {
        return player1;
    }

    public static void setPlayer1(Actor player1) {
        Camera.player1 = player1;
    }

    public static Actor getPlayer2() {
        return player2;
    }

    public static void setPlayer2(Actor player2) {
        Camera.player2 = player2;
    }

    public static double getX() {
        return x;
    }

    public static double getY() {
        return y;
    }
    
    public static double getMinX() {
        return x + PLAYER_HALF_WIDTH;
    }

    public static double getMaxX() {
        return player1.getX() > player2.getX() 
                ? player2.getX() + 276 - 2 * PLAYER_HALF_WIDTH 
                    : player1.getX() + 276 - 2 * PLAYER_HALF_WIDTH;
    }

    public static double getMinY() {
        double p1h = player1.getZ() - player1.getY();
        double p2h = player2.getZ() - player2.getY();
        return p1h > p2h 
                ? p1h - 207 + PLAYER_HEIGHT : p2h - 207 + PLAYER_HEIGHT;
    }

    public static double getMaxY() {
        double p1h = player1.getZ() - player1.getY();
        double p2h = player2.getZ() - player2.getY();
        return p1h > p2h 
                ? p2h + 207 - PLAYER_HEIGHT : p1h + 207 - PLAYER_HEIGHT;
    }
    
    public static void update() {
        double playerMinX 
                = Math.min(player1.getX(), player2.getX()) - PLAYER_HALF_WIDTH;
        
        double playerMaxX = Math.max(player1.getX(), player2.getX());
        double targetX = playerMaxX - 276 / 2;
        if (targetX < 0) {
            targetX = 0;
        }
        if (targetX > playerMinX) {
            targetX = playerMinX;
        }
        if (targetX < targetMinX) {
            targetX = targetMinX;
        }
        x = x + (targetX - x) * 0.05;
        targetMinX = x;
        
        double p1h = player1.getZ() - player1.getMinHeight();
        double p2h = player2.getZ() - player2.getMinHeight();
        double playerMinY = Math.min(p1h, p2h) - PLAYER_HEIGHT;
        double playerMaxY = Math.max(p1h, p2h);
        double targetY = playerMaxY - 207 * 0.67;
        if (targetY < 0) {
            targetY = 0;
        }
        if (targetY > playerMinY) {
            targetY = playerMinY;
        }
        if (targetY > targetMaxY) {
            targetY = targetMaxY;
        }
        y = y + (targetY - y) * 0.05;
    }
    
}
