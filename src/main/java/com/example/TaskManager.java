package com.example;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.StorageSystem.StorageTask;
import static com.example.StorageSystem.loadTasksFromCSV;
import static com.example.StorageSystem.saveTasksToCSV;
import static com.example.StorageSystem.storageTasks;

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
        System.out.println("=== Add a New Task ===");
        // Title input
        String title = "";
        while (title.isEmpty()) {
            System.out.print("Enter task title: ");
            title = scanner.nextLine();
            if (title.isEmpty()) {
                System.out.println("Title cannot be empty. Please try again.");
            }
        }
        // Description input
        String description = "";
        while (description.isEmpty()) {
            System.out.print("Enter task description: ");
            description = scanner.nextLine();
            if (description.isEmpty()) {
                System.out.println("Description cannot be empty. Please try again.");
            }
        }
        // Due date input with validation for format (YYYY-MM-DD)
        String dueDate = "";
        while (true) {
            System.out.print("Enter due date (YYYY-MM-DD): ");
            dueDate = scanner.nextLine();
            if (dueDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                break; // Valid date format, exit the loop
            } else {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
        // Category input validation
        String category = "";
        while (!category.equalsIgnoreCase("Homework") && !category.equalsIgnoreCase("Personal") && !category.equalsIgnoreCase("Work")) {
            System.out.print("Enter task category (Homework, Personal, Work): ");
            category = scanner.nextLine();
            if (!category.equalsIgnoreCase("Homework") && !category.equalsIgnoreCase("Personal") && !category.equalsIgnoreCase("Work")) {
                System.out.println("Invalid category. Please enter one of the following: Homework, Personal, or Work.");
            }
        }
        // Priority input validation
        String priority = "";
        while (!priority.equalsIgnoreCase("Low") && !priority.equalsIgnoreCase("Medium") && !priority.equalsIgnoreCase("High")) {
            System.out.print("Enter priority level (Low, Medium, High): ");
            priority = scanner.nextLine();
            if (!priority.equalsIgnoreCase("Low") && !priority.equalsIgnoreCase("Medium") && !priority.equalsIgnoreCase("High")) {
                System.out.println("Invalid priority level. Please enter one of the following: Low, Medium, or High.");
            }
        }
        
        String isComplete = "incomplete";
        String dependencies = "";
        String recurrence = "";
        String nextCreationDate = "";
        
        // Create a new StorageTask with null values for the optional fields
        StorageTask newTask = new StorageTask(
            title, description, dueDate, category, priority, 
            isComplete, dependencies, recurrence, nextCreationDate
        );
        // Add the new task to the list of tasks
        StorageSystem.storageTasks.add(newTask);  // Add directly to storageTasks (not GeneralTask)
        StorageSystem.addTask(newTask);  // Use StorageSystem.addTask to handle storage
        
        // Convert the StorageTask to a GeneralTask and add it to the tasks list 
        GeneralTask generalTask = StorageSystem.convertToGeneralTask(newTask); 
        tasks.add(generalTask); 

        // Save the updated tasks to the CSV file 
        StorageSystem.saveTasksToCSV();
        
        System.out.println("\nTask \"" + title + "\" added successfully!");
    }

    private static void markTaskComplete() {
        System.out.println("=== Mark Task as Complete ===");
        System.out.print("Enter the task number you want to mark as complete: ");
        int taskId = scanner.nextInt();
        System.out.println();

        // Load tasks from CSV before proceeding
        loadTasksFromCSV();

        if (taskId >= 1 && taskId <= StorageSystem.storageTasks.size()) {
            StorageTask task = StorageSystem.storageTasks.get(taskId - 1);
            boolean canMarkComplete = true;

            // Check dependencies only if they exist
            if (task.getDependencies() != null && !task.getDependencies().isEmpty()) {
                for (Integer dep : task.getDependencies()) {
                    StorageTask depTask = StorageSystem.storageTasks.get(dep - 1);
                    if (depTask.getIsComplete().equals("false")) {
                        System.out.println("Warning: Task \"" + task.getTitle()
                                + "\" cannot be marked as complete because it depends on \"" + depTask.getTitle() + "\". Please complete it first.");
                        canMarkComplete = false;
                        break;
                    }
                }
            }

            if (canMarkComplete) {
                task.setIsComplete("complete");
                System.out.println("Task \"" + task.getTitle() + "\" marked as complete!");

                // Save the updated tasks back to CSV
                saveTasksToCSV();
            }
        } else {
            System.out.println("Invalid task number.");
        }
    }

    private static void deleteTask() {
        System.out.println("=== Delete a Task ===");
        System.out.print("Enter the task number you want to delete: ");

        // Load tasks from CSV
        loadTasksFromCSV();

        if (StorageSystem.storageTasks.isEmpty()) {
            System.out.println("No tasks to delete.");
            return;
        }

        int taskId;
        try {
            taskId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid task number.");
            return;
        }

        if (taskId >= 1 && taskId <= StorageSystem.storageTasks.size()) {
            // Remove the task
            StorageTask removedTask = StorageSystem.storageTasks.remove(taskId - 1);
            System.out.println("Task \"" + removedTask.getTitle() + "\" deleted successfully!");

            // Adjust dependencies for remaining tasks
            for (StorageTask task : StorageSystem.storageTasks) {
                List<Integer> dependencies = task.getDependencies();
                if (dependencies != null) {
                    // Remove reference to the deleted task
                    dependencies.remove(Integer.valueOf(taskId));

                    // Shift indices of dependencies greater than the deleted task
                    for (int i = 0; i < dependencies.size(); i++) {
                        if (dependencies.get(i) > taskId) {
                            dependencies.set(i, dependencies.get(i) - 1);
                        }
                    }

                    // Update the task's dependencies string
                    task.setDependencies(dependencies.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(";")));
                }
            }

            // Save updated tasks
            saveTasksToCSV();
        } else {
            System.out.println("Invalid task number.");
        }
    }
    
    private static void sortTasks() {
        System.out.println("=== Sort Tasks ===");
        System.out.println("Sort by:");
        System.out.println("1. Due Date (Ascending)");
        System.out.println("2. Due Date (Descending)");
        System.out.println("3. Priority (High to Low)");
        System.out.println("4. Priority (Low to High)");
        System.out.print("\n> ");
        int choice;
        try {
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number between 1 and 4.");
            scanner.nextLine(); // Clear the invalid input
            return;
        }

        // Load tasks from CSV before sorting
        loadTasksFromCSV();

        if (StorageSystem.storageTasks.isEmpty()) {
            System.out.println("No tasks available to sort.");
            return;
        }

        // Save the original order of tasks for dependency adjustments
        List<StorageTask> originalTasks = new ArrayList<>(StorageSystem.storageTasks);

        // Sort tasks based on the user's choice
        switch (choice) {
            case 1:
                StorageSystem.storageTasks.sort(Comparator.comparing(task -> task.dueDate, Comparator.nullsLast(Comparator.naturalOrder())));
                System.out.println("Tasks sorted by Due Date (Ascending)!");
                break;
            case 2:
                StorageSystem.storageTasks.sort(Comparator.comparing(task -> task.dueDate, Comparator.nullsLast(Comparator.reverseOrder())));
                System.out.println("Tasks sorted by Due Date (Descending)!");
                break;
            case 3:
                StorageSystem.storageTasks.sort((o1, o2) -> Integer.compare(
                    getPriorityValue(o2.getPriority()), getPriorityValue(o1.getPriority())));
                System.out.println("Tasks sorted by Priority (High to Low)!");
                break;
            case 4:
                StorageSystem.storageTasks.sort((o1, o2) -> Integer.compare(
                    getPriorityValue(o1.getPriority()), getPriorityValue(o2.getPriority())));
                System.out.println("Tasks sorted by Priority (Low to High)!");
                break;
            default:
                System.out.println("Invalid option. Please select a number between 1 and 4.");
                return;
        }

        // Adjust dependencies to reflect the new order
        updateDependenciesAfterSorting(StorageSystem.storageTasks, originalTasks);

        // Save the updated tasks back to CSV after sorting
        saveTasksToCSV();
    }

    /**
     * Updates dependencies after sorting to ensure they reflect the new task order.
     */
    private static void updateDependenciesAfterSorting(List<StorageTask> sortedTasks, List<StorageTask> originalTasks) {
        // Map original titles to their new indices
        Map<String, Integer> titleToNewIndex = new HashMap<>();
        for (int i = 0; i < sortedTasks.size(); i++) {
            titleToNewIndex.put(sortedTasks.get(i).getTitle(), i + 1); // 1-based index
        }

        // Adjust dependencies for each task
        for (StorageTask task : sortedTasks) {
            List<Integer> originalDependencies = task.getDependencies();
            if (originalDependencies != null && !originalDependencies.isEmpty()) {
                List<Integer> updatedDependencies = originalDependencies.stream()
                    .map(dep -> titleToNewIndex.get(originalTasks.get(dep - 1).getTitle())) // Map old index to new index
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

                // Update dependencies in the task
                task.setDependencies(updatedDependencies.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(";")));
            }
        }
    }

    /**
     * Converts priority strings to numeric values for sorting.
     */
    private static int getPriorityValue(String priority) {
        switch (priority.toLowerCase()) {
            case "high":
                return 3;
            case "medium":
                return 2;
            case "low":
                return 1;
            default:
                return 0; // Default for unknown priorities
        }
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

        for (int i = 0; i < StorageSystem.tasks.size(); i++) {
            GeneralTask task = StorageSystem.tasks.get(i);
            String title = task.getTitle() != null ? task.getTitle().toLowerCase() : "";
            String description = task.getDescription() != null ? task.getDescription().toLowerCase() : "";

            if (title.contains(keyword) || description.contains(keyword)) {
                // Format the output with task details
                String completionStatus = task.isComplete() ? "[Complete]" : "[Incomplete]";
                String dueDate = task.getDueDate() != null ? task.getDueDate() : "N/A";
                String category = task.getCategory() != null ? task.getCategory() : "N/A";
                String priority = task.getPriority() != null ? task.getPriority() : "N/A";

                // Print task in the desired format
                System.out.println((i + 1) + ". " + completionStatus + " " + task.getTitle() + " - Due: " + dueDate + " - Category: " + category + " - Priority: " + priority);
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
        String recurrence = "";
        while(!recurrence.equals("daily") && !recurrence.equals("weekly") && !recurrence.equals("monthly")) {
            System.out.print("Enter recurrence interval (daily, weekly, monthly): ");
            recurrence = scanner.nextLine().toLowerCase();
            if(!recurrence.equals("daily") && !recurrence.equals("weekly") && !recurrence.equals("monthly"))
            System.out.println("Invalid interval! Please enter 'daily', 'weekly', or 'monthly'.");
        }
        
        // Load tasks from CSV before adding the new task
        loadTasksFromCSV();

        LocalDate initialDueDate = LocalDate.now();

        GeneralTask task = new GeneralTask(title, description, initialDueDate.format(dateFormatter), recurrence);

        if (task.getNextCreationDate() != null) {
            // Convert GeneralTask to StorageTask before saving
            StorageTask storageTask = convertToStorageTask(task);
            StorageSystem.storageTasks.add(storageTask);
            System.out.println("\nRecurring task \"" + title + "\" added successfully!");

            // Save the updated task list to the CSV file
            saveTasksToCSV();
        }
    }
    
    // Conversion from GeneralTask to StorageTask
    public static StorageTask convertToStorageTask(GeneralTask generalTask) {
        String dependenciesString = generalTask.getDependencies().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        String nextCreationDate = (generalTask.getNextCreationDate() != null) 
            ? generalTask.getNextCreationDate().toString() 
            : "";
        
        // Determine if the task is complete or incomplete
        String completionStatus = generalTask.isComplete() ? "complete" : "incomplete";
    
        return new StorageTask(
            generalTask.getTitle(),
            generalTask.getDescription(),
            generalTask.getDueDate(),
            generalTask.getCategory(),
            generalTask.getPriority(),
            completionStatus,  // Use the string "complete" or "incomplete"
            dependenciesString,
            generalTask.getRecurrence(),
            nextCreationDate
        );
    }
    

    public static void generateRecurringTasks() {
        LocalDate today = LocalDate.now();

        // Check if tasks were already generated today
        if (lastGenerationDate != null && lastGenerationDate.equals(today)) {
            return;
        }

        List<StorageTask> newTasks = new ArrayList<>();
        Set<String> existingTasks = new HashSet<>();

        // Load tasks from CSV before generating recurring tasks
        loadTasksFromCSV();

        // Collect already existing tasks in the set to check for duplicates
        for (StorageTask task : storageTasks) {
            if (task.getNextCreationDate() != null) {
                existingTasks.add(task.getTitle() + task.getNextCreationDate().toString());
            }
        }

        for (StorageTask task : storageTasks) {
            if (task.isRecurring() && task.getNextCreationDate() != null) {
                LocalDate nextCreationDate = task.getNextCreationDate();
                while (!nextCreationDate.isAfter(today)) {
                    String taskKey = task.getTitle() + nextCreationDate.toString();
                    if (!existingTasks.contains(taskKey)) {
                        StorageTask newTask = new StorageTask(
                            task.getTitle(),
                            task.getDescription(),
                            task.getDueDate(),
                            task.getCategory(),
                            task.getPriority(),
                            "incomplete",
                            task.getDependenciesAsString(),
                            task.getRecurrence(),
                            nextCreationDate.toString()
                        );
                        newTasks.add(newTask);
                        existingTasks.add(taskKey);
                    }
                    nextCreationDate = getNextCreationDate(nextCreationDate, task.getRecurrence());
                }
                task.setNextCreationDate(nextCreationDate);
            }
        }

        // Add newly generated tasks
        storageTasks.addAll(newTasks);

        // Save the updated tasks list to the CSV file
        saveTasksToCSV();

        // Update the last generation date
        lastGenerationDate = today;
    }

    private static LocalDate getNextCreationDate(LocalDate currentDate, String recurrence) {
        switch (recurrence.toLowerCase()) {
            case "daily":
                return currentDate.plusDays(1);
            case "weekly":
                return currentDate.plusWeeks(1);
            case "monthly":
                return currentDate.plusMonths(1);
            default:
                throw new IllegalArgumentException("Unknown recurrence: " + recurrence);
        }
    }

    private static void setTaskDependency() {
        // Load tasks from CSV before setting the dependency
        StorageSystem.loadTasksFromCSV();

        System.out.println("=== Set Task Dependency ===");
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
    
        // Update the GeneralTask list 
        StorageSystem.tasks.set(dependentTask - 1, dependent);
        
        // Convert the updated GeneralTask list to StorageTask list 
        StorageSystem.storageTasks = StorageSystem.tasks.stream() .map(StorageSystem::convertToStorageTask) .collect(Collectors.toList());
        
        // Save updated tasks to CSV
        StorageSystem.saveTasksToCSV();
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

        System.out.println("=== Edit Task ===");
        System.out.print("Enter the task number you want to edit: ");
        int taskNumber = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (taskNumber <= 0 || taskNumber > StorageSystem.tasks.size()) {
            System.out.println("Invalid task number.");
            return;
        }

        GeneralTask task = StorageSystem.tasks.get(taskNumber - 1);
        StorageTask storageTask = storageTasks.get(taskNumber - 1);  // Get the corresponding StorageTask

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
                String newTitle = scanner.nextLine();
                task.setTitle(newTitle);
                storageTask.setTitle(newTitle);  // Update the corresponding StorageTask
                // If the task is recurring, update all instances with the same recurrence pattern
                if (!task.getRecurrence().isEmpty()) {
                    for (GeneralTask t : StorageSystem.tasks) {
                        if (t.getRecurrence().equals(task.getRecurrence()) && t.getTitle().equals(temp)) {
                            t.setTitle(newTitle);
                        }
                    }
                    for (StorageTask st : StorageSystem.storageTasks) {
                        if (st.getRecurrence().equals(storageTask.getRecurrence()) && st.getTitle().equals(temp)) {
                            st.setTitle(newTitle);
                        }
                    }
                }
                System.out.println("\nTask \"" + temp + "\" has been updated to \"" + task.getTitle() + ".\"");
                break;
            case 2:
                System.out.print("Enter the new description: ");
                temp = task.getDescription();
                String newDescription = scanner.nextLine();
                task.setDescription(newDescription);
                storageTask.setDescription(newDescription);  // Update the corresponding StorageTask

                // If the task is recurring, update all instances with the same recurrence pattern
                if (!task.getRecurrence().isEmpty()) {
                    for (GeneralTask t : StorageSystem.tasks) {
                        if (t.getRecurrence().equals(task.getRecurrence()) && t.getDescription().equals(temp)) {
                            t.setDescription(newDescription);
                        }
                    }
                    for (StorageTask st : StorageSystem.storageTasks) {
                        if (st.getRecurrence().equals(storageTask.getRecurrence()) && st.getDescription().equals(temp)) {
                            st.setDescription(newDescription);
                        }
                    }
                }
                System.out.println("\nTask \"" + temp + "\" has been updated to \"" + task.getDescription()+ ".\"");
                break;
            case 3:
                if (!task.getRecurrence().isEmpty()) {
                    System.out.println("This is a recurring task. Only the title and description can be edited.");
                    break;
                }
                System.out.print("Enter the new due date (YYYY-MM-DD): ");
                String newDueDate = scanner.nextLine();
                if (isValidDate(newDueDate)) {
                    temp = task.getDueDate();
                    task.setDueDate(newDueDate);
                    storageTask.setDueDate(newDueDate);  // Update the corresponding StorageTask
                    System.out.println("\nTask \"" + temp + "\" has been updated to \"" + task.getDueDate() + ".\"");

                } else {
                    System.out.println("Invalid date format. Please try again.");
                    return;
                }
                break;
            case 4:
                if (!task.getRecurrence().isEmpty()) {
                    System.out.println("This is a recurring task. Only the title and description can be edited.");
                    break;
                }
                System.out.print("Enter the new category: ");
                temp = task.getCategory();
                task.setCategory(scanner.nextLine());
                storageTask.setCategory(task.getCategory());  // Update the corresponding StorageTask
                System.out.println("\nTask \"" + temp + "\" has been updated to \"" + task.getCategory()+ ".\"");
                break;
            case 5:
                if (!task.getRecurrence().isEmpty()) {
                    System.out.println("This is a recurring task. Only the title and description can be edited.");
                    break;
                }
                System.out.print("Enter the new priority (e.g., High, Medium, Low): ");
                temp = task.getPriority();
                String newPriority = scanner.nextLine();
                if (isValidPriority(newPriority)) {
                    task.setPriority(newPriority);
                    storageTask.setPriority(newPriority);  // Update the corresponding StorageTask
                    System.out.println("\nTask \"" + temp + "\" has been updated to \"" + task.getPriority()+ ".\"");
                } else {
                    System.out.println("Invalid priority. Please try again.");
                    return;
                }
                break;
            case 6:
                if (!task.getRecurrence().isEmpty()) {
                    System.out.println("This is a recurring task. Only the title and description can be edited.");
                    break;
                }
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
        generateRecurringTasks();

        if (StorageSystem.storageTasks.isEmpty()) {
            System.out.println("\nNo tasks available!");
            return;
        }

        System.out.println("=== View All Tasks ===");
        for (int i = 0; i < StorageSystem.storageTasks.size(); i++) {
            StorageTask task = StorageSystem.storageTasks.get(i);
            String taskStatus = task.getIsComplete().equalsIgnoreCase("complete") ? "[Complete]" : "[Incomplete]";
            String taskLabel = "Task " + (char) ('A' + i); // Convert index to Task A, Task B, etc.

            // Base task details without dependency information
            String taskInfo = String.format("%d. %s %s: %s - Due: %s ",
                    i + 1, taskStatus, taskLabel, task.getTitle(), task.getDueDate()
                    );

            // Handle dependencies if they exist (task.getDependencies() should be a List<Integer>)
        List<Integer> dependencies = task.getDependencies(); // List<Integer> directly

        // Display dependencies if there are any
        if (dependencies != null && !dependencies.isEmpty()) {
            String dependenciesText = dependencies.stream()
                    .map(dep -> "Task " + (char) ('A' + dep - 1)) // Convert dependency indices to Task A, Task B, etc.
                    .collect(Collectors.joining(", "));
            taskInfo += " (Depends on " + dependenciesText + ")";
        }

            // Print the task information
            System.out.println(taskInfo);
        }
    }
}
