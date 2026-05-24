import java.awt.*;

public class PointShape extends Shape {
    private int x, y;
    
    public PointShape(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public PointShape(int x, int y, Color border, Color fill) {
        super(border, fill);
        this.x = x;
        this.y = y;
    }
    
    // Getteri i Setteri
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    
    @Override
    public boolean contains(int px, int py) {
        return Math.abs(px - x) <= 3 && Math.abs(py - y) <= 3;
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x - 5, y - 5, 10, 10);
    }
    
    @Override
    public Shape copy() {
        return new PointShape(x, y, borderColor, fillColor);
    }
    
    @Override
    public String getProperties() {
        return "Point|X:" + x + "|Y:" + y;
    }
    
    @Override
    public void updateProperties(String[] values) {
        if (values.length >= 2) {
            this.x = Integer.parseInt(values[0]);
            this.y = Integer.parseInt(values[1]);
        }
    }
}