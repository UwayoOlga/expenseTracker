import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.Vector;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.awt.Color;
public class DashboardFrame extends JFrame {
    private JComboBox<String> accountDropdown;
    private DefaultTableModel tableModel;
    private JTable expenseTable;
    private JTextField dateField, descField, amountField;
    private JButton addBtn, selectBtn, exportBtn, logoutBtn, deleteBtn;
    private JLabel totalLabel, currentAccLabel;

    private Connection conn;
    private int selectedAccountId = -1;

    public DashboardFrame() {
        setTitle("Expense Tracker");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(224, 247, 255));

        initializeDB();
        initComponents();
        loadAccounts();
        setVisible(true);
    }

    private void initializeDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost/expense_tracker", "root", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(new Color(224, 247, 255));
        topPanel.add(new JLabel("Select A/C:"));

        accountDropdown = new JComboBox<>();
        accountDropdown.setPreferredSize(new Dimension(200, 25));
        topPanel.add(accountDropdown);

        selectBtn = createStyledButton("Select");
        selectBtn.addActionListener(e -> loadExpenses());
        topPanel.add(selectBtn);

        exportBtn = createStyledButton("Export to Excel");
        exportBtn.addActionListener(e -> exportToExcel());
        topPanel.add(exportBtn);

        logoutBtn = createStyledButton("Logout");
        logoutBtn.addActionListener(e -> logout());
        topPanel.add(logoutBtn);

        add(topPanel, BorderLayout.NORTH);


        String[] cols = {"ID", "Date", "Description", "Amount"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        expenseTable = new JTable(tableModel);
        expenseTable.getColumnModel().getColumn(0).setMinWidth(0);
        expenseTable.getColumnModel().getColumn(0).setMaxWidth(0);
        add(new JScrollPane(expenseTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(new Color(224, 247, 255));

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setBackground(new Color(224, 247, 255));

        inputPanel.add(new JLabel("Date:"));
        dateField = new JTextField(10);
        inputPanel.add(dateField);

        inputPanel.add(new JLabel("Description:"));
        descField = new JTextField(10);
        inputPanel.add(descField);

        inputPanel.add(new JLabel("Amount:"));
        amountField = new JTextField(5);
        inputPanel.add(amountField);

        addBtn = createStyledButton("Add");
        addBtn.addActionListener(e -> addExpense());
        inputPanel.add(addBtn);

        deleteBtn = createStyledButton("Delete");
        deleteBtn.addActionListener(e -> deleteExpense());
        inputPanel.add(deleteBtn);

        bottomPanel.add(inputPanel);

        JPanel infoPanel = new JPanel(new FlowLayout());
        infoPanel.setBackground(new Color(224, 247, 255));
        totalLabel = new JLabel("Total Expense: 0");
        currentAccLabel = new JLabel("Current Acc Name: None");
        infoPanel.add(totalLabel);
        infoPanel.add(currentAccLabel);

        bottomPanel.add(infoPanel);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0, 51, 102));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private void loadAccounts() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/expense_tracker", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, account_name FROM accounts")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("account_name");
                accountDropdown.addItem(id + " | " + name);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load accounts: " + e.getMessage());
        }
    }

    private void loadExpenses() {
        String selected = (String) accountDropdown.getSelectedItem();
        if (selected == null || !selected.contains(" | ")) {
            JOptionPane.showMessageDialog(this, "Please select a valid account.");
            return;
        }
        String[] parts = selected.split(" \\| ");
        selectedAccountId = Integer.parseInt(parts[0].trim());
        String accName = parts[1].trim();

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM expenses WHERE account_id = ?");
            ps.setInt(1, selectedAccountId);
            ResultSet rs = ps.executeQuery();

            tableModel.setRowCount(0);
            double total = 0;
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("date"));
                row.add(rs.getString("description"));
                row.add(rs.getDouble("amount"));
                tableModel.addRow(row);
                total += rs.getDouble("amount");
            }
            totalLabel.setText("Total Expense: " + total);
            currentAccLabel.setText("Current Acc Name: " + accName);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading expenses: " + e.getMessage());
        }
    }

    private void addExpense() {
        if (selectedAccountId == -1) {
            JOptionPane.showMessageDialog(this, "Please select an account first.");
            return;
        }

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO expenses(account_id, date, description, amount) VALUES (?, ?, ?, ?)"
            );
            ps.setInt(1, selectedAccountId);
            ps.setString(2, dateField.getText());
            ps.setString(3, descField.getText());
            ps.setDouble(4, Double.parseDouble(amountField.getText()));
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Expense added successfully!");
            loadExpenses();

        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error adding expense: " + e.getMessage());
        }
    }

    private void deleteExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an expense to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this expense?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        int expenseId = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM expenses WHERE id = ?");
            ps.setInt(1, expenseId);
            int affected = ps.executeUpdate();

            if (affected > 0) {
                JOptionPane.showMessageDialog(this, "Expense deleted.");
                loadExpenses();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete expense.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting expense: " + e.getMessage());
        }
    }

    private void exportToExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Expenses");

             Row header = sheet.createRow(0);
            for (int i = 1; i < tableModel.getColumnCount(); i++) {
                header.createCell(i - 1).setCellValue(tableModel.getColumnName(i));
            }

             for (int r = 0; r < tableModel.getRowCount(); r++) {
                Row row = sheet.createRow(r + 1);
                for (int c = 1; c < tableModel.getColumnCount(); c++) {
                    row.createCell(c - 1).setCellValue(tableModel.getValueAt(r, c).toString());
                }
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Excel File");
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                FileOutputStream out = new FileOutputStream(fileChooser.getSelectedFile().getAbsolutePath() + ".xlsx");
                workbook.write(out);
                out.close();
                JOptionPane.showMessageDialog(this, "Report exported successfully.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to export: " + e.getMessage());
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            JOptionPane.showMessageDialog(null, "You have logged out.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardFrame().setVisible(true));
    }
}
