package campus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {
    private final DataStore store;
    private final User currentUser;
    private final MainFrame parent;

    public DashboardPanel(DataStore store, User currentUser, MainFrame parent) {
        this.store = store;
        this.currentUser = currentUser;
        this.parent = parent;
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 32, 0));

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setOpaque(false);

        // Welcome Banner
        main.add(buildBanner());
        main.add(Box.createRigidArea(new Dimension(0, 32)));

        // Stats Row
        main.add(buildStatsRow());
        main.add(Box.createRigidArea(new Dimension(0, 32)));

        // Middle Content Area (Two columns logic)
        JPanel mid = new JPanel(new GridLayout(1, 2, 32, 0));
        mid.setOpaque(false);
        
        // Left Column: News Feed
        mid.add(buildNewsFeed());
        
        // Right Column: Trending Items
        mid.add(buildTrendingItems());
        
        main.add(mid);
        main.add(Box.createRigidArea(new Dimension(0, 32)));

        // Bottom: Recent Notifications
        main.add(buildRecentNotifications());
        
        add(main, BorderLayout.NORTH);
    }

    private JPanel buildNewsFeed() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel title = new JLabel("Campus News & Alerts");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT);
        p.add(title, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);
        
        String[][] news = {
            {"📢 Library Hours", "Extended until 2 AM for finals week."},
            {"🛠️ MakerSpace Workshop", "3D Printing basics this Friday @ 4PM."},
            {"🤝 New Club Partner", "Robotics Club joined the lending circle!"}
        };

        for (String[] n : news) {
            JPanel card = new JPanel(new BorderLayout(12, 4));
            card.setOpaque(true);
            card.setBackground(Color.WHITE);
            card.setBorder(new EmptyBorder(12, 16, 12, 16));
            JLabel t = new JLabel(n[0]); t.setFont(Theme.FONT_HEADING);
            JLabel d = new JLabel(n[1]); d.setFont(Theme.FONT_BODY); d.setForeground(Theme.MUTED);
            card.add(t, BorderLayout.NORTH);
            card.add(d, BorderLayout.CENTER);
            list.add(card);
            list.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        p.add(Theme.card(list), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildTrendingItems() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel title = new JLabel("Trending Today");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT);
        p.add(title, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);

        List<Resource> items = store.listResources();
        int count = 0;
        for (Resource r : items) {
            if (count++ >= 3) break;
            JPanel item = new JPanel(new BorderLayout(8, 0));
            item.setOpaque(false);
            item.add(new JLabel("⭐️"), BorderLayout.WEST);
            item.add(new JLabel(r.getTitle() + " (" + r.getCourseCode() + ")"), BorderLayout.CENTER);
            list.add(item);
            list.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        p.add(Theme.card(list), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildBanner() {
        JPanel banner = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, Theme.PRIMARY, getWidth(), 0, Theme.ACCENT);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.dispose();
            }
        };
        banner.setOpaque(false);
        banner.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        JLabel welcome = new JLabel("Masterpiece Studio @ " + currentUser.getCampusName());
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcome.setForeground(Color.WHITE);
        banner.add(welcome, BorderLayout.NORTH);
        
        JLabel sub = new JLabel("Your verified startup circle is growing. 🚀");
        sub.setFont(Theme.FONT_HEADING);
        sub.setForeground(new Color(224, 231, 255));
        banner.add(sub, BorderLayout.CENTER);

        return banner;
    }

    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
        row.setOpaque(false);
        row.add(statCard("Active Items", String.valueOf(store.listResourcesByOwner(currentUser.getUsername()).size()), Theme.PRIMARY));
        row.add(statCard("Lending Reputation", "98%", Theme.SUCCESS));
        row.add(statCard("Verified Friends", "24", Theme.ACCENT));
        return row;
    }

    private JPanel statCard(String title, String value, Color color) {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setPreferredSize(new Dimension(220, 100));
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 28));
        val.setForeground(color);
        content.add(val);
        JLabel lbl = new JLabel(title);
        lbl.setFont(Theme.FONT_SMALL);
        lbl.setForeground(Theme.MUTED);
        content.add(lbl);
        return Theme.card(content);
    }

    private JPanel buildRecentNotifications() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel title = new JLabel("Timeline Activity");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT);
        title.setBorder(new EmptyBorder(0, 0, 16, 0));
        p.add(title, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);

        List<Notification> notes = store.listNotifications(currentUser.getUsername());
        if (notes.isEmpty()) {
            list.add(new JLabel("No recent activity. Everything is quiet."));
        } else {
            for (int i = 0; i < Math.min(notes.size(), 3); i++) {
                Notification n = notes.get(i);
                JPanel item = new JPanel(new BorderLayout(16, 0));
                item.setOpaque(false);
                item.setBorder(new EmptyBorder(12, 0, 12, 0));
                JLabel icon = new JLabel("CHAT".equals(n.type) ? "💬" : "🔥");
                icon.setFont(new Font("Segoe UI", Font.PLAIN, 24));
                item.add(icon, BorderLayout.WEST);
                JPanel text = new JPanel(new BorderLayout());
                text.setOpaque(false);
                JLabel t = new JLabel(n.title); t.setFont(Theme.FONT_HEADING);
                text.add(t, BorderLayout.NORTH);
                JLabel m = new JLabel(n.message); m.setFont(Theme.FONT_BODY); m.setForeground(Theme.MUTED);
                text.add(m, BorderLayout.CENTER);
                item.add(text, BorderLayout.CENTER);
                list.add(item);
                if (i < 2) list.add(new JSeparator());
            }
        }
        p.add(Theme.card(list), BorderLayout.CENTER);
        return p;
    }
}
