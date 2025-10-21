package com.taskweaver;

import com.taskweaver.data.TaskManager;
import com.taskweaver.model.Task;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class TaskWeaverApp {
    private TaskManager taskManager;
    private Scanner scanner;

    public TaskWeaverApp() {
        this.taskManager = new TaskManager();
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("🎯 Welcome to TaskWeaver - Context-Aware Task Management System!");
        System.out.println("===============================================================");

        // Add some sample tasks for demonstration
        initializeSampleTasks();

        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getIntInput("Choose an option: ");
            
            switch (choice) {
                case 1:
                    createTask();
                    break;
                case 2:
                    viewFocusTask();
                    break;
                case 3:
                    viewAllTasks();
                    break;
                case 4:
                    completeTask();
                    break;
                case 5:
                    deleteTask();
                    break;
                case 6:
                    viewStreak();
                    break;
                case 7:
                    viewTasksByAlert();
                    break;
                case 8:
                    running = false;
                    System.out.println("Thank you for using TaskWeaver! 👋");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }

    private void displayMenu() {
        System.out.println("\n--- TaskWeaver Menu ---");
        System.out.println("1. Create New Task");
        System.out.println("2. View Focus Task");
        System.out.println("3. View All Tasks");
        System.out.println("4. Complete Task");
        System.out.println("5. Delete Task");
        System.out.println("6. View Current Streak");
        System.out.println("7. View Tasks by Alert Level");
        System.out.println("8. Exit");
    }

    private void initializeSampleTasks() {
        taskManager.createTask("Complete PDSA Project", 1, LocalDate.now().plusDays(1));
        taskManager.createTask("Study for Algorithms Exam", 2, LocalDate.now().plusDays(3));
        taskManager.createTask("Buy Groceries", 3, LocalDate.now().plusDays(7));
        taskManager.createTask("Call Family", 4, LocalDate.now().minusDays(1)); // Overdue
        taskManager.createTask("Read Programming Book", 5, LocalDate.now().plusDays(10));
    }

    private void createTask() {
        System.out.println("\n--- Create New Task ---");
        System.out.print("Enter task title: ");
        String title = scanner.nextLine();
        
        int priority = getIntInput("Enter priority (1-5, 1=highest): ");
        while (priority < 1 || priority > 5) {
            System.out.println("Priority must be between 1 and 5.");
            priority = getIntInput("Enter priority (1-5, 1=highest): ");
        }
        
        System.out.print("Enter due date (YYYY-MM-DD): ");
        String dateInput = scanner.nextLine();
        LocalDate dueDate = LocalDate.parse(dateInput);
        
        taskManager.createTask(title, priority, dueDate);
        System.out.println("✅ Task created successfully!");
    }

    private void viewFocusTask() {
        System.out.println("\n--- Your Focus Task ---");
        Task focusTask = taskManager.getFocusTask();
        if (focusTask != null) {
            displayTask(focusTask, true);
        } else {
            System.out.println("No tasks available. Create your first task!");
        }
    }

    private void viewAllTasks() {
        System.out.println("\n--- All Tasks ---");
        List<Task> allTasks = taskManager.getAllTasksByPriority();
        if (allTasks.isEmpty()) {
            System.out.println("No tasks found.");
        } else {
            for (int i = 0; i < allTasks.size(); i++) {
                System.out.print((i + 1) + ". ");
                displayTask(allTasks.get(i), false);
            }
        }
    }

    private void completeTask() {
        List<Task> incompleteTasks = taskManager.getIncompleteTasks();
        if (incompleteTasks.isEmpty()) {
            System.out.println("No incomplete tasks found.");
            return;
        }
        
        System.out.println("\n--- Complete Task ---");
        for (int i = 0; i < incompleteTasks.size(); i++) {
            System.out.print((i + 1) + ". ");
            displayTask(incompleteTasks.get(i), false);
        }
        
        int taskNum = getIntInput("Enter task number to complete: ") - 1;
        if (taskNum >= 0 && taskNum < incompleteTasks.size()) {
            Task task = incompleteTasks.get(taskNum);
            if (taskManager.completeTask(task)) {
                System.out.println("✅ Task completed! Current streak: " + taskManager.getCurrentStreak() + " days!");
            }
        } else {
            System.out.println("Invalid task number.");
        }
    }

    private void deleteTask() {
        List<Task> allTasks = taskManager.getAllTasks();
        if (allTasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }
        
        System.out.println("\n--- Delete Task ---");
        for (int i = 0; i < allTasks.size(); i++) {
            System.out.print((i + 1) + ". ");
            displayTask(allTasks.get(i), false);
        }
        
        int taskNum = getIntInput("Enter task number to delete: ") - 1;
        if (taskNum >= 0 && taskNum < allTasks.size()) {
            Task task = allTasks.get(taskNum);
            if (taskManager.deleteTask(task)) {
                System.out.println("🗑️ Task deleted successfully!");
            }
        } else {
            System.out.println("Invalid task number.");
        }
    }

    private void viewStreak() {
        int streak = taskManager.getCurrentStreak();
        System.out.println("\n--- Current Streak ---");
        System.out.println("🔥 You are on a " + streak + "-day streak!");
        
        if (streak == 0) {
            System.out.println("Complete a task today to start your streak!");
        } else if (streak >= 7) {
            System.out.println("Amazing! Keep up the great work! 💪");
        } else if (streak >= 3) {
            System.out.println("Great consistency! Keep going! 🚀");
        }
    }

    private void viewTasksByAlert() {
        System.out.println("\n--- Tasks by Alert Level ---");
        
        System.out.println("\n🚨 RED ALERT (Due today/overdue):");
        List<Task> redTasks = taskManager.getTasksByAlertLevel("red");
        if (redTasks.isEmpty()) {
            System.out.println("No red alert tasks.");
        } else {
            redTasks.forEach(task -> displayTask(task, false));
        }
        
        System.out.println("\n🟠 ORANGE ALERT (Due in 2 days):");
        List<Task> orangeTasks = taskManager.getTasksByAlertLevel("orange");
        if (orangeTasks.isEmpty()) {
            System.out.println("No orange alert tasks.");
        } else {
            orangeTasks.forEach(task -> displayTask(task, false));
        }
        
        System.out.println("\n🟢 GREEN ALERT (Due in future):");
        List<Task> greenTasks = taskManager.getTasksByAlertLevel("green");
        if (greenTasks.isEmpty()) {
            System.out.println("No green alert tasks.");
        } else {
            greenTasks.forEach(task -> displayTask(task, false));
        }
    }

    private void displayTask(Task task, boolean isFocus) {
        String status = task.isCompleted() ? "✅ COMPLETED" : "⏳ PENDING";
        String alertEmoji = getAlertEmoji(task.getAlertLevel());
        
        if (isFocus) {
            System.out.println("🎯 FOCUS TASK:");
        }
        
        System.out.printf("%s [Priority: %d] %s - Due: %s - %s%n",
                alertEmoji, task.getPriority(), task.getTitle(), 
                task.getDueDate(), status);
        
        if (!isFocus) {
            System.out.println("   Alert Level: " + task.getAlertLevel().toUpperCase());
        }
        System.out.println();
    }

    private String getAlertEmoji(String alertLevel) {
        switch (alertLevel) {
            case "red": return "🚨";
            case "orange": return "🟠";
            case "green": return "🟢";
            default: return "⚪";
        }
    }

    private int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.println("Please enter a valid number.");
            scanner.next();
            System.out.print(prompt);
        }
        int input = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return input;
    }

    public static void main(String[] args) {
        TaskWeaverApp app = new TaskWeaverApp();
        app.run();
    }
}