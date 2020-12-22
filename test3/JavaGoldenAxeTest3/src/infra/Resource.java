package infra;

import static infra.Settings.*;
import java.awt.Font;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * Resource class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Resource {
    
    private static final Map<String, BufferedImage> IMAGES = new HashMap<>();
    private static final Map<String, byte[]> SOUNDS = new HashMap<>();
    private static final Map<String, Font> FONTS = new HashMap<>();

    private Resource() {
    }

    public static BufferedImage getImage(String name) {
        BufferedImage image = null;
        try {
            InputStream is = Resource.class.getResourceAsStream(
                RES_IMAGE_PATH + name + RES_IMAGE_FILE_EXT);
            
            image = IMAGES.get(name);
            if (image == null) {
                image = ImageIO.read(is);
                IMAGES.put(name, image);
            }
        } catch (Exception ex) {
            Logger.getLogger(
                    Resource.class.getName()).log(Level.SEVERE, null, ex);

            System.exit(-1);
        }
        return image;
    }
    
    public static byte[] getSound(String name) {
        byte[] sound;
        sound = SOUNDS.get(name);
        if (sound == null) {
            String soundResource = RES_SOUND_PATH + name + RES_SOUND_FILE_EXT;
            try (
                InputStream is = 
                    Resource.class.getResourceAsStream(soundResource);
                    
                InputStream bis = new BufferedInputStream(is);            
                AudioInputStream ais = AudioSystem.getAudioInputStream(bis)) {
                if (!ais.getFormat().matches(SOUND_AUDIO_FORMAT)) {
                    throw new Exception("Sound '" + soundResource + 
                                        "' format not compatible !");
                }
                long soundSize = 
                    ais.getFrameLength() * ais.getFormat().getFrameSize();
                
                sound = new byte[(int) soundSize];
                ais.read(sound);
                SOUNDS.put(name, sound);
            } 
            catch (Exception ex) {
                Logger.getLogger(
                        Resource.class.getName()).log(Level.SEVERE, null, ex);
                
                System.exit(-1);
            }
        }
        return sound;
    }
    
    public static Font getFont(String name) {
        Font font = FONTS.get(name);
        if (font == null) {
            String fontResource 
                    = RES_FONT_PATH + name + RES_FONT_FILE_EXT;

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

    public static List<Collider> getColliders(String res) {
        List<Collider> colliders = new ArrayList<>();
        try {
            InputStream is = Resource.class.getResourceAsStream(res);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] data = line.split("\\ ");
                if (data[0].equals("collider")) {
                    Collider.Type type = Collider.Type.valueOf(data[1]);
                    Collider.AttackType attackType = Collider.AttackType.valueOf(data[2]);
                    boolean knockDown = Boolean.parseBoolean(data[3]);
                    int x = Integer.parseInt(data[4]);
                    int y = Integer.parseInt(data[5]);
                    int width = Integer.parseInt(data[6]);
                    int height = Integer.parseInt(data[7]);
                    Collider currentCollider;
                    colliders.add(currentCollider = new Collider(x, y, width, height));
                    currentCollider.setType(type);
                    currentCollider.setAttackType(attackType);
                    currentCollider.setKnockDown(knockDown);
                }
            }
            br.close();
        }
        catch (Exception ex) {
            Logger.getLogger(Resource.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }        
        return colliders;
    }
    
}
