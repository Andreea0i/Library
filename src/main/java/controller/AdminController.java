// controller/AdminController.java
package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.User;
import model.validator.Notification;
import service.book.BookService;
import service.user.UserService;
import view.AdminViewEnhanced;
import view.LoginView;
import view.model.UserDTO;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AdminController {

    private final AdminViewEnhanced adminView;
    private final UserService userService;
    private final BookService bookService;
    private final User currentUser;

    public AdminController(AdminViewEnhanced adminView, UserService userService,
                           BookService bookService, User currentUser) {
        this.adminView = adminView;
        this.userService = userService;
        this.bookService = bookService;
        this.currentUser = currentUser;

        this.adminView.getAddUserButton().setOnAction(new AddUserButtonListener());
        this.adminView.getDeleteUserButton().setOnAction(new DeleteUserButtonListener());
        this.adminView.getRefreshUsersButton().setOnAction(new RefreshUsersButtonListener());
        this.adminView.getGenerateReportButton().setOnAction(new GenerateReportButtonListener());
        this.adminView.getLogoutButton().setOnAction(new LogoutButtonListener());
    }

    private class AddUserButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            String username = adminView.getUsernameField().getText().trim();
            String password = adminView.getPasswordField().getText().trim();
            String role = adminView.getRoleComboBox().getValue();

            //controller
            if (username.isEmpty() || password.isEmpty()) {
                adminView.showError("Input Error", "Missing fields",
                        "Please fill in both username and password.");
                return;
            }

            //service
            Notification<Boolean> notification = userService.createUser(username, password, role);

            if (notification.hasErrors()) {
                adminView.showError("Create User Error", "Failed to create user",
                        notification.getFormattedErrors());
            } else {
                adminView.showMessage("Success", "User Created",
                        "User '" + username + "' was successfully created with role: " + role);

                adminView.clearUserForm();
                adminView.refreshUserTable();
            }
        }
    }

    private class DeleteUserButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            UserDTO selectedUser = adminView.getUserTableView().getSelectionModel().getSelectedItem();


            //validare defensiva
            if (selectedUser == null) {
                adminView.showError("Delete Error", "No user selected",
                        "Please select a user to delete.");
                return;
            }

            if (selectedUser.getId() == currentUser.getId()) {
                adminView.showError("Delete Error", "Cannot delete yourself",
                        "You cannot delete your own account.");
                return;
            }

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Delete");
            confirmAlert.setHeaderText("Delete User");
            confirmAlert.setContentText("Are you sure you want to delete user: " +
                    selectedUser.getUsername() + "? This action cannot be undone.");

            if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                Notification<Boolean> notification = userService.deleteUser(selectedUser.getId());

                if (notification.hasErrors()) {
                    adminView.showError("Delete Error", "Failed to delete user",
                            notification.getFormattedErrors());
                } else {
                    adminView.showMessage("Success", "User Deleted",
                            "User '" + selectedUser.getUsername() + "' was successfully deleted.");

                    adminView.refreshUserTable();
                }
            }
        }
    }

    private class RefreshUsersButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            adminView.refreshUserTable();
            adminView.showMessage("Refresh", "User List Updated",
                    "User list has been refreshed.");
        }
    }

    private class GenerateReportButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
        }

        private void generateMonthlyReport() {
        }
    }

    private class LogoutButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {

            adminView.getStage().close();


            Stage loginStage = new Stage();
            LoginView loginView = new LoginView(loginStage);

        }
    }
}