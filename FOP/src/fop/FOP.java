import java.time.LocalDate;
import java.util.Scanner;

public class Task {
    private String title;
    private String description;
    private LocalDate dueDate;
    private boolean isCompleted;
    private String category;
    private String priority;
    private String recurrenceInterval; // For recurring tasks (optional)
    Scanner sc = new Scanner(System.in);
    // Constructor
    public Task(String title, String description, LocalDate dueDate, boolean isCompleted, 
                String category, String priority, String recurrenceInterval) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.isCompleted = isCompleted;
        this.category = category;
        this.priority = priority;
        this.recurrenceInterval = recurrenceInterval;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean isRecurring() {
        return recurrenceInterval != null && !recurrenceInterval.isEmpty();
    }

    public String getRecurrenceInterval() {
        return recurrenceInterval;
    }

    public void setRecurrenceInterval(String recurrenceInterval) {
        this.recurrenceInterval = recurrenceInterval;
    }

     @Override
    public String toString() {
        return String.format("[%s] %s - Due: %s - Category: %s - Priority: %s", 
            isCompleted ? "Completed" : "Incomplete", title, dueDate, category, priority);
    }

        public void addTask() {
        System.out.print("Enter task title: ");
        this.title = sc.nextLine();
        System.out.print("Enter task description: ");
        this.description = sc.nextLine();
        System.out.print("Enter due date (YYYY-MM-DD): ");
        this.dueDate = LocalDate.parse(sc.nextLine());
        System.out.print("Enter task category: ");
        this.category = sc.nextLine();
        System.out.print("Enter priority (Low, Medium, High): ");
        this.priority = sc.nextLine();
        System.out.print("Enter recurrence interval (daily, weekly, monthly, or none): ");
        this.recurrenceInterval = sc.nextLine();

        Task task = new Task(title, description, dueDate, false, category, priority, 
            recurrenceInterval.equalsIgnoreCase("none") ? null : recurrenceInterval);
        taskList.add(task);
        System.out.println("Task added successfully!");
    }
}
public class TaskSearch {
    static Task[] tasks = new Task[10]; // Fixed-size array for tasks
    static int taskCount = 0;

    public static void main(String[] args) {
        // Add initial tasks
        tasks[0] = new Task("Finish Homework", "Complete the math homework", new Date(2024 - 1900, 11, 15), "Homework", "High");
        tasks[1] = new Task("Buy Groceries", "Purchase groceries for the week", new Date(2024 - 1900, 11, 14), "Personal", "Medium");
        tasks[2] = new Task("Submit Project", "Submit the final project report", new Date(2024 - 1900, 11, 20), "Work", "Low");
        taskCount = 3;

        // Example of task searching
        TaskSearching(tasks);
        
        // Viewing tasks
        viewTasks(tasks);
        
        // Execute recurring task creation
        RecurringTasks();
        
        // View tasks again to see the added recurring task
        viewTasks(tasks);
        
        // Editing tasks
        editTask();
        
        // View tasks again to see the changes
        viewTasks(tasks);
    }

    public void viewTasks(Task[] tasks) {
        System.out.println("=== View Tasks ===");
        for (int i = 0; i < taskCount; i++) {
            System.out.println((i + 1) + ". " + tasks[i]);
        }
    }

    public void TaskSearching(Task[] tasks) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Search Tasks ===");
        System.out.print("Enter a keyword to search by title or description: ");
        String keyword = sc.nextLine().toLowerCase();

        System.out.println("\n=== Search Results ===");
        boolean found = false;
        for (int i = 0; i < taskCount; i++) {
            Task task = tasks[i];
            // Check if the keyword is in the title or description
            if (task.title.toLowerCase().contains(keyword.toLowerCase()) || task.description.toLowerCase().contains(keyword.toLowerCase())) {
                System.out.println((i + 1) + ". " + task);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No tasks matched the search keyword.");
        }
    }

    public static void RecurringTasks() {
        Timer timer = new Timer();
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Add a Recurring Task ===");
        System.out.print("Enter task title: ");
        String title = sc.nextLine();
        System.out.print("Enter task description: ");
        String description = sc.nextLine();
        System.out.print("Enter recurrence interval (daily, weekly, monthly): ");
        String interval = sc.nextLine();
        long period = 0;
        switch(interval.toLowerCase()){
            case "daily":
                period = 1000 * 60 * 60 * 24;
                break;
            case "weekly":
                period = 1000 * 60 * 60 * 24 * 7;
                break;
            case "monthly":
                period = 1000 * 60 * 60 * 24 * 30;
                break;
            default:
                System.out.println("Invalid interval. Please enter 'daily', 'weekly', or 'monthly'.");
                return;
        }
        TimerTask task = new TimerTask() { 
            @Override 
            public void run() { 
                System.out.println("Executing task: " + title);
                if (taskCount < tasks.length) {
                    tasks[taskCount++] = new Task(title, description, new Date(), "Recurring", "None");
                } else {
                    System.out.println("Task list is full. Cannot add more tasks.");
                }
            }
        }; 
        long delay = 0; 
        timer.scheduleAtFixedRate(task, delay, period);
        System.out.println("Recurring Task \"" + title + "\" created successfully!");
    }

    public static void editTask() {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Edit Task ===");
        System.out.print("Enter the task number you want to edit: ");
        int taskNumber = sc.nextInt();
        sc.nextLine(); // Consume newline

        if (taskNumber <= 0 || taskNumber > taskCount) {
            System.out.println("Invalid task number.");
            return;
        }

        Task task = tasks[taskNumber - 1];

        System.out.println("What would you like to edit?");
        System.out.println("1. Title");
        System.out.println("2. Description");
        System.out.println("3. Due Date");
        System.out.println("4. Category");
        System.out.println("5. Priority");
        System.out.println("6. Cancel");
        System.out.print("> ");
        int choice = sc.nextInt();
        sc.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                System.out.print("Enter the new title: ");
                task.title = sc.nextLine();
                break;
            case 2:
                System.out.print("Enter the new description: ");
                task.description = sc.nextLine();
                break;
            case 3:
                System.out.print("Enter the new due date (YYYY-MM-DD): ");
                String dateStr = sc.nextLine();
                task.dueDate = new Date(Integer.parseInt(dateStr.substring(0, 4)) - 1900,
                                         Integer.parseInt(dateStr.substring(5, 7)) - 1,
                                         Integer.parseInt(dateStr.substring(8, 10)));
                break;
            case 4:
                System.out.print("Enter the new category: ");
                task.category = sc.nextLine();
                break;
            case 5:
                System.out.print("Enter the new priority: ");
                task.priority = sc.nextLine();
                break;
            case 6:
                System.out.println("Edit cancelled.");
                return;
            default:
                System.out.println("Invalid choice.");
        }

        System.out.println("Task updated successfully!");
    }
}
