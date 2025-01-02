package com.example;
import com.example.GeneralTask;
import static com.example.StorageSystem.StorageTask.parseDependencies;
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

            this.title = title;
            this.description = description;
            this.dueDate = dueDate;
            this.category = category;
            this.priority = priority;
            this.isComplete = isCompleteStr;
            this.dependencies = dependenciesStr;
            this.recurrence = recurrence;
            this.nextCreationDate = nextCreationDateStr;
        }
        
        public StorageTask(String title, String description, String dueDate, String category, String priority) {
            super(title, description, dueDate, category, priority); // Pass to parent constructor
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
        public String getDependenciesAsString() {
            return dependencies;
        }
        
        @Override
        public String getRecurrence() {
            return recurrence;
        }
        
        public static LocalDate parseNextCreationDate(String nextCreationDateStr) {
            if (nextCreationDateStr != null && !nextCreationDateStr.isEmpty()) {
                return LocalDate.parse(nextCreationDateStr, DateTimeFormatter.ISO_DATE);
            }
            return null;
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
                return Arrays.stream(dependencies.split(",")).map(Integer::parseInt).collect(Collectors.toList());
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
            return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", title, description, dueDate, category, priority, isComplete, dependencies, recurrence, nextCreationDate);
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
        
        // Helper method to parse the dependencies
        public static List<Integer> parseDependencies(String dependencyString) {
            List<Integer> dependencies = new ArrayList<>();
            if (!dependencyString.isEmpty()) {
                String[] depArray = dependencyString.split(";");
                for (String dep : depArray) {
                    dependencies.add(Integer.parseInt(dep));
                }
            }
            return dependencies;
        }
    }
    
    // File path for storing the tasks in CSV format
    private static final String CSV_FILE = "To-Do-List-App.csv";
    // List to store tasks
    static List<GeneralTask> tasks = new ArrayList<>();
    private static List<StorageTask> storageTasks = new ArrayList<>();
    
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
        String dependenciesString = String.join(";", generalTask.getDependencies().stream().map(String::valueOf).collect(Collectors.toList()));
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
                writer.write(task.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing tasks to file: " + e.getMessage());
        }
    }
}