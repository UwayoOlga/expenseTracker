import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    JTextField userField;
    JPasswordField passField;

    public LoginFrame() {
        setTitle("Login - Expense Tracker");
        setSize(350, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));
        panel.setBackground(new Color(173, 216, 230)); // Light Blue

        panel.add(new JLabel("Username:"));
        userField = new JTextField();
        panel.add(userField);

        panel.add(new JLabel("Password:"));
        passField = new JPasswordField();
        panel.add(passField);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> authenticate());
        panel.add(loginBtn);

        JButton signupBtn = new JButton("Signup");
        signupBtn.addActionListener(e -> new SignupFrame());
        panel.add(signupBtn);

        add(panel);
        setVisible(true);
    }

    private void authenticate() {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                dispose();
                new DashboardFrame(username);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
