import java.awt.*;

public class CircleShape extends Shape {
    private int x, y, radius;
    
    public CircleShape(int x, int y, int radius) {
        this.x = x; this.y = y;
        this.radius = radius;
    }
    
    // Getteri i Setteri
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getRadius() { return radius; }
    public void setRadius(int radius) { this.radius = radius; }
    
    @Override
    public boolean contains(int px, int py) {
        double dist = Math.sqrt((px - x)*(px - x) + (py - y)*(py - y));
        return dist <= radius;
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x - radius - 2, y - radius - 2, radius*2 + 4, radius*2 + 4);
    }
    
    @Override
    public Shape copy() {
        CircleShape copy = new CircleShape(x, y, radius);
        copy.setBorderColor(borderColor);
        copy.setFillColor(fillColor);
        return copy;
    }
    
    @Override
    public String getProperties() {
        return "Circle|X:" + x + "|Y:" + y + "|Radius:" + radius;
    }
    
    @Override
    public void updateProperties(String[] values) {
        if (values.length >= 3) {
            x = Integer.parseInt(values[0]);
            y = Integer.parseInt(values[1]);
            radius = Integer.parseInt(values[2]);
        }
    }
}