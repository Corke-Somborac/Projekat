import java.awt.*;

public class RectangleShape extends Shape {
    private int x, y, width, height;
    
    public RectangleShape(int x, int y, int width, int height) {
        this.x = x; this.y = y;
        this.width = width; this.height = height;
    }
    
    public RectangleShape(int x, int y, int width, int height, Color borderColor, Color fillColor) {
        super(borderColor, fillColor);
        this.x = x; this.y = y;
        this.width = width; this.height = height;
    }
    
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    
    @Override
    public boolean contains(int px, int py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x - 3, y - 3, width + 6, height + 6);
    }
    
    @Override
    public Shape copy() {
        return new RectangleShape(x, y, width, height, borderColor, fillColor);
    }
    
    @Override
    public String getProperties() {
        return "Rectangle|X:" + x + "|Y:" + y + "|Width:" + width + "|Height:" + height;
    }
    
    @Override
    public void updateProperties(String[] values) {
        if (values.length >= 4) {
            x = Integer.parseInt(values[0]);
            y = Integer.parseInt(values[1]);
            width = Integer.parseInt(values[2]);
            height = Integer.parseInt(values[3]);
        }
    }
}