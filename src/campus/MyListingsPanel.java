package campus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class MyListingsPanel extends JPanel {
    private final DataStore store;
    private final User currentUser;
    private final MainFrame parent;
    private final JPanel grid = new JPanel();

    public MyListingsPanel(DataStore store, User currentUser, MainFrame parent) {
        this.store = store;
        this.currentUser = currentUser;
        this.parent = parent;
        setLayout(new BorderLayout());
        setOpaque(false);

        grid.setLayout(new FlowLayout(FlowLayout.LEFT, 24, 24));
        grid.setOpaque(false);
        add(grid, BorderLayout.CENTER);
        
        reload();
    }

    private void reload() {
        grid.removeAll();
        List<Resource> items = store.listResourcesByOwner(currentUser.getUsername());
        if (items.isEmpty()) {
            JLabel empty = new JLabel("You haven't listed any items yet.");
            empty.setFont(Theme.FONT_HEADING);
            empty.setForeground(Theme.MUTED);
            empty.setBorder(new EmptyBorder(40, 40, 40, 40));
            grid.add(empty);
        } else {
            for (Resource r : items) grid.add(buildCard(r));
        }
        
        int rows = (int) Math.ceil(items.size() / 3.0);
        grid.setPreferredSize(new Dimension(900, Math.max(400, rows * 300)));
        grid.revalidate(); grid.repaint();
    }

    private JPanel buildCard(Resource r) {
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);
        content.setPreferredSize(new Dimension(280, 240));

        // Top Status
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        top.setOpaque(false);
        top.add(Theme.statusBadge(r.getCategory().name().replace("_", " "), Theme.ACCENT));
        Color sc = r.getStatus() == Resource.Status.AVAILABLE ? Theme.SUCCESS
                : r.getStatus() == Resource.Status.BORROWED ? Theme.WARNING : Theme.DANGER;
        top.add(Box.createRigidArea(new Dimension(8, 0)));
        top.add(Theme.statusBadge(r.getStatus().name(), sc));
        content.add(top, BorderLayout.NORTH);

        // Center Details
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        
        JLabel title = new JLabel(r.getTitle());
        title.setFont(Theme.FONT_HEADING);
        title.setForeground(Theme.TEXT);
        center.add(title);
        
        if (r.getStatus() == Resource.Status.BORROWED) {
            User b = store.getUser(r.getBorrowerUsername());
            JLabel borrower = new JLabel("Borrowed by " + (b != null ? b.getFullName() : r.getBorrowerUsername()));
            borrower.setFont(Theme.FONT_SMALL);
            borrower.setForeground(Theme.PRIMARY);
            center.add(Box.createRigidArea(new Dimension(0, 4)));
            center.add(borrower);
        }

        JTextArea desc = new JTextArea(r.getDescription());
        desc.setFont(Theme.FONT_BODY);
        desc.setLineWrap(true); desc.setWrapStyleWord(true);
        desc.setEditable(false); desc.setOpaque(false);
        desc.setRows(3);
        center.add(Box.createRigidArea(new Dimension(0, 8)));
        center.add(desc);
        
        content.add(center, BorderLayout.CENTER);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        if (r.getStatus() == Resource.Status.BORROWED) {
            JButton ret = Theme.primaryButton("Mark Returned");
            ret.setFont(Theme.FONT_SMALL);
            ret.addActionListener(e -> {
                r.setStatus(Resource.Status.AVAILABLE);
                r.setBorrowerUsername(null);
                r.setBorrowedAt(null);
                r.setDueAt(null);
                store.addResource(r);
                parent.rebuildTabs();
            });
            actions.add(ret);
        } else {
            JButton toggle = Theme.secondaryButton(r.getStatus() == Resource.Status.AVAILABLE ? "Hide" : "Show");
            toggle.setFont(Theme.FONT_SMALL);
            toggle.addActionListener(e -> {
                r.setStatus(r.getStatus() == Resource.Status.AVAILABLE ? Resource.Status.UNAVAILABLE : Resource.Status.AVAILABLE);
                store.addResource(r);
                parent.rebuildTabs();
            });
            actions.add(toggle);
        }

        JButton del = new JButton("Delete");
        del.setFont(Theme.FONT_SMALL);
        del.setForeground(Theme.DANGER);
        del.setContentAreaFilled(false);
        del.setBorderPainted(false);
        del.setCursor(new Cursor(Cursor.HAND_CURSOR));
        del.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this, "Really delete this item?", "Delete", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                store.deleteResource(r.getId());
                parent.rebuildTabs();
            }
        });
        actions.add(del);

        content.add(actions, BorderLayout.SOUTH);

        return Theme.card(content);
    }
}
