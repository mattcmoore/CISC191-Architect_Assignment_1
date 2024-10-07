// File: Common/src/main/java/edu/sdccd/cisc191/common/Task.java
package edu.sdccd.cisc191.common;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Task {
    private String description;
    private LocalDate creationDate;
    private LocalTime creationTime;
    private LocalDate dueDate;
    private LocalTime dueTime;
    private List<Task> subTasks;

    // Default constructor for Jackson
    public Task() {
        this.subTasks = new ArrayList<>();
    }

    public Task(String description, LocalDate creationDate, LocalTime creationTime,
                LocalDate dueDate, LocalTime dueTime) {
        this.description = description;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.subTasks = new ArrayList<>();
    }

    // Getters and Setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalTime getDueTime() {
        return dueTime;
    }

    public void setDueTime(LocalTime dueTime) {
        this.dueTime = dueTime;
    }

    public List<Task> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<Task> subTasks) {
        this.subTasks = subTasks;
    }

    // Methods to manage sub-tasks
    public void addSubTask(Task subTask) {
        this.subTasks.add(subTask);
    }

    /**
     * Removes a sub-task at the specified index.
     *
     * @param index The index of the sub-task to remove.
     * @return true if removal was successful, false otherwise.
     */
    public boolean removeSubTask(int index) {
        if (index >= 0 && index < subTasks.size()) {
            subTasks.remove(index);
            return true;
        }
        return false;
    }

    // Override toString for better readability
    @Override
    public String toString() {
        return description;
    }
}
