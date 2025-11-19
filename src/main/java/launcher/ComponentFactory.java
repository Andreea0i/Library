//package launcher;
//
//import controller.BookController;
//import database.DatabaseConnectionFactory;
//import javafx.stage.Stage;
//import mapper.BookMapper;
//import repository.book.BookRepository;
//import repository.book.BookRepositoryMySQL;
//import service.book.BookService;
//import service.book.BookServiceImpl;
//import view.BookView;
//import view.model.BookDTO;
//
//import java.sql.Connection;
//import java.util.List;
//
//public class ComponentFactory {
//
//    // instanța unică (marcată volatile pentru siguranță între thread-uri)
//    private static volatile ComponentFactory instance;
//
//    // dependențele
//    private final BookView bookView;
//    private final BookController bookController;
//    private final BookRepository bookRepository;
//    private final BookService bookService;
//
//    // constructorul este PRIVATE — nu se poate apela din afară
//    private ComponentFactory(Boolean componentsForTest, Stage primaryStage) {
//        // Obține conexiunea corectă
//        Connection connection = DatabaseConnectionFactory
//                .getConnectionWrapper(componentsForTest)
//                .getConnection();
//
//        // Inițializează repository și service
//        this.bookRepository = new BookRepositoryMySQL(connection);
//        this.bookService = new BookServiceImpl(bookRepository);
//
//        // Obține lista de cărți și convertește la DTO
//        List<BookDTO> bookDTOs = BookMapper.convertBookListToBookDTOList(bookService.findAll());
//
//        // Creează view + controller
//        this.bookView = new BookView(primaryStage, bookDTOs);
//        this.bookController = new BookController(bookView, bookService);
//    }
//
//    // implementare singleton thread-safe cu double-checked locking
//    public static ComponentFactory getInstance(Boolean componentsForTest, Stage primaryStage) {
//        if (instance == null) { // prima verificare fără lock (rapidă)
//            synchronized (ComponentFactory.class) {
//                if (instance == null) { // verificare dublă cu lock
//                    instance = new ComponentFactory(componentsForTest, primaryStage);
//                }
//            }
//        }
//        return instance;
//    }
//
//    // Gettere pentru componente
//    public BookView getBookView() {
//        return bookView;
//    }
//
//    public BookController getBookController() {
//        return bookController;
//    }
//
//    public BookRepository getBookRepository() {
//        return bookRepository;
//    }
//
//    public BookService getBookService() {
//        return bookService;
//    }
//}

package launcher;
import controller.LoginController;
import database.DatabaseConnectionFactory;
import javafx.stage.Stage;
import repository.book.BookRepositoryMySQL;
import repository.security.RightsRolesRepository;
import repository.security.RightsRolesRepositoryMySQL;
import repository.user.UserRepository;
import repository.user.UserRepositoryMySQL;
import service.user.AuthenticationService;
import service.user.AuthenticationServiceMySQL;
import view.LoginView;

import java.sql.Connection;

public class ComponentFactory {
    private final LoginView loginView;
    private final LoginController loginController;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final RightsRolesRepository rightsRolesRepository;
    private final BookRepositoryMySQL bookRepository;
    private static ComponentFactory instance;
    private static Boolean componentsForTests;
    private static Stage stage;

    public static ComponentFactory getInstance(Boolean aComponentsForTests, Stage aStage) {
        if (instance == null) {
            componentsForTests = aComponentsForTests;
            stage = aStage;
            instance = new ComponentFactory(componentsForTests, stage);
        }

        return instance;
    }

    public ComponentFactory(Boolean componentsForTests, Stage stage){
        Connection connection = DatabaseConnectionFactory.getConnectionWrapper(componentsForTests).getConnection();
        this.rightsRolesRepository = new RightsRolesRepositoryMySQL(connection);
        this.userRepository = new UserRepositoryMySQL(connection, rightsRolesRepository);
        this.authenticationService = new AuthenticationServiceMySQL(userRepository, rightsRolesRepository);
        this.loginView = new LoginView(stage);
        this.loginController = new LoginController(loginView, authenticationService);
        this.bookRepository = new BookRepositoryMySQL(connection);
    }

    public static Stage getStage(){
        return stage;
    }

    public static Boolean getComponentsForTests(){
        return componentsForTests;
    }

    public AuthenticationService getAuthenticationService(){
        return authenticationService;
    }

    public UserRepository getUserRepository(){
        return userRepository;
    }

    public RightsRolesRepository getRightsRolesRepository(){
        return rightsRolesRepository;
    }

    public LoginView getLoginView(){
        return loginView;
    }

    public BookRepositoryMySQL getBookRepository(){
        return bookRepository;
    }

    public LoginController getLoginController(){
        return loginController;
    }

}
