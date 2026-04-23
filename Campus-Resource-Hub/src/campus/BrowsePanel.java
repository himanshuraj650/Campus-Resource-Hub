package campus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class BrowsePanel extends JPanel {
    private final DataStore store;
    private final User currentUser;
    private final MainFrame parent;
    private final JPanel gridPanel = new JPanel();

    public BrowsePanel(DataStore store, User currentUser, MainFrame parent) {
        this.store = store;
        this.currentUser = currentUser;
        this.parent = parent;
        setLayout(new BorderLayout());
        setOpaque(false);

        gridPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 32, 32));
        gridPanel.setOpaque(false);
        add(gridPanel, BorderLayout.CENTER);

        reload();
    }

    private void reload() {
        gridPanel.removeAll();
        List<Resource> items = store.listResources();
        if (items.isEmpty()) {
            gridPanel.add(new JLabel("No items found."));
        } else {
            for (Resource r : items) gridPanel.add(buildCard(r));
        }
        
        int rows = (int) Math.ceil(items.size() / 3.0);
        gridPanel.setPreferredSize(new Dimension(1000, Math.max(600, rows * 350)));
        gridPanel.revalidate(); gridPanel.repaint();
    }

    private JPanel buildCard(Resource r) {
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);
        content.setPreferredSize(new Dimension(320, 200));

        JLabel title = new JLabel(r.getTitle());
        title.setFont(Theme.FONT_HEADING);
        content.add(title, BorderLayout.NORTH);
        
        content.add(new JLabel("Found at: " + r.getLocation()), BorderLayout.CENTER);
        
        JButton claim = Theme.primaryButton("Claim Item");
        claim.addActionListener(e -> {
            String msg = JOptionPane.showInputDialog(this, "Provide proof of ownership (e.g. description or serial no):", "Claim " + r.getTitle(), JOptionPane.PLAIN_MESSAGE);
            if (msg != null && !msg.trim().isEmpty()) {
                store.sendMessage(currentUser.getUsername(), r.getOwnerUsername(), "[LOST & FOUND CLAIM] " + r.getTitle() + "\nProof: " + msg);
                JOptionPane.showMessageDialog(this, "Claim request sent to reporter! They will contact you via Direct Messages.");
                parent.openChatWith(r.getOwnerUsername());
            }
        });
        content.add(claim, BorderLayout.SOUTH);

        return Theme.card(content);
    }
}
