package campus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AddResourcePanel extends JPanel {
    public AddResourcePanel(DataStore store, User currentUser, MainFrame parent) {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(32, 40, 32, 40));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        JTextField titleField = Theme.styledTextField(28);
        JComboBox<Resource.Category> catField = new JComboBox<>(Resource.Category.values());
        catField.setFont(Theme.FONT_BODY);
        
        JTextField locField = Theme.styledTextField(28);
        JTextField courseField = Theme.styledTextField(28);
        courseField.setToolTipText("e.g. CS101");
        
        JTextArea descField = new JTextArea(5, 28);
        descField.setFont(Theme.FONT_BODY);
        descField.setLineWrap(true); descField.setWrapStyleWord(true);
        descField.setBorder(BorderFactory.createLineBorder(new Color(209, 213, 219)));

        int row = 0;
        
        addLabel(form, "Resource Title", row, c);
        c.gridx=1; form.add(titleField, c); row++;

        addLabel(form, "Category", row, c);
        c.gridx=1; form.add(catField, c); row++;

        addLabel(form, "Course Code", row, c);
        c.gridx=1; form.add(courseField, c); row++;

        addLabel(form, "Pickup Location", row, c);
        c.gridx=1; form.add(locField, c); row++;

        addLabel(form, "Description", row, c);
        c.gridx=1; c.fill=GridBagConstraints.BOTH; c.weighty=1;
        form.add(new JScrollPane(descField), c); row++;

        c.gridx=1; c.gridy=row; c.fill=GridBagConstraints.NONE; c.anchor=GridBagConstraints.EAST; c.weighty=0;
        JButton submit = Theme.primaryButton("List Publicly");
        submit.addActionListener(e -> {
            String t = titleField.getText().trim();
            if (t.isEmpty()) return;
            Resource r = new Resource(t, descField.getText().trim(), (Resource.Category) catField.getSelectedItem(),
                    currentUser.getUsername(), locField.getText().trim());
            r.setCourseCode(courseField.getText().trim().isEmpty() ? "GEN-101" : courseField.getText().trim());
            store.addResource(r);
            JOptionPane.showMessageDialog(this, "Success! Your item is now in the " + currentUser.getCampusName() + " circle.");
            parent.rebuildTabs();
        });
        form.add(submit, c);

        add(Theme.card(form), BorderLayout.NORTH);
    }

    private void addLabel(JPanel p, String text, int row, GridBagConstraints c) {
        c.gridx=0; c.gridy=row; c.weightx=0; c.anchor=GridBagConstraints.WEST;
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_HEADING);
        p.add(l, c);
    }
}
