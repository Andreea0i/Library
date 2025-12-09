package launcher;

import controller.LoginController;
import database.DatabaseConnectionFactory;
import javafx.stage.Stage;
import repository.book.BookRepositoryMySQL;
import repository.security.RightsRolesRepository;
import repository.security.RightsRolesRepositoryMySQL;
import repository.user.UserRepository;
import repository.user.UserRepositoryMySQL;
import service.book.BookService;
import service.book.BookServiceImpl;
import service.user.AuthenticationService;
import service.user.AuthenticationServiceMySQL;
import service.user.UserService;
import service.user.UserServiceImpl;
import view.LoginView;
import view.model.sale.SaleRepositoryMySQL;
import view.model.sale.SaleService;
import view.model.sale.SaleServiceImpl;

import java.sql.Connection;

public class ComponentFactory {

    private static ComponentFactory instance;

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final RightsRolesRepository rightsRolesRepository;
    private final UserService userService; // ADAUGĂ ASTA

    private final BookRepositoryMySQL bookRepository;
    private final BookService bookService;
    private final SaleService saleService;

    private final LoginView loginView;
    private final LoginController loginController;

    private static Boolean componentsForTests;
    private static Stage stage;
    private final SaleRepositoryMySQL saleRepository;

    public static ComponentFactory getInstance(Boolean aComponentsForTests, Stage aStage) {
        if (instance == null) {
            componentsForTests = aComponentsForTests;
            stage = aStage;
            instance = new ComponentFactory(componentsForTests, stage);
        }
        return instance;
    }

    private ComponentFactory(Boolean componentsForTests, Stage stage) {

        Connection connection = DatabaseConnectionFactory
                .getConnectionWrapper(componentsForTests)
                .getConnection();

        // SECURITY
        this.rightsRolesRepository = new RightsRolesRepositoryMySQL(connection);
        this.userRepository = new UserRepositoryMySQL(connection, rightsRolesRepository);
        this.authenticationService = new AuthenticationServiceMySQL(userRepository, rightsRolesRepository);

        // USER SERVICE
        this.userService = new UserServiceImpl(userRepository, rightsRolesRepository, authenticationService);

        // BOOKS
        this.bookRepository = new BookRepositoryMySQL(connection);
        this.bookService = new BookServiceImpl(bookRepository);

        // SALES
        this.saleRepository = new SaleRepositoryMySQL(connection);
        this.saleService = new SaleServiceImpl(saleRepository, bookRepository, userRepository);

        // LOGIN VIEW
        this.loginView = new LoginView(stage);

        // LOGIN CONTROLLER
        this.loginController =
                new LoginController(loginView, authenticationService, bookService, saleService);
    }

    public static void resetForNewStage(Stage newStage) {
        instance = null;
        getInstance(false, newStage);
    }

    public UserService getUserService() {
        return userService;
    }

    public LoginView getLoginView() {
        return loginView;
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public BookService getBookService() {
        return bookService;
    }

    public SaleService getSaleService() {
        return saleService;
    }

    public BookRepositoryMySQL getBookRepository() {
        return bookRepository;
    }
}