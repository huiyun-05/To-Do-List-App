
package com.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class TaskManager {
    static List<Task> tasks = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        checkAndGenerateRecurringTasks();
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
            System.out.println("");
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
                case 10:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void addTask() {
        System.out.print("Enter task title: ");
        String title = scanner.nextLine();
        System.out.print("Enter task description: ");
        String description = scanner.nextLine();
        System.out.print("Enter due date (YYYY-MM-DD): ");
        String dueDate = scanner.nextLine();
        System.out.print("Enter task category (Homework, Personal, Work): ");
        String category = scanner.nextLine();
        System.out.print("Enter priority level (Low, Medium, High): ");
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
        System.out.println("=== Search Tasks ===");
        System.out.print("Enter a keyword to search by title or description: ");
        String keyword = scanner.nextLine().toLowerCase();

        System.out.println("\n=== Search Results ===");
        boolean found = false;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getTitle().toLowerCase().contains(keyword) ||
                task.getDescription().toLowerCase().contains(keyword)) {
               
                System.out.println((i + 1) + ". " + task);
                found = true;
            }
            
        }
        if (!found) {
            System.out.println("No tasks found with the keyword: " + keyword);
        }
    }

     private static void addRecurringTask() {
        System.out.println("=== Add a Recurring Task ===");
        System.out.print("Enter task title: ");
        String title = scanner.nextLine();
        System.out.print("Enter task description: ");
        String description = scanner.nextLine();
        System.out.print("Enter recurrence interval (daily, weekly, monthly): ");
        String interval = scanner.nextLine().toLowerCase();

        // Create task with today's date as start
        LocalDate today = LocalDate.now();
        Task task = new Task(title, description, interval, today);
        tasks.add(task);
        System.out.println("\nRecurring Task \"" + title + "\" created successfully!");
    }

    private static void checkAndGenerateRecurringTasks() {
        LocalDate today = LocalDate.now(); // Get today's date
        List<Task> newTasks = new ArrayList<>(); // Temporary list for new tasks

        for (Task task : tasks) {
            if (task.isRecurring() && (task.nextCreationDate != null) && !today.isBefore(task.nextCreationDate)) {
                // Create a new task for the recurring task
                Task newTask = new Task(task.title, task.description, task.recurrence, task.nextCreationDate);
                newTasks.add(newTask);

                // Update the next creation date based on recurrence interval
                task.updateNextCreationDate();
            }
        }
        // Add all newly generated tasks to the main task list
        tasks.addAll(newTasks);
    }

    private static void setTaskDependency() {
  
    }

    public static void editTask() {
        System.out.println("=== Edit Task ===");
        System.out.print("Enter the task number you want to edit: ");
        int taskNumber = scanner.nextInt();
        scanner.nextLine(); 
        if (taskNumber <= 0 || taskNumber > tasks.size()) {
            System.out.println("Invalid task number.");
            return;
        }
        Task task = tasks.get(taskNumber - 1);
        System.out.println("\nWhat would you like to edit?");
        System.out.println("1. Title");
        System.out.println("2. Description");
        System.out.println("3. Due Date");
        System.out.println("4. Category");
        System.out.println("5. Priority");
        System.out.println("6. Set Task Dependency");
        System.out.println("7. Cancel");
        System.out.print("\n> ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                System.out.print("Enter the new title: ");
                String temp = task.getTitle();
                task.setTitle(scanner.nextLine());
                System.out.println("\nTask \"" + temp + "\" has been updated to \"" + task.getTitle() + ".\"");
                break;
            case 2:
                System.out.print("Enter the new description: ");
                temp = task.getDescription();
                task.setDescription(scanner.nextLine());
                System.out.println("\nTask \"" + temp + "\" has been updated to \"" + task.getDescription()+ ".\"");
                break;
            case 3:
                System.out.print("Enter the new due date (YYYY-MM-DD): ");
                temp = task.getDueDate();
                String newDueDate = scanner.nextLine();
                task.setDueDate(newDueDate);
                System.out.println("\nTask \"" + temp + "\" has been updated to \"" + task.getDueDate() + ".\"");
                break;
            case 4:
                System.out.print("Enter the new category: ");
                temp = task.getCategory();
                task.setCategory(scanner.nextLine());
                System.out.println("\nTask \"" + temp + "\" has been updated to \"" + task.getCategory()+ ".\"");
                break;
            case 5:
                System.out.print("Enter the new priority: ");
                temp = task.getPriority();
                task.setPriority(scanner.nextLine());
                System.out.println("\nTask \"" + temp + "\" has been updated to \"" + task.getPriority()+ ".\"");
                break;
            case 6:
                System.out.println("Set Task Dependency");
                
                return;
            case 7:
                System.out.println("Canceled editing.");
                return;
            default:
                System.out.println("Invalid choice.");
        }
        viewAllTasks();
    }

    
    private static void viewAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("\nNo tasks available!");
            return;
        }
        System.out.println("\n=== View All Tasks ===");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
    }
}