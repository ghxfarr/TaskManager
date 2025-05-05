import controller.TaskManager;
import view.TaskUI;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TaskManager taskManager = new TaskManager();
            new TaskUI(taskManager);
        });
    }
}