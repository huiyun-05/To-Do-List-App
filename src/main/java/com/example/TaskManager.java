package com.example;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;
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
        StorageSystem.addTask(newTask);
        StorageSystem.saveTasksToCSV();
        System.out.println("\nTask \"" + title + "\" added successfully!");
    }

    private static void markTaskComplete() {
        System.out.println("\n=== Mark Task as Complete ===");
        System.out.print("Enter the task number you want to mark as complete:");
        int taskId = scanner.nextInt();
        System.out.println();
        
        if (taskId >= 1 && taskId <= tasks.size()) {
            GeneralTask task = tasks.get(taskId - 1);
            boolean canMarkComplete = true;
         
            for (Integer dep : task.dependencies) {
                if (!tasks.get(dep - 1).isComplete) {
                    System.out.println("Warning: Task \"" + task.title + "\" cannot be marked as complete because it depends on \"" 
                            + tasks.get(dep - 1).getTitle() + "\". Please complete \"" 
                            + tasks.get(dep - 1).getTitle() + "\" first.");
                    canMarkComplete = false;
                    break;
                }
            }

            if (canMarkComplete) {
                task.markComplete();
                System.out.println("Task \"" + task.title + "\" marked as complete!");
            }
        } else {
            System.out.println("Invalid task number.");
        }
    }

    private static void deleteTask() {
        System.out.println("\n=== Delete a Task ===");
        System.out.print("Enter the task number you want to delete:");
        int taskId = scanner.nextInt();
        
        if (taskId >= 1 && taskId <= tasks.size()) {
            // Remove dependencies from other tasks
            for (GeneralTask t : tasks) {
                t.dependencies.remove(Integer.valueOf(taskId));
            }
            GeneralTask task = tasks.remove(taskId - 1);
            System.out.println("Task \"" + task.title + "\" deleted successfully!");
            // Update dependencies of remaining tasks
            for (int i = 0; i < tasks.size(); i++) {
                task = tasks.get(i);

                // Remove references to the deleted task
                task.dependencies.removeIf(dep -> dep == taskId);

                // Adjust indices of dependencies greater than the deleted task ID
                for (int j = 0; j < task.dependencies.size(); j++) {
                    if (task.dependencies.get(j) > taskId) {
                        task.dependencies.set(j, task.dependencies.get(j) - 1);
                    }
                }
            }
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
            GeneralTask task = tasks.get(i);
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
        String recurrence = scanner.nextLine().toLowerCase();
        if (!recurrence.equals("daily") && !recurrence.equals("weekly") && !recurrence.equals("monthly")) {
            System.out.println("Invalid interval! Please enter 'daily', 'weekly', or 'monthly'.");
            return;
        }
        LocalDate initialDueDate = LocalDate.now();
        
        GeneralTask task = new GeneralTask(title, description, initialDueDate.format(dateFormatter), recurrence);
     
        if (task.nextCreationDate != null) {
            tasks.add(task);
            System.out.println("\nRecurring task \"" + title + "\" added successfully!");
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

        // Update the last generation date
        lastGenerationDate = today;
    }

    private static void setTaskDependency() {
        System.out.println("\n=== Set Task Dependency ===");
        System.out.print("Enter task number that depends on another task: ");
        int dependentTask = scanner.nextInt();
        System.out.print("Enter the task number it depends on: ");
        int precedingTask = scanner.nextInt();

        if (!isValidTaskNumber(dependentTask) || !isValidTaskNumber(precedingTask)) {
            System.out.println("Invalid task numbers.");
            return;
        }
        
        // Prevent a task from depending on itself
        if (dependentTask == precedingTask) {
            System.out.println("A task cannot depend on itself. Try again.");
            return;
        }   
            
         // Check if the dependency already exists
        if (tasks.get(dependentTask - 1).getDependencies().contains(precedingTask)) {
            System.out.println("This dependency already exists.");
            return;
        }
        
        if (detectCycle(dependentTask, precedingTask)) {
            System.out.println("Cannot set dependency. Cycle detected!");
            return;
        }

        tasks.get(dependentTask - 1).getDependencies().add(precedingTask);
        System.out.printf("Task \"%s\" now depends on \"%s\".\n",
                tasks.get(dependentTask - 1).getTitle(),
                tasks.get(precedingTask - 1).getTitle());
    }

    private static boolean detectCycle(int dependentTask, int precedingTask) {
        Set<Integer> visited = new HashSet<>();
        return hasCycle(precedingTask, dependentTask, visited);
    }

    private static boolean hasCycle(int currentTask, int targetTask, Set<Integer> visited) {
        if (currentTask == targetTask) {
            return true; // Cycle detected
        }

        if (visited.contains(currentTask)) {
            return false; // Already visited this task, no cycle from here
        }

        visited.add(currentTask);

        // Check dependencies recursively
        if (isValidTaskNumber(currentTask) && !tasks.get(currentTask - 1).getDependencies().isEmpty()) {
            for (int dependency : tasks.get(currentTask - 1).getDependencies()) {
                if (hasCycle(dependency, targetTask, visited)) {
                    return true; // Cycle detected through a dependency
                }
            }
        }

        return false; // No cycle detected
    }

    private static boolean isValidTaskNumber(Integer taskNum) {
        return taskNum != null && taskNum > 0 && taskNum <= tasks.size();
    }

    public static void editTask() {
        System.out.println("\n=== Edit Task ===");
        System.out.print("Enter the task number you want to edit: ");
        int taskNumber = scanner.nextInt();
        scanner.nextLine(); 
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
                setTaskDependency();
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
        generateRecurringTasks();
        for (int i = 0; i < tasks.size(); i++) {
         GeneralTask task = tasks.get(i);
         String taskStatus = task.isComplete ? "[Complete]" : "[Incomplete]";
         String taskLabel = "Task " + (char)('A' + i); // Convert index to Task A, Task B, etc.
         String dependencies = task.dependencies.isEmpty() ? "" : " (Depends on " + task.dependencies.stream()
                 .map(dep -> "Task " + (char)('A' + dep - 1))  // Convert dependency indices to Task A, Task B, etc.
                 .collect(Collectors.joining(", ")) + ")";

         // Print the task details
         System.out.println((i + 1) + ". " + taskStatus + " " + taskLabel + ": " + task.title + " - Due: " + task.dueDate + dependencies);
        }
    }
}
