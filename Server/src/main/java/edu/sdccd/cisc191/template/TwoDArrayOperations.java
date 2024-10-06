package edu.sdccd.cisc191.template;

import java.io.Serializable;

public class TwoDArrayOperations implements Serializable {
    private int[][] array;

    // Constructor to initialize 2D array
    public TwoDArrayOperations(int rows, int cols) {
        this.array = new int[rows][cols];
    }

    // Method to get element at specific index
    public int getAtIndex(int rowIndex, int colIndex) throws IndexOutOfBoundsException {
        if (rowIndex >= 0 && rowIndex < array.length && colIndex >= 0 && colIndex < array[0].length) {
            return array[rowIndex][colIndex];
        } else {
            throw new IndexOutOfBoundsException("Index out of bounds.");
        }
    }

    // Method to set element at specific index
    public void setAtIndex(int rowIndex, int colIndex, int value) throws IndexOutOfBoundsException {
        if (rowIndex >= 0 && rowIndex < array.length && colIndex >= 0 && colIndex < array[0].length) {
            array[rowIndex][colIndex] = value;
        } else {
            throw new IndexOutOfBoundsException("Index out of bounds.");
        }
    }

    // Method to find the index of a value in the 2D array
    public String findIndexOf(int value) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                if (array[i][j] == value) {
                    return "Value found at: [" + i + "][" + j + "]";
                }
            }
        }
        return "Value not found in the array.";
    }

    // Method to delete an element at specific index (set to 0 or another default value)
    public void deleteAtIndex(int rowIndex, int colIndex) throws IndexOutOfBoundsException {
        setAtIndex(rowIndex, colIndex, 0); // setting value to 0 (or any other default) to 'delete' it
    }

    // Method to expand the 2D array by adding new rows and columns
    public void expand(int newRows, int newCols) {
        int[][] newArray = new int[newRows][newCols];

        // Copy existing elements into the new array
        for (int i = 0; i < Math.min(array.length, newRows); i++) {
            for (int j = 0; j < Math.min(array[i].length, newCols); j++) {
                newArray[i][j] = array[i][j];
            }
        }

        array = newArray; // Reassign the expanded array
    }

    // Method to shrink the 2D array (keeping only elements within new dimensions)
    public void shrink(int newRows, int newCols) {
        if (newRows > array.length || newCols > array[0].length) {
            throw new IllegalArgumentException("New dimensions must be smaller than or equal to current dimensions.");
        }

        int[][] newArray = new int[newRows][newCols];

        // Copy elements into the new, smaller array
        for (int i = 0; i < newRows; i++) {
            for (int j = 0; j < newCols; j++) {
                newArray[i][j] = array[i][j];
            }
        }

        array = newArray; // Reassign the shrunk array
    }

    // Method to print all elements of the 2D array
    public String printAll() {
        StringBuilder sb = new StringBuilder();
        for (int[] row : array) {
            for (int elem : row) {
                sb.append(elem).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
