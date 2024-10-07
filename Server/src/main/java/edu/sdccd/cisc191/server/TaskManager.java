// File: Server/src/main/java/edu/sdccd/cisc191/server/TaskManager.java
package edu.sdccd.cisc191.server;

import edu.sdccd.cisc191.common.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private List<Task> tasks; // List to hold main tasks

    public TaskManager() {
        tasks = new ArrayList<>();
    }

    // Add a main task
    public synchronized void addTask(Task task) {
        tasks.add(task);
    }

    // Retrieve all tasks
    public synchronized List<Task> getAllTasks() {
        return new ArrayList<>(tasks); // Return a copy to prevent external modification
    }

    // Find a task by description (case-insensitive)
    public synchronized Task findTaskByDescription(String description) {
        for (Task task : tasks) {
            if (task.getDescription().equalsIgnoreCase(description)) {
                return task;
            }
            // Search sub-tasks recursively
            Task found = findSubTaskByDescription(task, description);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    // Recursive helper to find sub-task
    private Task findSubTaskByDescription(Task parent, String description) {
        for (Task subTask : parent.getSubTasks()) {
            if (subTask.getDescription().equalsIgnoreCase(description)) {
                return subTask;
            }
            // Further recursion if needed
            Task found = findSubTaskByDescription(subTask, description);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    // Add a sub-task to a parent task
    public synchronized boolean addSubTask(String parentDescription, Task subTask) {
        Task parentTask = findTaskByDescription(parentDescription);
        if (parentTask != null) {
            parentTask.addSubTask(subTask);
            return true;
        }
        return false;
    }

    // Remove a sub-task from a parent task by index
    public synchronized boolean removeSubTask(String parentDescription, int subTaskIndex) {
        Task parentTask = findTaskByDescription(parentDescription);
        if (parentTask != null) {
            return parentTask.removeSubTask(subTaskIndex);
        }
        return false;
    }

}
