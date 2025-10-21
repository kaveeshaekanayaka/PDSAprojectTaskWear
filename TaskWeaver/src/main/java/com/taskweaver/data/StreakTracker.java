package com.taskweaver.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StreakTracker {
    private List<LocalDate> completionDates;
    private int currentStreak;

    public StreakTracker() {
        this.completionDates = new ArrayList<>();
        this.currentStreak = 0;
    }

    public void addCompletionDate(LocalDate date) {
        // Only add if not already present for that date
        if (!completionDates.contains(date)) {
            completionDates.add(date);
            calculateCurrentStreak();
        }
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void reset() {
        completionDates.clear();
        currentStreak = 0;
    }

    public List<LocalDate> getCompletionDates() {
        return new ArrayList<>(completionDates);
    }

    private void calculateCurrentStreak() {
        if (completionDates.isEmpty()) {
            currentStreak = 0;
            return;
        }

        // Sort dates in descending order (most recent first)
        completionDates.sort((d1, d2) -> d2.compareTo(d1));
        
        currentStreak = 1;
        LocalDate currentDate = completionDates.get(0);
        LocalDate today = LocalDate.now();
        
        // Check if the most recent completion is today or yesterday
        if (currentDate.isBefore(today.minusDays(1))) {
            currentStreak = 0;
            return;
        }
        
        // Calculate consecutive days
        for (int i = 1; i < completionDates.size(); i++) {
            LocalDate previousDate = completionDates.get(i);
            if (currentDate.minusDays(1).equals(previousDate)) {
                currentStreak++;
                currentDate = previousDate;
            } else {
                break;
            }
        }
    }
}