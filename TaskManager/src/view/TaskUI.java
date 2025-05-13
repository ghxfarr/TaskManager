package view;

import controller.TaskManager;
import model.Status;
import model.Task;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TaskUI extends JFrame {
    private TaskManager taskManager;
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JTextField descriptionField;
    private JComboBox<Status> statusComboBox;
    private JTextField startDateField;
    private JTextField dueDateField;

    public TaskUI(TaskManager taskManager) {
        this.taskManager = taskManager; 
        setTitle("Task Manager");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initComponents();
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> filterComboBox = new JComboBox<>(new String[]{
            "All", "Pending", "In progress", "Completed", "Canceled"
        });
        filterPanel.add(new JLabel("Filter by Status:"));
        filterPanel.add(filterComboBox);
        filterComboBox.addActionListener(e -> refreshTable((String) filterComboBox.getSelectedItem())); // Add a listener to a ComboBox filter

        add(filterPanel, BorderLayout.BEFORE_FIRST_LINE); // Add to Layout
        JButton deleteButton = new JButton("Delete Task"); // Delete and complete buttons
        JButton completeButton = new JButton("Mark as Completed");
        JButton saveButton = new JButton("Save Tasks");
        JButton loadButton = new JButton("Load Tasks");
        JButton editButton = new JButton("Edit Task");
        JPanel buttonPanel = new JPanel();

        saveButton.addActionListener(e -> {
            taskManager.saveTasksToFile("tasks.json");
            JOptionPane.showMessageDialog(this, "Tasks saved!");
        });

        loadButton.addActionListener(e -> {
            taskManager.loadTasksFromFile("tasks.json");
            refreshTable();
            JOptionPane.showMessageDialog(this, "Tasks loaded!");
        });

        deleteButton.addActionListener(e -> deleteSelectedTask());
        completeButton.addActionListener(e -> completeSelectedTask());
        editButton.addActionListener(e -> editSelectedTask());

        buttonPanel.add(deleteButton);
        buttonPanel.add(completeButton);
        buttonPanel.add(editButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        add(buttonPanel, BorderLayout.SOUTH);



        setVisible(true);
    }

    private void initComponents() {
        JPanel inputPanel = new JPanel(new GridLayout(2, 5, 10, 10)); // Input panel

        descriptionField = new JTextField();
        startDateField = new JTextField("2025-05-05");
        dueDateField = new JTextField("2025-05-10");

        statusComboBox = new JComboBox<>(); // ComboBox Status
        statusComboBox.addItem(new Status(1, "Pending"));
        statusComboBox.addItem(new Status(2, "In progress"));
        statusComboBox.addItem(new Status(3, "Completed"));
        statusComboBox.addItem(new Status(4, "Canceled"));

        JButton addButton = new JButton("Add Task");

        inputPanel.add(new JLabel("Description"));
        inputPanel.add(new JLabel("Start Date (YYYY-MM-DD)"));
        inputPanel.add(new JLabel("Due Date (YYYY-MM-DD)"));
        inputPanel.add(new JLabel("Status"));
        inputPanel.add(new JLabel(""));
        inputPanel.add(descriptionField);
        inputPanel.add(startDateField);
        inputPanel.add(dueDateField);
        inputPanel.add(statusComboBox);
        inputPanel.add(addButton);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Description", "Start", "Due", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // This means that all table cells cannot be edited directly
            }
        };
        taskTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(taskTable);
        addButton.addActionListener(e -> addTask()); // Add task action

        // Layout
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
    }

    private void addTask() {
        try {
            String description = descriptionField.getText();
            LocalDate start = LocalDate.parse(startDateField.getText());
            LocalDate due = LocalDate.parse(dueDateField.getText());
            Status status = (Status) statusComboBox.getSelectedItem();

            taskManager.addTask(description, status, start, due);
            refreshTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input format!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void deleteSelectedTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow >= 0) {
            int taskId = (int) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this task?");
            if (confirm == JOptionPane.YES_OPTION) {
                taskManager.deleteTask(taskId);
                refreshTable();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to delete.");
        }
    }
    private void completeSelectedTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow >= 0) {
            int taskId = (int) tableModel.getValueAt(selectedRow, 0);
            taskManager.markAsCompleted(taskId);
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to mark as completed.");
        }
    }
    private void editSelectedTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow >= 0) {
            int taskId = (int) tableModel.getValueAt(selectedRow, 0);
    
            String newDescription = JOptionPane.showInputDialog(this, "New Description:");
            String newStartDate = JOptionPane.showInputDialog(this, "New Start Date (YYYY-MM-DD):");
            String newDueDate = JOptionPane.showInputDialog(this, "New Due Date (YYYY-MM-DD):");
    
            Status[] statusOptions = {
                new Status(1, "Pending"),
                new Status(2, "In progress"),
                new Status(3, "Completed"),
                new Status(4, "Canceled")
            };
            Status newStatus = (Status) JOptionPane.showInputDialog(this, "New Status:",
                    "Edit Status", JOptionPane.QUESTION_MESSAGE, null, statusOptions, statusOptions[0]);
    
            try {
                LocalDate start = LocalDate.parse(newStartDate);
                LocalDate due = LocalDate.parse(newDueDate);
    
                taskManager.updateTask(taskId, newDescription, newStatus, start, due);
                refreshTable();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to edit.");
        }
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0); // clear table
        List<Task> tasks = taskManager.getAllTasks();
        for (Task task : tasks) {
            tableModel.addRow(new Object[]{
                task.getId(),
                task.getDescription(),
                task.getStartDate(),
                task.getDueDate(),
                task.getStatus().getName()
            });
        }
    }
    private void refreshTable(String filterStatus) {
        tableModel.setRowCount(0); // clear table
        List<Task> tasks = taskManager.getAllTasks();
        for (Task task : tasks) {
            String statusName = task.getStatus().getName();
            if (filterStatus.equals("All") || statusName.equals(filterStatus)) {
                tableModel.addRow(new Object[]{
                    task.getId(),
                    task.getDescription(),
                    task.getStartDate(),
                    task.getDueDate(),
                    statusName
                });
            }
        }
    }
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        new TaskUI(manager);
    }    
}