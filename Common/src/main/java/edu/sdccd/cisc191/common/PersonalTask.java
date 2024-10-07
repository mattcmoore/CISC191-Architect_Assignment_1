// File: Common/src/main/java/edu/sdccd/cisc191/common/PersonalTask.java
package edu.sdccd.cisc191.common;

import java.time.LocalDate;
import java.time.LocalTime;

public class PersonalTask extends Task {
    public PersonalTask() {
        super();
    }

    public PersonalTask(String description, LocalDate creationDate, LocalTime creationTime,
                        LocalDate dueDate, LocalTime dueTime) {
        super(description, creationDate, creationTime, dueDate, dueTime);
    }
}
