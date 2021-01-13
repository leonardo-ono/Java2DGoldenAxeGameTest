package test;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author leonardo
 */
public class MouseTrail {

    private Color color;
    private final List<Point> trail = new ArrayList<Point>();
    private final int size;
    public final Stroke[] strokes;
    private boolean visible;
    
    public MouseTrail(int size, Color color) {
        this.size = size;
        this.color = color;
        strokes = new Stroke[size];
        createStrokes();
    }

    public List<Point> getTrail() {
        return trail;
    }

    public Stroke[] getStrokes() {
        return strokes;
    }

    public boolean isVisible() {
        return visible;
    }

    private void createStrokes() {
        for (int i = 0; i < strokes.length; i++) {
            strokes[i] = new BasicStroke(1 + i * 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        }
    }
    
    public int getSize() {
        return size;
    }

    public void standBy() {
        addTrail(-1, -1);
    }
    
    public void addTrail(int x, int y) {
        if (x < 0 || y < 0) {
            trail.add(null);
        }
        else {
            trail.add(new Point(x, y));
        }
        
        while (trail.size() > size) {
            trail.remove(0);
        }
        
        visible = true;
    }

    public void clear() {
        trail.clear();
    }
    
    public void draw(Graphics2D g) {
        if (!visible) {
            return;
        }
        Stroke originalStroke = g.getStroke();
        g.setColor(color);
        for (int i = 0; i < trail.size() - 1; i++) {
            g.setStroke(strokes[2]);
            Point p1 = trail.get(i);
            Point p2 = trail.get(i + 1);
            if (p1 != null && p2 != null) {
                g.drawLine(p1.x, p1.y, p2.x + 1, p2.y + 1);
            }
        }
        g.setStroke(originalStroke);
    }

}
