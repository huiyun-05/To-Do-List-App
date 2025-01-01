package com.example;

import com.example.Task;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class StorageSystem {
    // File path for storing the tasks in CSV format
    static final String CSV_FILE = "To-Do-List-App.csv";

    // List to store tasks
    private static List<Task> tasks = new ArrayList<>();
    
    // Retrieve the list of tasks
    public static List<Task> getTasks() {
        return tasks;
    }
    
    public static void addTask(Task task) {
        tasks.add(task);
    }

    public static void main(String[] args) {
        // Load tasks from the CSV file
        loadTasksFromCSV();
        // You can add tasks here manually or load them from some source
        saveTasksToCSV();  // Save tasks to CSV file
    }
    
    // Load tasks from the CSV file
    public static void loadTasksFromCSV() {
        tasks.clear();  // Clear existing tasks 
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(CSV_FILE))) {
            String line;
            reader.readLine(); // Skip the header line

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                String title = parts[0];
                String description = parts[1];
                String dueDate = parts[2];
                String category = parts[3];
                String priority = parts[4];
                boolean completed = Boolean.parseBoolean(parts[5]);

                // Parse dependencies as a List<Integer>
                List<Integer> dependencies = Arrays.stream(parts[6].split(";"))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                String recurrence = parts[7];
                String nextCreationDate = parts[8];

                // Create and add the task
                Task task = new Task(title, description, dueDate, category, priority);
                task.setComplete(completed);
                task.setDependencies(dependencies);
                task.setRecurrence(recurrence);
                task.setNextCreationDate(nextCreationDate);
                tasks.add(task);
            }
            System.out.println("Tasks successfully loaded from CSV file.");
            reader.close();  // Close the reader after use
        } catch (IOException e) {
            System.err.println("Error loading tasks from CSV file: " + e.getMessage());
        }
    }
    
    public static void saveTasksToCSV() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(CSV_FILE))) {
            // Writing the header row
            writer.write("Title,Description,Due Date (YYYY-MM-DD),Category (Homework/ Personal/ Work),Priority Level (low/ medium/ high),Completion Status (complete/ incomplete),Dependencies,Recurrence,Next Creation Date (YYYY-MM-DD)");
            writer.newLine();

            // Writing tasks data
            for (Task task : tasks) {
                writer.write(task.getTitle() + ","
                        + task.getDescription() + ","
                        + task.getDueDate() + ","
                        + task.getCategory() + ","
                        + task.getPriority() + ","
                        + (task.isComplete() ? "complete" : "incomplete") + ","
                        + String.join(";", task.getDependencies().stream().map(String::valueOf).collect(Collectors.toList())) + ","
                        + task.getRecurrence() + ","
                        + (task.getNextCreationDate() != null ? task.getNextCreationDate() : ""));
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
        private List<Integer> dependencies;
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
        public List<Integer> getDependencies() { return dependencies; }
        public void setDependencies(List<Integer> dependencies) { this.dependencies = dependencies; }
        public String getRecurrence() { return recurrence; }
        public void setRecurrence(String recurrence) { this.recurrence = recurrence; }
        public String getNextCreationDate() { return nextCreationDate; }
        public void setNextCreationDate(String nextCreationDate) { this.nextCreationDate = nextCreationDate; }
    }
}
