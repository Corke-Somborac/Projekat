import java.awt.*;

public class HollowCircleShape extends Shape {
    private int x, y, outerRadius, innerRadius;
    
    public HollowCircleShape(int x, int y, int outerRadius, int innerRadius) {
        this.x = x; this.y = y;
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
    }
    
    public HollowCircleShape(int x, int y, int outerRadius, int innerRadius, Color borderColor, Color fillColor) {
        super(borderColor, fillColor);
        this.x = x; this.y = y;
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
    }
    
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getOuterRadius() { return outerRadius; }
    public void setOuterRadius(int outerRadius) { this.outerRadius = outerRadius; }
    public int getInnerRadius() { return innerRadius; }
    public void setInnerRadius(int innerRadius) { this.innerRadius = innerRadius; }
    
    @Override
    public boolean contains(int px, int py) {
        double dist = Math.sqrt((px - x)*(px - x) + (py - y)*(py - y));
        return dist <= outerRadius && dist >= innerRadius;
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x - outerRadius - 3, y - outerRadius - 3, 
                            outerRadius*2 + 6, outerRadius*2 + 6);
    }
    
    @Override
    public Shape copy() {
        return new HollowCircleShape(x, y, outerRadius, innerRadius, borderColor, fillColor);
    }
    
    @Override
    public String getProperties() {
        return "HollowCircle|X:" + x + "|Y:" + y + "|OuterRadius:" + outerRadius + "|InnerRadius:" + innerRadius;
    }
    
    @Override
    public void updateProperties(String[] values) {
        if (values.length >= 4) {
            x = Integer.parseInt(values[0]);
            y = Integer.parseInt(values[1]);
            outerRadius = Integer.parseInt(values[2]);
            innerRadius = Integer.parseInt(values[3]);
        }
    }
}