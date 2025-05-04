import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SignupFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public SignupFrame() {
        setTitle("Sign Up");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(173, 216, 230));
        panel.setLayout(null);

        JLabel label = new JLabel("Sign Up");
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setBounds(200, 30, 200, 30);
        panel.add(label);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(100, 100, 100, 30);
        panel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(200, 100, 200, 30);
        panel.add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(100, 150, 100, 30);
        panel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(200, 150, 200, 30);
        panel.add(passwordField);

        JButton signupButton = new JButton("Sign Up");
        signupButton.setBounds(150, 220, 200, 40);
        signupButton.setBackground(new Color(0, 51, 102));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFont(new Font("Arial", Font.BOLD, 16));
        signupButton.addActionListener(e -> signUp());
        panel.add(signupButton);

        add(panel);
        setVisible(true);
    }

    private void signUp() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/expense_tracker", "root", "");
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "User registered successfully!");
            new LoginFrame();
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}

