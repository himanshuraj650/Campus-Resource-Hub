package campus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class BorrowedPanel extends JPanel {
    private final DataStore store;
    private final User currentUser;
    private final MainFrame parent;
    private final JPanel grid = new JPanel();

    public BorrowedPanel(DataStore store, User currentUser, MainFrame parent) {
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
        List<Resource> items = store.listResourcesBorrowedBy(currentUser.getUsername());
        if (items.isEmpty()) {
            JLabel empty = new JLabel("You aren't borrowing anything right now.");
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
        content.setPreferredSize(new Dimension(280, 200));

        // Category & Status
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        top.setOpaque(false);
        top.add(Theme.statusBadge(r.getCategory().name().replace("_", " "), Theme.ACCENT));
        top.add(Box.createRigidArea(new Dimension(8, 0)));
        top.add(Theme.statusBadge("DUE: " + (r.getDueAt() != null ? r.getDueAt().toLocalDate() : "TBD"), Theme.WARNING));
        content.add(top, BorderLayout.NORTH);

        // Center Details
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        
        JLabel title = new JLabel(r.getTitle());
        title.setFont(Theme.FONT_HEADING);
        title.setForeground(Theme.TEXT);
        center.add(title);
        
        User owner = store.getUser(r.getOwnerUsername());
        JLabel ownerLabel = new JLabel("Lent by " + (owner != null ? owner.getFullName() : r.getOwnerUsername()));
        ownerLabel.setFont(Theme.FONT_BODY);
        ownerLabel.setForeground(Theme.MUTED);
        center.add(Box.createRigidArea(new Dimension(0, 4)));
        center.add(ownerLabel);
        
        JTextArea desc = new JTextArea(r.getDescription());
        desc.setFont(Theme.FONT_BODY);
        desc.setLineWrap(true); desc.setWrapStyleWord(true);
        desc.setEditable(false); desc.setOpaque(false);
        desc.setRows(2);
        center.add(Box.createRigidArea(new Dimension(0, 8)));
        center.add(desc);
        
        content.add(center, BorderLayout.CENTER);

        // Actions
        JPanel footerGrid = new JPanel(new BorderLayout());
        footerGrid.setOpaque(false);
        
        JPanel footerBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        footerBtns.setOpaque(false);

        JButton qr = Theme.secondaryButton("QR Code");
        qr.setFont(Theme.FONT_SMALL);
        qr.addActionListener(e -> {
            JDialog dialog = new JDialog((Frame)null, "Item Verification QR", true);
            dialog.setLayout(new BorderLayout());
            dialog.add(Theme.qrCodeComponent(r.getId()), BorderLayout.CENTER);
            JLabel info = new JLabel("<html><center>Scan this QR to verify<br><b>" + r.getTitle() + "</b></center></html>", SwingConstants.CENTER);
            info.setBorder(new EmptyBorder(16, 16, 16, 16));
            dialog.add(info, BorderLayout.SOUTH);
            dialog.setSize(300, 400);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });
        footerBtns.add(qr);
        
        JButton ret = Theme.primaryButton("Return");
        ret.setFont(Theme.FONT_SMALL);
        ret.addActionListener(e -> {
            r.setStatus(Resource.Status.AVAILABLE);
            r.setBorrowerUsername(null);
            r.setBorrowedAt(null);
            r.setDueAt(null);
            store.addResource(r);
            parent.rebuildTabs();
        });
        footerBtns.add(ret);

        footerGrid.add(footerBtns, BorderLayout.EAST);
        content.add(footerGrid, BorderLayout.SOUTH);

        return Theme.card(content);
    }
}
