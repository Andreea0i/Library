
package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import model.validator.Notification;
import service.book.BookService;
import service.user.UserService;
import view.AdminViewEnhanced;
import view.BookView;
import view.EmployeeView;
import view.LoginView;
import service.user.AuthenticationService;
import view.model.sale.SaleService;
import launcher.ComponentFactory;


public class LoginController {

    private final LoginView loginView;
    private final AuthenticationService authenticationService;
    private final BookService bookService;
    private final SaleService saleService;

    public LoginController(LoginView loginView, AuthenticationService authenticationService,
                           BookService bookService, SaleService saleService) {
        this.loginView = loginView;
        this.authenticationService = authenticationService;
        this.bookService = bookService;
        this.saleService = saleService;

        this.loginView.addLoginButtonListener(new LoginButtonListener());
        this.loginView.addRegisterButtonListener(new RegisterButtonListener());

        if (loginView.getLoginButton() == null) {
            System.err.println("ERROR: loginButton is null!");
        } else {
            System.out.println("Adding listener to login button...");
            this.loginView.addLoginButtonListener(new LoginButtonListener());
        }

        if (loginView.getSignInButton() == null) {
            System.err.println("ERROR: signInButton is null!");
        } else {
            System.out.println("Adding listener to sign in button...");
            this.loginView.addRegisterButtonListener(new RegisterButtonListener());
        }

        System.out.println("LoginController setup complete");
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

    private void openUserView(User user) {
        Stage currentStage = (Stage) loginView.getLoginButton().getScene().getWindow();

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> "Administrator".equals(role.getRole()) || "ADMIN".equals(role.getRole()));

        if (isAdmin) {
            openAdminView(currentStage, user);
        } else {
            openEmployeeView(currentStage, user);
        }
    }

    private void openAdminView(Stage currentStage, User user) {
        currentStage.close();
        Stage adminStage = new Stage();

        UserService userService = ComponentFactory.getInstance(false, adminStage).getUserService();

        AdminViewEnhanced adminView = new AdminViewEnhanced(adminStage, user, bookService, userService);
        AdminController adminController = new AdminController(adminView, userService, bookService, user);
        adminStage.show();
    }

    private void openEmployeeView(Stage currentStage, User user) {
        currentStage.close();
        Stage employeeStage = new Stage();
        EmployeeView employeeView = new EmployeeView(employeeStage, user, bookService, saleService);
        employeeStage.show();
    }
}