// File: common/Array1DUpdate.java
package edu.sdccd.cisc191.common;

public class Array1DUpdate {
    private Integer index;
    private String value;

    // Default constructor for Jackson
    public Array1DUpdate() {
    }

    public Array1DUpdate(Integer index, String value) {
        this.index = index;
        this.value = value;
    }

    // Getters and Setters
    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
