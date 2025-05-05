import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        TaskManager tm = new TaskManager();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Status pending = new Status(4, "Pending");
        Status inProgress = new Status(3, "In progress");

        tm.addTask("Belajar Java", pending, sdf.parse("2025-04-30"), sdf.parse("2025-05-10"));
        tm.addTask("Kerjakan Project", inProgress, sdf.parse("2025-05-01"), sdf.parse("2025-05-05"));

        tm.listAllTasks();
    }
}