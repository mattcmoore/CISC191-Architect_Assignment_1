// File: Client/src/main/java/edu/sdccd/cisc191/client/ClientApp.java
package edu.sdccd.cisc191.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.sdccd.cisc191.common.Message;
import edu.sdccd.cisc191.common.SubTaskMessage;
import edu.sdccd.cisc191.common.Task;
import edu.sdccd.cisc191.common.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ClientApp extends Application {
    private static Client client;
    private TreeView<Task> taskTreeView;
    private TreeItem<Task> rootItem;
    private Stage primaryStage;
    private ObjectMapper objectMapper;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        objectMapper = new ObjectMapper();
        // Explicitly register JavaTimeModule and configure the ObjectMapper
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Show the login screen
        showLoginScreen();

        // Attempt to connect to the server asynchronously
        connectToServer();
    }

    /**
     * Establishes a connection to the server in a background thread.
     */
    private void connectToServer() {
        new Thread(() -> {
            try {
                client = new Client("localhost", 5555); // Replace with your server's host and port
            } catch (IOException e) {
                Platform.runLater(() -> showError("Connection Error", "Unable to connect to the server: " + e.getMessage()));
            }
        }).start();
    }

    /**
     * Displays the login and registration UI.
     */
    private void showLoginScreen() {
        // Create UI elements for login and registration
        Label titleLabel = new Label("Task Management");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        // Handle Login Button Action
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showError("Login Error", "Please enter both username and password.");
            } else {
                authenticateUser(username, password);
            }
        });

        // Handle Register Button Action
        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showError("Registration Error", "Please enter both username and password.");
            } else {
                registerUser(username, password);
            }
        });

        HBox buttonLayout = new HBox(10, loginButton, registerButton);
        buttonLayout.setPadding(new Insets(10));

        VBox loginLayout = new VBox(10, titleLabel, usernameField, passwordField, buttonLayout);
        loginLayout.setPadding(new Insets(20));
        loginLayout.setStyle("-fx-alignment: center;");

        Scene loginScene = new Scene(loginLayout, 400, 300);

        Platform.runLater(() -> {
            primaryStage.setTitle("Task Management");
            primaryStage.setScene(loginScene);
            primaryStage.show();
        });
    }

    /**
     * Authenticates the user by sending login credentials to the server.
     *
     * @param username The entered username.
     * @param password The entered password.
     */
    private void authenticateUser(String username, String password) {
        new Thread(() -> {
            try {
                // Wait until client is initialized
                while (client == null) {
                    Thread.sleep(100);
                }

                User user = new User(username, password);
                client.sendMessage(new Message("LOGIN", user));

                Message response = client.receiveMessage();
                if (response != null && "LOGIN_RESPONSE".equals(response.getAction())) {
                    String result = (String) response.getData();
                    if ("SUCCESS".equals(result)) {
                        Platform.runLater(this::showMainApplication);
                    } else {
                        showError("Login Failed", "Invalid username or password.");
                    }
                } else {
                    showError("Login Error", "No response from server.");
                }
            } catch (IOException | InterruptedException e) {
                showError("Error", "Failed to authenticate: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Registers a new user by sending credentials to the server.
     *
     * @param username The desired username.
     * @param password The desired password.
     */
    private void registerUser(String username, String password) {
        new Thread(() -> {
            try {
                // Wait until client is initialized
                while (client == null) {
                    Thread.sleep(100);
                }

                User user = new User(username, password);
                client.sendMessage(new Message("REGISTER", user));

                Message response = client.receiveMessage();
                if (response != null && "REGISTER_RESPONSE".equals(response.getAction())) {
                    String result = (String) response.getData();
                    if ("SUCCESS".equals(result)) {
                        showInfo("Registration Successful", "You can now log in with your credentials.");
                    } else if ("USER_EXISTS".equals(result)) {
                        showError("Registration Failed", "Username already exists.");
                    } else {
                        showError("Registration Error", "Unknown server response.");
                    }
                } else {
                    showError("Registration Error", "No response from server.");
                }
            } catch (IOException | InterruptedException e) {
                showError("Error", "Failed to register: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Displays the main application UI after successful login.
     */
    private void showMainApplication() {
        // Initialize the UI components
        Label welcomeLabel = new Label("Welcome to Task Management");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // MenuBar for additional functionalities
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem saveMenuItem = new MenuItem("Save Tasks");
        saveMenuItem.setOnAction(e -> saveTasksToFile());
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(e -> {
            try {
                if (client != null) {
                    client.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Platform.exit();
        });
        fileMenu.getItems().addAll(saveMenuItem, exitMenuItem);
        menuBar.getMenus().add(fileMenu);

        // Task TreeView for hierarchical tasks and sub-tasks
        rootItem = new TreeItem<>(new Task("Tasks", null, null, null, null));
        rootItem.setExpanded(true);
        taskTreeView = new TreeView<>(rootItem);
        taskTreeView.setShowRoot(true);
        taskTreeView.setPrefHeight(400);
        taskTreeView.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(Task item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    if ("Tasks".equals(item.getDescription())) {
                        setText(item.getDescription());
                    } else {
                        String dueInfo = "";
                        if (item.getDueDate() != null && item.getDueTime() != null) {
                            dueInfo = " (Due: " + item.getDueDate() + " " + item.getDueTime() + ")";
                        }
                        setText(item.getDescription() + dueInfo);
                    }
                }
            }
        });

        // Input fields for adding new tasks
        TextField taskDescriptionField = new TextField();
        taskDescriptionField.setPromptText("Enter task description");

        ComboBox<String> taskTypeComboBox = new ComboBox<>();
        taskTypeComboBox.getItems().addAll("Work", "Personal", "Other");
        taskTypeComboBox.setPromptText("Select task type");

        DatePicker dueDatePicker = new DatePicker();
        dueDatePicker.setPromptText("Select Due Date");

        TextField dueTimeField = new TextField();
        dueTimeField.setPromptText("Enter Due Time (HH:MM)");

        Button addTaskButton = new Button("Add Task");
        addTaskButton.setOnAction(e -> {
            String description = taskDescriptionField.getText();
            String type = taskTypeComboBox.getValue();
            LocalDate dueDate = dueDatePicker.getValue();
            String dueTimeText = dueTimeField.getText();

            if (description.isEmpty() || type == null) {
                showError("Input Error", "Please enter a task description and select a task type.");
                return;
            }

            final Task[] newTask = new Task[1];
            switch (type) {
                case "Work":
                    TextInputDialog projectDialog = new TextInputDialog();
                    projectDialog.setTitle("Project Name");
                    projectDialog.setHeaderText("Enter the project name for the Work Task:");
                    projectDialog.setContentText("Project Name:");
                    projectDialog.showAndWait().ifPresent(projectName -> {
                        if (projectName.isEmpty()) {
                            showError("Input Error", "Project name cannot be empty.");
                        } else {
                            newTask[0] = new edu.sdccd.cisc191.common.WorkTask(description, projectName, LocalDate.now(), LocalTime.now(), null, null);
                            sendAddTaskToServer(newTask[0]);
                            addTaskToTreeView(newTask[0], rootItem);
                            // Clear input fields
                            taskDescriptionField.clear();
                            taskTypeComboBox.getSelectionModel().clearSelection();
                            dueDatePicker.setValue(null);
                            dueTimeField.clear();
                        }
                    });
                    return; // Exit the method since addTaskToServer is handled inside the lambda
                case "Personal":
                    newTask[0] = new edu.sdccd.cisc191.common.PersonalTask(description, LocalDate.now(), LocalTime.now(), null, null);
                    sendAddTaskToServer(newTask[0]);
                    addTaskToTreeView(newTask[0], rootItem);
                    break;
                case "Other":
                default:
                    newTask[0] = new edu.sdccd.cisc191.common.OtherTask(description, LocalDate.now(), LocalTime.now(), null, null);
                    sendAddTaskToServer(newTask[0]);
                    addTaskToTreeView(newTask[0], rootItem);
                    break;
            }

            // Clear input fields
            taskDescriptionField.clear();
            taskTypeComboBox.getSelectionModel().clearSelection();
            dueDatePicker.setValue(null);
            dueTimeField.clear();
        });

        HBox inputLayout = new HBox(10, taskDescriptionField, taskTypeComboBox, dueDatePicker, dueTimeField, addTaskButton);
        inputLayout.setPadding(new Insets(10));

        VBox layout = new VBox(menuBar, welcomeLabel, inputLayout, taskTreeView);
        Scene mainScene = new Scene(layout, 800, 600);

        Platform.runLater(() -> {
            primaryStage.setTitle("Task Management");
            primaryStage.setScene(mainScene);
            primaryStage.show();
        });

        // Load tasks from the server
        loadTasksFromServer();

        // Set up sub-task addition and task editing via double-click
        setupTaskDoubleClick();
    }

    /**
     * Loads tasks from the server and populates the TreeView.
     */
    private void loadTasksFromServer() {
        new Thread(() -> {
            try {
                client.sendMessage(new Message("GET_TASKS", null));
                Message response = client.receiveMessage();
                if (response != null && "TASK_LIST".equals(response.getAction())) {
                    @SuppressWarnings("unchecked")
                    List<Task> tasks = (List<Task>) response.getData();
                    Platform.runLater(() -> populateTaskTree(tasks));
                }
            } catch (IOException e) {
                showError("Error", "Failed to load tasks: " + e.getMessage());
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
            TreeItem<Task> taskItem = createTreeItem(task);
            rootItem.getChildren().add(taskItem);
        }
    }

    /**
     * Recursively creates TreeItems for tasks and their sub-tasks.
     *
     * @param task The task for which to create a TreeItem.
     * @return The created TreeItem.
     */
    private TreeItem<Task> createTreeItem(Task task) {
        TreeItem<Task> item = new TreeItem<>(task);
        item.setExpanded(true);
        for (Task subTask : task.getSubTasks()) {
            item.getChildren().add(createTreeItem(subTask));
        }
        return item;
    }

    /**
     * Sends a new task to the server for persistence.
     *
     * @param task The task to be added.
     */
    private void sendAddTaskToServer(Task task) {
        new Thread(() -> {
            try {
                client.sendMessage(new Message("ADD_TASK", task));
                Message response = client.receiveMessage();
                if (response != null && "ADD_TASK_RESPONSE".equals(response.getAction())) {
                    String result = (String) response.getData();
                    if ("SUCCESS".equals(result)) {
                        showInfo("Success", "Task added successfully.");
                    } else {
                        showError("Error", "Failed to add task.");
                    }
                }
            } catch (IOException e) {
                showError("Error", "Failed to send task: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Adds a task to the TreeView in the UI.
     *
     * @param task       The task to be added.
     * @param parentItem The parent TreeItem under which the task should be added.
     */
    private void addTaskToTreeView(Task task, TreeItem<Task> parentItem) {
        TreeItem<Task> taskItem = createTreeItem(task);
        parentItem.getChildren().add(taskItem);
    }

    /**
     * Sets up the event handler for adding sub-tasks and editing tasks via double-clicking a task.
     */
    private void setupTaskDoubleClick() {
        taskTreeView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                TreeItem<Task> selectedItem = taskTreeView.getSelectionModel().getSelectedItem();
                if (selectedItem != null && selectedItem != rootItem) {
                    showEditAndAddSubTaskDialog(selectedItem);
                }
            }
        });
    }

    /**
     * Displays a dialog to edit the selected task and add a sub-task.
     *
     * @param taskItem The TreeItem representing the selected task.
     */
    private void showEditAndAddSubTaskDialog(TreeItem<Task> taskItem) {
        Task task = taskItem.getValue();

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Edit Task & Add Sub-Task");
        dialog.setHeaderText("Edit Task: " + task.getDescription());

        ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        ButtonType addSubTaskButtonType = new ButtonType("Add Sub-Task", ButtonBar.ButtonData.LEFT);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, addSubTaskButtonType, ButtonType.CANCEL);

        // Create input fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField descriptionField = new TextField();
        descriptionField.setText(task.getDescription());

        DatePicker dueDatePicker = new DatePicker();
        if (task.getDueDate() != null) {
            dueDatePicker.setValue(task.getDueDate());
        }

        TextField dueTimeField = new TextField();
        if (task.getDueTime() != null) {
            dueTimeField.setText(task.getDueTime().toString());
        }

        grid.add(new Label("Description:"), 0, 0);
        grid.add(descriptionField, 1, 0);
        grid.add(new Label("Due Date:"), 0, 1);
        grid.add(dueDatePicker, 1, 1);
        grid.add(new Label("Due Time (HH:MM):"), 0, 2);
        grid.add(dueTimeField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Handle Save Changes
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String newDescription = descriptionField.getText().trim();
                LocalDate newDueDate = dueDatePicker.getValue();
                String newDueTimeText = dueTimeField.getText().trim();

                if (newDescription.isEmpty()) {
                    showError("Input Error", "Description cannot be empty.");
                    return null;
                }

                LocalTime newDueTime = null;
                if (!newDueTimeText.isEmpty()) {
                    try {
                        newDueTime = LocalTime.parse(newDueTimeText);
                    } catch (Exception e) {
                        showError("Input Error", "Invalid time format. Use HH:MM.");
                        return null;
                    }
                }

                task.setDescription(newDescription);
                task.setDueDate(newDueDate);
                task.setDueTime(newDueTime);

                // Send update to server
                sendUpdateTaskToServer(task);

                // Update TreeView
                taskTreeView.refresh();

                return null;
            } else if (dialogButton == addSubTaskButtonType) {
                showAddSubTaskDialog(taskItem);
                return null;
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Sends an updated task to the server.
     *
     * @param task The task to be updated.
     */
    private void sendUpdateTaskToServer(Task task) {
        new Thread(() -> {
            try {
                client.sendMessage(new Message("UPDATE_TASK", task));
                Message response = client.receiveMessage();
                if (response != null && "UPDATE_TASK_RESPONSE".equals(response.getAction())) {
                    String result = (String) response.getData();
                    if ("SUCCESS".equals(result)) {
                        showInfo("Success", "Task updated successfully.");
                    } else {
                        showError("Error", "Failed to update task: " + result);
                    }
                }
            } catch (IOException e) {
                showError("Error", "Failed to update task: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Displays a dialog to add a sub-task to the selected task.
     *
     * @param parentItem The TreeItem representing the parent task.
     */
    private void showAddSubTaskDialog(TreeItem<Task> parentItem) {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Add Sub-Task");
        dialog.setHeaderText("Adding sub-task to: " + parentItem.getValue().getDescription());

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create input fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

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
        grid.add(new Label("Due Time (HH:MM):"), 0, 3);
        grid.add(dueTimeField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a Task object when the Add button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String description = descriptionField.getText().trim();
                String type = typeComboBox.getValue();
                LocalDate dueDate = dueDatePicker.getValue();
                String dueTimeText = dueTimeField.getText().trim();

                if (description.isEmpty() || type == null) {
                    showError("Input Error", "Please enter a description and select a type.");
                    return null;
                }

                final Task[] newSubTask = new Task[1];
                switch (type) {
                    case "Work":
                        TextInputDialog projectDialog = new TextInputDialog();
                        projectDialog.setTitle("Project Name");
                        projectDialog.setHeaderText("Enter the project name for the Work Sub-Task:");
                        projectDialog.setContentText("Project Name:");
                        projectDialog.showAndWait().ifPresent(projectName -> {
                            if (projectName.isEmpty()) {
                                showError("Input Error", "Project name cannot be empty.");
                            } else {
                                try {
                                    LocalTime dueTime = dueTimeText.isEmpty() ? null : LocalTime.parse(dueTimeText);
                                    newSubTask[0] = new edu.sdccd.cisc191.common.WorkTask(description, projectName, LocalDate.now(), LocalTime.now(), dueDate, dueTime);
                                    dialog.setResult(newSubTask[0]);
                                } catch (Exception e) {
                                    showError("Input Error", "Invalid time format. Use HH:MM.");
                                }
                            }
                        });
                        return null; // Exit early since addTaskToServer is handled inside the lambda
                    case "Personal":
                        try {
                            LocalTime dueTime = dueTimeText.isEmpty() ? null : LocalTime.parse(dueTimeText);
                            newSubTask[0] = new edu.sdccd.cisc191.common.PersonalTask(description, LocalDate.now(), LocalTime.now(), dueDate, dueTime);
                        } catch (Exception e) {
                            showError("Input Error", "Invalid time format. Use HH:MM.");
                            return null;
                        }
                        break;
                    case "Other":
                    default:
                        try {
                            LocalTime dueTime = dueTimeText.isEmpty() ? null : LocalTime.parse(dueTimeText);
                            newSubTask[0] = new edu.sdccd.cisc191.common.OtherTask(description, LocalDate.now(), LocalTime.now(), dueDate, dueTime);
                        } catch (Exception e) {
                            showError("Input Error", "Invalid time format. Use HH:MM.");
                            return null;
                        }
                        break;
                }

                return newSubTask[0];
            }
            return null;
        });

        dialog.showAndWait().ifPresent(subTask -> {
            // Send sub-task to server
            String parentDescription = parentItem.getValue().getDescription();
            sendAddSubTaskToServer(parentDescription, subTask);

            // Add sub-task to UI
            TreeItem<Task> subTaskItem = createTreeItem(subTask);
            parentItem.getChildren().add(subTaskItem);
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
                // Create a SubTaskMessage using the common class
                SubTaskMessage subTaskMessage = new SubTaskMessage(parentDescription, subTask);
                client.sendMessage(new Message("ADD_SUB_TASK", subTaskMessage));

                Message response = client.receiveMessage();
                if (response != null && "ADD_SUB_TASK_RESPONSE".equals(response.getAction())) {
                    String result = (String) response.getData();
                    if ("SUCCESS".equals(result)) {
                        showInfo("Success", "Sub-task added successfully.");
                    } else {
                        showError("Error", "Failed to add sub-task: " + result);
                    }
                }
            } catch (IOException e) {
                showError("Error", "Failed to send sub-task: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Saves the current tasks to a JSON file.
     */
    private void saveTasksToFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Tasks");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            List<Task> tasks = new ArrayList<>();
            for (TreeItem<Task> taskItem : rootItem.getChildren()) {
                tasks.add(collectTaskFromTreeItem(taskItem));
            }
            try (FileWriter writer = new FileWriter(file)) {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, tasks);
                showInfo("Success", "Tasks saved successfully.");
            } catch (IOException e) {
                showError("Error", "Failed to save tasks: " + e.getMessage());
            }
        }
    }

    /**
     * Recursively collects a Task and its sub-tasks from a TreeItem.
     *
     * @param treeItem The TreeItem from which to collect the Task.
     * @return The collected Task.
     */
    private Task collectTaskFromTreeItem(TreeItem<Task> treeItem) {
        Task task = treeItem.getValue();
        List<Task> collectedSubTasks = new ArrayList<>();
        for (TreeItem<Task> child : treeItem.getChildren()) {
            collectedSubTasks.add(collectTaskFromTreeItem(child));
        }
        task.setSubTasks(collectedSubTasks);
        return task;
    }

    /**
     * Displays an error alert dialog to the user.
     *
     * @param title   The title of the alert.
     * @param message The error message to display.
     */
    private void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, message);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.showAndWait();
        });
    }

    /**
     * Displays an information alert dialog to the user.
     *
     * @param title   The title of the alert.
     * @param message The information message to display.
     */
    private void showInfo(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.showAndWait();
        });
    }

    @Override
    public void stop() throws Exception {
        if (client != null) {
            client.close();
        }
        super.stop();
    }

    /**
     * Static getter method to access the Client instance.
     *
     * @return The Client instance.
     */
    public static Client getClient() {
        return client;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
