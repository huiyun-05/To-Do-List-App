package com.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Task {
    protected String title;
    protected String description;
    protected String dueDate;
    protected String category;
    protected String priority;
    protected boolean isComplete;
    protected List<Integer> dependencies; 
    protected String recurrence; // daily, weekly, monthly
    protected LocalDate nextCreationDate;

    public Task(String title, String description, String dueDate, String category, String priority) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.isComplete = false;
        this.category = category;
        this.priority = priority;
        this.dependencies = new ArrayList<>();
    }
    
    public Task(String title, String description, String recurrence, LocalDate now) {
        this.title = title;
        this.description = description;
        this.isComplete = false;
        this.dependencies = new ArrayList<>();
        this.recurrence = recurrence;
        this.nextCreationDate = now; // Initialize to the current date
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

    public boolean isIsComplete() {
        return isComplete;
    }

    public void setIsComplete(boolean isComplete) {
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
    
    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }
    
    public boolean isRecurring() {
        return recurrence != null && !recurrence.isEmpty();
    }
    
    public void updateNextCreationDate() {
        if (nextCreationDate == null) {
            nextCreationDate = LocalDate.now(); // Initialize if null
        }
        switch (recurrence.toLowerCase()) {
            case "daily":
                nextCreationDate = nextCreationDate.plusDays(1);
                break;
            case "weekly":
                nextCreationDate = nextCreationDate.plusWeeks(1);
                break;
            case "monthly":
                nextCreationDate = nextCreationDate.plusMonths(1);
                break;
            default:
                System.out.println("Invalid recurrence type.");
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

    public String toString() {
        return (isComplete ? "[Complete] " : "[Incomplete] ") + title + " - Due: " + dueDate + " - Category: " + category + " - Priority: " + priority;
    }
}
