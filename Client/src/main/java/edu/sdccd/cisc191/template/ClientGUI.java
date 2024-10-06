package edu.sdccd.cisc191.template;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientGUI extends Application {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private TextArea resultArea;
    private ComboBox<String> taskSelection;
    private ComboBox<String> operationSelection;
    private TextField indexField;
    private TextField valueField;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Client Operations");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        // Task Selection
        taskSelection = new ComboBox<>();
        taskSelection.getItems().addAll("1D Array Operation", "2D Array Operation");
        taskSelection.setPromptText("Select Task");

        // Operation Selection
        operationSelection = new ComboBox<>();
        taskSelection.setOnAction(event -> updateOperations());

        // Input Fields
        indexField = new TextField();
        indexField.setPromptText("Index");

        valueField = new TextField();
        valueField.setPromptText("Value");

        // Execute Button
        Button executeButton = new Button("Execute Task");
        executeButton.setOnAction(event -> handleTaskExecution());

        // Result Area
        resultArea = new TextArea();
        resultArea.setEditable(false);

        // Layout Configuration
        layout.getChildren().addAll(new Label("Task:"), taskSelection, new Label("Operation:"), operationSelection,
                new Label("Index (optional):"), indexField, new Label("Value (optional):"), valueField, executeButton, resultArea);

        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateOperations() {
        operationSelection.getItems().clear();
        if ("1D Array Operation".equals(taskSelection.getValue())) {
            operationSelection.getItems().addAll("Fetch value at index", "Update value at index", "Find index of a value",
                    "Print all values", "Delete value at index", "Expand the array", "Shrink the array");
        } else if ("2D Array Operation".equals(taskSelection.getValue())) {
            operationSelection.getItems().addAll("Get value at (row, column)", "Set value at (row, column)", "Find index of a value",
                    "Print all values", "Delete value at (row, column)", "Expand the array", "Shrink the array");
        }
    }

    private void handleTaskExecution() {
        try {
            startConnection("127.0.0.1", 4444);

            ArrayList<Integer> params = new ArrayList<>();
            String task = taskSelection.getValue();
            String operation = operationSelection.getValue();

            if (task == null || operation == null) {
                resultArea.setText("Please select a task and operation.");
                return;
            }

            int taskID = "1D Array Operation".equals(task) ? 1 : 2;
            int operationID = operationSelection.getSelectionModel().getSelectedIndex() + 1;

            if (operationID >= 1 && operationID <= 5) {
                String index = indexField.getText();
                if (!index.isEmpty()) {
                    params.add(Integer.parseInt(index));
                }
            }

            if ("Update value at index".equals(operation) || "Set value at (row, column)".equals(operation)) {
                String value = valueField.getText();
                if (!value.isEmpty()) {
                    params.add(Integer.parseInt(value));
                }
            }

            CustomerRequest request = new CustomerRequest(1, taskID, operationID, params);

            // Send the request to the server
            CustomerResponse response = sendRequest(request);
            if (response != null) {
                resultArea.setText("Received result from server: " + response.getServerResposne());
            }

            stopConnection();

        } catch (Exception e) {
            resultArea.setText("Error: " + e.getMessage());
        }
    }

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public CustomerResponse sendRequest(CustomerRequest request) throws Exception {
        String requestStr = CustomerRequest.toJSON(request);
        out.println(requestStr);  // Send the request as JSON
        return CustomerResponse.fromJSON(in.readLine());  // Receive the response as JSON
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
}