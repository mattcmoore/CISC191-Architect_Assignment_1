// File: Common/src/main/java/edu/sdccd/cisc191/common/SubTaskMessage.java
package edu.sdccd.cisc191.common;

public class SubTaskMessage extends Message {
    private String parentDescription;
    private Task subTask;

    public SubTaskMessage() {
        super();
    }

    public SubTaskMessage(String parentDescription, Task subTask) {
        super("SUBTASK", null);
        this.parentDescription = parentDescription;
        this.subTask = subTask;
    }

    public String getParentDescription() {
        return parentDescription;
    }

    public void setParentDescription(String parentDescription) {
        this.parentDescription = parentDescription;
    }

    public Task getSubTask() {
        return subTask;
    }

    public void setSubTask(Task subTask) {
        this.subTask = subTask;
    }
}
