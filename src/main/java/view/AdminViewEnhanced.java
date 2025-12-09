package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.User;
import service.book.BookService;
import service.user.UserService;
import view.model.UserDTO;

import java.util.List;
import java.util.stream.Collectors;

public class AdminViewEnhanced {
    private Stage primaryStage;
    private User currentUser;
    private BookService bookService;
    private UserService userService;

    private TableView<UserDTO> userTableView;
    private ObservableList<UserDTO> userObservableList;

    private TextField usernameField;
    private PasswordField passwordField;
    private ComboBox<String> roleComboBox;
    private Button addUserButton;
    private Button deleteUserButton;
    private Button generateReportButton;
    private Button logoutButton;
    private Button refreshUsersButton;

    public AdminViewEnhanced(Stage primaryStage, User currentUser,
                             BookService bookService, UserService userService) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
        this.bookService = bookService;
        this.userService = userService;

        initializeView();
    }

    private void initializeView() {
        primaryStage.setTitle("Admin Dashboard - Welcome " + currentUser.getUsername());

        BorderPane borderPane = new BorderPane();

        //welcome in
        Label welcomeLabel = new Label(" Admin - User Management");
        //welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        HBox topBox = new HBox(welcomeLabel);
        topBox.setPadding(new Insets(20));
        topBox.setAlignment(Pos.CENTER);
        borderPane.setTop(topBox);

        // Centrare
        VBox centerBox = new VBox(20);
        centerBox.setPadding(new Insets(20));

        // add users
        Label addUserLabel = new Label(" Add New User");
        //addUserLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(15));
        //formGrid.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 5;");

        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-font-weight: bold;");
        usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setPrefWidth(200);

        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-weight: bold;");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setPrefWidth(200);

        Label roleLabel = new Label("Role:");
        roleLabel.setStyle("-fx-font-weight: bold;");
        roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Administrator", "Employee", "Customer");
        roleComboBox.setValue("Employee");
        roleComboBox.setPrefWidth(150);

        addUserButton = new Button("Add User");
        //addUserButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
        addUserButton.setPrefWidth(100);

        formGrid.add(usernameLabel, 0, 0);
        formGrid.add(usernameField, 1, 0);
        formGrid.add(passwordLabel, 0, 1);
        formGrid.add(passwordField, 1, 1);
        formGrid.add(roleLabel, 0, 2);
        formGrid.add(roleComboBox, 1, 2);
        formGrid.add(addUserButton, 1, 3);

        // arata users
        Label userListLabel = new Label("👥 User List");
        //userListLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        userTableView = new TableView<>();
        userObservableList = FXCollections.observableArrayList();
        userTableView.setItems(userObservableList);
        userTableView.setPrefHeight(300);

        TableColumn<UserDTO, Long> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(80);

        TableColumn<UserDTO, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameColumn.setPrefWidth(200);

        TableColumn<UserDTO, String> rolesColumn = new TableColumn<>("Roles");
        rolesColumn.setCellValueFactory(new PropertyValueFactory<>("roles"));
        rolesColumn.setPrefWidth(250);

        userTableView.getColumns().addAll(idColumn, usernameColumn, rolesColumn);

        // Load users
        loadUsers();

        // Bottom: Action buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setPadding(new Insets(20));
        buttonBox.setAlignment(Pos.CENTER);

        refreshUsersButton = new Button("🔄 Refresh Users");
        refreshUsersButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white;");

        deleteUserButton = new Button("🗑️ Delete Selected User");
        deleteUserButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");

        generateReportButton = new Button("📊 Generate Monthly Report");
        generateReportButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");

        logoutButton = new Button("🚪 Logout");
        logoutButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");

        buttonBox.getChildren().addAll(refreshUsersButton, deleteUserButton, generateReportButton, logoutButton);

        centerBox.getChildren().addAll(addUserLabel, formGrid, userListLabel, userTableView);
        borderPane.setCenter(centerBox);
        borderPane.setBottom(buttonBox);

        Scene scene = new Scene(borderPane, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadUsers() {
        List<User> users = userService.findAllUsers();
        List<UserDTO> userDTOs = users.stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getRoles().stream()
                                .map(role -> role.getRole())
                                .collect(Collectors.joining(", "))
                ))
                .collect(Collectors.toList());

        userObservableList.setAll(userDTOs);
    }

    // Getters for buttons
    public Button getAddUserButton() { return addUserButton; }
    public Button getDeleteUserButton() { return deleteUserButton; }
    public Button getGenerateReportButton() { return generateReportButton; }
    public Button getLogoutButton() { return logoutButton; }
    public Button getRefreshUsersButton() { return refreshUsersButton; }

    public TextField getUsernameField() { return usernameField; }
    public PasswordField getPasswordField() { return passwordField; }
    public ComboBox<String> getRoleComboBox() { return roleComboBox; }
    public TableView<UserDTO> getUserTableView() { return userTableView; }
    public ObservableList<UserDTO> getUserObservableList() { return userObservableList; }
    public Stage getStage() { return primaryStage; }

    public void showMessage(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Stilizare
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStyleClass().add("dialog-pane");

        alert.showAndWait();
    }

    public void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStyleClass().add("dialog-pane");

        alert.showAndWait();
    }

    public void refreshUserTable() {
        loadUsers();
    }

    public void clearUserForm() {
        usernameField.clear();
        passwordField.clear();
        roleComboBox.setValue("Employee");
    }
}
