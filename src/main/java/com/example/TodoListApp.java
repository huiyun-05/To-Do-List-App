package com.example;
import com.example.Task;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.Optional;

public class TodoListApp extends Application {
    
    public static List<TodoListApp.Task> convertTasks(List<StorageSystem.Task> storageTasks) {
        List<TodoListApp.Task> appTasks = new ArrayList<>();
        for (StorageSystem.Task storageTask : storageTasks) {
            // Assuming both Task classes have similar fields, map them accordingly
            TodoListApp.Task appTask = new TodoListApp.Task(
                storageTask.getTitle(), 
                storageTask.getDescription(), 
                storageTask.getDueDate(), 
                storageTask.getCategory(), 
                storageTask.getPriority()
            );
            appTasks.add(appTask);
        }
        return appTasks;
    }

    private ObservableList<Task> tasks = FXCollections.observableArrayList();
    private ListView<Task> taskListView = new ListView<>();

    @Override
    public void start(Stage primaryStage) {
        // Load tasks from the CSV file when the application starts
        StorageSystem.loadTasksFromCSV();  // Load tasks from CSV into StorageSystem
        tasks.setAll(FXCollections.observableArrayList(convertTasks(StorageSystem.getTasks())));
        
        primaryStage.setTitle("To-Do List App");

        // Task List View
        taskListView.setItems(tasks);
        taskListView.setCellFactory(param -> new TaskCell());

        // Add Task Button
        Button addButton = new Button("Add Task");
        addButton.setOnAction(e -> addTask());

        // Sort Buttons
        Button sortByDueDateAscButton = new Button("Sort by Due Date (Ascending)");
        Button sortByDueDateDescButton = new Button("Sort by Due Date (Descending)");
        Button sortByPriorityAscButton = new Button("Sort by Priority (Low to High)");
        Button sortByPriorityDescButton = new Button("Sort by Priority (High to Low)");

        // Set actions for sorting
        sortByDueDateAscButton.setOnAction(e -> sortTasks("dueDate", true));
        sortByDueDateDescButton.setOnAction(e -> sortTasks("dueDate", false));
        sortByPriorityAscButton.setOnAction(e -> sortTasks("priority", true));
        sortByPriorityDescButton.setOnAction(e -> sortTasks("priority", false));

        // Layout
        HBox inputLayout = new HBox(10, addButton);
        VBox controlLayout = new VBox(10, inputLayout, sortByDueDateAscButton, sortByDueDateDescButton, sortByPriorityAscButton, sortByPriorityDescButton);
        HBox root = new HBox(10, controlLayout, taskListView);

        VBox.setMargin(taskListView, new Insets(10, 0, 0, 0));
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }

    // Add task to the list
    private void addTask() {
        // Create a dialog box for adding a task
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Task");

        // Set the dialog layout
        VBox dialogLayout = new VBox(10);
        dialogLayout.setPadding(new Insets(10));

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        DatePicker dueDatePicker = new DatePicker();

        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Homework", "Personal", "Work");

        ComboBox<String> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll("Low", "Medium", "High");

        dialogLayout.getChildren().addAll(
                new Label("Title:"), titleField,
                new Label("Description:"), descriptionField,
                new Label("Due Date:"), dueDatePicker,
                new Label("Category:"), categoryBox,
                new Label("Priority:"), priorityBox);

        dialog.getDialogPane().setContent(dialogLayout);

        // Add "OK" and "Cancel" buttons
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
        // Handle the result
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == okButtonType) {
            // Create a Task object using the entered data
            String title = titleField.getText();
            String description = descriptionField.getText();
            String dueDate = (dueDatePicker.getValue() != null) ? dueDatePicker.getValue().toString() : "";
            String category = categoryBox.getValue();
            String priority = priorityBox.getValue();

            // Create and add the new task
            Task newTask = new Task(title, description, dueDate, category, priority);
            tasks.add(newTask); // Add the new task to the list
            StorageSystem.saveTasksToCSV(); // Save the updated task list to the CSV
            taskListView.setItems(tasks); // Refresh ListView
        }
    }

    // Sort tasks based on ascending or descending order for the specified field
    private void sortTasks(String criteria, boolean ascending) {
        switch (criteria) {
            case "dueDate":
                tasks.sort((t1, t2) -> {
                    if (ascending) {
                        return t1.getDueDate().compareTo(t2.getDueDate());
                    } else {
                        return t2.getDueDate().compareTo(t1.getDueDate());
                    }
                });
                break;
            case "priority":
                tasks.sort((t1, t2) -> {
                    int priority1 = changePriorityToValue(t1.getPriority());
                    int priority2 = changePriorityToValue(t2.getPriority());
                    return ascending ? Integer.compare(priority1, priority2) : Integer.compare(priority2, priority1);
                });
                break;
        }
    }

    private int changePriorityToValue(String priority) {
        switch (priority) {
            case "Low":
                return 1;
            case "Medium":
                return 2;
            case "High":
                return 3;
            default:
                return 0; // Default for undefined priorities
        }
    }

    // Task class representing each task
    public static class Task {
        private final SimpleStringProperty title;
        private final SimpleStringProperty description;
        private final SimpleStringProperty dueDate;
        private final SimpleStringProperty category;
        private final SimpleStringProperty priority;
        private final SimpleBooleanProperty completed;

        public Task(String title, String description, String dueDate, String category, String priority) {
            this.title = new SimpleStringProperty(title);
            this.description = new SimpleStringProperty(description);
            this.dueDate = new SimpleStringProperty(dueDate);
            this.category = new SimpleStringProperty(category);
            this.priority = new SimpleStringProperty(priority);
            this.completed = new SimpleBooleanProperty(false);
        }

        // Accessor for each field
        public String getTitle() {
            return title.get();
        }

        public String getDescription() {
            return description.get();
        }

        public String getDueDate() {
            return dueDate.get();
        }

        public String getCategory() {
            return category.get();
        }

        public String getPriority() {
            return priority.get();
        }

        public boolean isCompleted() {
            return completed.get();
        }

        public void toggleCompleted() {
            this.completed.set(!this.completed.get());
        }
    }

    // Custom ListCell to display tasks
    private class TaskCell extends ListCell<Task> {
        @Override
        protected void updateItem(Task task, boolean empty) {
            super.updateItem(task, empty);
            if (empty || task == null) {
                setText(null);
                setGraphic(null);
            } else {
                VBox taskBox = new VBox(5);
                HBox taskRow = new HBox(10);
                taskRow.setSpacing(10);
    
                // Task title
                Label titleLabel = new Label(task.getTitle());
                titleLabel.setStyle("-fx-font-weight: bold;");
                titleLabel.setTextFill(task.isCompleted() ? Color.GRAY : Color.BLACK);
    
                // Task description
                Label descriptionLabel = new Label("Description: " + task.getDescription());
                descriptionLabel.setTextFill(task.isCompleted() ? Color.GRAY : Color.BLACK);
    
                // Task due date
                Label dueDateLabel = new Label("Due Date: " + task.getDueDate());
                dueDateLabel.setTextFill(task.isCompleted() ? Color.GRAY : Color.BLACK);
    
                // Task category
                Label categoryLabel = new Label("Category: " + task.getCategory());
                categoryLabel.setTextFill(task.isCompleted() ? Color.GRAY : Color.BLACK);
    
                // Task priority
                Label priorityLabel = new Label("Priority: " + task.getPriority());
                priorityLabel.setTextFill(task.isCompleted() ? Color.GRAY : Color.BLACK);
    
                // "Complete" image button
                Image completeImage = new Image("file:public/complete.png");
                ImageView completeImageView = new ImageView(completeImage);
                completeImageView.setFitHeight(10);
                completeImageView.setFitWidth(10);
                Button completeButton = new Button("", completeImageView);
                completeButton.setStyle("-fx-background-color: transparent;");
                completeButton.setOnAction(e -> {
                    task.toggleCompleted();
                    updateItem(task, false);
                });
    
                // "Delete" image button
                Image deleteImage = new Image("file:public/dustbin.png");
                ImageView deleteImageView = new ImageView(deleteImage);
                deleteImageView.setFitHeight(10);
                deleteImageView.setFitWidth(10);
                Button deleteButton = new Button("", deleteImageView);
                deleteButton.setStyle("-fx-background-color: transparent;");
                deleteButton.setOnAction(e -> {
                    tasks.remove(task);
                    StorageSystem.saveTasksToCSV(); // Save after removing task
                });
    
                taskRow.getChildren().addAll(titleLabel, completeButton, deleteButton);
                taskBox.getChildren().addAll(taskRow, descriptionLabel, dueDateLabel, categoryLabel, priorityLabel);
    
                setGraphic(taskBox);
            }
        }
    }
}