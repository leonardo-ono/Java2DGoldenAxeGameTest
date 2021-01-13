/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.List;

/**
 *
 * @author admin
 */
public class Arrow {

    private List<Point> path;
    private Vec2 position = new Vec2();
    private Vec2 velocity = new Vec2();
    private Vec2 direction = new Vec2();
    private Vec2 target = new Vec2();
    private int targetIndex = 0;
    private MouseTrail mouseTrail;
    
    private int wait;
    private int waitCount;
    private Color color;
    
    private double speed;
    
    private boolean lastPoint = false;
    private boolean finished = false;
    private boolean finished2 = false;
    
    public Arrow(List<Point> path, Color color, int wait, double speed) {
        this.path = path;
        this.color = color;
        mouseTrail = new MouseTrail(20, color);
        this.wait = wait;
        this.speed = speed;
        Point p0 = path.get(0);
        position.set(p0.x, p0.y);
        nextTarget();
    }

    public Vec2 getPosition() {
        return position;
    }

    public boolean isFinished() {
        return finished;
    }
    
    private void nextTarget() {
        if (lastPoint) return;
                
        //targetIndex = (targetIndex + 1) % path.size();
        targetIndex++;
        if (targetIndex > path.size() - 1) {
            targetIndex = path.size() - 1;
            lastPoint = true;
        }
        Point p = path.get(targetIndex);
        target.set(p.x, p.y);
    }
    
    public void update() {
        if (finished) {
            position.set(target);
            return;
        }
        
        waitCount++;
        if (waitCount < wait) {
            return;
        }
        
        direction.set(target);
        direction.sub(position);

        if (lastPoint) {
            if (finished2) {
                finished = true;
            }
            if (direction.getLength() < 0.1) {
                position.set(target);
                finished2 = true;
                return;
            }
            direction.scale(0.5);
            position.add(direction);
            return;
        }
        
        double distance = direction.getLength();
        if (distance < 20) {
            nextTarget();
        }
        
        direction.normalize();
        direction.scale(0.5);
        
        velocity.add(direction);
        
        if (velocity.getLength() > speed) {
            velocity.normalize();
            velocity.scale(speed);
        }
        
        position.add(velocity);
        mouseTrail.addTrail((int) position.x, (int) position.y);
    }
    
    public void draw(Graphics2D g) {
        if (waitCount < wait) {
            return;
        }
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //g.setColor(Color.ORANGE);
        //g.fillOval((int) (position.x - 5), (int) (position.y - 5), 10, 10);
        mouseTrail.draw(g);
        
        AffineTransform t = g.getTransform();
        g.setStroke(mouseTrail.getStrokes()[2]);
        g.translate(position.x, position.y);
        g.rotate(velocity.getAngle());
        g.drawLine(0, 0, -15, -8);
        g.drawLine(0, 0, -15, +8);
        g.setTransform(t);
    }
    
}
