package campus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class MyRequestsPanel extends JPanel {
    private final DataStore store;
    private final User currentUser;
    private final JPanel grid = new JPanel();

    public MyRequestsPanel(DataStore store, User currentUser) {
        this.store = store;
        this.currentUser = currentUser;
        setLayout(new BorderLayout());
        setOpaque(false);

        grid.setLayout(new FlowLayout(FlowLayout.LEFT, 24, 24));
        grid.setOpaque(false);
        add(grid, BorderLayout.CENTER);
        
        reload();
    }

    private void reload() {
        grid.removeAll();
        List<BorrowRequest> reqs = store.listRequestsByUser(currentUser.getUsername());
        if (reqs.isEmpty()) {
            JLabel empty = new JLabel("You haven't sent any borrow requests yet.");
            empty.setFont(Theme.FONT_HEADING);
            empty.setForeground(Theme.MUTED);
            empty.setBorder(new EmptyBorder(40, 40, 40, 40));
            grid.add(empty);
        } else {
            for (BorrowRequest rq : reqs) grid.add(buildCard(rq));
        }
        
        int rows = (int) Math.ceil(reqs.size() / 3.0);
        grid.setPreferredSize(new Dimension(900, Math.max(400, rows * 300)));
        grid.revalidate(); grid.repaint();
    }

    private JPanel buildCard(BorrowRequest rq) {
        Resource r = store.getResource(rq.getResourceId());
        User owner = r != null ? store.getUser(r.getOwnerUsername()) : null;

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);
        content.setPreferredSize(new Dimension(280, 180));

        // Status
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        top.setOpaque(false);
        Color sc = rq.getStatus() == BorrowRequest.Status.PENDING ? Theme.WARNING 
                : rq.getStatus() == BorrowRequest.Status.APPROVED ? Theme.SUCCESS 
                : rq.getStatus() == BorrowRequest.Status.DECLINED ? Theme.DANGER : Theme.MUTED;
        top.add(Theme.statusBadge(rq.getStatus().name(), sc));
        content.add(top, BorderLayout.NORTH);

        // Details
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        
        JLabel title = new JLabel(r != null ? r.getTitle() : "Archive Item");
        title.setFont(Theme.FONT_HEADING);
        title.setForeground(Theme.TEXT);
        center.add(title);
        
        JLabel ownerLabel = new JLabel("Lent by " + (owner != null ? owner.getFullName() : (r != null ? r.getOwnerUsername() : "?")));
        ownerLabel.setFont(Theme.FONT_SMALL);
        ownerLabel.setForeground(Theme.MUTED);
        center.add(Box.createRigidArea(new Dimension(0, 4)));
        center.add(ownerLabel);
        
        JLabel dateLabel = new JLabel("Requested on " + rq.getCreatedAt().toLocalDate());
        dateLabel.setFont(Theme.FONT_SMALL);
        dateLabel.setForeground(Theme.MUTED);
        center.add(dateLabel);
        
        content.add(center, BorderLayout.CENTER);

        return Theme.card(content);
    }
}
