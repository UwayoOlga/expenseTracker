import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardFrame extends JFrame {
    String username;
    JTable table;
    DefaultTableModel model;

    public DashboardFrame(String username) {
        this.username = username;
        setTitle("Expense Tracker - Welcome " + username);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 51, 102)); // Dark blue

        model = new DefaultTableModel(new String[]{"Date", "Category", "Amount"}, 0);
        table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(1, 4));
        JTextField category = new JTextField();
        JTextField amount = new JTextField();
        JButton addBtn = new JButton("Add Expense");

        addBtn.addActionListener(e -> {
            String cat = category.getText();
            double amt = Double.parseDouble(amount.getText());
            String date = java.time.LocalDate.now().toString();
            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO expenses(username, date, category, amount) VALUES (?, ?, ?, ?)");
                stmt.setString(1, username);
                stmt.setString(2, date);
                stmt.setString(3, cat);
                stmt.setDouble(4, amt);
                stmt.executeUpdate();
                model.addRow(new Object[]{date, cat, amt});
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(category);
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amount);
        inputPanel.add(addBtn);

        panel.add(inputPanel, BorderLayout.SOUTH);
        add(panel);
        loadExpenses();
        setVisible(true);
    }

    private void loadExpenses() {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT date, category, amount FROM expenses WHERE username=?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("date"), rs.getString("category"), rs.getDouble("amount")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
