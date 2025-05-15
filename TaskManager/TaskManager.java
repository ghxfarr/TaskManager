import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskManager extends JFrame {
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterComboBox;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public TaskManager() {
        setTitle("Task Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        initComponents();
        setLayout(new BorderLayout());

        // Add components to frame
        JPanel buttonPanel = createButtonPanel();
        JPanel filterPanel = createFilterPanel();
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(buttonPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(taskTable), BorderLayout.CENTER);
        refreshTable();
    }

    private void initComponents() {
        // Table setup
        String[] columns = { "Task Name", "Description", "Start Date", "End Date", "Status" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        taskTable = new JTable(tableModel);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Task Name
        taskTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Description
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Start Date
        taskTable.getColumnModel().getColumn(3).setPreferredWidth(100); // End Date
        taskTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Status
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Task");
        JButton updateButton = new JButton("Update Task");
        JButton deleteButton = new JButton("Delete Task");
        JButton markCompletedButton = new JButton("Mark as Completed");
        addButton.addActionListener(e -> showAddTaskDialog());
        updateButton.addActionListener(e -> showUpdateTaskDialog());
        deleteButton.addActionListener(e -> showDeleteTaskDialog());
        markCompletedButton.addActionListener(e -> markAsCompleted());

        // Add buttons to panel
        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(markCompletedButton);
        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        JLabel filterLabel = new JLabel("Filter by Status:");
        String[] filters = { "All", "Pending", "In Progress", "Completed", "Canceled" };
        filterComboBox = new JComboBox<>(filters);
        filterComboBox.addActionListener(e -> refreshTable());
        panel.add(filterLabel);
        panel.add(filterComboBox);
        return panel;
    }

    private void showAddTaskDialog() {
        JDialog dialog = new JDialog(this, "Add Task", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));
        JLabel nameLabel = new JLabel("Task Name:");
        JTextField nameField = new JTextField(20);
        JLabel descLabel = new JLabel("Description:");
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JLabel startDateLabel = new JLabel("Start Date (dd/MM/yyyy):");
        JTextField startDateField = new JTextField(10);
        JLabel endDateLabel = new JLabel("End Date (dd/MM/yyyy):");
        JTextField endDateField = new JTextField(10);
        JLabel statusLabel = new JLabel("Status:");
        String[] statuses = { "Pending", "In Progress", "Completed", "Canceled" };
        JComboBox<String> statusComboBox = new JComboBox<>(statuses);
        JButton submitButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");

        submitButton.addActionListener(e -> {
            String taskName = nameField.getText().trim();
            String description = descArea.getText().trim();
            String startDateStr = startDateField.getText().trim();
            String endDateStr = endDateField.getText().trim();
            String status = (String) statusComboBox.getSelectedItem();

            if (taskName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a task name", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Date startDate = parseDate(startDateStr, dialog);
            if (startDate == null)
                return;
            Date endDate = parseDate(endDateStr, dialog);
            if (endDate == null)
                return;

            if (endDate.before(startDate)) {
                JOptionPane.showMessageDialog(dialog, "End Date cannot be before Start Date", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            addTask(taskName, description, startDate, endDate, status);
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(nameLabel);
        dialog.add(nameField);
        dialog.add(descLabel);
        dialog.add(new JScrollPane(descArea));
        dialog.add(startDateLabel);
        dialog.add(startDateField);
        dialog.add(endDateLabel);
        dialog.add(endDateField);
        dialog.add(statusLabel);
        dialog.add(statusComboBox);
        dialog.add(new JLabel());
        dialog.add(submitButton);
        dialog.add(new JLabel());
        dialog.add(cancelButton);
        dialog.setVisible(true);
    }

    private void showUpdateTaskDialog() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a task to update", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Task task = getTaskAtRow(selectedRow);
        JDialog dialog = new JDialog(this, "Update Task", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));

        JLabel nameLabel = new JLabel("Task Name:");
        JTextField nameField = new JTextField(task.getName(), 20);
        JLabel descLabel = new JLabel("Description:");
        JTextArea descArea = new JTextArea(task.getDescription(), 3, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JLabel startDateLabel = new JLabel("Start Date (dd/MM/yyyy):");
        JTextField startDateField = new JTextField(dateFormat.format(task.getStartDate()), 10);
        JLabel endDateLabel = new JLabel("End Date (dd/MM/yyyy):");
        JTextField endDateField = new JTextField(dateFormat.format(task.getEndDate()), 10);
        JLabel statusLabel = new JLabel("Status:");
        String[] statuses = { "Pending", "In Progress", "Completed", "Canceled" };
        JComboBox<String> statusComboBox = new JComboBox<>(statuses);
        statusComboBox.setSelectedItem(task.getStatus());
        JButton submitButton = new JButton("Update");
        JButton cancelButton = new JButton("Cancel");

        submitButton.addActionListener(e -> {
            String taskName = nameField.getText().trim();
            String description = descArea.getText().trim();
            String startDateStr = startDateField.getText().trim();
            String endDateStr = endDateField.getText().trim();
            String status = (String) statusComboBox.getSelectedItem();

            if (taskName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a task name", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Date startDate = parseDate(startDateStr, dialog);
            if (startDate == null)
                return;
            Date endDate = parseDate(endDateStr, dialog);
            if (endDate == null)
                return;

            if (endDate.before(startDate)) {
                JOptionPane.showMessageDialog(dialog, "End Date cannot be before Start Date", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            updateTask(task.getId(), taskName, description, startDate, endDate, status);
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(nameLabel);
        dialog.add(nameField);
        dialog.add(descLabel);
        dialog.add(new JScrollPane(descArea));
        dialog.add(startDateLabel);
        dialog.add(startDateField);
        dialog.add(endDateLabel);
        dialog.add(endDateField);
        dialog.add(statusLabel);
        dialog.add(statusComboBox);
        dialog.add(new JLabel());
        dialog.add(submitButton);
        dialog.add(new JLabel());
        dialog.add(cancelButton);
        dialog.setVisible(true);
    }

    private void showDeleteTaskDialog() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a task to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Task task = getTaskAtRow(selectedRow);
        JDialog dialog = new JDialog(this, "Delete Task", true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(3, 1, 10, 10));

        JLabel confirmLabel = new JLabel("Delete task: " + task.getName() + "?");
        JButton deleteButton = new JButton("Delete");
        JButton cancelButton = new JButton("Cancel");

        deleteButton.addActionListener(e -> {
            deleteTask(task.getId());
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(confirmLabel);
        dialog.add(deleteButton);
        dialog.add(cancelButton);
        dialog.setVisible(true);
    }

    private void markAsCompleted() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow >= 0) {
            Task task = getTaskAtRow(selectedRow);
            updateTask(task.getId(), task.getName(), task.getDescription(), task.getStartDate(), task.getEndDate(),
                    "Completed");
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to mark as completed", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        String filter = (String) filterComboBox.getSelectedItem();
        List<Task> tasks = getTasks(filter);
        for (Task task : tasks) {
            tableModel.addRow(new Object[] {
                    task.getName(),
                    task.getDescription(),
                    dateFormat.format(task.getStartDate()),
                    dateFormat.format(task.getEndDate()),
                    task.getStatus()
            });
        }
    }

    private List<Task> getTasks(String filter) {
        List<Task> tasks = new ArrayList<>();
        String query = filter.equals(
                "All") ? "SELECT t.task_id, t.name, t.description, t.start_date, t.end_date, s.status_name, t.updated_at " +
                        "FROM tasks t JOIN task_status s ON t.status_id = s.status_id ORDER BY t.updated_at DESC"
                        : "SELECT t.task_id, t.name, t.description, t.start_date, t.end_date, s.status_name, t.updated_at "
                                +
                                "FROM tasks t JOIN task_status s ON t.status_id = s.status_id WHERE s.status_name = ? ORDER BY t.updated_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            if (!filter.equals("All")) {
                stmt.setString(1, filter);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tasks.add(new Task(
                        rs.getLong("task_id"),
                        rs.getString("name"),
                        rs.getString("description") != null ? rs.getString("description") : "",
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getString("status_name")));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return tasks;
    }

    private void addTask(String name, String description, Date startDate, Date endDate, String status) {
        String query = "INSERT INTO tasks (name, description, start_date, end_date, status_id, created_at, updated_at) "
                +
                "VALUES (?, ?, ?, ?, (SELECT status_id FROM task_status WHERE status_name = ?), NOW(), NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, description.isEmpty() ? null : description);
            stmt.setDate(3, new java.sql.Date(startDate.getTime()));
            stmt.setDate(4, new java.sql.Date(endDate.getTime()));
            stmt.setString(5, status);
            stmt.executeUpdate();
            refreshTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTask(long taskId, String name, String description, Date startDate, Date endDate, String status) {
        String query = "UPDATE tasks SET name = ?, description = ?, start_date = ?, end_date = ?, " +
                "status_id = (SELECT status_id FROM task_status WHERE status_name = ?), updated_at = NOW() " +
                "WHERE task_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, description.isEmpty() ? null : description);
            stmt.setDate(3, new java.sql.Date(startDate.getTime()));
            stmt.setDate(4, new java.sql.Date(endDate.getTime()));
            stmt.setString(5, status);
            stmt.setLong(6, taskId);
            stmt.executeUpdate();
            refreshTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTask(long taskId) {
        String query = "DELETE FROM tasks WHERE task_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, taskId);
            stmt.executeUpdate();
            refreshTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Date parseDate(String dateStr, JDialog dialog) {
        if (dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Please enter a date", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(dialog, "Invalid date format. Use dd/MM/yyyy", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private Task getTaskAtRow(int row) {
        String filter = (String) filterComboBox.getSelectedItem();
        List<Task> tasks = getTasks(filter);
        return tasks.get(row);
    }

    private static class Task {
        private long id;
        private String name;
        private String description;
        private Date startDate;
        private Date endDate;
        private String status;

        public Task(long id, String name, String description, Date startDate, Date endDate, String status) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.startDate = startDate;
            this.endDate = endDate;
            this.status = status;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Date getStartDate() {
            return startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public String getStatus() {
            return status;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TaskManager().setVisible(true));
    }
}