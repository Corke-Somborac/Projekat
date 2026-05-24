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
    
    private boolean isDragging = false;
    private int dragStartX, dragStartY;
    private int originalX, originalY, originalWidth, originalHeight;
    private int resizeHandle = -1;
    private boolean isResizing = false;
    private String selectedShapeType = "";
    
    private final int HANDLE_SIZE = 10;
    
    @FunctionalInterface
    public interface DrawCommand {
        void draw(Graphics2D g2d, Shape shape);
    }
    
    private DrawCommand pointDraw = (g2d, s) -> {
        PointShape p = (PointShape) s;
        g2d.setColor(p.getFillColor());
        g2d.fill(new Ellipse2D.Float(p.getX() - 4, p.getY() - 4, 9, 9));
        g2d.setColor(p.getBorderColor());
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(new Ellipse2D.Float(p.getX() - 4, p.getY() - 4, 9, 9));
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
        if (h.getFillColor().getAlpha() > 0) {
            g2d.setColor(h.getFillColor());
            g2d.fillOval(h.getX() - h.getOuterRadius(), h.getY() - h.getOuterRadius(),
                       h.getOuterRadius() * 2, h.getOuterRadius() * 2);
            g2d.setColor(Color.WHITE);
            g2d.fillOval(h.getX() - h.getInnerRadius(), h.getY() - h.getInnerRadius(),
                       h.getInnerRadius() * 2, h.getInnerRadius() * 2);
        }
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
    
    public void addShape(Shape shape) {
        shapes.add(shape);
        selectedShape = shape;
        repaint();
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
    
    public Shape getSelectedShape() {
        return selectedShape;
    }
    
    public void deleteSelectedShape() {
        if (selectedShape != null) {
            shapes.remove(selectedShape);
            selectedShape = null;
            repaint();
        }
    }
    
    public void clearAllShapes() {
        shapes.clear();
        selectedShape = null;
        repaint();
    }
    
    public void updateShape(Shape oldShape, Shape newShape) {
        int index = shapes.indexOf(oldShape);
        if (index != -1) {
            shapes.set(index, newShape);
            selectedShape = newShape;
            repaint();
        }
    }
    
    public void setSelectionMode(boolean mode) {
        this.isSelectionMode = mode;
        if (!mode) {
            selectedShape = null;
        }
    }
    
    public void setTransformMode(boolean mode) {}
    
    public void handleMousePress(int x, int y) {
        if (!isSelectionMode) return;
        
        if (selectedShape != null) {
            resizeHandle = getResizeHandleAt(x, y);
            if (resizeHandle != -1) {
                isResizing = true;
                saveOriginalValues();
                dragStartX = x;
                dragStartY = y;
                return;
            }
        }
        
        for (int i = shapes.size() - 1; i >= 0; i--) {
            if (shapes.get(i).contains(x, y)) {
                selectedShape = shapes.get(i);
                isDragging = true;
                dragStartX = x;
                dragStartY = y;
                repaint();
                return;
            }
        }
        
        selectedShape = null;
        repaint();
    }
    
    private void saveOriginalValues() {
        if (selectedShape instanceof RectangleShape) {
            RectangleShape r = (RectangleShape) selectedShape;
            originalX = r.getX();
            originalY = r.getY();
            originalWidth = r.getWidth();
            originalHeight = r.getHeight();
            selectedShapeType = "Rectangle";
        } else if (selectedShape instanceof CircleShape) {
            CircleShape c = (CircleShape) selectedShape;
            originalX = c.getX();
            originalY = c.getY();
            originalWidth = c.getRadius() * 2;
            selectedShapeType = "Circle";
        } else if (selectedShape instanceof HollowCircleShape) {
            HollowCircleShape h = (HollowCircleShape) selectedShape;
            originalX = h.getX();
            originalY = h.getY();
            originalWidth = h.getOuterRadius() * 2;
            selectedShapeType = "HollowCircle";
        }
    }
    
    public void handleMouseDrag(int x, int y) {
        if (selectedShape == null) return;
        
        if (isResizing) {
            resizeShape(x, y);
        } else if (isDragging) {
            moveShape(x, y);
        }
        
        repaint();
    }
    
    public void handleMouseRelease() {
        isDragging = false;
        isResizing = false;
        resizeHandle = -1;
    }
    
    private void moveShape(int currentX, int currentY) {
        int deltaX = currentX - dragStartX;
        int deltaY = currentY - dragStartY;
        
        if (selectedShape instanceof PointShape) {
            PointShape p = (PointShape) selectedShape;
            p.setX(p.getX() + deltaX);
            p.setY(p.getY() + deltaY);
        } else if (selectedShape instanceof LineShape) {
            LineShape l = (LineShape) selectedShape;
            l.setX1(l.getX1() + deltaX);
            l.setY1(l.getY1() + deltaY);
            l.setX2(l.getX2() + deltaX);
            l.setY2(l.getY2() + deltaY);
        } else if (selectedShape instanceof RectangleShape) {
            RectangleShape r = (RectangleShape) selectedShape;
            r.setX(r.getX() + deltaX);
            r.setY(r.getY() + deltaY);
        } else if (selectedShape instanceof CircleShape) {
            CircleShape c = (CircleShape) selectedShape;
            c.setX(c.getX() + deltaX);
            c.setY(c.getY() + deltaY);
        } else if (selectedShape instanceof HollowCircleShape) {
            HollowCircleShape h = (HollowCircleShape) selectedShape;
            h.setX(h.getX() + deltaX);
            h.setY(h.getY() + deltaY);
        }
        
        dragStartX = currentX;
        dragStartY = currentY;
    }
    
    private void resizeShape(int currentX, int currentY) {
        int deltaX = currentX - dragStartX;
        int deltaY = currentY - dragStartY;
        
        if (selectedShape instanceof RectangleShape) {
            RectangleShape r = (RectangleShape) selectedShape;
            int newX = originalX;
            int newY = originalY;
            int newWidth = originalWidth;
            int newHeight = originalHeight;
            
            switch (resizeHandle) {
                case 0:
                    newX = originalX + deltaX;
                    newY = originalY + deltaY;
                    newWidth = originalWidth - deltaX;
                    newHeight = originalHeight - deltaY;
                    break;
                case 1:
                    newY = originalY + deltaY;
                    newWidth = originalWidth + deltaX;
                    newHeight = originalHeight - deltaY;
                    break;
                case 2:
                    newX = originalX + deltaX;
                    newWidth = originalWidth - deltaX;
                    newHeight = originalHeight + deltaY;
                    break;
                case 3:
                    newWidth = originalWidth + deltaX;
                    newHeight = originalHeight + deltaY;
                    break;
            }
            
            if (newWidth > 10 && newHeight > 10) {
                r.setX(newX);
                r.setY(newY);
                r.setWidth(newWidth);
                r.setHeight(newHeight);
            }
        } else if (selectedShape instanceof CircleShape) {
            CircleShape c = (CircleShape) selectedShape;
            int delta = Math.max(deltaX, deltaY);
            int newRadius = originalWidth / 2 + delta;
            if (newRadius > 5) {
                c.setRadius(newRadius);
            }
        } else if (selectedShape instanceof HollowCircleShape) {
            HollowCircleShape h = (HollowCircleShape) selectedShape;
            int delta = Math.max(deltaX, deltaY);
            int newOuterRadius = originalWidth / 2 + delta;
            if (newOuterRadius > h.getInnerRadius() + 5) {
                h.setOuterRadius(newOuterRadius);
            }
        }
    }
    
    private int getResizeHandleAt(int x, int y) {
        if (selectedShape == null) return -1;
        
        Rectangle bounds = selectedShape.getBounds();
        int handleZone = HANDLE_SIZE + 4;
        
        if (x >= bounds.x - handleZone/2 && x <= bounds.x + handleZone/2 &&
            y >= bounds.y - handleZone/2 && y <= bounds.y + handleZone/2) {
            return 0;
        }
        if (x >= bounds.x + bounds.width - handleZone/2 && x <= bounds.x + bounds.width + handleZone/2 &&
            y >= bounds.y - handleZone/2 && y <= bounds.y + handleZone/2) {
            return 1;
        }
        if (x >= bounds.x - handleZone/2 && x <= bounds.x + handleZone/2 &&
            y >= bounds.y + bounds.height - handleZone/2 && y <= bounds.y + bounds.height + handleZone/2) {
            return 2;
        }
        if (x >= bounds.x + bounds.width - handleZone/2 && x <= bounds.x + bounds.width + handleZone/2 &&
            y >= bounds.y + bounds.height - handleZone/2 && y <= bounds.y + bounds.height + handleZone/2) {
            return 3;
        }
        
        return -1;
    }
    
    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(new Color(220, 220, 230));
        g2d.setStroke(new BasicStroke(0.5f));
        
        for (int x = 0; x < getWidth(); x += 20) {
            g2d.drawLine(x, 0, x, getHeight());
        }
        for (int y = 0; y < getHeight(); y += 20) {
            g2d.drawLine(0, y, getWidth(), y);
        }
        
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
        
        g2d.setColor(new Color(70, 130, 200, (int)(alpha * 100)));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRect(bounds.x - 3, bounds.y - 3, bounds.width + 6, bounds.height + 6);
        
        g2d.setColor(new Color(70, 130, 200));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4);
        
        int handleSize = HANDLE_SIZE;
        int offset = handleSize / 2;
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(bounds.x - offset, bounds.y - offset, handleSize, handleSize);
        g2d.fillRect(bounds.x + bounds.width - offset, bounds.y - offset, handleSize, handleSize);
        g2d.fillRect(bounds.x - offset, bounds.y + bounds.height - offset, handleSize, handleSize);
        g2d.fillRect(bounds.x + bounds.width - offset, bounds.y + bounds.height - offset, handleSize, handleSize);
        
        g2d.setColor(new Color(70, 130, 200));
        g2d.drawRect(bounds.x - offset, bounds.y - offset, handleSize, handleSize);
        g2d.drawRect(bounds.x + bounds.width - offset, bounds.y - offset, handleSize, handleSize);
        g2d.drawRect(bounds.x - offset, bounds.y + bounds.height - offset, handleSize, handleSize);
        g2d.drawRect(bounds.x + bounds.width - offset, bounds.y + bounds.height - offset, handleSize, handleSize);
        
        g2d.setStroke(new BasicStroke(1));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        drawGrid(g2d);
        
        for (Shape shape : shapes) {
            if (shape instanceof PointShape) pointDraw.draw(g2d, shape);
            else if (shape instanceof LineShape) lineDraw.draw(g2d, shape);
            else if (shape instanceof RectangleShape) rectDraw.draw(g2d, shape);
            else if (shape instanceof CircleShape) circleDraw.draw(g2d, shape);
            else if (shape instanceof HollowCircleShape) hollowDraw.draw(g2d, shape);
        }
        
        if (selectedShape != null) {
            drawAnimatedSelectionBorder(g2d, selectedShape);
        }
    }
}