import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WelcomeFrame extends JFrame {
    public WelcomeFrame() {
        setTitle("Welcome to Expense Tracker");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(173, 216, 230)); // Light blue
        panel.setLayout(null);

        JLabel label = new JLabel("Welcome!");
        label.setFont(new Font("Arial", Font.BOLD, 30));
        label.setBounds(180, 30, 200, 40);
        panel.add(label);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(150, 100, 200, 50);
        loginBtn.setBackground(new Color(0, 51, 102));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 20));
        loginBtn.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        JButton signupBtn = new JButton("Sign Up");
        signupBtn.setBounds(150, 180, 200, 50);
        signupBtn.setBackground(new Color(0, 51, 102));
        signupBtn.setForeground(Color.WHITE);
        signupBtn.setFont(new Font("Arial", Font.BOLD, 20));
        signupBtn.addActionListener(e -> {
            new SignupFrame();
            dispose();
        });

        panel.add(loginBtn);
        panel.add(signupBtn);
        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new WelcomeFrame();
    }
}

