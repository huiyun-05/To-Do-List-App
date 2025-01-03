package com.example;
import com.example.UITask;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import static javafx.application.Application.launch;

public class TodoListApp extends Application {

    private UITask uiTask = new UITask(); // Instantiate UITask to manage tasks
    private ListView<UITask.Task> taskListView = new ListView<>(); // Task list view

    @Override
    public void start(Stage primaryStage) {
        
        primaryStage.setTitle("To-Do List App");

        // Task List View
        taskListView.setItems(uiTask.getTaskList());
        taskListView.setCellFactory(param -> new UITask.TaskCell(uiTask));

        // Add Task Button
        Button addButton = new Button("Add Task");
        addButton.setOnAction(e -> uiTask.addTask());

        // Sort Buttons
        Button sortByDueDateAscButton = new Button("Sort by Due Date (Ascending)");
        Button sortByDueDateDescButton = new Button("Sort by Due Date (Descending)");
        Button sortByPriorityAscButton = new Button("Sort by Priority (Low to High)");
        Button sortByPriorityDescButton = new Button("Sort by Priority (High to Low)");

        // Set actions for sorting
        sortByDueDateAscButton.setOnAction(e -> uiTask.sortTasks("dueDate", true));
        sortByDueDateDescButton.setOnAction(e -> uiTask.sortTasks("dueDate", false));
        sortByPriorityAscButton.setOnAction(e -> uiTask.sortTasks("priority", true));
        sortByPriorityDescButton.setOnAction(e -> uiTask.sortTasks("priority", false));

        // Layout
        HBox inputLayout = new HBox(10, addButton);
        VBox controlLayout = new VBox(10, inputLayout, sortByDueDateAscButton, sortByDueDateDescButton, sortByPriorityAscButton, sortByPriorityDescButton);
        HBox root = new HBox(10, controlLayout, taskListView);

        VBox.setMargin(taskListView, new Insets(10, 0, 0, 0));
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
        // Load tasks when the application starts
        uiTask.loadTasks();
    }
    
    public static void main(String[] args) {
        launch(args);
    }    
}