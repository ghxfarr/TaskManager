import java.util.*;

public class TaskManager {
    private List<Task> tasks = new ArrayList<>();
    private int nextId = 1;

    public void addTask(String description, Status status, Date startDate, Date dueDate) {
        tasks.add(new Task(nextId++, description, status, startDate, dueDate));
    }

    public void editTask(int id, String newDesc, Status newStatus, Date newStart, Date newDue) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                tasks.set(tasks.indexOf(task), new Task(id, newDesc, newStatus, newStart, newDue));
                break;
            }
        }
    }

    public void deleteTask(int id) {
        tasks.removeIf(task -> task.getId() == id);
    }

    public void markCompleted(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                task.setStatus(new Status(1, "Completed"));
                break;
            }
        }
    }

    public List<Task> viewTasksByStatus(String statusName) {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getStatus().getName().equalsIgnoreCase(statusName)) {
                result.add(task);
            }
        }
        return result;
    }

    public List<Task> viewTasksByStartDate(Date date) {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getStartDate().equals(date)) {
                result.add(task);
            }
        }
        return result;
    }

    public List<Task> viewTasksByDueDate(Date date) {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDueDate().equals(date)) {
                result.add(task);
            }
        }
        return result;
    }

    public void listAllTasks() {
        for (Task task : tasks) {
            System.out.println("ID: " + task.getId() +
                    ", Desc: " + task.getDescription() +
                    ", Status: " + task.getStatus().getName() +
                    ", Start: " + task.getStartDate() +
                    ", Due: " + task.getDueDate());
        }
    }
}