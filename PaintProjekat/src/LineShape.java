import java.awt.*;

public class LineShape extends Shape {
    private int x1, y1, x2, y2;
    
    public LineShape(int x1, int y1, int x2, int y2) {
        this.x1 = x1; this.y1 = y1;
        this.x2 = x2; this.y2 = y2;
    }
    
    // Getteri i Setteri
    public int getX1() { return x1; }
    public void setX1(int x1) { this.x1 = x1; }
    public int getY1() { return y1; }
    public void setY1(int y1) { this.y1 = y1; }
    public int getX2() { return x2; }
    public void setX2(int x2) { this.x2 = x2; }
    public int getY2() { return y2; }
    public void setY2(int y2) { this.y2 = y2; }
    
    @Override
    public boolean contains(int px, int py) {
        double dist = pointToLineDistance(px, py, x1, y1, x2, y2);
        return dist <= 5;
    }
    
    private double pointToLineDistance(int px, int py, int x1, int y1, int x2, int y2) {
        double area = Math.abs((x2 - x1) * (py - y1) - (px - x1) * (y2 - y1));
        double length = Math.sqrt((x2 - x1)*(x2 - x1) + (y2 - y1)*(y2 - y1));
        if (length == 0) return Math.sqrt((px - x1)*(px - x1) + (py - y1)*(py - y1));
        return area / length;
    }
    
    @Override
    public Rectangle getBounds() {
        int minX = Math.min(x1, x2);
        int minY = Math.min(y1, y2);
        return new Rectangle(minX - 5, minY - 5, 
                            Math.abs(x2 - x1) + 10, Math.abs(y2 - y1) + 10);
    }
    
    @Override
    public Shape copy() {
        return new LineShape(x1, y1, x2, y2);
    }
    
    @Override
    public String getProperties() {
        return "Line|X1:" + x1 + "|Y1:" + y1 + "|X2:" + x2 + "|Y2:" + y2;
    }
    
    @Override
    public void updateProperties(String[] values) {
        if (values.length >= 4) {
            x1 = Integer.parseInt(values[0]);
            y1 = Integer.parseInt(values[1]);
            x2 = Integer.parseInt(values[2]);
            y2 = Integer.parseInt(values[3]);
        }
    }
}