
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;


/**
 *
 * @author admin
 */
public class SpriteSheet {

    private BufferedImage image;
    private List<Sprite> sprites = new ArrayList<>();
    private int frame;

    public SpriteSheet(String imageRes, String spritesRes) {
        loadImage(imageRes);
        loadSprites(spritesRes);
    }
    
    private void loadImage(String imageRes) {
        try {
            image = ImageIO.read(getClass().getResourceAsStream(imageRes));
        } catch (IOException ex) {
            Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }
    
    private void loadSprites(String spritesRes) {
        try {
            InputStream is = getClass().getResourceAsStream(spritesRes);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] data = line.split("\\ ");
                if (data[0].equals("sprite")) {
                    int x = Integer.parseInt(data[1]);
                    int y = Integer.parseInt(data[2]);
                    int width = Integer.parseInt(data[3]);
                    int height = Integer.parseInt(data[4]);
                    int ox = Integer.parseInt(data[5]);
                    int oy = Integer.parseInt(data[6]);
                    BufferedImage subimage = image.getSubimage(x, y, width, height);
                    Rectangle rectangle = new Rectangle(x, y, width, height);
                    Sprite sprite = new Sprite(subimage, ox, oy, rectangle);
                    sprites.add(sprite);
                }
            }
            br.close();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }

    public int getFrame() {
        return frame;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public List<Sprite> getSprites() {
        return sprites;
    }
    
    public Sprite getSprite() {
        return sprites.get(frame);
    }
    
    public void draw(Graphics2D g, int x, int y) {
        sprites.get(frame).draw(g, x, y);
    }
    
}
