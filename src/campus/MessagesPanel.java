package campus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class MessagesPanel extends JPanel {
    private final DataStore store;
    private final User currentUser;
    private final MainFrame parent;
    
    private final DefaultListModel<String> contactsModel = new DefaultListModel<>();
    private final JList<String> contactsList = new JList<>(contactsModel);
    private final JPanel chatArea = new JPanel(new BorderLayout());
    private final JPanel messagesList = new JPanel();
    private final JScrollPane scroll;
    private final JTextField input = Theme.styledTextField(20);
    private String activeContact = null;

    public MessagesPanel(DataStore store, User currentUser, MainFrame parent) {
        this.store = store;
        this.currentUser = currentUser;
        this.parent = parent;
        setLayout(new BorderLayout());
        setOpaque(false);

        // Sidebar (Contacts)
        JPanel sidebarZone = new JPanel(new BorderLayout());
        sidebarZone.setPreferredSize(new Dimension(280, 0));
        sidebarZone.setOpaque(false);
        sidebarZone.setBorder(new EmptyBorder(0, 16, 0, 16));
        
        contactsList.setFont(Theme.FONT_HEADING);
        contactsList.setFixedCellHeight(64);
        contactsList.setSelectionBackground(new Color(238, 242, 255));
        contactsList.setSelectionForeground(Theme.PRIMARY);
        contactsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                openChat(contactsList.getSelectedValue());
            }
        });
        
        sidebarZone.add(new JScrollPane(contactsList), BorderLayout.CENTER);
        add(sidebarZone, BorderLayout.WEST);

        // Chat Area
        chatArea.setOpaque(false);
        messagesList.setLayout(new BoxLayout(messagesList, BoxLayout.Y_AXIS));
        messagesList.setOpaque(false);
        
        scroll = new JScrollPane(messagesList);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(new EmptyBorder(20, 20, 20, 20));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        chatArea.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(12, 0));
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(16, 16, 16, 16));
        
        JButton send = Theme.primaryButton("Send 📤");
        send.addActionListener(e -> sendMessage());
        input.addActionListener(e -> sendMessage());
        
        bottom.add(input, BorderLayout.CENTER);
        bottom.add(send, BorderLayout.EAST);
        chatArea.add(bottom, BorderLayout.SOUTH);
        
        add(chatArea, BorderLayout.CENTER);
        
        reloadContacts();
    }

    public void reloadContacts() {
        String lastSelected = activeContact;
        contactsModel.clear();
        List<String> contacts = store.getChatContacts(currentUser.getUsername());
        for (String c : contacts) contactsModel.addElement(c);
        
        if (lastSelected != null && contactsModel.contains(lastSelected)) {
            contactsList.setSelectedValue(lastSelected, true);
        } else if (!contactsModel.isEmpty() && activeContact == null) {
            contactsList.setSelectedIndex(0);
        }
    }

    private void openChat(String contact) {
        if (contact == null) return;
        this.activeContact = contact;
        messagesList.removeAll();
        
        List<ChatMessage> msgs = store.getMessages(currentUser.getUsername(), contact);
        for (ChatMessage m : msgs) {
            boolean isMe = m.from.equals(currentUser.getUsername());
            JPanel row = new JPanel(new FlowLayout(isMe ? FlowLayout.RIGHT : FlowLayout.LEFT));
            row.setOpaque(false);
            
            // Use JTextArea for better wrapping in long messages
            JTextArea textArea = new JTextArea(m.content);
            textArea.setFont(Theme.FONT_BODY);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setEditable(false);
            textArea.setOpaque(true);
            textArea.setBackground(isMe ? Theme.PRIMARY : Color.WHITE);
            textArea.setForeground(isMe ? Color.WHITE : Theme.TEXT);
            textArea.setBorder(new EmptyBorder(10, 15, 10, 15));
            
            // Limit width for bubble effect
            int width = Math.min(textArea.getPreferredSize().width, 400);
            textArea.setSize(new Dimension(width, 100)); // height will adapt
            
            // Hack to make rounded bubbles (approximate)
            JPanel bubble = Theme.card(textArea);
            bubble.setBorder(null); // card already has padding
            
            row.add(textArea); // Just use textArea for now, card might be too big
            messagesList.add(row);
            messagesList.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        messagesList.revalidate();
        messagesList.repaint();
        
        // Push scroll to bottom
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scroll.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    public void selectContact(String contact) {
        if (contactsModel.contains(contact)) {
            contactsList.setSelectedValue(contact, true);
        }
    }

    private void sendMessage() {
        String text = input.getText().trim();
        if (activeContact == null) {
            JOptionPane.showMessageDialog(this, "Please select someone to message from the left or via an item listing.");
            return;
        }
        if (text.isEmpty()) return;
        
        store.sendMessage(currentUser.getUsername(), activeContact, text);
        input.setText("");
        openChat(activeContact);
    }
}
