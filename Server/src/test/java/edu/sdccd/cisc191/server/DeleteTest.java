package edu.sdccd.cisc191.server;

import edu.sdccd.cisc191.common.Task;
import org.junit.jupiter.api.Test;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DeleteTest {
    LocalDate currentDate = LocalDate.now();
    LocalTime currentTime = LocalTime.now();
    LocalDate nextDay = currentDate.plusDays(1);
    ArrayList<Task> Tasks = new ArrayList<Task>();
    ArrayList<Task> Subtasks = new ArrayList<Task>();

    @Test
    public void testDelete() {
        Tasks.add(new Task("task1",currentDate,currentTime,nextDay, currentTime));
        Tasks.add(new Task("task2",currentDate,currentTime,nextDay, currentTime));

        Subtasks.add(new Task("subtaskA",currentDate,currentTime,nextDay, currentTime));
        Subtasks.add(new Task("subtaskB",currentDate,currentTime,nextDay, currentTime));

        Tasks.get(1).setSubTasks(Subtasks);
        Tasks.get(1).removeSubTask(0);

       assertEquals(Tasks.get(1).getSubTasks().size(),1, "The ArrayList should be size = 1 after deleting subtask");

    }
}
