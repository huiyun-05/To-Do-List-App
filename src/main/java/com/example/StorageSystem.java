package com.example;
import com.example.GeneralTask;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class StorageSystem {
    public static class StorageTask extends GeneralTask{
        private String title;
        private String description;
        private String dueDate;
        private String category;
        private String priority;

        // Constructor
        public StorageTask(String title, String description, String dueDate, String category, String priority) {
            super(title, description, dueDate, category, priority);
        }

        // Getters
        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String getDueDate() {
            return dueDate;
        }

        @Override
        public String getCategory() {
            return category;
        }

        @Override
        public String getPriority() {
            return priority;
        }

        // Setters (optional, if tasks need to be updated)
        @Override
        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public void setDueDate(String dueDate) {
            this.dueDate = dueDate;
        }

        @Override
        public void setCategory(String category) {
            this.category = category;
        }

        @Override
        public void setPriority(String priority) {
            this.priority = priority;
        }

        // For CSV serialization/deserialization
        @Override
        public String toString() {
            return String.format("%s,%s,%s,%s,%s", title, description, dueDate, category, priority);
        }

        public static StorageTask fromCSV(String csvLine) {
            String[] fields = csvLine.split(",", -1); // Ensure all fields are captured
            return new StorageTask(
                fields[0], // title
                fields[1], // description
                fields[2], // dueDate
                fields[3], // category
                fields[4]  // priority
            );
        }
    }
    // File path for storing the tasks in CSV format
    private static final String CSV_FILE = "To-Do-List-App.csv";

    // List to store tasks
    static List<GeneralTask> tasks = new ArrayList<>();
    private static List<StorageTask> storageTasks = new ArrayList<>();
    
    // Retrieve the list of tasks
    public static List<GeneralTask> getTasks() {
        return storageTasks.stream()
        .map(storageTask -> new GeneralTask(
            storageTask.getTitle(),
            storageTask.getDescription(),
            storageTask.getDueDate(),
            storageTask.getCategory(),
            storageTask.getPriority()))
        .collect(Collectors.toList());
    }
    public static List<StorageTask> getStorageTasks() {
        return storageTasks;
    }

    
    public static void addTask(GeneralTask task) {
        tasks.add(task);
        System.out.println("Task added to storage.");
    }

    public static void main(String[] args) {
        // Load tasks from the CSV file
        loadTasksFromCSV();
        // Prompt user for task details via TaskManager
        TaskManager.addTask();  // Call TaskManager's method to add the task
        saveTasksToCSV();  // Save tasks to CSV file
    }
    
    // Load tasks from CSV file
    public static void loadTasksFromCSV() {
        tasks.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                tasks.add(GeneralTask.fromCSV(line));
            }
        } catch (IOException e) {
            System.err.println("Error reading tasks from file: " + e.getMessage());
        }
    }
    
    // Save tasks to CSV file
    public static void saveTasksToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE))) {
            for (GeneralTask task : tasks) {
                writer.write(task.toCSVFormat());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing tasks to file: " + e.getMessage());
        }
    }
}