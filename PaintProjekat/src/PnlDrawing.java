import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PnlDrawing extends JPanel {
    private ArrayList<Shape> shapes = new ArrayList<>();
    private Shape selectedShape = null;
    private boolean isSelectionMode = false;
    private float selectionAnimation = 0;
    private Timer animationTimer;
    
    // Functional interface for drawing
    @FunctionalInterface
    public interface DrawCommand {
        void draw(Graphics2D g2d, Shape shape);
    }
    
    private DrawCommand pointDraw = (g2d, s) -> {
        PointShape p = (PointShape) s;
        g2d.setColor(p.getBorderColor());
        g2d.fill(new Ellipse2D.Float(p.getX() - 3, p.getY() - 3, 7, 7));
    };
    
    private DrawCommand lineDraw = (g2d, s) -> {
        LineShape l = (LineShape) s;
        g2d.setColor(l.getBorderColor());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(l.getX1(), l.getY1(), l.getX2(), l.getY2());
        g2d.setStroke(new BasicStroke(1));
    };
    
    private DrawCommand rectDraw = (g2d, s) -> {
        RectangleShape r = (RectangleShape) s;
        if (r.getFillColor().getAlpha() > 0) {
            g2d.setColor(r.getFillColor());
            g2d.fillRect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
        }
        g2d.setColor(r.getBorderColor());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
        g2d.setStroke(new BasicStroke(1));
    };
    
    private DrawCommand circleDraw = (g2d, s) -> {
        CircleShape c = (CircleShape) s;
        if (c.getFillColor().getAlpha() > 0) {
            g2d.setColor(c.getFillColor());
            g2d.fillOval(c.getX() - c.getRadius(), c.getY() - c.getRadius(), 
                       c.getRadius() * 2, c.getRadius() * 2);
        }
        g2d.setColor(c.getBorderColor());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(c.getX() - c.getRadius(), c.getY() - c.getRadius(), 
                   c.getRadius() * 2, c.getRadius() * 2);
        g2d.setStroke(new BasicStroke(1));
    };
    
    private DrawCommand hollowDraw = (g2d, s) -> {
        HollowCircleShape h = (HollowCircleShape) s;
        g2d.setColor(h.getBorderColor());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(h.getX() - h.getOuterRadius(), h.getY() - h.getOuterRadius(),
                   h.getOuterRadius() * 2, h.getOuterRadius() * 2);
        g2d.drawOval(h.getX() - h.getInnerRadius(), h.getY() - h.getInnerRadius(),
                   h.getInnerRadius() * 2, h.getInnerRadius() * 2);
        g2d.setStroke(new BasicStroke(1));
    };
    
    public PnlDrawing() {
        setBackground(new Color(248, 248, 255));
        setPreferredSize(new Dimension(800, 600));
        
        // Animation for selection highlight
        animationTimer = new Timer(true);
        animationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (selectedShape != null) {
                    selectionAnimation = (selectionAnimation + 0.1f) % (float)(2 * Math.PI);
                    repaint();
                }
            }
        }, 0, 100);
    }
    
    public void setSelectionMode(boolean mode) {
        this.isSelectionMode = mode;
        if (!mode) {
            selectedShape = null;
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Draw grid for better precision
        drawGrid(g2d);
        
        // Draw all shapes
        for (Shape shape : shapes) {
            if (shape instanceof PointShape) pointDraw.draw(g2d, shape);
            else if (shape instanceof LineShape) lineDraw.draw(g2d, shape);
            else if (shape instanceof RectangleShape) rectDraw.draw(g2d, shape);
            else if (shape instanceof CircleShape) circleDraw.draw(g2d, shape);
            else if (shape instanceof HollowCircleShape) hollowDraw.draw(g2d, shape);
        }
        
        // Draw selection highlight with animation
        if (selectedShape != null) {
            drawAnimatedSelectionBorder(g2d, selectedShape);
        }
    }
    
    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(new Color(220, 220, 230));
        g2d.setStroke(new BasicStroke(0.5f));
        
        // Draw grid lines every 20 pixels
        for (int x = 0; x < getWidth(); x += 20) {
            g2d.drawLine(x, 0, x, getHeight());
        }
        for (int y = 0; y < getHeight(); y += 20) {
            g2d.drawLine(0, y, getWidth(), y);
        }
        
        // Draw thicker lines every 100 pixels
        g2d.setColor(new Color(200, 200, 210));
        g2d.setStroke(new BasicStroke(1.0f));
        for (int x = 0; x < getWidth(); x += 100) {
            g2d.drawLine(x, 0, x, getHeight());
        }
        for (int y = 0; y < getHeight(); y += 100) {
            g2d.drawLine(0, y, getWidth(), y);
        }
    }
    
    private void drawAnimatedSelectionBorder(Graphics2D g2d, Shape shape) {
        Rectangle bounds = shape.getBounds();
        float alpha = 0.5f + (float)(Math.sin(selectionAnimation) * 0.3f);
        
        // Outer glow
        g2d.setColor(new Color(70, 130, 200, (int)(alpha * 100)));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRect(bounds.x - 3, bounds.y - 3, bounds.width + 6, bounds.height + 6);
        
        // Selection border
        g2d.setColor(new Color(70, 130, 200));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4);
        
        // Corner handles
        g2d.setColor(Color.WHITE);
        g2d.fillRect(bounds.x - 5, bounds.y - 5, 8, 8);
        g2d.fillRect(bounds.x + bounds.width - 3, bounds.y - 5, 8, 8);
        g2d.fillRect(bounds.x - 5, bounds.y + bounds.height - 3, 8, 8);
        g2d.fillRect(bounds.x + bounds.width - 3, bounds.y + bounds.height - 3, 8, 8);
        
        g2d.setColor(new Color(70, 130, 200));
        g2d.drawRect(bounds.x - 5, bounds.y - 5, 8, 8);
        g2d.drawRect(bounds.x + bounds.width - 3, bounds.y - 5, 8, 8);
        g2d.drawRect(bounds.x - 5, bounds.y + bounds.height - 3, 8, 8);
        g2d.drawRect(bounds.x + bounds.width - 3, bounds.y + bounds.height - 3, 8, 8);
        
        g2d.setStroke(new BasicStroke(1));
    }
    
    public void addShape(Shape shape) {
        shapes.add(shape);
        selectedShape = shape;
    }
    
    public void selectShapeAt(int x, int y) {
        for (int i = shapes.size() - 1; i >= 0; i--) {
            if (shapes.get(i).contains(x, y)) {
                selectedShape = shapes.get(i);
                repaint();
                return;
            }
        }
        selectedShape = null;
        repaint();
    }
    
    public Shape getSelectedShape() { return selectedShape; }
    
    public void deleteSelectedShape() { 
        shapes.remove(selectedShape); 
        selectedShape = null; 
    }
    
    public void clearAllShapes() {
        shapes.clear();
        selectedShape = null;
    }
    
    public void updateShape(Shape oldShape, Shape newShape) {
        int index = shapes.indexOf(oldShape);
        if (index != -1) {
            shapes.set(index, newShape);
            selectedShape = newShape;
        }
    }
}