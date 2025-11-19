package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import model.User;
import model.validator.Notification;
import model.validator.UserValidator;
//import repository.user.AuthenticationService;
import service.book.BookService;
import view.AdminView;
import view.EmployeeView;
import view.LoginView;
import service.user.AuthenticationService;


import java.util.List;

public class LoginController {

    private final LoginView loginView;
    private final AuthenticationService authenticationService;
    //private final UserValidator userValidator;

    private final BookService bookService;

    public LoginController(LoginView loginView, AuthenticationService authenticationService, BookService bookService) {
        this.loginView = loginView;
        this.authenticationService = authenticationService;
        //this.userValidator = userValidator;

        this.bookService = bookService;

        this.loginView.addLoginButtonListener(new LoginButtonListener());
        this.loginView.addRegisterButtonListener(new RegisterButtonListener());
    }

    private class LoginButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            String username = loginView.getUsername();
            String password = loginView.getPassword();

            Notification<User> loginNotification = authenticationService.login(username, password);

            if (loginNotification.hasErrors()) {
                loginView.setActionTargetText(loginNotification.getFormattedErrors());
            } else {
                loginView.setActionTargetText("Login successful! Welcome " + username);
                openUserView(loginNotification.getResult());
            }
        }
    }

    private class RegisterButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            String username = loginView.getUsername();
            String password = loginView.getPassword();

            Notification<Boolean> registerNotification = authenticationService.register(username, password);
            if (registerNotification.hasErrors()) {
                loginView.setActionTargetText(registerNotification.getFormattedErrors());
            } else {
                loginView.setActionTargetText("Register successful!");
            }
        }
    }

    // ADAUGĂ METODELE ASTEA NOI:

    private void openUserView(User user) {
        Stage currentStage = (Stage) loginView.getLoginButton().getScene().getWindow();

        // Verifică rolurile utilizatorului
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getRole()));

        if (isAdmin) {
            openAdminView(currentStage, user);
        } else {
            openEmployeeView(currentStage, user);
        }
    }

    private void openAdminView(Stage currentStage, User user) {
        currentStage.close();
        Stage adminStage = new Stage();
        AdminView adminView = new AdminView(adminStage, user, bookService); // PASEAZĂ bookService
        adminStage.show();
    }

    private void openEmployeeView(Stage currentStage, User user) {
        currentStage.close();
        Stage employeeStage = new Stage();
        EmployeeView employeeView = new EmployeeView(employeeStage, user, bookService); // ȘI AICI
        employeeStage.show();
    }
}