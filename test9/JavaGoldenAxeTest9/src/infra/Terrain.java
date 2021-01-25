package infra;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * Terrain class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Terrain {
    
    private static BufferedImage image;
    private static BufferedImage collisionMask;
    private static BufferedImage stageBackground;
    
    private static int[] maxY;
    
    public static int getMaxY(int x) {
        if (x > image.getWidth() - 1) {
            return Integer.MAX_VALUE;
        }
        return maxY[x];
    }
    
    public static void load(int stage) {
        image = Resource.getImage("stage_" + stage);
        //stageBackground = Resource.getImage("stage_" + stage + "_background");

        collisionMask = Resource.getImage("stage_" + stage + "_collision_mask");
        
        BufferedImage maxYImage = Resource.getImage("stage_" + stage + "_max_y");
        maxY = new int[maxYImage.getWidth()];
        outer:
        for (int x = 0; x < maxYImage.getWidth(); x++) {
            for (int y = 0; y < maxYImage.getHeight(); y++) {
                if (maxYImage.getRGB(x, y) == Color.WHITE.getRGB()) {
                    maxY[x] = y;
                    continue outer;
                }
            }
        }
    }
    
    public static void draw(Graphics2D g) {
        g.drawImage(stageBackground, (int) (Camera.getX() * 0.25), 0, null);
        g.drawImage(image, 0, 0, null);
        //g.drawImage(collisionMask, 0, 0, null);
    }
    
    public static boolean isWalkable(int x, int y) {
        try {
            int c = collisionMask.getRGB(x, y);
            return c == Color.WHITE.getRGB() || c == Color.RED.getRGB() || c == Color.YELLOW.getRGB() || c == Color.BLUE.getRGB();
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    public static int getHeight(int x, int y) {
        int c = Color.WHITE.getRGB();
        
        try {
            c = collisionMask.getRGB(x, y);
        }
        catch (ArrayIndexOutOfBoundsException e) {
        }
        
        if (c == Color.WHITE.getRGB()) {
            return 0;
        }
        else if (c == Color.RED.getRGB()) {
            return 52;
        }
        else if (c == Color.YELLOW.getRGB()) {
            return -18;
        }
        else if (c == Color.BLUE.getRGB()) {
            return -200;
        }
        else {
            return -999999;
        }
    }
    
    public static void drawShadow(Graphics2D g, int x, int y) {
        g.setColor(Color.BLACK);
        int terrainHeight = getHeight(x, y);
        g.fillOval((int) (x - 16), (int) (y - 1 - terrainHeight), 32, 6);
    }
}
