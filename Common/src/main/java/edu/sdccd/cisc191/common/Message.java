// File: Common/src/main/java/edu/sdccd/cisc191/common/Message.java
package edu.sdccd.cisc191.common;

public class Message {
    private String action;
    private Object data;

    // Default constructor for Jackson
    public Message() {
    }

    public Message(String action, Object data) {
        this.action = action;
        this.data = data;
    }

    // Getters and Setters
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
