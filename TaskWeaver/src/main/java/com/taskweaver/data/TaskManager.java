package com.taskweaver.data;

import com.taskweaver.algorithm.MinHeap;
import com.taskweaver.model.Task;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskManager {
    private MinHeap taskHeap;
    private StreakTracker streakTracker;
    private List<Task> allTasks;

    public TaskManager() {
        this.taskHeap = new MinHeap();
        this.streakTracker = new StreakTracker();
        this.allTasks = new ArrayList<>();
    }

    public void createTask(String title, int priority, LocalDate dueDate) {
        Task task = new Task(title, priority, dueDate);
        taskHeap.insert(task);
        allTasks.add(task);
    }

    public boolean completeTask(Task task) {
        if (allTasks.contains(task) && !task.isCompleted()) {
            task.setCompleted(true);
            streakTracker.addCompletionDate(LocalDate.now());
            rebuildHeap();
            return true;
        }
        return false;
    }

    public boolean deleteTask(Task task) {
        boolean removed = allTasks.remove(task);
        if (removed) {
            rebuildHeap();
        }
        return removed;
    }

    public Task getFocusTask() {
        return taskHeap.peek();
    }

    public List<Task> getAllTasksByPriority() {
        return allTasks.stream()
                .sorted((t1, t2) -> Integer.compare(t1.getPriority(), t2.getPriority()))
                .collect(Collectors.toList());
    }

    public List<Task> getIncompleteTasks() {
        return allTasks.stream()
                .filter(task -> !task.isCompleted())
                .collect(Collectors.toList());
    }

    public List<Task> getCompletedTasks() {
        return allTasks.stream()
                .filter(Task::isCompleted)
                .collect(Collectors.toList());
    }

    public int getCurrentStreak() {
        return streakTracker.getCurrentStreak();
    }

    public List<Task> getTasksByAlertLevel(String alertLevel) {
        return allTasks.stream()
                .filter(task -> task.getAlertLevel().equals(alertLevel) && !task.isCompleted())
                .collect(Collectors.toList());
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(allTasks);
    }

    private void rebuildHeap() {
        MinHeap newHeap = new MinHeap();
        for (Task task : allTasks) {
            if (!task.isCompleted()) {
                newHeap.insert(task);
            }
        }
        this.taskHeap = newHeap;
    }

    public void clearAllTasks() {
        allTasks.clear();
        taskHeap = new MinHeap();
        streakTracker.reset();
    }
}