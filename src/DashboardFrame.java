import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {
    public DashboardFrame() {
        setTitle("Expense Tracker Dashboard");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(173, 216, 230));

        JLabel label = new JLabel("Welcome to your Dashboard!");
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label);

        add(panel);
        setVisible(true);
    }
}