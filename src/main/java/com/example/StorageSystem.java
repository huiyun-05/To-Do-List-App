package com.example;
import com.example.GeneralTask;
import static com.example.GeneralTask.parseNextCreationDate;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class StorageSystem {
    public static class StorageTask extends GeneralTask{
        private String isComplete;  // Will be stored as "true" or "false"
        private String dependencies;  // Will be stored as a comma-separated String (e.g., "1,2,3")
        private String nextCreationDate;
        private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      
        // Constructor using String for all parameters
        public StorageTask(
            String title, 
            String description, 
            String dueDate, 
            String category, 
            String priority, 
            String isCompleteStr, 
            String dependenciesStr, 
            String recurrence, 
            String nextCreationDateStr
        ) {
            super(
                title, 
                description, 
                dueDate, 
                category, 
                priority, 
                Boolean.parseBoolean(isCompleteStr), 
                parseDependencies(dependenciesStr), 
                recurrence, 
                parseNextCreationDate(nextCreationDateStr)
            );

            this.isComplete = isCompleteStr;
            this.dependencies = dependenciesStr;
            this.nextCreationDate = nextCreationDateStr;
        }
        
        public StorageTask(String title, String description, String dueDate, String category, String priority) {
            super(title, description, dueDate, category, priority); // Pass to parent constructor
            this.isComplete = "false";
            this.dependencies = "";
            this.nextCreationDate = "";
        }

        // Utility method to parse dependencies from a comma-separated string
        private static List<Integer> parseDependencies(String dependenciesStr) {
            List<Integer> dependenciesList = new ArrayList<>();
            if (dependenciesStr != null && !dependenciesStr.isEmpty()) {
                String[] deps = dependenciesStr.split(";");
                for (String dep : deps) {
                    // Extract dependency number only 
                    String[] parts = dep.split(":");
                    dependenciesList.add(Integer.parseInt(dep.trim())); // Parse only numeric dependencies
                }
            }
            return dependenciesList;
        }
        
        @Override 
        public String getDependenciesAsString() {
            return dependencies;
        }
        
        // Utility method to parse next creation date from a string
        public static LocalDate parseNextCreationDate(String nextCreationDateStr) {
            if (nextCreationDateStr != null && !nextCreationDateStr.isEmpty()) {
                return LocalDate.parse(nextCreationDateStr, DATE_FORMATTER);  // Parse the string into a LocalDate
            }
            return null;  // Return null if no next creation date is provided
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

        public String getIsComplete() {
            return isComplete;
        }

        @Override
        public List<Integer> getDependencies() {
            return parseDependencies(dependencies);
        }

        @Override
        public String getRecurrence() {
            return recurrence;
        }

        @Override
        public LocalDate getNextCreationDate() {
            return parseNextCreationDate(nextCreationDate);
        }

        // Optional: String representation
        public String getNextCreationDateAsString() {
            return nextCreationDate;
        }
            
        // Instance method for parsing dependencies
        public List<Integer> parseDependencies() {
            if (dependencies != null && !dependencies.isEmpty()) {
                return Arrays.stream(dependencies.split(";")).map(Integer::parseInt).collect(Collectors.toList());
            }
            return new ArrayList<>();
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

        public void setIsComplete(String isComplete) {
            this.isComplete = isComplete;
        }

        public void setDependencies(String dependencies) {
            this.dependencies = dependencies;
        }

        public void setRecurrence(String recurrence) {
            this.recurrence = recurrence;
        }

        public void setNextCreationDate(String nextCreationDate) {
            this.nextCreationDate = nextCreationDate;
        }

        // For CSV serialization/deserialization
        @Override
        public String toString() {
            return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s", title, description, dueDate, category, priority, isComplete, dependencies, recurrence, nextCreationDate);
        }

        public static StorageTask fromCSV(String csvLine) {
            String[] fields = csvLine.split(",", -1); // Ensure all fields are captured
            
            String title = fields[0];
            String description = fields[1];
            String dueDate = fields[2];
            String category = fields[3];
            String priority = fields[4];
            String isComplete = fields[5]; 
            String dependencies = fields[6]; 
            String recurrence = fields[7];
            String nextCreationDate = fields[8];
            
            // Parse dependencies and nextCreationDate
            List<Integer> dependenciesList = parseDependencies(dependencies);
            LocalDate nextCreationDateParsed = nextCreationDate.isEmpty() ? null : LocalDate.parse(nextCreationDate);
    
            return new StorageTask(
                title,
                description,
                dueDate,
                category,
                priority,
                isComplete,
                dependencies,
                recurrence,
                nextCreationDate
            );
        }
    }
    
    // File path for storing the tasks in CSV format
    private static final String CSV_FILE = "To-Do-List-App.csv";
    // List to store tasks
    static List<GeneralTask> tasks = new ArrayList<>();
    public static List<StorageTask> storageTasks = new ArrayList<>();
    
    // Retrieve the list of tasks
    public static List<GeneralTask> getTasks() {
        return storageTasks.stream().map(storageTask -> {
            // Convert StorageTask to GeneralTask using the constructor
            GeneralTask task = new GeneralTask(
                storageTask.getTitle(),
                storageTask.getDescription(),
                storageTask.getDueDate(),
                storageTask.getCategory(),
                storageTask.getPriority(),
                Boolean.parseBoolean(storageTask.getIsComplete()),  // Convert isComplete from String to boolean
                new ArrayList<>(),  // Empty dependencies list for now
                storageTask.getRecurrence(),
                storageTask.getNextCreationDate()
            );

            // Set the dependencies if they exist in the StorageTask
            if (!storageTask.getDependencies().isEmpty()) {
                List<Integer> deps = new ArrayList<>();
                for (String dep : storageTask.getDependenciesAsString().split(";")) {
                    deps.add(Integer.parseInt(dep));  // Convert each dependency to Integer and add to the list
                }
                task.setDependencies(deps);  // Set the dependencies in GeneralTask
            }

            return task;
        }).collect(Collectors.toList());
    }
    public static void setStorageTasks(List<StorageTask> tasks) {
        storageTasks = tasks;
    }
    
    public static List<StorageTask> getStorageTasks() {
        return storageTasks;
    }

    
    public static void addTask(GeneralTask task) {
        tasks.add(task);
        System.out.println("Task added to storage.");
    }
    
    // Conversion from StorageTask to GeneralTask
    public static GeneralTask convertToGeneralTask(StorageTask storageTask) {
        List<Integer> dependenciesList = StorageTask.parseDependencies(storageTask.dependencies);
        LocalDate nextCreationDate = (storageTask.nextCreationDate != null && !storageTask.nextCreationDate.isEmpty())
                ? LocalDate.parse(storageTask.nextCreationDate) : null;
        return new GeneralTask(
            storageTask.title,
            storageTask.description,
            storageTask.dueDate,
            storageTask.category,
            storageTask.priority,
            Boolean.parseBoolean(storageTask.isComplete), // Convert isComplete from String to boolean
            dependenciesList,
            storageTask.recurrence,
            nextCreationDate
        );
    }
    // Conversion from GeneralTask to StorageTask
    public static StorageTask convertToStorageTask(GeneralTask generalTask) {
        String dependenciesString = generalTask.getDependencies().stream() .map(String::valueOf) .collect(Collectors.joining(";"));
        String nextCreationDate = (generalTask.getNextCreationDate() != null) ? generalTask.getNextCreationDate().toString() : "";
        return new StorageTask(
            generalTask.title,
            generalTask.description,
            generalTask.dueDate,
            generalTask.category,
            generalTask.priority,
            Boolean.toString(generalTask.isComplete),  // Convert boolean to String
            dependenciesString,
            generalTask.getRecurrence(),
            nextCreationDate
        );
    }
    
    public static void displayTaskWithDependencies(GeneralTask task) {
        String dependencyMessages = task.getDependencies().stream()
                .map(dep -> StorageSystem.tasks.get(dep - 1).getTitle())
                .collect(Collectors.joining("; "));

        System.out.printf("Task \"%s\" depends on: %s\n", task.getTitle(), dependencyMessages);
    }

    // Load tasks from CSV file
    public static void loadTasksFromCSV() {
        storageTasks.clear();  // Clear existing tasks

        try (BufferedReader reader = new BufferedReader(new FileReader("To-Do-List-App.csv"))) {
            String line;
            reader.readLine();  // Skip the header row

            while ((line = reader.readLine()) != null) {
                String[] taskData = line.split(",");
                System.out.println(Arrays.toString(taskData));  // This will print the array for each row
                // Check if there are enough columns in the line
                if (taskData.length >= 6) {  // Check if there are at least 6 fields
                    String title = taskData[0];
                    String description = taskData[1];
                    String dueDate = taskData[2];
                    String category = taskData[3];
                    String priority = taskData[4];
                    String isComplete = taskData[5];
                    String dependencies = taskData.length > 6 ? taskData[6] : "";  // Default empty if missing
                    String recurrence = taskData.length > 7 ? taskData[7] : "";  // Default empty if missing
                    String nextCreationDate = taskData.length > 8 ? taskData[8] : "";  // Default empty if missing
                    
                    StorageTask task = new StorageTask(
                            title, description, dueDate, category, priority,
                            isComplete, dependencies, recurrence, nextCreationDate
                    );
                    StorageSystem.storageTasks.add(task);
                } else {
                    System.out.println("Skipping invalid line: " + line);  // Handle invalid lines
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading from CSV: " + e.getMessage());
        }
        tasks.clear();  // Clear the in-memory task list before loading new tasks
        for (StorageTask storageTask : storageTasks) {
            GeneralTask generalTask = new GeneralTask( 
                    storageTask.getTitle(), 
                    storageTask.getDescription(), 
                    storageTask.getDueDate(), 
                    storageTask.getCategory(), 
                    storageTask.getPriority(), 
                    Boolean.parseBoolean(storageTask.getIsComplete()), 
                    StorageTask.parseDependencies(storageTask.getDependenciesAsString()), 
                    storageTask.getRecurrence(), 
                    storageTask.getNextCreationDate() 
            ); 
            tasks.add(generalTask); 
        }
    }

    // Save tasks to CSV file
    public static void saveTasksToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("To-Do-List-App.csv"))) {
            // Write the header with the new column
            writer.write("Title,Description,Due Date (YYYY-MM-DD),Category (Homework/ Personal/ Work),Priority Level (low/ medium/ high),Completion Status (complete/ incomplete),Dependencies,Recurrence,Next Creation Date (YYYY-MM-DD)");
            writer.newLine();

            // Write each task in CSV format
            for (StorageTask task : storageTasks) {
                writer.write(String.join(",",
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getCategory(),
                task.getPriority(),
                task.getIsComplete(),
                task.getDependenciesAsString(),
                task.getRecurrence(),
                task.getNextCreationDateAsString()
                ));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to CSV: " + e.getMessage());
        }
    }
    
    public static List<Integer> parseDependencies(String dependenciesStr) {
        if (dependenciesStr == null || dependenciesStr.trim().isEmpty()) {
            return new ArrayList<>();  // Return an empty list for missing dependencies
        }
        return Arrays.stream(dependenciesStr.split(";"))
                .map(dep -> {
                    try {
                        return Integer.parseInt(dep);  // Try to parse dependency as integer
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid dependency format: " + dep);  // Log the error
                        return null;  // Skip invalid dependency
                    }
                })
                .filter(Objects::nonNull) // Filter out any null values from invalid dependencies
                .collect(Collectors.toList());
    }
}
