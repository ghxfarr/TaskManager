package controller;

import model.Task;
import model.Status;
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;


public class TaskManager {
    private List<Task> tasks, taskList = new ArrayList<>();
    private int nextId;

    public TaskManager() {
        tasks = new ArrayList<>();
        nextId = 1;
    }
    public void addTask(String description, Status status, java.time.LocalDate startDate, java.time.LocalDate dueDate) {
        Task task = new Task(nextId++, description, status, startDate, dueDate);
        tasks.add(task);
    }
    public void updateTask(int id, String description, Status status, LocalDate start, LocalDate due) {
    for (Task task : taskList) {
        if (task.getId() == id) {
            task.setDescription(description);
            task.setStatus(status);
            task.setStartDate(start);
            task.setDueDate(due);
            break;
        }
    }
}
    public boolean editTask(int id, String newDescription, Status newStatus, java.time.LocalDate newStartDate, java.time.LocalDate newDueDate) {
        Task task = getTaskById(id);
        if (task != null) {
            task.setDescription(newDescription);
            task.setStatus(newStatus);
            task.setStartDate(newStartDate);
            task.setDueDate(newDueDate);
            return true;
        }
        return false;
    }
    public boolean deleteTask(int id) {
        Task task = getTaskById(id);
        if (task != null) {
            tasks.remove(task);
            return true;
        }
        return false;
    }
    public boolean markAsCompleted(int id) {
        Task task = getTaskById(id);
        if (task != null) {
            task.setStatus(new Status(1, "Completed")); // ID 1 untuk Completed
            return true;
        }
        return false;
    }

    public Task getTaskById(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;
    }
    public List<Task> getAllTasks() {
        return tasks;
    }

    public List<Task> filterByStatus(String statusName) {
        return tasks.stream()
                .filter(task -> task.getStatus().getName().equalsIgnoreCase(statusName))
                .collect(Collectors.toList());
    }
    public List<Task> sortByDate(String type) {
        return tasks.stream()
                .sorted((t1, t2) -> {
                    if (type.equalsIgnoreCase("start")) {
                        return t1.getStartDate().compareTo(t2.getStartDate());
                    } else {
                        return t1.getDueDate().compareTo(t2.getDueDate());
                    }
                })
                .collect(Collectors.toList());
    }
    public void saveTasksToFile(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            Gson gson = new Gson();
            gson.toJson(taskList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadTasksFromFile(String filename) {
        try (FileReader reader = new FileReader(filename)) {
            Gson gson = new Gson();
            Type taskListType = new TypeToken<List<Task>>() {}.getType();
            List<Task> loadedTasks = gson.fromJson(reader, taskListType);
            if (loadedTasks != null) {
                taskList.clear();
                taskList.addAll(loadedTasks);
                nextId = taskList.stream().mapToInt(Task::getId).max().orElse(0) + 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }    
}