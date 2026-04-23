package campus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final DataStore store;
    private final JPanel mainContainer = new JPanel(new CardLayout());
    private final CardLayout cardLayout = (CardLayout) mainContainer.getLayout();

    public LoginFrame(DataStore store) {
        this.store = store;
        setTitle("Campus Hub | Sign In");
        setSize(450, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel bgPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, Theme.SIDEBAR, getWidth(), getHeight(), Theme.PRIMARY));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        
        mainContainer.setOpaque(false);
        mainContainer.add(buildLoginPanel(), "login");
        mainContainer.add(buildSignupPanel(), "signup");
        
        bgPanel.add(mainContainer);
        add(bgPanel);
    }

    private JPanel buildLoginPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(340, 450));

        JLabel title = new JLabel("Welcome Back");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(title);
        
        p.add(Box.createRigidArea(new Dimension(0, 32)));

        JTextField user = Theme.styledTextField(15);
        user.setMaximumSize(new Dimension(300, 40));
        p.add(fieldLabel("Username"));
        p.add(user);
        
        p.add(Box.createRigidArea(new Dimension(0, 16)));

        JPasswordField pass = new JPasswordField(15);
        pass.setFont(Theme.FONT_BODY);
        pass.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        pass.setMaximumSize(new Dimension(300, 40));
        p.add(fieldLabel("Password"));
        p.add(pass);

        p.add(Box.createRigidArea(new Dimension(0, 32)));

        JButton login = Theme.primaryButton("Sign In");
        login.setAlignmentX(Component.CENTER_ALIGNMENT);
        login.setMaximumSize(new Dimension(300, 45));
        login.addActionListener(e -> {
            System.out.println("[DEBUG] Login button clicked for: " + user.getText().trim());
            User u = store.authenticate(user.getText().trim(), new String(pass.getPassword()));
            if (u != null) {
                System.out.println("[DEBUG] Authentication successful for: " + u.getUsername());
                try {
                    System.out.println("[DEBUG] Attempting to initialize MainFrame...");
                    MainFrame frame = new MainFrame(store, u);
                    System.out.println("[DEBUG] MainFrame initialized successfully. Setting visible...");
                    frame.setVisible(true);
                    System.out.println("[DEBUG] MainFrame is now visible. Disposing LoginFrame...");
                    dispose();
                } catch (Exception ex) {
                    System.out.println("[DEBUG] ERROR: MainFrame initialization failed!");
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Masterpiece error: " + ex.getMessage());
                }
            } else {
                System.out.println("[DEBUG] Authentication failed.");
                JOptionPane.showMessageDialog(this, "Wait! Invalid credentials. Check your username/password.");
            }
        });
        p.add(login);

        p.add(Box.createRigidArea(new Dimension(0, 16)));

        JButton toSignup = new JButton("New here? Create account");
        toSignup.setFont(Theme.FONT_SMALL);
        toSignup.setForeground(new Color(224, 231, 255));
        toSignup.setContentAreaFilled(false);
        toSignup.setBorderPainted(false);
        toSignup.setAlignmentX(Component.CENTER_ALIGNMENT);
        toSignup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toSignup.addActionListener(e -> cardLayout.show(mainContainer, "signup"));
        p.add(toSignup);

        return p;
    }

    private JPanel buildSignupPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(340, 520));

        JLabel title = new JLabel("Join Campus Hub");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(title);
        
        p.add(Box.createRigidArea(new Dimension(0, 24)));

        JTextField full = Theme.styledTextField(15);
        full.setMaximumSize(new Dimension(300, 40));
        p.add(fieldLabel("Full Name (e.g. Arjun Sharma)"));
        p.add(full);
        
        p.add(Box.createRigidArea(new Dimension(0, 12)));

        JTextField cid = Theme.styledTextField(15);
        cid.setMaximumSize(new Dimension(300, 40));
        p.add(fieldLabel("Campus ID"));
        p.add(cid);

        p.add(Box.createRigidArea(new Dimension(0, 12)));

        JTextField user = Theme.styledTextField(15);
        user.setMaximumSize(new Dimension(300, 40));
        p.add(fieldLabel("Choose Username"));
        p.add(user);

        p.add(Box.createRigidArea(new Dimension(0, 12)));

        JPasswordField pass = new JPasswordField(15);
        pass.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        pass.setMaximumSize(new Dimension(300, 40));
        p.add(fieldLabel("Set Password"));
        p.add(pass);

        p.add(Box.createRigidArea(new Dimension(0, 24)));

        JButton signup = Theme.primaryButton("Create Account");
        signup.setAlignmentX(Component.CENTER_ALIGNMENT);
        signup.setMaximumSize(new Dimension(300, 45));
        signup.addActionListener(e -> {
            User u = new User(user.getText().trim(), new String(pass.getPassword()), full.getText().trim(), cid.getText().trim());
            if (store.registerUser(u)) {
                JOptionPane.showMessageDialog(this, "Welcome aboard! Initializing your dashboard...");
                MainFrame frame = new MainFrame(store, u);
                frame.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Oops! This username is already taken. Please choose a unique one.");
            }
        });
        p.add(signup);

        p.add(Box.createRigidArea(new Dimension(0, 12)));

        JButton back = new JButton("Already have an account? Sign in");
        back.setFont(Theme.FONT_SMALL);
        back.setForeground(new Color(224, 231, 255));
        back.setContentAreaFilled(false);
        back.setBorderPainted(false);
        back.setAlignmentX(Component.CENTER_ALIGNMENT);
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.addActionListener(e -> cardLayout.show(mainContainer, "login"));
        p.add(back);

        return p;
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_SMALL);
        l.setForeground(new Color(209, 213, 219));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setBorder(new EmptyBorder(0, 0, 4, 0));
        return l;
    }
}
