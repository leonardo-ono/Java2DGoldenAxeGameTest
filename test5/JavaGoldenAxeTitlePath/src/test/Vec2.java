package test;

/**
 *
 * @author leonardo
 */
public class Vec2 {

    public double x;
    public double y;

    public Vec2() {
    }

    public Vec2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec2(Vec2 v) {
        this.x = v.x;
        this.y = v.y;
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void set(Vec2 v) {
        this.x = v.x;
        this.y = v.y;
    }

    public void add(Vec2 v) {
        this.x += v.x;
        this.y += v.y;
    }

    public void sub(Vec2 v) {
        this.x -= v.x;
        this.y -= v.y;
    }

    public void scale(double s) {
        this.x *= s;
        this.y *= s;
    }

    public double getLength() {
        return Math.sqrt(x * x + y * y);
    }

    public void normalize() {
        double length = getLength();
        if (length == 0) {
            return;
        }
        scale(1 / length);
    }
    
    public double dot(Vec2 v) {
        return x * v.x + y * v.y;
    }
    
    // http://www.pontov.com.br/site/arquitetura/54-matematica-e-fisica/132-o-uso-de-vetores-nos-jogos
    public double relativeAngleBetween(Vec2 v) {
        return Math.acos(dot(v) / (getLength() * v.getLength()));
    }
    
    public void rotate(double angle) {
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        double nx = x * c - y * s;
        double ny = x * s + y * c;
        x = nx;
        y = ny;
    }

    public double getAngle() {
        return Math.atan2(y, x);
    }

    @Override
    public String toString() {
        return "Vec2{" + "x=" + x + ", y=" + y + '}';
    }
    
}
