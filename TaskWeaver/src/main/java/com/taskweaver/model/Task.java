package com.taskweaver.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Task implements Comparable<Task> {
    private static int nextId = 1;
    
    private final int id;
    private String title;
    private int priority; // 1-5, 1 = highest
    private LocalDate dueDate;
    private boolean completed;
    private LocalDate completionDate;
    private LocalDate createdAt;

    public Task(String title, int priority, LocalDate dueDate) {
        this.id = nextId++;
        this.title = title;
        this.priority = priority;
        this.dueDate = dueDate;
        this.completed = false;
        this.createdAt = LocalDate.now();
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public boolean isCompleted() { return completed; }
    public LocalDate getCreatedAt() { return createdAt; }

    public void setCompleted(boolean completed) { 
        this.completed = completed;
        if (completed) {
            this.completionDate = LocalDate.now();
        } else {
            this.completionDate = null;
        }
    }

    public LocalDate getCompletionDate() { return completionDate; }

    public String getAlertLevel() {
        long daysUntilDue = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
        
        if (daysUntilDue < 0) return "red";    // Overdue
        if (daysUntilDue == 0) return "red";   // Due today
        if (daysUntilDue <= 2) return "orange"; // Due in 2 days
        return "green";                        // Due in future
    }

    @Override
    public int compareTo(Task other) {
        return Integer.compare(this.priority, other.priority);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("Task{id=%d, title='%s', priority=%d, dueDate=%s, completed=%s}", 
                           id, title, priority, dueDate, completed);
    }
}