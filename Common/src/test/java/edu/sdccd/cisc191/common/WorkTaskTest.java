// File: WorkTaskTest.java
package edu.sdccd.cisc191.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class WorkTaskTest {

    @Test
    void testWorkTaskCreation() {
        String description = "Complete project report";
        String projectName = "Project Alpha";
        LocalDate creationDate = LocalDate.of(2024, 4, 27);
        LocalTime creationTime = LocalTime.of(14, 30);
        LocalDate dueDate = LocalDate.of(2024, 5, 4);
        LocalTime dueTime = LocalTime.of(17, 0);

        WorkTask workTask = new WorkTask(description, projectName, creationDate, creationTime, dueDate, dueTime);

        assertEquals(description, workTask.getDescription());
        // Removed assertEquals("Work", workTask.getType()); // getType() does not exist
        assertEquals(projectName, workTask.getProjectName());
        assertEquals(creationDate, workTask.getCreationDate());
        assertEquals(creationTime, workTask.getCreationTime());
        assertEquals(dueDate, workTask.getDueDate());
        assertEquals(dueTime, workTask.getDueTime());
    }

    @Test
    void testSetters() {
        String initialDescription = "Initial Description";
        String initialProjectName = "Initial Project";
        LocalDate initialCreationDate = LocalDate.of(2024, 4, 27);
        LocalTime initialCreationTime = LocalTime.of(10, 0);
        LocalDate initialDueDate = LocalDate.of(2024, 5, 15);
        LocalTime initialDueTime = LocalTime.of(12, 0);

        WorkTask workTask = new WorkTask(initialDescription, initialProjectName, initialCreationDate, initialCreationTime, initialDueDate, initialDueTime);

        String updatedDescription = "Updated Description";
        String updatedProjectName = "Updated Project";
        LocalDate updatedDueDate = LocalDate.of(2024, 5, 20);
        LocalTime updatedDueTime = LocalTime.of(16, 45);

        workTask.setDescription(updatedDescription);
        workTask.setProjectName(updatedProjectName);
        // Removed workTask.setType("Work"); // setType() does not exist
        workTask.setDueDate(updatedDueDate);
        workTask.setDueTime(updatedDueTime);

        assertEquals(updatedDescription, workTask.getDescription());
        assertEquals(updatedProjectName, workTask.getProjectName());
        // Removed assertEquals("Work", workTask.getType()); // getType() does not exist
        assertEquals(updatedDueDate, workTask.getDueDate());
        assertEquals(updatedDueTime, workTask.getDueTime());
    }

    @Test
    void testWorkTaskSerialization() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String description = "Finish project";
        String projectName = "Project A";
        LocalDate creationDate = LocalDate.of(2024, 4, 27);
        LocalTime creationTime = LocalTime.of(9, 15);
        LocalDate dueDate = LocalDate.of(2024, 5, 10);
        LocalTime dueTime = LocalTime.of(17, 0);

        WorkTask workTask = new WorkTask(description, projectName, creationDate, creationTime, dueDate, dueTime);

        // Serialize the WorkTask to JSON
        String json = objectMapper.writeValueAsString(workTask);

        // Deserialize the JSON back to WorkTask
        WorkTask deserializedTask = objectMapper.readValue(json, WorkTask.class);

        assertEquals(workTask.getDescription(), deserializedTask.getDescription());
        assertEquals(workTask.getProjectName(), deserializedTask.getProjectName());
        // Removed assertEquals(workTask.getType(), deserializedTask.getType()); // getType() does not exist
        assertEquals(workTask.getCreationDate(), deserializedTask.getCreationDate());
        assertEquals(workTask.getCreationTime(), deserializedTask.getCreationTime());
        assertEquals(workTask.getDueDate(), deserializedTask.getDueDate());
        assertEquals(workTask.getDueTime(), deserializedTask.getDueTime());
    }
}
