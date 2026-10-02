package ui.components;

import ui.theme.AppTheme;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.net.URL;

/** Cropped clinic imagery with an intentional contrast overlay and descriptive copy. */
public final class AuthImagePanel extends JPanel {
    private final Image image;

    public AuthImagePanel(String resource, String title, String description) {
        super(new BorderLayout());
        URL imageUrl = AuthImagePanel.class.getResource(resource);
        image = imageUrl == null ? null : new ImageIcon(imageUrl).getImage();
        setBackground(AppTheme.PRIMARY_COLOR);
        JPanel copy = new JPanel(new BorderLayout(0, 12));
        copy.setOpaque(false);
        copy.setBorder(javax.swing.BorderFactory.createEmptyBorder(36, 34, 38, 30));
        JLabel heading = new JLabel("<html><div style='width:300px'>" + title + "</div></html>");
        heading.setFont(AppTheme.FONT_TITLE.deriveFont(30f));
        heading.setForeground(Color.WHITE);
        JLabel body = new JLabel("<html><div style='width:300px;line-height:1.5'>" + description + "</div></html>");
        body.setFont(AppTheme.FONT_BODY.deriveFont(16f));
        body.setForeground(new Color(242, 249, 250));
        copy.add(heading, BorderLayout.NORTH);
        copy.add(body, BorderLayout.CENTER);
        add(copy, BorderLayout.SOUTH);
        setOpaque(true);
    }

    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        if (image != null) {
            double scale = Math.max((double) getWidth() / image.getWidth(this),
                    (double) getHeight() / image.getHeight(this));
            int width = (int) Math.ceil(image.getWidth(this) * scale);
            int height = (int) Math.ceil(image.getHeight(this) * scale);
            g.drawImage(image, (getWidth() - width) / 2, (getHeight() - height) / 2, width, height, this);
            g.setColor(new Color(11, 49, 68, 145));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        g.dispose();
    }
}
