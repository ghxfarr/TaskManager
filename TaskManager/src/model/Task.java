package model;

import java.time.LocalDate;

public class Task {
    private int id;
    private String description;
    private Status status;
    private LocalDate startDate;
    private LocalDate dueDate;

    public Task(int id, String description, Status status, LocalDate startDate, LocalDate dueDate) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.dueDate = dueDate;
    }

    public int getId() {
        return id;
    }
    public String getDescription() {
        return description;
    }
    public Status getStatus() {
        return status;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return description + " (" + status.getName() + ")";
    }
}