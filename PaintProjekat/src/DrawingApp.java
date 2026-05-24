import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class DrawingApp extends JFrame {
    private PnlDrawing drawingPanel;
    private String currentTool = "Point";
    private ArrayList<Point> linePoints = new ArrayList<>();
    private JLabel lblStatus, lblShapeInfo;
    private JPanel toolPanel;
    private JButton[] toolButtons;
    
    public DrawingApp() {
        setTitle("Professional Drawing Application");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Setup look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initComponents();
        setupKeyboardShortcuts();
    }
    
    private void initComponents() {
        // Drawing panel with better rendering
        drawingPanel = new PnlDrawing();
        drawingPanel.setBackground(new Color(248, 248, 255));
        drawingPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        drawingPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                handleCanvasClick(e.getX(), e.getY());
            }
        });
        
        // Modern tool panel with gradient
        toolPanel = createModernToolPanel();
        
        // Status bar with shape info
        JPanel statusBar = createStatusBar();
        
        // Add components
        add(toolPanel, BorderLayout.WEST);
        add(drawingPanel, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private JPanel createModernToolPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 240, 245));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setPreferredSize(new Dimension(180, 0));
        
        // Title
        JLabel title = new JLabel("TOOLS", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(new Color(70, 70, 70));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(title);
        
        // Tool buttons
        String[] tools = {"✏️ Point", "📏 Line", "⬛ Rectangle", "⚪ Circle", "⭕ Hollow Circle", "🖱️ Select"};
        String[] toolCommands = {"Point", "Line", "Rectangle", "Circle", "Hollow Circle", "Select"};
        toolButtons = new JButton[tools.length];
        
        for (int i = 0; i < tools.length; i++) {
            JButton btn = createStyledButton(tools[i]);
            final String command = toolCommands[i];
            btn.addActionListener(e -> {
                currentTool = command;
                linePoints.clear();
                updateToolSelection(command);
                lblStatus.setText("🔧 Active Tool: " + command);
                drawingPanel.setSelectionMode(command.equals("Select"));
            });
            btn.setToolTipText(getToolTipText(command));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(btn);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
            toolButtons[i] = btn;
        }
        
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Separator
        panel.add(new JSeparator());
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Action buttons
        JButton btnModify = createStyledButton("✏️ Modify Selected");
        JButton btnDelete = createStyledButton("🗑️ Delete Selected");
        JButton btnClear = createStyledButton("🧹 Clear All");
        
        btnModify.addActionListener(e -> modifySelectedShape());
        btnDelete.addActionListener(e -> deleteSelectedShape());
        btnClear.addActionListener(e -> clearAllShapes());
        
        btnModify.setToolTipText("Modify selected shape (Ctrl+M)");
        btnDelete.setToolTipText("Delete selected shape (Delete)");
        btnClear.setToolTipText("Clear all shapes from canvas");
        
        btnModify.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDelete.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnClear.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(btnModify);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(btnDelete);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(btnClear);
        
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setBackground(new Color(250, 250, 250));
        button.setForeground(new Color(50, 50, 50));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(230, 240, 255));
                button.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 200)));
            }
            public void mouseExited(MouseEvent e) {
                if (!button.isSelected()) {
                    button.setBackground(new Color(250, 250, 250));
                    button.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
                }
            }
        });
        
        return button;
    }
    
    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 240, 245));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        lblStatus = new JLabel("🔧 Active Tool: Point");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        lblShapeInfo = new JLabel("ℹ️ No shape selected");
        lblShapeInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblShapeInfo.setForeground(new Color(100, 100, 100));
        
        JLabel lblCoordinates = new JLabel();
        lblCoordinates.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        // Mouse motion listener for coordinates
        drawingPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                lblCoordinates.setText(String.format("📍 X: %d, Y: %d", e.getX(), e.getY()));
            }
        });
        
        panel.add(lblStatus, BorderLayout.WEST);
        panel.add(lblShapeInfo, BorderLayout.CENTER);
        panel.add(lblCoordinates, BorderLayout.EAST);
        
        return panel;
    }
    
    private void setupKeyboardShortcuts() {
        // Delete key for deleting selected shape
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .addKeyEventDispatcher(e -> {
                if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_DELETE) {
                    deleteSelectedShape();
                    return true;
                }
                if (e.getID() == KeyEvent.KEY_PRESSED && e.isControlDown() && e.getKeyCode() == KeyEvent.VK_M) {
                    modifySelectedShape();
                    return true;
                }
                return false;
            });
    }
    
    private void updateToolSelection(String selectedTool) {
        for (JButton btn : toolButtons) {
            btn.setBackground(new Color(250, 250, 250));
            btn.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        }
        // Find and highlight selected button
        for (JButton btn : toolButtons) {
            if (btn.getText().contains(selectedTool) || 
                (selectedTool.equals("Point") && btn.getText().contains("Point")) ||
                (selectedTool.equals("Select") && btn.getText().contains("Select"))) {
                btn.setBackground(new Color(200, 220, 255));
                btn.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 200), 2));
            }
        }
    }
    
    private String getToolTipText(String tool) {
        switch(tool) {
            case "Point": return "Click anywhere to draw a point";
            case "Line": return "Click two points to draw a line";
            case "Rectangle": return "Click, then enter width and height";
            case "Circle": return "Click, then enter radius";
            case "Hollow Circle": return "Click, then enter outer and inner radius";
            case "Select": return "Click on a shape to select it";
            default: return "";
        }
    }
    
    private void handleCanvasClick(int x, int y) {
        switch (currentTool) {
            case "Select":
                drawingPanel.selectShapeAt(x, y);
                updateShapeInfo();
                break;
            case "Point":
                drawingPanel.addShape(new PointShape(x, y));
                break;
            case "Line":
                linePoints.add(new Point(x, y));
                if (linePoints.size() == 2) {
                    Point p1 = linePoints.get(0);
                    Point p2 = linePoints.get(1);
                    drawingPanel.addShape(new LineShape(p1.x, p1.y, p2.x, p2.y));
                    linePoints.clear();
                } else {
                    lblStatus.setText("🔧 Line: Click second point");
                }
                break;
            case "Rectangle":
                showRectangleDialog(x, y);
                break;
            case "Circle":
                showCircleDialog(x, y);
                break;
            case "Hollow Circle":
                showHollowCircleDialog(x, y);
                break;
        }
        drawingPanel.repaint();
        updateShapeInfo();
    }
    
    private void updateShapeInfo() {
        Shape selected = drawingPanel.getSelectedShape();
        if (selected != null) {
            String info = selected.getProperties().replace("|", " | ");
            lblShapeInfo.setText("✅ Selected: " + info);
        } else {
            lblShapeInfo.setText("ℹ️ No shape selected");
        }
    }
    
    private void showRectangleDialog(int x, int y) {
        JTextField wField = new JTextField(10);
        JTextField hField = new JTextField(10);
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Width:"));
        panel.add(wField);
        panel.add(new JLabel("Height:"));
        panel.add(hField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Rectangle Dimensions", 
                      JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int w = Integer.parseInt(wField.getText());
                int h = Integer.parseInt(hField.getText());
                if (w > 0 && h > 0) {
                    drawingPanel.addShape(new RectangleShape(x, y, w, h));
                } else {
                    JOptionPane.showMessageDialog(this, "Dimensions must be positive!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid number!");
            }
        }
    }
    
    private void showCircleDialog(int x, int y) {
        JTextField rField = new JTextField(10);
        int result = JOptionPane.showConfirmDialog(this, rField, "Circle Radius", 
                      JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int r = Integer.parseInt(rField.getText());
                if (r > 0) {
                    drawingPanel.addShape(new CircleShape(x, y, r));
                } else {
                    JOptionPane.showMessageDialog(this, "Radius must be positive!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid number!");
            }
        }
    }
    
    private void showHollowCircleDialog(int x, int y) {
        JTextField outerField = new JTextField(10);
        JTextField innerField = new JTextField(10);
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Outer Radius:"));
        panel.add(outerField);
        panel.add(new JLabel("Inner Radius:"));
        panel.add(innerField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Hollow Circle", 
                      JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int outer = Integer.parseInt(outerField.getText());
                int inner = Integer.parseInt(innerField.getText());
                if (outer > 0 && inner > 0 && inner < outer) {
                    drawingPanel.addShape(new HollowCircleShape(x, y, outer, inner));
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid radii! Inner must be < Outer and > 0");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid number!");
            }
        }
    }
    
    private void modifySelectedShape() {
        Shape selected = drawingPanel.getSelectedShape();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "No shape selected!\nPlease select a shape first.", 
                                        "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new ModifyDialog(this, selected, drawingPanel).setVisible(true);
        drawingPanel.repaint();
        updateShapeInfo();
    }
    
    private void deleteSelectedShape() {
        Shape selected = drawingPanel.getSelectedShape();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "No shape selected!\nPlease select a shape first.", 
                                        "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, 
                      "Are you sure you want to delete the selected shape?", 
                      "Confirm Delete", JOptionPane.YES_NO_OPTION, 
                      JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            drawingPanel.deleteSelectedShape();
            drawingPanel.repaint();
            updateShapeInfo();
            lblStatus.setText("✅ Shape deleted");
        }
    }
    
    private void clearAllShapes() {
        int confirm = JOptionPane.showConfirmDialog(this, 
                      "Are you sure you want to clear ALL shapes?", 
                      "Confirm Clear All", JOptionPane.YES_NO_OPTION, 
                      JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            drawingPanel.clearAllShapes();
            drawingPanel.repaint();
            updateShapeInfo();
            lblStatus.setText("🧹 All shapes cleared");
        }
    }
}