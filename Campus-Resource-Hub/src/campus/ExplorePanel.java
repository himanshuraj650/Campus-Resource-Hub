package campus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ExplorePanel extends JPanel {
    protected final DataStore store;
    protected final User currentUser;
    protected final MainFrame parent;
    private final JPanel gridPanel = new JPanel();
    private final JTextField searchField = Theme.styledTextField(20);
    private final JComboBox<String> categoryFilter = new JComboBox<>();
    private final JComboBox<String> courseFilter = new JComboBox<>();
    private Resource.Category forceCategory = null;

    public ExplorePanel(DataStore store, User currentUser, MainFrame parent) {
        this.store = store;
        this.currentUser = currentUser;
        this.parent = parent;
        setLayout(new BorderLayout());
        setOpaque(false);

        // Filter Header
        JPanel headerLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 20));
        headerLine.setOpaque(false);
        headerLine.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        headerLine.add(new JLabel("🔍"));
        headerLine.add(searchField);
        
        categoryFilter.addItem("All Categories");
        for (Resource.Category c : Resource.Category.values()) categoryFilter.addItem(c.name());
        headerLine.add(categoryFilter);

        courseFilter.addItem("All Courses");
        courseFilter.addItem("CS101"); courseFilter.addItem("MATH202"); courseFilter.addItem("MECH300");
        headerLine.add(courseFilter);
        
        add(headerLine, BorderLayout.NORTH);

        // High-density Grid
        gridPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 32, 40));
        gridPanel.setOpaque(false);
        add(gridPanel, BorderLayout.CENTER);

        searchField.addCaretListener(e -> reload());
        categoryFilter.addActionListener(e -> reload());
        courseFilter.addActionListener(e -> reload());
        reload();
    }

    public void setCategory(Resource.Category cat) {
        this.forceCategory = cat;
        categoryFilter.setSelectedItem(cat.name());
        categoryFilter.setEnabled(false);
        reload();
    }

    protected void reload() {
        gridPanel.removeAll();
        String q = searchField.getText().trim().toLowerCase();
        String cat = (String) categoryFilter.getSelectedItem();
        String crs = (String) courseFilter.getSelectedItem();

        List<Resource> items = store.listResources().stream()
                .filter(r -> forceCategory == null || r.getCategory() == forceCategory)
                .filter(r -> q.isEmpty() || r.getTitle().toLowerCase().contains(q) || r.getDescription().toLowerCase().contains(q))
                .filter(r -> forceCategory != null || "All Categories".equals(cat) || r.getCategory().name().equals(cat))
                .filter(r -> "All Courses".equals(crs) || crs.equals(r.getCourseCode()))
                .collect(Collectors.toList());

        if (items.isEmpty()) {
            gridPanel.add(new JLabel("No items found."));
        } else {
            for (Resource r : items) gridPanel.add(buildCard(r));
        }
        
        int perRow = 3;
        int numRows = (int) Math.ceil(items.size() / (double)perRow);
        gridPanel.setPreferredSize(new Dimension(1040, Math.max(750, numRows * 400)));
        gridPanel.revalidate(); gridPanel.repaint();
    }

    private JPanel buildCard(Resource r) {
        boolean isOwner = r.getOwnerUsername().equals(currentUser.getUsername());
        
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);
        content.setPreferredSize(new Dimension(320, 310)); // Increased height to prevent clipping

        JPanel badges = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badges.setOpaque(false);
        badges.add(Theme.statusBadge(r.getCategory().name(), Theme.ACCENT));
        if (r.getCourseCode() != null) {
            JLabel c = new JLabel("  •  " + r.getCourseCode());
            c.setFont(Theme.FONT_BADGE);
            c.setForeground(Theme.MUTED);
            badges.add(c);
        }
        content.add(badges, BorderLayout.NORTH);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        JLabel title = new JLabel(r.getTitle());
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        info.add(title);
        JLabel author = new JLabel("by " + (isOwner ? "You" : store.getUser(r.getOwnerUsername()).getFullName()));
        author.setFont(Theme.FONT_SMALL);
        author.setForeground(Theme.MUTED);
        info.add(author);
        info.add(Box.createRigidArea(new Dimension(0, 10)));
        JTextArea desc = new JTextArea(r.getDescription());
        desc.setFont(Theme.FONT_BODY);
        desc.setLineWrap(true); desc.setWrapStyleWord(true);
        desc.setEditable(false); desc.setOpaque(false);
        desc.setRows(3);
        info.add(desc);
        content.add(info, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        Color sc = r.getStatus() == Resource.Status.AVAILABLE ? Theme.SUCCESS : Theme.WARNING;
        footer.add(Theme.statusBadge(r.getStatus().name(), sc), BorderLayout.WEST);
        
        JPanel rowBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rowBtns.setOpaque(false);
        if (isOwner) {
            JButton manage = Theme.secondaryButton("Manage ⚙️");
            manage.setFont(Theme.FONT_SMALL);
            manage.addActionListener(e -> parent.navigateTo("My Listings"));
            rowBtns.add(manage);
        } else if (r.getStatus() == Resource.Status.AVAILABLE) {
            JButton chat = Theme.secondaryButton("💬");
            chat.addActionListener(e -> {
                store.sendMessage(currentUser.getUsername(), r.getOwnerUsername(), "Interested in " + r.getTitle());
                parent.openChatWith(r.getOwnerUsername());
            });
            rowBtns.add(chat);
            JButton borrow = Theme.primaryButton("Borrow");
            borrow.setFont(Theme.FONT_SMALL);
            borrow.addActionListener(e -> {
                store.addRequest(new BorrowRequest(r.getId(), currentUser.getUsername(), "Auto-request", 7));
                JOptionPane.showMessageDialog(this, "Borrow request sent! Track its status in 'My Requests'.");
                parent.rebuildTabs();
                parent.navigateTo("My Requests");
            });
            rowBtns.add(borrow);
        }
        footer.add(rowBtns, BorderLayout.EAST);
        content.add(footer, BorderLayout.SOUTH);
        return Theme.card(content);
    }
}
