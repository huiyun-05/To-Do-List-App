package com.example;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AnalyticsDashboard extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Data sample needed
        int totalTasks = 10;
        int completedTasks = 5;
        int pendingTasks = 5;
        double completionRate = (double) completedTasks / totalTasks * 100;


        Label totalTasksLabel = new Label("Total Tasks: " + totalTasks);
        Label completedLabel = new Label("Completed: " + completedTasks);
        Label pendingLabel = new Label("Pending: " + pendingTasks);
        Label completionRateLabel = new Label(String.format("Completion Rate: %.2f%%", completionRate));

        // Task Categories
        Label homeworkLabel = new Label("Homework: 3");
        Label personalLabel = new Label("Personal: 5");
        Label workLabel = new Label("Work: 2");

        // Layout using GridPane
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.add(totalTasksLabel, 0, 0);
        gridPane.add(completedLabel, 0, 1);
        gridPane.add(pendingLabel, 0, 2);
        gridPane.add(completionRateLabel, 0, 3);
        gridPane.add(new Label("Task Categories:"), 0, 4);
        gridPane.add(homeworkLabel, 0, 5);
        gridPane.add(personalLabel, 0, 6);
        gridPane.add(workLabel, 0, 7);

        // Scene
        Scene scene = new Scene(gridPane, 300, 250);
        primaryStage.setTitle("Analytics Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

