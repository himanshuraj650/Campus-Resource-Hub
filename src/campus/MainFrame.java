package campus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private final DataStore store;
    private final User currentUser;
    
    private final JPanel contentArea = new JPanel(new BorderLayout());
    private final Map<String, JPanel> panels = new LinkedHashMap<>();
    private final JPanel sidebar = new JPanel();
    private String currentNav = null;

    public MainFrame(DataStore store, User currentUser) {
        this.store = store;
        this.currentUser = currentUser;
        setTitle("Cgc University Hub | Masterpiece Studio");
        setSize(1350, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);
        add(contentArea, BorderLayout.CENTER);
        
        contentArea.setOpaque(false);
        rebuildTabs();
    }

    private JPanel buildSidebar() {
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Theme.SIDEBAR);
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setMinimumSize(new Dimension(280, 0));
        sidebar.setBorder(new EmptyBorder(32, 0, 32, 0));
        return sidebar;
    }

    public void navigateTo(String name) {
        this.currentNav = name;
        rebuildSidebarButtons();
        contentArea.removeAll();
        
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(32, 48, 16, 48));
        
        JLabel heading = new JLabel(name);
        heading.setFont(Theme.FONT_TITLE);
        heading.setForeground(Theme.TEXT);
        header.add(heading, BorderLayout.WEST);
        
        JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        userInfo.setOpaque(false);
        JLabel userLabel = new JLabel("<html><div style='text-align: right;'><b>" + currentUser.getFullName() + "</b><br><font color='#6B7280'>" + currentUser.getCampusName() + "</font></div></html>");
        userLabel.setFont(Theme.FONT_BODY);
        userInfo.add(userLabel);
        header.add(userInfo, BorderLayout.EAST);
        
        contentArea.add(header, BorderLayout.NORTH);
        
        JPanel p = panels.get(name);
        if (p != null) {
            p.setOpaque(false);
            
            if (name.equals("Direct Messages")) {
                JPanel wrap = new JPanel(new BorderLayout());
                wrap.setOpaque(false);
                wrap.setBorder(new EmptyBorder(0, 32, 24, 32));
                wrap.add(p, BorderLayout.CENTER);
                contentArea.add(wrap, BorderLayout.CENTER);
            } else {
                JPanel wrap = new JPanel(new BorderLayout());
                wrap.setOpaque(false);
                wrap.setBorder(new EmptyBorder(0, 32, 48, 32));
                wrap.add(p, BorderLayout.CENTER);
                
                JScrollPane scroll = new JScrollPane(wrap);
                scroll.setBorder(null);
                scroll.setOpaque(false);
                scroll.getViewport().setOpaque(false);
                scroll.getVerticalScrollBar().setUnitIncrement(24);
                scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                contentArea.add(scroll, BorderLayout.CENTER);
            }
        }
        
        contentArea.revalidate();
        contentArea.repaint();
    }

    private void rebuildSidebarButtons() {
        sidebar.removeAll();
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 32, 0));
        logoPanel.setOpaque(false);
        JLabel logo = new JLabel("🚀 CGC HUB");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logo.setForeground(Color.WHITE);
        logoPanel.add(logo);
        sidebar.add(logoPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 48)));

        // Core Sidebar Sections
        String[][] menu = {
            {"Dashboard", "📊 "}, {"Explore Hub", "🌐 "}, {"Direct Messages", "💬 "},
            {"Skill Share", "🎯 "}, {"Study Groups", "👥 "}, {"Lost & Found", "🔍 "},
            {"My Listings", "📦 "}, {"Borrowed by Me", "🔄 "}, {"Incoming Requests", "📥 "},
            {"My Requests", "📤 "}, {"Add Resource", "➕ "}
        };
        
        for (String[] m : menu) {
            final String name = m[0];
            JButton btn = Theme.navButton(m[1] + name, name.equals(currentNav));
            btn.addActionListener(e -> navigateTo(name));
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        }
        
        sidebar.add(Box.createVerticalGlue());
        JButton logout = Theme.navButton("🔴 Sign Out", false);
        logout.setForeground(new Color(252, 165, 165));
        logout.addActionListener(e -> {
            new LoginFrame(store).setVisible(true);
            dispose();
        });
        sidebar.add(logout);
        sidebar.revalidate();
        sidebar.repaint();
    }

    private String pendingChatTarget = null;

    public void openChatWith(String target) {
        this.pendingChatTarget = target;
        rebuildTabs();
        navigateTo("Direct Messages");
        
        // After navigation, find the panel and select the contact
        JPanel p = panels.get("Direct Messages");
        if (p instanceof MessagesPanel mp) {
            mp.selectContact(target);
        }
    }

    public void rebuildTabs() {
        String lastNav = (currentNav == null) ? "Dashboard" : currentNav;
        panels.clear();
        panels.put("Dashboard", new DashboardPanel(store, currentUser, this));
        panels.put("Explore Hub", new ExplorePanel(store, currentUser, this));
        
        MessagesPanel mp = new MessagesPanel(store, currentUser, this);
        panels.put("Direct Messages", mp);
        
        ExplorePanel skillHub = new ExplorePanel(store, currentUser, this);
        skillHub.setCategory(Resource.Category.SKILL_SHARE);
        panels.put("Skill Share", skillHub);
        
        ExplorePanel studyHub = new ExplorePanel(store, currentUser, this);
        studyHub.setCategory(Resource.Category.STUDY_GROUP);
        panels.put("Study Groups", studyHub);
        
        panels.put("Lost & Found", new BrowsePanel(store, currentUser, this));
        panels.put("My Listings", new MyListingsPanel(store, currentUser, this));
        panels.put("Borrowed by Me", new BorrowedPanel(store, currentUser, this));
        panels.put("Incoming Requests", new IncomingRequestsPanel(store, currentUser, this));
        panels.put("My Requests", new MyRequestsPanel(store, currentUser));
        panels.put("Add Resource", new AddResourcePanel(store, currentUser, this));
        
        navigateTo(lastNav);
    }
}
