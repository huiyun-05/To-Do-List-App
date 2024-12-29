import java.io.*;
import java.nio.file.*;
import java.util.*;

public class StorageSystem {
    // File path for storing the tasks in CSV format
    static final String CSV_FILE = "To-Do-List-App.csv";

    // List to store tasks
    static List<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {
        // You can add tasks here manually or load them from some source
        saveTasksToCSV();  // Save tasks to CSV file
    }

    // Save tasks to CSV file
    private static void saveTasksToCSV() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(CSV_FILE))) {
            // Writing the header row
            writer.write("Title,Description,Due Date (YYYY-MM-DD),Category (Homework/ Personal/ Work),Priority Level (low/ medium/ high),Completion Status (complete/ incomplete),Dependencies,Recurrence,Next Creation Date (YYYY-MM-DD)");
            writer.newLine();

            // Writing tasks data
            for (Task task : tasks) {
                writer.write(task.getTitle() + "," +
                        task.getDescription() + "," +
                        task.getDueDate() + "," +
                        task.getCategory() + "," +
                        task.getPriority() + "," +
                        task.isComplete() + "," +
                        String.join(";", task.getDependencies()) + "," +
                        task.getRecurrence() + "," +
                        (task.getNextCreationDate() != null ? task.getNextCreationDate() : ""));
                writer.newLine();
            }
            System.out.println("Tasks successfully saved to CSV file.");
        } catch (IOException e) {
            System.out.println("Error saving tasks to CSV file: " + e.getMessage());
        }
    }

    // Example of a Task class to represent tasks (You may modify it as needed)
    static class Task {
        private String title;
        private String description;
        private String dueDate;
        private String category;
        private String priority;
        private boolean isComplete;
        private List<String> dependencies;
        private String recurrence;
        private String nextCreationDate;

        // Constructor
        public Task(String title, String description, String dueDate, String category, String priority) {
            this.title = title;
            this.description = description;
            this.dueDate = dueDate;
            this.category = category;
            this.priority = priority;
            this.isComplete = false;
            this.dependencies = new ArrayList<>();
            this.recurrence = "";
            this.nextCreationDate = null;
        }

        // Getters and setters for Task fields
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getDueDate() { return dueDate; }
        public String getCategory() { return category; }
        public String getPriority() { return priority; }
        public boolean isComplete() { return isComplete; }
        public void setComplete(boolean isComplete) { this.isComplete = isComplete; }
        public List<String> getDependencies() { return dependencies; }
        public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
        public String getRecurrence() { return recurrence; }
        public void setRecurrence(String recurrence) { this.recurrence = recurrence; }
        public String getNextCreationDate() { return nextCreationDate; }
        public void setNextCreationDate(String nextCreationDate) { this.nextCreationDate = nextCreationDate; }
    }
}
