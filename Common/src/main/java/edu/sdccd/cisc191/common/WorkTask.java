// File: Common/src/main/java/edu/sdccd/cisc191/common/WorkTask.java
package edu.sdccd.cisc191.common;

import java.time.LocalDate;
import java.time.LocalTime;

public class WorkTask extends Task {
    private String projectName;

    public WorkTask() {
        super();
    }

    public WorkTask(String description, String projectName, LocalDate creationDate, LocalTime creationTime,
                    LocalDate dueDate, LocalTime dueTime) {
        super(description, creationDate, creationTime, dueDate, dueTime);
        this.projectName = projectName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
