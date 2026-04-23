package campus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class IncomingRequestsPanel extends JPanel {
    private final DataStore store;
    private final User currentUser;
    private final MainFrame parent;
    private final JPanel grid = new JPanel();

    public IncomingRequestsPanel(DataStore store, User currentUser, MainFrame parent) {
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
        List<BorrowRequest> reqs = store.listRequestsForOwner(currentUser.getUsername());
        if (reqs.isEmpty()) {
            JLabel empty = new JLabel("No incoming requests for your items.");
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
        User who = store.getUser(rq.getRequesterUsername());

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);
        content.setPreferredSize(new Dimension(280, 240));

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
        
        JLabel title = new JLabel(r != null ? r.getTitle() : "Deleted Item");
        title.setFont(Theme.FONT_HEADING);
        title.setForeground(Theme.TEXT);
        center.add(title);
        
        JLabel from = new JLabel("Requested by " + (who != null ? who.getFullName() : rq.getRequesterUsername()));
        from.setFont(Theme.FONT_SMALL);
        from.setForeground(Theme.PRIMARY);
        center.add(Box.createRigidArea(new Dimension(0, 4)));
        center.add(from);
        
        center.add(Box.createRigidArea(new Dimension(0, 8)));
        
        JTextArea msg = new JTextArea(rq.getMessage() != null && !rq.getMessage().isEmpty() ? "\"" + rq.getMessage() + "\"" : "");
        msg.setFont(Theme.FONT_BODY);
        msg.setLineWrap(true); msg.setWrapStyleWord(true);
        msg.setEditable(false); msg.setOpaque(false);
        msg.setRows(2);
        center.add(msg);
        
        content.add(center, BorderLayout.CENTER);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        if (rq.getStatus() == BorrowRequest.Status.PENDING && r != null && r.getStatus() == Resource.Status.AVAILABLE) {
            JButton dec = Theme.secondaryButton("Decline");
            dec.setFont(Theme.FONT_SMALL);
            dec.addActionListener(e -> {
                rq.setStatus(BorrowRequest.Status.DECLINED);
                rq.setDecidedAt(LocalDateTime.now());
                store.addRequest(rq);
                parent.rebuildTabs();
            });
            actions.add(dec);

            JButton app = Theme.primaryButton("Approve");
            app.setFont(Theme.FONT_SMALL);
            app.addActionListener(e -> {
                rq.setStatus(BorrowRequest.Status.APPROVED);
                rq.setDecidedAt(LocalDateTime.now());
                r.setStatus(Resource.Status.BORROWED);
                r.setBorrowerUsername(rq.getRequesterUsername());
                r.setBorrowedAt(LocalDateTime.now());
                r.setDueAt(LocalDateTime.now().plusDays(rq.getDays()));
                store.addRequest(rq);
                store.addResource(r);
                parent.rebuildTabs();
            });
            actions.add(app);
        }

        content.add(actions, BorderLayout.SOUTH);

        return Theme.card(content);
    }
}
