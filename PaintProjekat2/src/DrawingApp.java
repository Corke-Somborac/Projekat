import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class DrawingApp extends JFrame {
    private PnlDrawing drawingPanel;
    private String currentTool = "Tačka";
    private ArrayList<Point> linePoints = new ArrayList<>();
    private JLabel lblStatus, lblShapeInfo;
    private JPanel toolPanel;
    private JButton[] toolButtons;
    
    private final Color GREEN_COLOR = new Color(217, 245, 213);
    private final Color GREEN_DARK = new Color(180, 220, 170);
    private final Color GREEN_SELECTED = new Color(150, 210, 130);
    
    private Color currentBorderColor = Color.BLACK;
    private Color currentFillColor = new Color(0, 0, 0, 0);
    private JButton btnBorderColor, btnFillColor;
    
    public DrawingApp() {
        setTitle("Profesionalna Aplikacija za Crtanje");
        setSize(1300, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initComponents();
        setupKeyboardShortcuts();
    }
    
    private void initComponents() {
        JPanel topToolbar = createTopToolbar();
        
        drawingPanel = new PnlDrawing();
        drawingPanel.setBackground(new Color(248, 248, 255));
        drawingPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        drawingPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (currentTool.equals("Selektuj")) {
                    drawingPanel.selectShapeAt(e.getX(), e.getY());
                    updateShapeInfo();
                } else {
                    handleCanvasClick(e.getX(), e.getY());
                }
            }
        });
        
        drawingPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                drawingPanel.handleMousePress(e.getX(), e.getY());
            }
            public void mouseReleased(MouseEvent e) {
                drawingPanel.handleMouseRelease();
            }
        });
        
        drawingPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                drawingPanel.handleMouseDrag(e.getX(), e.getY());
                drawingPanel.repaint();
                updateShapeInfo();
            }
        });
        
        toolPanel = createModernToolPanel();
        JPanel statusBar = createStatusBar();
        
        add(topToolbar, BorderLayout.NORTH);
        add(toolPanel, BorderLayout.WEST);
        add(drawingPanel, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private JPanel createTopToolbar() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 240, 245));
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        colorPanel.setBackground(new Color(240, 240, 245));
        colorPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            "Boje", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 11)
        ));
        
        btnBorderColor = new JButton("Ivica");
        btnBorderColor.setBackground(currentBorderColor);
        btnBorderColor.setForeground(isColorDark(currentBorderColor) ? Color.WHITE : Color.BLACK);
        btnBorderColor.setPreferredSize(new Dimension(80, 30));
        btnBorderColor.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Izaberi boju ivice", currentBorderColor);
            if (newColor != null) {
                currentBorderColor = newColor;
                btnBorderColor.setBackground(currentBorderColor);
                btnBorderColor.setForeground(isColorDark(currentBorderColor) ? Color.WHITE : Color.BLACK);
                
                Shape selected = drawingPanel.getSelectedShape();
                if (selected != null) {
                    selected.setBorderColor(currentBorderColor);
                    drawingPanel.repaint();
                }
            }
        });
        
        // ПОПУНИ уместо ИСПУНА
        btnFillColor = new JButton("Popuni");
        btnFillColor.setBackground(currentFillColor);
        btnFillColor.setPreferredSize(new Dimension(80, 30));
        btnFillColor.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Izaberi boju popune", currentFillColor);
            if (newColor != null) {
                currentFillColor = newColor;
                btnFillColor.setBackground(currentFillColor);
                
                Shape selected = drawingPanel.getSelectedShape();
                if (selected != null) {
                    selected.setFillColor(currentFillColor);
                    drawingPanel.repaint();
                }
            }
        });
        
        JPanel quickColors = new JPanel(new GridLayout(1, 10, 3, 0));
        quickColors.setBackground(new Color(240, 240, 245));
        Color[] colors = {
            Color.BLACK, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
            Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.PINK, Color.GRAY
        };
        
        for (Color c : colors) {
            JButton colorBtn = new JButton();
            colorBtn.setBackground(c);
            colorBtn.setPreferredSize(new Dimension(25, 25));
            colorBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            colorBtn.addActionListener(e -> {
                currentBorderColor = c;
                btnBorderColor.setBackground(currentBorderColor);
                btnBorderColor.setForeground(isColorDark(currentBorderColor) ? Color.WHITE : Color.BLACK);
                
                Shape selected = drawingPanel.getSelectedShape();
                if (selected != null) {
                    selected.setBorderColor(currentBorderColor);
                    drawingPanel.repaint();
                }
            });
            quickColors.add(colorBtn);
        }
        
        colorPanel.add(btnBorderColor);
        colorPanel.add(btnFillColor);
        colorPanel.add(new JLabel("Brze:"));
        colorPanel.add(quickColors);
        
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        selectionPanel.setBackground(GREEN_COLOR);
        selectionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GREEN_DARK, 1),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        
        JLabel lblSelectionInfo = new JLabel("⚡ Prevlači za pomeranje | Prevlači ivice za promenu veličine");
        lblSelectionInfo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSelectionInfo.setForeground(new Color(40, 80, 30));
        selectionPanel.add(lblSelectionInfo);
        
        topPanel.add(colorPanel, BorderLayout.WEST);
        topPanel.add(selectionPanel, BorderLayout.EAST);
        
        return topPanel;
    }
    
    private boolean isColorDark(Color color) {
        double brightness = 0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue();
        return brightness < 128;
    }
    
    private JPanel createModernToolPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(GREEN_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, GREEN_DARK),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setPreferredSize(new Dimension(200, 0));
        
        JLabel title = new JLabel("ALATI", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(40, 80, 30));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(title);
        
        String[] tools = {"✏️ Tačka", "📏 Linija", "⬛ Pravougaonik", "⚪ Krug", "⭕ Krug sa rupom", "🖱️ Selektuj"};
        String[] toolCommands = {"Tačka", "Linija", "Pravougaonik", "Krug", "Krug sa rupom", "Selektuj"};
        toolButtons = new JButton[tools.length];
        
        for (int i = 0; i < tools.length; i++) {
            JButton btn = createGreenButton(tools[i]);
            final String command = toolCommands[i];
            btn.addActionListener(e -> {
                currentTool = command;
                linePoints.clear();
                updateToolSelection(command);
                lblStatus.setText("🔧 Aktivni alat: " + command);
                drawingPanel.setSelectionMode(command.equals("Selektuj"));
            });
            btn.setToolTipText(getToolTipText(command));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(160, 40));
            panel.add(btn);
            panel.add(Box.createRigidArea(new Dimension(0, 8)));
            toolButtons[i] = btn;
        }
        
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JSeparator separator = new JSeparator();
        separator.setForeground(GREEN_DARK);
        separator.setMaximumSize(new Dimension(160, 2));
        panel.add(separator);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        JButton btnModify = createGreenButton("✏️ Izmeni selektovano");
        JButton btnDelete = createGreenButton("🗑️ Obriši selektovano");
        JButton btnClear = createGreenButton("🧹 Obriši sve");
        
        btnModify.addActionListener(e -> modifySelectedShape());
        btnDelete.addActionListener(e -> deleteSelectedShape());
        btnClear.addActionListener(e -> clearAllShapes());
        
        btnModify.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDelete.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnClear.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        btnModify.setMaximumSize(new Dimension(160, 40));
        btnDelete.setMaximumSize(new Dimension(160, 40));
        btnClear.setMaximumSize(new Dimension(160, 40));
        
        panel.add(btnModify);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(btnDelete);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(btnClear);
        
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JButton createGreenButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(new Color(40, 80, 30));
        button.setBackground(GREEN_COLOR);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GREEN_DARK, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(GREEN_DARK);
                button.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 100), 1));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(GREEN_COLOR);
                button.setBorder(BorderFactory.createLineBorder(GREEN_DARK, 1));
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
        
        lblStatus = new JLabel("🔧 Aktivni alat: Tačka");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        lblShapeInfo = new JLabel("ℹ️ Nijedan oblik nije selektovan");
        lblShapeInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblShapeInfo.setForeground(new Color(100, 100, 100));
        
        JLabel lblCoordinates = new JLabel();
        lblCoordinates.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
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
            btn.setBackground(GREEN_COLOR);
            btn.setBorder(BorderFactory.createLineBorder(GREEN_DARK, 1));
        }
        for (JButton btn : toolButtons) {
            if (btn.getText().contains(selectedTool)) {
                btn.setBackground(GREEN_SELECTED);
                btn.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 200), 2));
            }
        }
    }
    
    private String getToolTipText(String tool) {
        switch(tool) {
            case "Tačka": return "Kliknite bilo gde da nacrtate tačku";
            case "Linija": return "Kliknite dva puta da nacrtate liniju";
            case "Pravougaonik": return "Kliknite, zatim unesite širinu i visinu";
            case "Krug": return "Kliknite, zatim unesite poluprečnik";
            case "Krug sa rupom": return "Kliknite, zatim unesite spoljašnji i unutrašnji poluprečnik";
            case "Selektuj": return "Kliknite na oblik da ga selektujete | Prevlačite za pomeranje | Prevlačite ivice za promenu veličine";
            default: return "";
        }
    }
    
    private void handleCanvasClick(int x, int y) {
        switch (currentTool) {
            case "Tačka":
                drawingPanel.addShape(new PointShape(x, y, currentBorderColor, currentFillColor));
                break;
            case "Linija":
                linePoints.add(new Point(x, y));
                if (linePoints.size() == 2) {
                    Point p1 = linePoints.get(0);
                    Point p2 = linePoints.get(1);
                    drawingPanel.addShape(new LineShape(p1.x, p1.y, p2.x, p2.y, currentBorderColor, currentFillColor));
                    linePoints.clear();
                } else {
                    lblStatus.setText("🔧 Linija: Kliknite drugu tačku");
                }
                break;
            case "Pravougaonik":
                showRectangleDialog(x, y);
                break;
            case "Krug":
                showCircleDialog(x, y);
                break;
            case "Krug sa rupom":
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
            lblShapeInfo.setText("✅ Selektovano: " + info);
            
            currentBorderColor = selected.getBorderColor();
            currentFillColor = selected.getFillColor();
            btnBorderColor.setBackground(currentBorderColor);
            btnBorderColor.setForeground(isColorDark(currentBorderColor) ? Color.WHITE : Color.BLACK);
            btnFillColor.setBackground(currentFillColor);
        } else {
            lblShapeInfo.setText("ℹ️ Nijedan oblik nije selektovan");
        }
    }
    
    private void showRectangleDialog(int x, int y) {
        JTextField wField = new JTextField(10);
        JTextField hField = new JTextField(10);
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Širina:"));
        panel.add(wField);
        panel.add(new JLabel("Visina:"));
        panel.add(hField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Dimenzije Pravougaonika", 
                      JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int w = Integer.parseInt(wField.getText());
                int h = Integer.parseInt(hField.getText());
                if (w > 0 && h > 0) {
                    drawingPanel.addShape(new RectangleShape(x, y, w, h, currentBorderColor, currentFillColor));
                } else {
                    JOptionPane.showMessageDialog(this, "Širina i visina moraju biti pozitivne!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Neispravan broj!");
            }
        }
    }
    
    private void showCircleDialog(int x, int y) {
        JTextField rField = new JTextField(10);
        int result = JOptionPane.showConfirmDialog(this, rField, "Poluprečnik Kruga", 
                      JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int r = Integer.parseInt(rField.getText());
                if (r > 0) {
                    drawingPanel.addShape(new CircleShape(x, y, r, currentBorderColor, currentFillColor));
                } else {
                    JOptionPane.showMessageDialog(this, "Poluprečnik mora biti pozitivan!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Neispravan broj!");
            }
        }
    }
    
    private void showHollowCircleDialog(int x, int y) {
        JTextField outerField = new JTextField(10);
        JTextField innerField = new JTextField(10);
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Spoljašnji poluprečnik:"));
        panel.add(outerField);
        panel.add(new JLabel("Unutrašnji poluprečnik:"));
        panel.add(innerField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Krug sa rupom", 
                      JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int outer = Integer.parseInt(outerField.getText());
                int inner = Integer.parseInt(innerField.getText());
                if (outer > 0 && inner > 0 && inner < outer) {
                    drawingPanel.addShape(new HollowCircleShape(x, y, outer, inner, currentBorderColor, currentFillColor));
                } else {
                    JOptionPane.showMessageDialog(this, "Neispravni poluprečnici! Unutrašnji mora biti manji od spoljašnjeg i veći od 0");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Neispravan broj!");
            }
        }
    }
    
    private void modifySelectedShape() {
        Shape selected = drawingPanel.getSelectedShape();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Nijedan oblik nije selektovan!\nMolimo vas da prvo selektujete oblik.", 
                                        "Upozorenje", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new ModifyDialog(this, selected, drawingPanel).setVisible(true);
        drawingPanel.repaint();
        updateShapeInfo();
    }
    
    private void deleteSelectedShape() {
        Shape selected = drawingPanel.getSelectedShape();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Nijedan oblik nije selektovan!\nMolimo vas da prvo selektujete oblik.", 
                                        "Upozorenje", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, 
                      "Da li ste sigurni da želite da obrišete selektovani oblik?", 
                      "Potvrda brisanja", JOptionPane.YES_NO_OPTION, 
                      JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            drawingPanel.deleteSelectedShape();
            drawingPanel.repaint();
            updateShapeInfo();
            lblStatus.setText("✅ Oblik obrisan");
        }
    }
    
    private void clearAllShapes() {
        int confirm = JOptionPane.showConfirmDialog(this, 
                      "Da li ste sigurni da želite da obrišete SVE oblike?", 
                      "Potvrda brisanja", JOptionPane.YES_NO_OPTION, 
                      JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            drawingPanel.clearAllShapes();
            drawingPanel.repaint();
            updateShapeInfo();
            lblStatus.setText("🧹 Svi oblici obrisani");
        }
    }
}