/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package taskmanager;

import java.util.*;

class Task {
    String title;
    String description;
    String dueDate;
    boolean isComplete;
    String category;
    String priority;
    String recurrence; // daily, weekly, monthly
    List<Integer> dependencies = new ArrayList<>();

    public Task(String title, String description, String dueDate, String category, String priority) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.isComplete = false;
        this.category = category;
        this.priority = priority;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }

    public void addDependency(int taskId) {
        dependencies.add(taskId);
    }

    public void markComplete() {
        isComplete = true;
    }

    public void markIncomplete() {
        isComplete = false;
    }

    public String toString() {
        return (isComplete ? "[Complete]" : "[Incomplete]") + " " + title + " - Due: " + dueDate + " - Category: " + category + " - Priority: " + priority;
    }
}

public class TaskManager {
    static List<Task> tasks = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n1. Add Task");
            System.out.println("2. Mark Task as Complete");
            System.out.println("3. Delete Task");
            System.out.println("4. Sort Tasks");
            System.out.println("5. Search Tasks");
            System.out.println("6. Add Recurring Task");
            System.out.println("7. Set Task Dependency");
            System.out.println("8. Edit Task");
            System.out.println("9. View All Tasks");
            System.out.println("10. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume the newline character

            switch (choice) {
                case 1:
                    addTask();
                    break;
                case 2:
                    markTaskComplete();
                    break;
                case 3:
                    deleteTask();
                    break;
                case 4:
                    sortTasks();
                    break;
                case 5:
                    searchTasks();
                    break;
                case 6:
                    addRecurringTask();
                    break;
                case 7:
                    setTaskDependency();
                    break;
                case 8:
                    editTask();
                    break;
                case 9:
                    viewAllTasks();
                    break;
                case 0:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void addTask() {
        System.out.print("Enter task title:");
        String title = scanner.nextLine();
        System.out.print("Enter task description:");
        String description = scanner.nextLine();
        System.out.print("Enter due date (YYYY-MM-DD):");
        String dueDate = scanner.nextLine();
        System.out.print("Enter task category (Homework, Personal, Work):");
        String category = scanner.nextLine();
        System.out.print("Enter priority level (Low, Medium, High):");
        String priority = scanner.nextLine();

        Task task = new Task(title, description, dueDate, category, priority);
        tasks.add(task);
        System.out.println("\nTask \"" + title + "\" added successfully!");
    }

    private static void markTaskComplete() {
        System.out.print("Enter the task number you want to mark as complete:");
        int taskId = scanner.nextInt();
        if (taskId >= 1 && taskId <= tasks.size()) {
            Task task = tasks.get(taskId - 1);
            if (task.dependencies.isEmpty() || task.dependencies.stream().allMatch(dep -> tasks.get(dep - 1).isComplete)) {
                task.markComplete();
                System.out.println("Task \"" + task.title + "\" marked as complete!");
            } else {
                System.out.println("Warning: Task \"" + task.title + "\" cannot be marked as complete because it depends on other tasks.");
            }
        } else {
            System.out.println("Invalid task number.");
        }
    }

    private static void deleteTask() {
        System.out.print("Enter the task number you want to delete:");
        int taskId = scanner.nextInt();
        if (taskId >= 1 && taskId <= tasks.size()) {
            Task task = tasks.remove(taskId - 1);
            System.out.println("Task \"" + task.title + "\" deleted successfully!");
        } else {
            System.out.println("Invalid task number.");
        }
    }

    private static void sortTasks() {
        System.out.println("Sort by:");
        System.out.println("1. Due Date (Ascending)");
        System.out.println("2. Due Date (Descending)");
        System.out.println("3. Priority (High to Low)");
        System.out.println("4. Priority (Low to High)");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume the newline character

        switch (choice) {
            case 1:
                tasks.sort(Comparator.comparing(task -> task.dueDate));
                System.out.println("Tasks sorted by Due Date (Ascending)!");
                break;
            case 2:
                tasks.sort((task1, task2) -> task2.dueDate.compareTo(task1.dueDate));
                System.out.println("Tasks sorted by Due Date (Descending)!");
                break;
            case 3:
                tasks.sort((task1, task2) -> task2.priority.compareTo(task1.priority));
                System.out.println("Tasks sorted by Priority (High to Low)!");
                break;
            case 4:
                tasks.sort(Comparator.comparing(task -> task.priority));
                System.out.println("Tasks sorted by Priority (Low to High)!");
                break;
            default:
                System.out.println("Invalid option.");
        }
    }
private static void searchTasks() {
    }

    private static void addRecurringTask() {
    }

    private static void setTaskDependency() {
  
    }

    private static void editTask() {     
    }
    
    private static void viewAllTasks() {     
    }


}

