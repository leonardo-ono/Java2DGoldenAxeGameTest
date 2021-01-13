package infra;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Text (renderer) class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Text {
    
    private static final Font FONT;
    
    static {
        FONT = Resource.getFont("golden-axe").deriveFont(8.0f);
    }
    
    public static void draw(
            Graphics2D g, String text, int col, int row, Color color) {
        
        g.setFont(FONT);
        g.setColor(color);
        g.drawString(text, col * 8, (row + 1) * 10);
    }

    public static void draw(
            Graphics2D g, String text, int col, int row, Color color, Color colorShadow) {
        
        g.setFont(FONT);
        g.setColor(colorShadow);
        g.drawString(text, col * 8 + 0, (row + 1) * 10 + 0);
        g.drawString(text, col * 8 + 1, (row + 1) * 10 + 0);
        g.drawString(text, col * 8 + 0, (row + 1) * 10 + 1);
        g.drawString(text, col * 8 + 1, (row + 1) * 10 + 1);
        g.setColor(color);
        g.drawString(text, col * 8, (row + 1) * 10);
    }
    
}
