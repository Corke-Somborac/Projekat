import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ModifyDialog extends JDialog {
    private Shape original, modified;
    private PnlDrawing drawingPanel;
    private JTextField[] fields;
    private Color borderColor, fillColor;
    private JButton btnBorder, btnFill, btnOk, btnCancel;
    
    public ModifyDialog(Frame owner, Shape shape, PnlDrawing drawingPanel) {
        super(owner, "Modify Shape", true);
        this.original = shape;
        this.modified = shape.copy();
        this.drawingPanel = drawingPanel;
        this.borderColor = shape.getBorderColor();
        this.fillColor = shape.getFillColor();
        
        setLayout(new BorderLayout());
        setSize(400, 350);
        setLocationRelativeTo(owner);
        
        String[] parts = shape.getProperties().split("\\|");
        fields = new JTextField[parts.length - 1];
        
        JPanel propPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JLabel lblType = new JLabel("Shape Type: " + parts[0]);
        lblType.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        propPanel.add(lblType, gbc);
        
        for (int i = 1; i < parts.length; i++) {
            String[] kv = parts[i].split(":");
            gbc.gridwidth = 1;
            gbc.gridx = 0;
            gbc.gridy = i;
            propPanel.add(new JLabel(kv[0] + ":"), gbc);
            gbc.gridx = 1;
            fields[i-1] = new JTextField(kv[1], 10);
            propPanel.add(fields[i-1], gbc);
        }
        
        JPanel colorPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        colorPanel.setBorder(BorderFactory.createTitledBorder("Colors"));
        
        btnBorder = new JButton("Border Color");
        btnBorder.setBackground(borderColor);
        btnBorder.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, "Border Color", borderColor);
            if (c != null) { borderColor = c; btnBorder.setBackground(borderColor); }
        });
        
        btnFill = new JButton("Fill Color");
        btnFill.setBackground(fillColor);
        btnFill.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, "Fill Color", fillColor);
            if (c != null) { fillColor = c; btnFill.setBackground(fillColor); }
        });
        
        colorPanel.add(btnBorder);
        colorPanel.add(btnFill);
        
        JPanel buttonPanel = new JPanel();
        btnOk = new JButton("OK");
        btnCancel = new JButton("Cancel");
        
        btnOk.addActionListener(e -> saveChanges());
        btnCancel.addActionListener(e -> dispose());
        
        buttonPanel.add(btnOk);
        buttonPanel.add(btnCancel);
        
        add(propPanel, BorderLayout.CENTER);
        add(colorPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void saveChanges() {
        try {
            String[] values = new String[fields.length];
            for (int i = 0; i < fields.length; i++)
                values[i] = fields[i].getText();
            
            modified.updateProperties(values);
            modified.setBorderColor(borderColor);
            modified.setFillColor(fillColor);
            
            drawingPanel.updateShape(original, modified);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input!");
        }
    }
}