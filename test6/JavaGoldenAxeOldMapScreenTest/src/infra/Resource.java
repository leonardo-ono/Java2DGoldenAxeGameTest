/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infra;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author admin
 */
public class Resource {
    
    private static BufferedImage spriteSheet;
    private static final Map<String, Sprite1[]> spritesMap = new HashMap<>();

    private static final Map<String, Font> FONTS = new HashMap<>();

    public static Sprite1[] getSprite(String spriteId) {
        return spritesMap.get(spriteId);
    }
    
    public static void loadSpriteSheet(String imageRes, String spriteSheetRes) {
        try {
            spriteSheet = ImageIO.read(Resource.class.getResourceAsStream(imageRes));
            
            String name = null;
            List<Sprite1> spritesList = new ArrayList<>();
            InputStream is = Resource.class.getResourceAsStream(spriteSheetRes);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] data = line.split("\\ ");
                if (data[0].equals("sprite")) {
                    if (name != null && !name.equals(data[1])) {
                        Sprite1[] spritesArr = spritesList.toArray(new Sprite1[0]);
                        spritesMap.put(name, spritesArr);
                        spritesList.clear();
                    }
                    name = data[1];
                    Direction originalDirection = Direction.valueOf(data[2]);
                    int x = Integer.parseInt(data[3]);
                    int y = Integer.parseInt(data[4]);
                    int width = Integer.parseInt(data[5]);
                    int height = Integer.parseInt(data[6]);
                    int ox = Integer.parseInt(data[7]);
                    int oy = Integer.parseInt(data[8]);
                    BufferedImage subimage = spriteSheet.getSubimage(x, y, width, height);
                    Rectangle rectangle = new Rectangle(x, y, width, height);
                    Sprite1 sprite = new Sprite1(name, subimage, ox, oy, originalDirection, rectangle);
                    spritesList.add(sprite);
                }
            }
            
            if (name != null) {
                Sprite1[] spritesArr = spritesList.toArray(new Sprite1[0]);
                spritesMap.put(name, spritesArr);
                spritesList.clear();
            }
            
            br.close();
            
        } catch (Exception ex) {
            Logger.getLogger(Resource.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }

    public static Font getFont(String name) {
        Font font = FONTS.get(name);
        if (font == null) {
            String fontResource 
                    = "/res/font/" + name + ".ttf";

            try {
                font = Font.createFont(Font.TRUETYPE_FONT
                    , Resource.class.getResourceAsStream(fontResource));

                FONTS.put(name, font);
            } catch (Exception ex) {
                Logger.getLogger(
                        Resource.class.getName()).log(Level.SEVERE, null, ex);

                System.exit(-1);
            }
        }
        return font;
    }
    
}
