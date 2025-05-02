import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SignupFrame extends JFrame {
    JTextField userField;
    JPasswordField passField;

    public SignupFrame() {
        setTitle("Signup - Expense Tracker");
        setSize(300, 180);
        setLocationRelativeTo(null);
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBackground(new Color(173, 216, 230));

        panel.add(new JLabel("New Username:"));
        userField = new JTextField();
        panel.add(userField);

        panel.add(new JLabel("New Password:"));
        passField = new JPasswordField();
        panel.add(passField);

        JButton createBtn = new JButton("Create Account");
        createBtn.addActionListener(e -> createUser());
        panel.add(createBtn);

        add(panel);
        setVisible(true);
    }

    private void createUser() {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO users(username, password) VALUES (?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Account created! Now login.");
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

