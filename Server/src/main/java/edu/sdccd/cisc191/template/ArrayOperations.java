package edu.sdccd.cisc191.template;

import java.io.Serializable;
import java.util.Arrays;
class ArrayOperations implements Serializable {
    private static ArrayOperations instance;

    private int[] array; // Example array for demonstration
    private int size;

    private ArrayOperations() {
        // Initialize array and size
        this.array = new int[10];
        this.size = 0;
    }

    public static ArrayOperations getInstance() {
        if (instance == null) {
            instance = new ArrayOperations();
        }
        return instance;
    }

    public synchronized int getAtIndex(int index) {
        // Fetch value at index
        return array[index];
    }

    public synchronized void setAtIndex(int index, int value) {
        // Update value at index
        array[index] = value;
    }

    public synchronized int findIndexOf(int value) {
        // Find index of a value
        for (int i = 0; i < size; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public synchronized int[] printAll() {
        // Return all values
        return Arrays.copyOf(array, size);
    }

    public synchronized void deleteAtIndex(int index) {
        // Delete value at index
        if (index < size) {
            for (int i = index; i < size - 1; i++) {
                array[i] = array[i + 1];
            }
            size--;
        }
    }

    public synchronized void expandArray() {
        // Expand the array
        int[] newArray = new int[array.length * 2];
        System.arraycopy(array, 0, newArray, 0, size);
        array = newArray;
    }

    public synchronized void shrinkArray() {
        // Shrink the array
        if (size < array.length / 2) {
            int[] newArray = new int[array.length / 2];
            System.arraycopy(array, 0, newArray, 0, size);
            array = newArray;
        } else {
            throw new IllegalStateException("Cannot shrink array, size is too large.");
        }
    }
}