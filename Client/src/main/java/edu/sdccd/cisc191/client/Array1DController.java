// File: client/Array1DController.java
package edu.sdccd.cisc191.client;

import edu.sdccd.cisc191.common.Array1DUpdate;
import edu.sdccd.cisc191.common.Message;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.List;

public class Array1DController {
    @FXML
    private ListView<String> listView;
    @FXML
    private TextField indexField;
    @FXML
    private TextField valueField;
    @FXML
    private Button getButton, setButton, addButton, deleteButton, expandButton, shrinkButton;

    private Client client;

    public void initialize() {
        client = ClientApp.getClient(); // Assume a method to get the Client instance
        loadArrayFromServer();
    }

    private void loadArrayFromServer() {
        new Thread(() -> {
            try {
                client.sendMessage(new Message("ARRAY1D_GET", null));
                Message response = client.receiveMessage();
                if (response != null && "ARRAY1D_GET_RESPONSE".equals(response.getAction())) {
                    List<String> arrayData = (List<String>) response.getData();
                    Platform.runLater(() -> listView.getItems().addAll(arrayData));
                }
            } catch (IOException e) {
                showError("Error", "Failed to load array data: " + e.getMessage());
            }
        }).start();
    }

    @FXML
    private void handleGet() {
        try {
            int index = Integer.parseInt(indexField.getText());
            String value = listView.getItems().get(index);
            showInfo("Get Element", "Element at index " + index + ": " + value);
        } catch (IndexOutOfBoundsException e) {
            showError("Error", "Index out of bounds.");
        } catch (NumberFormatException e) {
            showError("Error", "Invalid index format.");
        }
    }

    @FXML
    private void handleSet() {
        try {
            int index = Integer.parseInt(indexField.getText());
            String value = valueField.getText();
            listView.getItems().set(index, value);
            sendArrayUpdate("ARRAY1D_SET", new Array1DUpdate(index, value));
            showInfo("Success", "Element updated.");
        } catch (IndexOutOfBoundsException e) {
            showError("Error", "Index out of bounds.");
        } catch (NumberFormatException e) {
            showError("Error", "Invalid index format.");
        }
    }

    @FXML
    private void handleAdd() {
        String value = valueField.getText();
        listView.getItems().add(value);
        sendArrayUpdate("ARRAY1D_ADD", value);
        showInfo("Success", "Element added.");
    }

    @FXML
    private void handleDelete() {
        try {
            int index = Integer.parseInt(indexField.getText());
            listView.getItems().remove(index);
            sendArrayUpdate("ARRAY1D_DELETE", index);
            showInfo("Success", "Element deleted.");
        } catch (IndexOutOfBoundsException e) {
            showError("Error", "Index out of bounds.");
        } catch (NumberFormatException e) {
            showError("Error", "Invalid index format.");
        }
    }

    @FXML
    private void handleExpand() {
        // Implement array expansion logic as needed
        showInfo("Expand", "Expand functionality not implemented yet.");
    }

    @FXML
    private void handleShrink() {
        // Implement array shrinking logic as needed
        showInfo("Shrink", "Shrink functionality not implemented yet.");
    }

    private void sendArrayUpdate(String action, Object data) {
        new Thread(() -> {
            try {
                client.sendMessage(new Message(action, data));
                Message response = client.receiveMessage();
                if (response != null) {
                    String result = (String) response.getData();
                    if ("SUCCESS".equals(result)) {
                        // Optionally, confirm success to the user
                    } else {
                        showError("Error", "Operation failed: " + result);
                    }
                }
            } catch (IOException e) {
                showError("Error", "Failed to send update: " + e.getMessage());
            }
        }).start();
    }

    private void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, message);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.showAndWait();
        });
    }

    private void showInfo(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.showAndWait();
        });
    }
}
