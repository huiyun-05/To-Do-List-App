package com.example;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GeneralTask {
    protected String title;
    protected String description;
    protected String dueDate;
    protected String category;
    protected String priority;
    protected boolean isComplete;
    protected List<Integer> dependencies; 
    protected String recurrence; // daily, weekly, monthly
    protected LocalDate nextCreationDate;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public static LocalDate parseDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public GeneralTask(String title, String description, String dueDate, String category, String priority) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.category = category;
        this.priority = priority;
    }
    
    public GeneralTask(String title, String description, String dueDate, String category, String priority, boolean isComplete, List<Integer> dependencies, String recurrence, LocalDate nextCreationDate) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.category = category;
        this.priority = priority;
        this.isComplete = isComplete;
        this.dependencies = dependencies;
        this.recurrence = recurrence;
        this.nextCreationDate = nextCreationDate;
    }
    
    public GeneralTask(String title, String description, String nextCreationDate, String recurrence) {
        try {
            this.nextCreationDate = LocalDate.parse(nextCreationDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            return;
        }
        this.title = title;
        this.description = description;
        this.recurrence = recurrence;
        this.isComplete = false;
        this.dependencies = new ArrayList<>();
    }
    
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

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
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

    public List<Integer> getDependencies() { 
        return dependencies; 
    }
    
    public void setDependencies(List<Integer> dependencies) { 
        this.dependencies = dependencies; 
    }
    
    public String getRecurrence() {
        return recurrence;
    }
    
    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }
    
    public boolean isRecurring() {
        return recurrence != null && !recurrence.isEmpty();
    }
    
    public LocalDate getNextCreationDate() {
        return nextCreationDate;
    }
    
    public void setNextCreationDate(LocalDate nextCreationDate) {
        this.nextCreationDate = nextCreationDate;
    }
    
    public LocalDate getNextDueDate() {
        switch (recurrence) {
            case "daily":
                return nextCreationDate.plusDays(1);
            case "weekly":
                return nextCreationDate.plusWeeks(1);
            case "monthly":
                return nextCreationDate.plusMonths(1);
            default:
                return nextCreationDate;
        }
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

    // Convert GeneralTask to a CSV-compatible string
    @Override
    public String toString() {
        String dependenciesString = dependencies == null ? "" : String.join(";", dependencies.stream().map(String::valueOf).toArray(String[]::new));
        String nextDateString = nextCreationDate == null ? "" : nextCreationDate.toString();
        return String.format("%s,%s,%s,%s,%s,%b,%s,%s,%s", 
            title, 
            description, 
            dueDate, 
            category, 
            priority, 
            isComplete, 
            dependenciesString, 
            recurrence, 
            nextDateString
        );
    }
    
    public void toggleCompleted() {
        this.isComplete = !this.isComplete;
        // Call StorageSystem to save updated task list after toggling completion
        StorageSystem.saveTasksToCSV();  // Ensure that save method is called here
    }
    // Method to convert Task to CSV format
    public String toCSVFormat() {
    return title + "," +
           description + "," +
           dueDate + "," +
           category + "," +
           priority + "," +
           isComplete() + "," +
           String.join(";", dependencies.stream().map(String::valueOf).collect(Collectors.toList())) + "," +
           recurrence + "," +
           (nextCreationDate != null ? nextCreationDate : "");
    }
    
    // Factory method to create a GeneralTask from a CSV line
    public static GeneralTask fromCSV(String csvLine) {
        String[] fields = csvLine.split(",", -1); // Handle empty fields gracefully
        String title = fields[0];
        String description = fields[1];
        String dueDate = fields[2];
        String category = fields[3];
        String priority = fields[4];
        boolean isComplete = Boolean.parseBoolean(fields[5]);
        List<Integer> dependencies = parseDependencies(fields[6]); // Convert dependency string to List<Integer>
        String recurrence = fields[7];
        LocalDate nextCreationDate = fields[8].isEmpty() ? null : LocalDate.parse(fields[8]);
        
        return new GeneralTask(title, description, dueDate, category, priority, isComplete, dependencies, recurrence, nextCreationDate);
    }
    // Helper to parse dependencies
    private static List<Integer> parseDependencies(String dependencyString) {
        if (dependencyString == null || dependencyString.isEmpty()) {
            return List.of();
        }
        String[] depArray = dependencyString.split(";");
        return List.of(depArray).stream().map(Integer::parseInt).toList();
    }
}
