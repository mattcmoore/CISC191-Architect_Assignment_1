// File: Client/src/main/java/edu/sdccd/cisc191/client/TaskController.java
package edu.sdccd.cisc191.client;

import edu.sdccd.cisc191.common.Message;
import edu.sdccd.cisc191.common.SubTaskMessage;
import edu.sdccd.cisc191.common.Task;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class TaskController {
    @FXML
    private TreeView<String> taskTreeView;

    @FXML
    private TextField taskDescriptionField;

    @FXML
    private ComboBox<String> taskTypeComboBox;

    @FXML
    private DatePicker dueDatePicker;

    @FXML
    private TextField dueTimeField;

    @FXML
    private Button addTaskButton;

    @FXML
    private VBox mainLayout;

    private Client client;
    private TreeItem<String> rootItem;

    // ObservableList to map TreeItems to Task objects
    private ObservableList<Task> tasksList;

    @FXML
    public void initialize() {
        // Initialize components
        client = ClientApp.getClient(); // Assumes a static method to get the Client instance
        if (client == null) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Client connection is not established.");
            return;
        }

        rootItem = new TreeItem<>("Tasks");
        rootItem.setExpanded(true);
        taskTreeView.setRoot(rootItem);

        tasksList = FXCollections.observableArrayList();

        // Populate task types
        taskTypeComboBox.getItems().addAll("Work", "Personal", "Other");

        // Set up event handlers
        addTaskButton.setOnAction(e -> handleAddTask());

        // Handle double-click on tree items to add sub-tasks
        taskTreeView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                TreeItem<String> selectedItem = taskTreeView.getSelectionModel().getSelectedItem();
                if (selectedItem != null && selectedItem != rootItem) {
                    showAddSubTaskDialog(selectedItem);
                }
            }
        });

        // Load tasks from the server
        loadTasksFromServer();
    }

    /**
     * Handles adding a new main task.
     */
    private void handleAddTask() {
        String description = taskDescriptionField.getText().trim();
        String type = taskTypeComboBox.getValue();
        LocalDate dueDate = dueDatePicker.getValue();
        String dueTimeText = dueTimeField.getText().trim();

        // Input validation
        if (description.isEmpty() || type == null || dueDate == null || dueTimeText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill in all fields.");
            return;
        }

        LocalTime dueTime;
        try {
            dueTime = LocalTime.parse(dueTimeText);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid time format. Please use HH:MM.");
            return;
        }

        // Create Task object based on type
        Task newTask;
        switch (type) {
            case "Work":
                TextInputDialog projectDialog = new TextInputDialog();
                projectDialog.setTitle("Project Name");
                projectDialog.setHeaderText("Enter the project name for the Work Task:");
                projectDialog.setContentText("Project Name:");
                Optional<String> projectNameOpt = projectDialog.showAndWait();
                if (projectNameOpt.isPresent()) {
                    String projectName = projectNameOpt.get().trim();
                    if (projectName.isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, "Input Error", "Project name cannot be empty.");
                        return;
                    }
                    newTask = new edu.sdccd.cisc191.common.WorkTask(description, projectName, LocalDate.now(),
                            LocalTime.now(), dueDate, dueTime);
                } else {
                    // User cancelled the dialog
                    return;
                }
                break;
            case "Personal":
                newTask = new edu.sdccd.cisc191.common.PersonalTask(description, LocalDate.now(),
                        LocalTime.now(), dueDate, dueTime);
                break;
            case "Other":
            default:
                newTask = new edu.sdccd.cisc191.common.OtherTask(description, LocalDate.now(),
                        LocalTime.now(), dueDate, dueTime);
                break;
        }

        // Send the new task to the server
        sendAddTaskToServer(newTask);

        // Add the task to the UI immediately
        addTaskToTreeView(newTask, rootItem);

        // Clear input fields
        taskDescriptionField.clear();
        taskTypeComboBox.getSelectionModel().clearSelection();
        dueDatePicker.setValue(null);
        dueTimeField.clear();
    }

    /**
     * Sends the newly created task to the server for persistence.
     *
     * @param task The task to be added.
     */
    private void sendAddTaskToServer(Task task) {
        new Thread(() -> {
            try {
                Message message = new Message("ADD_TASK", task);
                client.sendMessage(message);
                Message response = client.receiveMessage();
                if (response != null && "ADD_TASK_RESPONSE".equals(response.getAction())) {
                    String result = (String) response.getData();
                    if ("SUCCESS".equals(result)) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Task added successfully.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to add task.");
                    }
                }
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Network Error", "Failed to communicate with server.");
            }
        }).start();
    }

    /**
     * Adds a task to the TreeView in the UI.
     *
     * @param task       The task to be added.
     * @param parentItem The parent TreeItem under which the task should be added.
     */
    private void addTaskToTreeView(Task task, TreeItem<String> parentItem) {
        TreeItem<String> taskItem = createTreeItem(task);
        parentItem.getChildren().add(taskItem);
    }

    /**
     * Loads all tasks from the server and populates the TreeView.
     */
    private void loadTasksFromServer() {
        new Thread(() -> {
            try {
                Message message = new Message("GET_TASKS", null);
                client.sendMessage(message);
                Message response = client.receiveMessage();
                if (response != null && "TASK_LIST".equals(response.getAction())) {
                    @SuppressWarnings("unchecked")
                    List<Task> tasks = (List<Task>) response.getData();
                    Platform.runLater(() -> populateTaskTree(tasks));
                }
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Network Error", "Failed to load tasks from server.");
            }
        }).start();
    }

    /**
     * Populates the TreeView with the list of tasks retrieved from the server.
     *
     * @param tasks The list of tasks to display.
     */
    private void populateTaskTree(List<Task> tasks) {
        rootItem.getChildren().clear();
        for (Task task : tasks) {
            TreeItem<String> taskItem = createTreeItem(task);
            rootItem.getChildren().add(taskItem);
        }
    }

    /**
     * Recursively creates TreeItems for tasks and their sub-tasks.
     *
     * @param task The task for which to create a TreeItem.
     * @return The created TreeItem.
     */
    private TreeItem<String> createTreeItem(Task task) {
        TreeItem<String> item = new TreeItem<>(task.getDescription());
        item.setExpanded(true);
        for (Task subTask : task.getSubTasks()) {
            item.getChildren().add(createTreeItem(subTask));
        }
        return item;
    }

    /**
     * Shows a dialog to add a sub-task to the selected task.
     *
     * @param parentItem The TreeItem representing the parent task.
     */
    private void showAddSubTaskDialog(TreeItem<String> parentItem) {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Add Sub-Task");
        dialog.setHeaderText("Adding sub-task to: " + parentItem.getValue());

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create input fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Work", "Personal", "Other");
        typeComboBox.setPromptText("Type");
        DatePicker dueDatePicker = new DatePicker();
        dueDatePicker.setPromptText("Due Date");
        TextField dueTimeField = new TextField();
        dueTimeField.setPromptText("Due Time (HH:MM)");

        grid.add(new Label("Description:"), 0, 0);
        grid.add(descriptionField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeComboBox, 1, 1);
        grid.add(new Label("Due Date:"), 0, 2);
        grid.add(dueDatePicker, 1, 2);
        grid.add(new Label("Due Time:"), 0, 3);
        grid.add(dueTimeField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable Add button based on input
        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        // Add listeners to input fields to enable the Add button when appropriate
        descriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(newValue.trim().isEmpty() || typeComboBox.getValue() == null ||
                    dueDatePicker.getValue() == null || dueTimeField.getText().trim().isEmpty());
        });

        typeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(newValue == null || descriptionField.getText().trim().isEmpty() ||
                    dueDatePicker.getValue() == null || dueTimeField.getText().trim().isEmpty());
        });

        dueDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(newValue == null || descriptionField.getText().trim().isEmpty() ||
                    typeComboBox.getValue() == null || dueTimeField.getText().trim().isEmpty());
        });

        dueTimeField.textProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(newValue.trim().isEmpty() || descriptionField.getText().trim().isEmpty() ||
                    typeComboBox.getValue() == null || dueDatePicker.getValue() == null);
        });

        // Convert the result to a Task object when the Add button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String description = descriptionField.getText().trim();
                String type = typeComboBox.getValue();
                LocalDate dueDate = dueDatePicker.getValue();
                String dueTimeText = dueTimeField.getText().trim();

                LocalTime dueTime;
                try {
                    dueTime = LocalTime.parse(dueTimeText);
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid time format. Please use HH:MM.");
                    return null;
                }

                // Create Task object based on type
                Task subTask;
                switch (type) {
                    case "Work":
                        TextInputDialog projectDialog = new TextInputDialog();
                        projectDialog.setTitle("Project Name");
                        projectDialog.setHeaderText("Enter the project name for the Work Sub-Task:");
                        projectDialog.setContentText("Project Name:");
                        Optional<String> projectNameOpt = projectDialog.showAndWait();
                        if (projectNameOpt.isPresent()) {
                            String projectName = projectNameOpt.get().trim();
                            if (projectName.isEmpty()) {
                                showAlert(Alert.AlertType.ERROR, "Input Error", "Project name cannot be empty.");
                                return null;
                            }
                            subTask = new edu.sdccd.cisc191.common.WorkTask(description, projectName, LocalDate.now(),
                                    LocalTime.now(), dueDate, dueTime);
                        } else {
                            // User cancelled the dialog
                            return null;
                        }
                        break;
                    case "Personal":
                        subTask = new edu.sdccd.cisc191.common.PersonalTask(description, LocalDate.now(),
                                LocalTime.now(), dueDate, dueTime);
                        break;
                    case "Other":
                    default:
                        subTask = new edu.sdccd.cisc191.common.OtherTask(description, LocalDate.now(),
                                LocalTime.now(), dueDate, dueTime);
                        break;
                }

                return subTask;
            }
            return null;
        });

        Optional<Task> result = dialog.showAndWait();

        result.ifPresent(subTask -> {
            // Send sub-task to server
            String parentDescription = parentItem.getValue();
            sendAddSubTaskToServer(parentDescription, subTask);

            // Add sub-task to the TreeView
            addTaskToTreeView(subTask, parentItem);
        });
    }

    /**
     * Sends a sub-task to the server under the specified parent task.
     *
     * @param parentDescription The description of the parent task.
     * @param subTask           The sub-task to be added.
     */
    private void sendAddSubTaskToServer(String parentDescription, Task subTask) {
        new Thread(() -> {
            try {
                // Create a SubTaskMessage containing parent description and sub-task
                SubTaskMessage subTaskMessage = new SubTaskMessage(parentDescription, subTask);
                Message message = new Message("ADD_SUB_TASK", subTaskMessage);
                client.sendMessage(message);
                Message response = client.receiveMessage();
                if (response != null && "ADD_SUB_TASK_RESPONSE".equals(response.getAction())) {
                    String result = (String) response.getData();
                    if ("SUCCESS".equals(result)) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Sub-task added successfully.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to add sub-task: " + result);
                    }
                }
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Network Error", "Failed to communicate with server.");
            }
        }).start();
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
            Alert alert = new Alert(alertType, message, ButtonType.OK);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.showAndWait();
        });
    }
}
