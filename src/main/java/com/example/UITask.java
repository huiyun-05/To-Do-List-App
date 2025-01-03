package com.example;
import com.example.GeneralTask;
import com.example.StorageSystem;
import com.example.StorageSystem.StorageTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class UITask {

    private ObservableList<Task> taskList = FXCollections.observableArrayList();

    // Constructor: Load tasks from StorageSystem
    public UITask() {
        loadTasks();
    }

    // Get the ObservableList for the GUI
    public ObservableList<UITask.Task> getTaskList() {
        return taskList;
    }

    // Load tasks from StorageSystem
    public void loadTasks() {
        List<StorageTask> storageTasks = StorageSystem.getStorageTasks();
        taskList.setAll(convertStorageTasksToTasks(storageTasks));
    }

    // Save tasks to StorageSystem
    public void saveTasks() {
        List<StorageTask> storageTasks = convertTasksToStorageTasks(taskList);
        StorageSystem.setStorageTasks(storageTasks);
        StorageSystem.saveTasksToCSV();
    }
    
    public void removeTask(Task task) {
        taskList.remove(task);
        saveTasks();  // Save the tasks to the CSV after removal
    }

    // Add a new task
    public void addTask() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Task");

        // Dialog layout
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

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == okButtonType) {
            String title = titleField.getText();
            String description = descriptionField.getText();
            String dueDate = (dueDatePicker.getValue() != null) ? dueDatePicker.getValue().toString() : "";
            String category = categoryBox.getValue();
            String priority = priorityBox.getValue();

            Task newTask = new Task(title, description, dueDate, category, priority);
            taskList.add(newTask);
            saveTasks();
        }
    }

    // Sort tasks
    public void sortTasks(String criteria, boolean ascending) {
        Comparator<Task> comparator = null;
        if ("dueDate".equals(criteria)) {
            comparator = Comparator.comparing(Task::getDueDate);
        } else if ("priority".equals(criteria)) {
            comparator = Comparator.comparingInt(task -> priorityToValue(task.getPriority()));
        }

        if (comparator != null) {
            if (!ascending) {
                comparator = comparator.reversed();
            }
            FXCollections.sort(taskList, comparator);
        }
    }

    // Convert priority string to numeric value for sorting
    private int priorityToValue(String priority) {
        switch (priority) {
            case "Low":
                return 1;
            case "Medium":
                return 2;
            case "High":
                return 3;
            default:
                return 0;
        }
    }

    // Convert StorageTask to Task
    private List<Task> convertStorageTasksToTasks(List<StorageTask> storageTasks) {
        List<Task> tasks = new ArrayList<>();
        for (StorageTask st : storageTasks) {
            tasks.add(new Task(st.getTitle(), st.getDescription(), st.getDueDate(), st.getCategory(), st.getPriority()));
        }
        return tasks;
    }

    // Convert Task to StorageTask
    private List<StorageTask> convertTasksToStorageTasks(List<Task> tasks) {
        List<StorageTask> storageTasks = new ArrayList<>();
        for (Task t : tasks) {
            storageTasks.add(new StorageTask(t.getTitle(), t.getDescription(), t.getDueDate(), t.getCategory(), t.getPriority()));
        }
        return storageTasks;
    }

    // Inner Task class
    public static class Task {
        private String title;
        private String description;
        private String dueDate;
        private String category;
        private String priority;
        private boolean completed;

        public Task(String title, String description, String dueDate, String category, String priority) {
            this.title = title;
            this.description = description;
            this.dueDate = dueDate;
            this.category = category;
            this.priority = priority;
            this.completed = false;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getDueDate() {
            return dueDate;
        }

        public String getCategory() {
            return category;
        }

        public String getPriority() {
            return priority;
        }
        
        public void toggleCompleted() {
            this.completed = !this.completed;
        }
        
        public boolean isCompleted() {
            return completed;
        }
    }

    // Custom ListCell for displaying tasks
    public static class TaskCell extends ListCell<UITask.Task> {
        private UITask uiTask;

        public TaskCell(UITask uiTask) {
            this.uiTask = uiTask;  // Pass the UITask instance to the cell
        }
        
        @Override
        protected void updateItem(UITask.Task task, boolean empty) {
            super.updateItem(task, empty);
            if (empty || task == null) {
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
                    updateItem(task, false); // Update task display
                });

                // "Delete" image button
                Image deleteImage = new Image("file:public/dustbin.png");
                ImageView deleteImageView = new ImageView(deleteImage);
                deleteImageView.setFitHeight(10);
                deleteImageView.setFitWidth(10);
                Button deleteButton = new Button("", deleteImageView);
                deleteButton.setStyle("-fx-background-color: transparent;");
                deleteButton.setOnAction(e -> {
                    uiTask.removeTask(task);
                });

                taskRow.getChildren().addAll(titleLabel, completeButton, deleteButton);
                taskBox.getChildren().addAll(taskRow, descriptionLabel, dueDateLabel, categoryLabel, priorityLabel);

                setGraphic(taskBox);
            }
        }
    }
}
