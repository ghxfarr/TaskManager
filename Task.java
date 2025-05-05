import java.util.Date;

public class Task {
    private int id;
    private String description;
    private Status status;
    private Date startDate;
    private Date dueDate;

    public Task(int id, String description, Status status, Date startDate, Date dueDate) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.dueDate = dueDate;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() { return id; }
    public String getDescription() { return description; }
    public Status getStatus() { return status; }
    public Date getStartDate() { return startDate; }
    public Date getDueDate() { return dueDate; }
}