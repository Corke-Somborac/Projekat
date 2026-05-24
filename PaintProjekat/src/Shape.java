import java.awt.*;

public abstract class Shape {
    protected Color borderColor = Color.BLACK;
    protected Color fillColor = new Color(0, 0, 0, 0); // providno
    
    // Konstruktori
    public Shape() {}
    
    public Shape(Color borderColor, Color fillColor) {
        this.borderColor = borderColor;
        this.fillColor = fillColor;
    }
    
    // Apstraktne metode
    public abstract boolean contains(int x, int y);
    public abstract Rectangle getBounds();
    public abstract Shape copy();
    public abstract String getProperties();
    public abstract void updateProperties(String[] values);
    
    // Getteri i Setteri
    public Color getBorderColor() { return borderColor; }
    public void setBorderColor(Color borderColor) { this.borderColor = borderColor; }
    public Color getFillColor() { return fillColor; }
    public void setFillColor(Color fillColor) { this.fillColor = fillColor; }
}