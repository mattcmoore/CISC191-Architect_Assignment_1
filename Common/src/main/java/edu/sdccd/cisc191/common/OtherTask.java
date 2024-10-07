// File: Common/src/main/java/edu/sdccd/cisc191/common/OtherTask.java
package edu.sdccd.cisc191.common;

import java.time.LocalDate;
import java.time.LocalTime;

public class OtherTask extends Task {
    public OtherTask() {
        super();
    }

    public OtherTask(String description, LocalDate creationDate, LocalTime creationTime,
                     LocalDate dueDate, LocalTime dueTime) {
        super(description, creationDate, creationTime, dueDate, dueTime);
    }
}
