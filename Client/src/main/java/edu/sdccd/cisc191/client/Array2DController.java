// File: client/Array2DController.java
package edu.sdccd.cisc191.client;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class Array2DController {
    @FXML
    private TextField rowsField;

    @FXML
    private TextField columnsField;

    @FXML
    private Button createArrayButton;

    @FXML
    private Button populateArrayButton;

    @FXML
    private TableView<List<String>> arrayTableView;

    @FXML
    private VBox mainLayout;

    private String[][] array2D;
    private ObservableList<List<String>> tableData;

    @FXML
    public void initialize() {
        // Initialize TableView
        tableData = FXCollections.observableArrayList();
        arrayTableView.setItems(tableData);

        // Set up event handlers
        createArrayButton.setOnAction(e -> createArray());
        populateArrayButton.setOnAction(e -> populateArray());
    }

    /**
     * Creates a two-dimensional array based on user input for rows and columns.
     */
    private void createArray() {
        String rowsText = rowsField.getText();
        String columnsText = columnsField.getText();

        int rows, columns;

        try {
            rows = Integer.parseInt(rowsText);
            columns = Integer.parseInt(columnsText);

            if (rows <= 0 || columns <= 0) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Rows and columns must be positive integers.");
                return;
            }

            array2D = new String[rows][columns];
            tableData.clear();
            arrayTableView.getColumns().clear();

            // Set up TableView columns
            for (int i = 0; i < columns; i++) {
                final int colIndex = i;
                TableColumn<List<String>, String> column = new TableColumn<>("Col " + (i + 1));
                column.setCellValueFactory(data -> {
                    List<String> row = data.getValue();
                    if (colIndex < row.size()) {
                        return new ReadOnlyStringWrapper(row.get(colIndex));
                    } else {
                        return new ReadOnlyStringWrapper("");
                    }
                });
                arrayTableView.getColumns().add(column);
            }

            // Initialize table data with empty strings
            for (int i = 0; i < rows; i++) {
                List<String> rowData = new ArrayList<>();
                for (int j = 0; j < columns; j++) {
                    rowData.add("");
                }
                tableData.add(FXCollections.observableArrayList(rowData));
            }

            showAlert(Alert.AlertType.INFORMATION, "Success", "2D Array created with " + rows + " rows and " + columns + " columns.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter valid integers for rows and columns.");
        }
    }

    /**
     * Populates the two-dimensional array with user-provided data.
     */
    private void populateArray() {
        if (array2D == null) {
            showAlert(Alert.AlertType.ERROR, "Array Not Created", "Please create the array first.");
            return;
        }

        // Iterate through the TableView and populate the array
        for (int i = 0; i < tableData.size(); i++) {
            List<String> rowData = tableData.get(i);
            for (int j = 0; j < rowData.size(); j++) {
                array2D[i][j] = rowData.get(j);
            }
        }

        showAlert(Alert.AlertType.INFORMATION, "Success", "2D Array populated successfully.");
    }

    /**
     * Displays an alert dialog to the user.
     *
     * @param alertType Type of the alert.
     * @param title     Title of the alert window.
     * @param message   Message to display.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
