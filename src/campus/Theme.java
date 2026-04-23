package campus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public final class Theme {
    public static final Color ACCENT = new Color(79, 70, 229);
    public static final Color PRIMARY = new Color(99, 102, 241);
    public static final Color PRIMARY_DARK = new Color(67, 56, 202);
    public static final Color BG = new Color(243, 244, 246);
    public static final Color SIDEBAR = new Color(15, 23, 42);
    public static final Color SIDEBAR_HOVER = new Color(30, 41, 59);
    public static final Color SIDEBAR_TEXT = new Color(148, 163, 184); 
    public static final Color CARD = Color.WHITE;
    public static final Color TEXT = new Color(30, 41, 59);
    public static final Color MUTED = new Color(100, 116, 139);
    public static final Color SUCCESS = new Color(34, 197, 94);
    public static final Color WARNING = new Color(245, 158, 11);
    public static final Color DANGER = new Color(239, 68, 68);

    public static final Font FONT_TITLE = new Font("Inter", Font.BOLD, 28);
    public static final Font FONT_HEADING = new Font("Inter", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Inter", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Inter", Font.PLAIN, 12);
    public static final Font FONT_BADGE = new Font("Inter", Font.BOLD, 10);

    private Theme() {}

    public static JButton primaryButton(String text) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) g2.setColor(PRIMARY_DARK);
                else if (getModel().isRollover()) g2.setColor(PRIMARY);
                else g2.setColor(ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setForeground(Color.WHITE);
        b.setFont(FONT_HEADING);
        b.setBorder(new EmptyBorder(12, 24, 12, 24));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JButton secondaryButton(String text) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(248, 250, 252) : Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(226, 232, 240));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setForeground(TEXT);
        b.setFont(FONT_BODY);
        b.setBorder(new EmptyBorder(12, 24, 12, 24));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JButton navButton(String text, boolean active) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (active || getModel().isRollover()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(active ? PRIMARY : SIDEBAR_HOVER);
                    g2.fillRoundRect(12, 2, getWidth() - 24, getHeight() - 4, 12, 12);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setForeground(active ? Color.WHITE : SIDEBAR_TEXT);
        b.setFont(FONT_HEADING);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(new EmptyBorder(0, 32, 0, 0));
        b.setMaximumSize(new Dimension(280, 50));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JComponent qrCodeComponent(long id) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(PRIMARY);
                int size = 160;
                int cellSize = size / 8;
                int startX = (getWidth() - size) / 2;
                int startY = (getHeight() - size) / 2;
                java.util.Random rand = new java.util.Random(id + 1337);
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        if ((i < 2 && j < 2) || (i > 5 && j < 2) || (i < 2 && j > 5)) {
                            g2.fillRect(startX + i * cellSize, startY + j * cellSize, cellSize - 1, cellSize - 1);
                        } else if (rand.nextBoolean()) {
                            g2.fillRect(startX + i * cellSize, startY + j * cellSize, cellSize - 1, cellSize - 1);
                        }
                    }
                }
                g2.dispose();
            }
        };
    }

    public static JLabel statusBadge(String text, Color color) {
        JLabel l = new JLabel(text.toUpperCase()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setOpaque(false);
        l.setForeground(color);
        l.setBorder(new EmptyBorder(5, 14, 5, 14));
        l.setFont(FONT_BADGE);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        return l;
    }

    public static JPanel card(JComponent content) {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, 20, 20);
                g2.setColor(CARD);
                g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(24, 24, 24, 24));
        p.add(content);
        return p;
    }

    public static JTextField styledTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(FONT_BODY);
        tf.setBackground(Color.WHITE);
        tf.setCaretColor(PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(10, 16, 10, 16)
        ));
        return tf;
    }
}
