package com.example;
import static com.example.StorageSystem.loadTasksFromCSV;
import static com.example.StorageSystem.saveTasksToCSV;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.stream.Collectors;

public class TaskManager {
    static List<GeneralTask> tasks = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);
    static LocalDate lastGenerationDate = LocalDate.of(2000, 1, 1); //Initialize to a past date
    static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        generateRecurringTasks();
        while (true) {
            System.out.println("\n=== Menu ===");
            System.out.println("1. Add Task");
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

    public static void addTask() {
        System.out.println("\n=== Add a New Task ===");
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
        
        GeneralTask newTask = new GeneralTask(title, description, dueDate, category, priority);
        tasks.add(newTask);
        StorageSystem.addTask(newTask);
        saveTasksToCSV();
        System.out.println("\nTask \"" + title + "\" added successfully!");
    }

    private static void markTaskComplete() {
        System.out.println("\n=== Mark Task as Complete ===");
        System.out.print("Enter the task number you want to mark as complete:");
        int taskId = scanner.nextInt();
        System.out.println();
        
        // Load tasks from CSV before proceeding
        loadTasksFromCSV();
        
        if (taskId >= 1 && taskId <= tasks.size()) {
            GeneralTask task = tasks.get(taskId - 1);
            boolean canMarkComplete = true;
         
            // Check dependencies only if they exist
            if (task.dependencies != null) {
                for (Integer dep : task.dependencies) {
                    if (!tasks.get(dep - 1).isComplete) {
                        System.out.println("Warning: Task \"" + task.title
                                + "\" cannot be marked as complete because it depends on \""
                                + tasks.get(dep - 1).getTitle() + "\". Please complete \""
                                + tasks.get(dep - 1).getTitle() + "\" first.");
                        canMarkComplete = false;
                        break;
                    }
                }
            }

            if (canMarkComplete) {
                task.markComplete();
                System.out.println("Task \"" + task.title + "\" marked as complete!");
                // Save the updated tasks back to CSV
                saveTasksToCSV();
            }
        } else {
            System.out.println("Invalid task number.");
        }
    }

    private static void deleteTask() {
        System.out.println("\n=== Delete a Task ===");
        System.out.print("Enter the task number you want to delete:");
        int taskId = scanner.nextInt();
        
        // Load tasks from CSV before proceeding
        loadTasksFromCSV();
        
        if (taskId >= 1 && taskId <= tasks.size()) {
            // Remove dependencies from other tasks
            for (GeneralTask t : tasks) {
                if (t.dependencies != null) {
                    t.dependencies.remove(Integer.valueOf(taskId));
                }
            }
            GeneralTask task = tasks.remove(taskId - 1);
            System.out.println("Task \"" + task.title + "\" deleted successfully!");
            // Update dependencies of remaining tasks
            for (int i = 0; i < tasks.size(); i++) {
                task = tasks.get(i);

                if (task.dependencies != null) {
                    // Remove references to the deleted task
                    task.dependencies.removeIf(dep -> dep == taskId);

                    // Adjust indices of dependencies greater than the deleted task ID
                    for (int j = 0; j < task.dependencies.size(); j++) {
                        if (task.dependencies.get(j) > taskId) {
                            task.dependencies.set(j, task.dependencies.get(j) - 1);
                        }
                    }
                }
            }
            // Save the updated tasks back to CSV
            saveTasksToCSV();
        } else {
            System.out.println("Invalid task number.");
        }
    }
    
    private static void sortTasks() {
        System.out.println("\n=== Sort Tasks ===");
        System.out.println("Sort by:");
        System.out.println("1. Due Date (Ascending)");
        System.out.println("2. Due Date (Descending)");
        System.out.println("3. Priority (High to Low)");
        System.out.println("4. Priority (Low to High)");
        
        int choice;
        try {
            choice = scanner.nextInt();
            scanner.nextLine(); // consume the newline character
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number between 1 and 4.");
            scanner.nextLine(); // clear the invalid input
            return;
        }

        // Load tasks from CSV before sorting
        loadTasksFromCSV();

        if (tasks.isEmpty()) {
            System.out.println("No tasks available to sort.");
            return;
        }

        switch (choice) {
            case 1:
                tasks.sort(Comparator.comparing(task -> task.dueDate, Comparator.nullsLast(Comparator.naturalOrder())));
                System.out.println("Tasks sorted by Due Date (Ascending)!");
                break;
            case 2:
                tasks.sort(Comparator.comparing(task -> task.dueDate, Comparator.nullsLast(Comparator.reverseOrder())));
                System.out.println("Tasks sorted by Due Date (Descending)!");
                break;
            case 3:
                tasks.sort(Comparator.comparing(task -> task.priority, Comparator.nullsLast(Comparator.reverseOrder())));
                System.out.println("Tasks sorted by Priority (High to Low)!");
                break;
            case 4:
                tasks.sort(Comparator.comparing(task -> task.priority, Comparator.nullsLast(Comparator.naturalOrder())));
                System.out.println("Tasks sorted by Priority (Low to High)!");
                break;
            default:
                System.out.println("Invalid option. Please select a number between 1 and 4.");
        }
        // Save the updated tasks back to CSV after sorting
        saveTasksToCSV();
    }
    
    private static void searchTasks() {
        System.out.println("=== Search Tasks ===");
        System.out.print("Enter a keyword to search by title or description: ");
        String keyword = scanner.nextLine().trim().toLowerCase();

        if (keyword.isEmpty()) {
            System.out.println("Keyword cannot be empty. Please try again.");
            return;
        }

        // Load tasks from CSV before searching
        loadTasksFromCSV();

        System.out.println("\n=== Search Results ===");
        boolean found = false;

        for (int i = 0; i < tasks.size(); i++) {
            GeneralTask task = tasks.get(i);
            String title = task.getTitle() != null ? task.getTitle().toLowerCase() : "";
            String description = task.getDescription() != null ? task.getDescription().toLowerCase() : "";

            if (title.contains(keyword) || description.contains(keyword)) {
                System.out.println((i + 1) + ". Title: " + task.getTitle() + " | Description: " + task.getDescription());
                found = true;
            }
        }

        if (!found) {
            System.out.println("No tasks found with the keyword: \"" + keyword + "\"");
        }
    }

    private static void addRecurringTask() {
        System.out.println("=== Add a Recurring Task ===");
        System.out.print("Enter task title: ");
        String title = scanner.nextLine();
        System.out.print("Enter task description: ");
        String description = scanner.nextLine();
        System.out.print("Enter recurrence interval (daily, weekly, monthly): ");
        String recurrence = scanner.nextLine().toLowerCase();
        if (!recurrence.equals("daily") && !recurrence.equals("weekly") && !recurrence.equals("monthly")) {
            System.out.println("Invalid interval! Please enter 'daily', 'weekly', or 'monthly'.");
            return;
        }
        
        // Load tasks from CSV before adding the new task
        loadTasksFromCSV();
        
        LocalDate initialDueDate = LocalDate.now();
        
        GeneralTask task = new GeneralTask(title, description, initialDueDate.format(dateFormatter), recurrence);
     
        if (task.nextCreationDate != null) {
            tasks.add(task);
            System.out.println("\nRecurring task \"" + title + "\" added successfully!");
            
            // Save the updated task list to the CSV file
            saveTasksToCSV();
        }
    }

    private static void generateRecurringTasks() {
        LocalDate today = LocalDate.now();
        if (lastGenerationDate != null && lastGenerationDate.equals(today)) {
            return; // Don't generate tasks more than once a day
        }

        List<GeneralTask> newTasks = new ArrayList<>();
        Set<String> existingTasks = new HashSet<>();

        // Collect already existing tasks in the set to check for duplicates
        for (GeneralTask task : tasks) {
            existingTasks.add(task.title + task.nextCreationDate.toString());
        }

        for (GeneralTask task : tasks) {
            if (!task.recurrence.isEmpty()) {
                LocalDate nextDueDate = task.getNextDueDate();
                
                // Load tasks from CSV before generating recurring tasks
                loadTasksFromCSV();

                // Generate tasks until the next due date is beyond today
                while (!nextDueDate.isAfter(today)) {
                    if (!existingTasks.contains(task.title + nextDueDate.toString())) {
                        // Create new task for the next due date
                        GeneralTask newTask = new GeneralTask(task.title, task.description, nextDueDate.format(dateFormatter), task.recurrence);
                        newTasks.add(newTask);
                        existingTasks.add(task.title + nextDueDate.toString()); // Mark this task as existing
                    }
                    // Increment the due date based on the recurrence interval
                    switch (task.recurrence) {
                        case "daily":
                            nextDueDate = nextDueDate.plusDays(1);
                            break;
                        case "weekly":
                            nextDueDate = nextDueDate.plusWeeks(1);
                            break;
                        case "monthly":
                            nextDueDate = nextDueDate.plusMonths(1);
                            break;
                    }
                }
            }
        }

        // Add newly generated tasks
        tasks.addAll(newTasks);

        // Sort tasks by due date
        tasks.sort(Comparator.comparing(t -> t.nextCreationDate));
        
        // Save the updated tasks list to the CSV file
        saveTasksToCSV();

        // Update the last generation date
        lastGenerationDate = today;
    }

    private static void setTaskDependency() {
        // Load tasks from CSV before setting the dependency
        loadTasksFromCSV();

        System.out.println("\n=== Set Task Dependency ===");
        System.out.print("Enter task number that depends on another task: ");
        int dependentTask = scanner.nextInt();
        System.out.print("Enter the task number it depends on: ");
        int precedingTask = scanner.nextInt();

        // Validate task numbers
        if (!isValidTaskNumber(dependentTask) || !isValidTaskNumber(precedingTask)) {
            System.out.println("Invalid task numbers. Please provide valid task numbers.");
            return;
        }

        // Prevent self-dependency
        if (dependentTask == precedingTask) {
            System.out.println("Error: A task cannot depend on itself.");
            return;
        }

        // Retrieve tasks
        GeneralTask dependent = tasks.get(dependentTask - 1);
        GeneralTask preceding = tasks.get(precedingTask - 1);

        // Ensure dependencies are initialized
        if (dependent.getDependencies() == null) {
            dependent.setDependencies(new ArrayList<>());
        }

        // Check for duplicate dependencies
        if (dependent.getDependencies().contains(precedingTask)) {
            System.out.println("This dependency already exists.");
            return;
        }

        // Detect and prevent dependency cycles
        if (detectCycle(dependentTask, precedingTask)) {
            System.out.println("Error: Cannot set dependency. A cycle would be created.");
            return;
        }

        // Add the dependency
        dependent.getDependencies().add(precedingTask);
        System.out.printf("Dependency added: Task \"%s\" now depends on Task \"%s\".\n",
                dependent.getTitle(), preceding.getTitle());

        // Save updated tasks to CSV
        saveTasksToCSV();
    }

    // Detects cycles by checking if adding a dependency creates a loop
    private static boolean detectCycle(int dependentTask, int precedingTask) {
        Set<Integer> visited = new HashSet<>();
        return hasCycle(precedingTask, dependentTask, visited);
    }

    // Recursive helper function to detect cycles in the dependency graph
    private static boolean hasCycle(int currentTask, int targetTask, Set<Integer> visited) {
        if (currentTask == targetTask) {
            return true; // Cycle detected
        }

        if (visited.contains(currentTask)) {
            return false; // Already visited
        }

        visited.add(currentTask);

        // Check dependencies recursively
        GeneralTask task = tasks.get(currentTask - 1);
        if (task.getDependencies() != null) {
            for (int dependency : task.getDependencies()) {
                if (hasCycle(dependency, targetTask, visited)) {
                    return true;
                }
            }
        }

        return false; // No cycle found
    }

    // Validates task numbers
    private static boolean isValidTaskNumber(Integer taskNum) {
        return taskNum != null && taskNum > 0 && taskNum <= tasks.size();
    }

    public static void editTask() {
        // Load tasks from CSV before editing
        loadTasksFromCSV();

        System.out.println("\n=== Edit Task ===");
        System.out.print("Enter the task number you want to edit: ");
        int taskNumber = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (taskNumber <= 0 || taskNumber > tasks.size()) {
            System.out.println("Invalid task number.");
            return;
        }

        GeneralTask task = tasks.get(taskNumber - 1);

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

        String temp;
        switch (choice) {
            case 1:
                System.out.print("Enter the new title: ");
                temp = task.getTitle();
                task.setTitle(scanner.nextLine());
                System.out.printf("\nTitle updated: \"%s\" -> \"%s\"%n", temp, task.getTitle());
                break;
            case 2:
                System.out.print("Enter the new description: ");
                temp = task.getDescription();
                task.setDescription(scanner.nextLine());
                System.out.printf("\nDescription updated: \"%s\" -> \"%s\"%n", temp, task.getDescription());
                break;
            case 3:
                System.out.print("Enter the new due date (YYYY-MM-DD): ");
                String newDueDate = scanner.nextLine();
                if (isValidDate(newDueDate)) {
                    temp = task.getDueDate();
                    task.setDueDate(newDueDate);
                    System.out.printf("\nDue date updated: \"%s\" -> \"%s\"%n", temp, task.getDueDate());
                } else {
                    System.out.println("Invalid date format. Please try again.");
                    return;
                }
                break;
            case 4:
                System.out.print("Enter the new category: ");
                temp = task.getCategory();
                task.setCategory(scanner.nextLine());
                System.out.printf("\nCategory updated: \"%s\" -> \"%s\"%n", temp, task.getCategory());
                break;
            case 5:
                System.out.print("Enter the new priority (e.g., High, Medium, Low): ");
                temp = task.getPriority();
                String newPriority = scanner.nextLine();
                if (isValidPriority(newPriority)) {
                    task.setPriority(newPriority);
                    System.out.printf("\nPriority updated: \"%s\" -> \"%s\"%n", temp, task.getPriority());
                } else {
                    System.out.println("Invalid priority. Please try again.");
                    return;
                }
                break;
            case 6:
                System.out.println("Set Task Dependency:");
                setTaskDependency();
                break;
            case 7:
                System.out.println("Canceled editing.");
                return;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        // Save the updated tasks list to the CSV file
        saveTasksToCSV();

        System.out.println("\nUpdated task list:");
        viewAllTasks();
    }

    // Helper function to validate date format
    private static boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    // Helper function to validate priority values
    private static boolean isValidPriority(String priority) {
        return priority.equalsIgnoreCase("High") || priority.equalsIgnoreCase("Medium") || priority.equalsIgnoreCase("Low");
    }
    
    private static void viewAllTasks() {
        // Load tasks from CSV before viewing
        loadTasksFromCSV();

        if (tasks.isEmpty()) {
            System.out.println("\nNo tasks available!");
            return;
        }

        // Sort tasks by due date before displaying
        tasks.sort(Comparator.comparing(GeneralTask::getDueDate));

        System.out.println("\n=== View All Tasks ===");
        for (int i = 0; i < tasks.size(); i++) {
            GeneralTask task = tasks.get(i);
            String taskStatus = task.isComplete ? "[Complete]" : "[Incomplete]";
            String taskLabel = "Task " + (char) ('A' + i); // Convert index to Task A, Task B, etc.

            // Base task details without dependency information
            String taskInfo = String.format("%d. %s %s: %s - Due: %s - Priority: %s - Category: %s",
                    i + 1, taskStatus, taskLabel, task.getTitle(), task.getDueDate(),
                    task.getPriority(), task.getCategory());

            // Check if dependencies exist
            if (!task.dependencies.isEmpty()) {
                String dependencies = task.dependencies.stream()
                        .map(dep -> "Task " + (char) ('A' + dep - 1)) // Convert dependency indices to Task A, Task B, etc.
                        .collect(Collectors.joining(", "));
                taskInfo += " (Depends on " + dependencies + ")";
            }

            // Print the task information
            System.out.println(taskInfo);
        }
    }
}
