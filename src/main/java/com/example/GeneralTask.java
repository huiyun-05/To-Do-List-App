package com.example;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
    private String dependenciesAsString;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public GeneralTask(String title, String description, String dueDate, String category, String priority) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.category = category;
        this.priority = priority;
        this.dependencies = new ArrayList<>();
    }
    
    public GeneralTask(String title, String description, String dueDate, String category, String priority, boolean isComplete, List<Integer> dependencies, String recurrence, LocalDate nextCreationDate) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.category = category;
        this.priority = priority;
        this.isComplete = isComplete;
        this.dependencies = (dependencies != null) ? dependencies : new ArrayList<>();
        this.recurrence = recurrence;
        this.nextCreationDate = nextCreationDate;
    }
    
    public GeneralTask(String title, String description, String nextCreationDate, String recurrence) {
        try {
            this.nextCreationDate = parseDate(nextCreationDate);
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
    
    public String getIsComplete() {
        return isComplete ? "complete" : "incomplete";
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
    
    // Getter for dependencies
    public List<Integer> getDependencies() { 
        return dependencies; 
    }
    
    // Setter for dependencies 
    public void setDependencies(List<Integer> dependencies) { 
        this.dependencies = dependencies; 
    }
    
    // Getter for dependenciesAsString 
    public String getDependenciesAsString() { 
        if (dependencies == null || dependencies.isEmpty()) { 
            return ""; 
        } 
        return dependencies.stream() .map(dep -> "depends on \"" + dep + "\"") .collect(Collectors.joining(", ")); 
    }
    
    // Setter for dependenciesAsString 
    public void setDependenciesAsString(String dependenciesAsString) { 
        this.dependenciesAsString = dependenciesAsString; 
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
    
    public String getNextCreationDateAsString() {
        if (nextCreationDate == null) {
            return "";
        }
        return nextCreationDate.toString(); // Convert LocalDate to String
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
        String dependencyStr = dependencies.isEmpty() ? "None" : dependencies.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        String recurrenceStr = (recurrence == null || recurrence.isEmpty()) ? "None" : recurrence;
        String nextDateStr = (nextCreationDate == null) ? "Not Set" : nextCreationDate.toString();

        return String.format(
                "Title: %s\nDescription: %s\nDue Date: %s\nCategory: %s\nPriority: %s\nStatus: %s\nDependencies: %s\nRecurrence: %s\nNext Creation Date: %s",
                title, description, dueDate, category, priority, isComplete ? "Complete" : "Incomplete",
                dependencyStr, recurrenceStr, nextDateStr
        );
    }

    public void toggleCompleted() {
        this.isComplete = !this.isComplete;
        // Call StorageSystem to save updated task list after toggling completion
    }
    
    // Method to convert Task to CSV format
    public String toCSV() {
        return String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                escapeCsvField(getTitle()),
                escapeCsvField(getDescription()),
                escapeCsvField(getDueDate()),
                escapeCsvField(getCategory()),
                escapeCsvField(getPriority()),
                isComplete ? "complete" : "incomplete",
                escapeCsvField(String.join(";", dependencies.stream().map(String::valueOf).collect(Collectors.toList()))),
                escapeCsvField(getRecurrence()),
                nextCreationDate != null ? nextCreationDate.toString() : "");
    }

    // Helper to escape commas and quotes in fields
    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        return field.contains(",") || field.contains("\"") ? "\"" + field.replace("\"", "\"\"") + "\"" : field;
    }

    // Factory method to create a GeneralTask from a CSV line
    public static GeneralTask fromCSV(String csvLine) {
        String[] fields = csvLine.split(",", -1); // Handle empty fields gracefully

        // Ensure the fields have the correct number of columns (9 in total now)
        if (fields.length != 9) {
            throw new IllegalArgumentException("Invalid CSV format. Expected 9 fields.");
        }

        String title = fields[0];
        String description = fields[1];
        String dueDate = fields[2];
        String category = fields[3];
        String priority = fields[4];
        boolean isComplete = fields[5].equalsIgnoreCase("complete");
        List<Integer> dependencies = parseDependencies(fields[6].isEmpty() ? null : fields[6]); // Convert dependency string to List<Integer>
        String recurrence = fields[7];
        // Parse nextCreationDate directly
        LocalDate parsedLocalDate = parseNextCreationDate(fields[8]);

        // Create and return the GeneralTask object
        return new GeneralTask(title, description, dueDate, category, priority, isComplete, dependencies, recurrence, parsedLocalDate);
    }
    // Helper to parse dependencies
    private static List<Integer> parseDependencies(String dependenciesStr) {
        if (dependenciesStr == null || dependenciesStr.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(dependenciesStr.split("[,;]")) // Allow both ',' and ';' as delimiters
                .map(dep -> {
                    try {
                        return Integer.parseInt(dep.trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid dependency: " + dep);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    // Helper method to parse date with error handling
    private static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null; // Return null if the date string is empty
        }

        try {
            return LocalDate.parse(dateString); // Parse string into LocalDate
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing date: " + dateString);
            return null; // Return null or handle the error if necessary
        }
    }
    // Helper method to parse next creation date
    public static LocalDate parseNextCreationDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;  // Return null if the date string is empty or null
        }
        try {
            return LocalDate.parse(dateStr);  // Attempt to parse the date string
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format: " + dateStr);  // Print error message
            return null;  // Return null if parsing fails
        }
    }
}