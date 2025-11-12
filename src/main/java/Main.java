import controller.LoginController;
import database.DatabaseConnectionFactory;
import database.JDBConnectionWrapper;
import javafx.application.Application;
import javafx.stage.Stage;
import model.Book;
import model.builder.BookBuilder;
import model.validator.UserValidator;
import repository.book.BookRepository;
import repository.book.BookRepositoryCacheDecorator;
import repository.book.BookRepositoryMySQL;
import repository.book.Cache;
import repository.security.RightsRolesRepository;
import repository.security.RightsRolesRepositoryMySQL;
import repository.user.AuthenticationService;
import repository.user.UserRepository;
import repository.user.UserRepositoryMySQL;
import service.book.BookService;
import service.book.BookServiceImpl;
import service.user.AuthenticationServiceMySQL;
import view.LoginView;

import java.sql.Connection;


import java.time.LocalDate;

import static database.Constants.Schemas.PRODUCTION;

public class Main extends Application {
    public static void main(String[] args) {
        //System.out.println("Hello World");
        launch(args);

//        Book book = new BookBuilder()
//                .setTitle("Ion")
//                .setAuthor("Liviu Rebreanu")
//                .setPublishedDate(LocalDate.of(1910, 10, 20))
//                .build();
//        System.out.println(book);
//
//        BookRepository bookRepository = new BookRepositoryMock();
//
//        bookRepository.save(book);
//        bookRepository.save(new BookBuilder().setAuthor("Ioan Slavici").setTitle("Moara cu noroc").setPublishedDate(LocalDate.of(1950, 2, 10)).build());
//        System.out.println(bookRepository.findAll());
//        bookRepository.removeAll();
//        System.out.println(bookRepository.findAll());

        //DatabaseConnectionFactory.getConnectionWrapper(false);

       // Connection connection = DatabaseConnectionFactory.getConnectionWrapper(false).getConnection();
       // BookRepository bookRepository = new BookRepositoryMySQL(connection);
       // BookService bookService = new BookServiceImpl(bookRepository);

//        BookRepository bookRepository = new BookRepositoryCacheDecorator(new BookRepositoryMySQL(connection), new Cache<>());
//        BookService bookService = new BookServiceImpl(bookRepository);
//
//        bookRepository.save(book);
//        System.out.println(bookRepository.findAll());




//        bookService.save(book);
//        System.out.println(bookService.findAll());
//        Book bookMoaraCuNoroc = new BookBuilder().setAuthor("Ioan Slavici").setTitle("Moara cu noroc").setPublishedDate(LocalDate.of(1950, 2, 10)).build();
//        bookService.save(bookMoaraCuNoroc);
//        System.out.println(bookService.findAll());
//        bookService.delete(bookMoaraCuNoroc);
//        bookService.delete(book);
//       // bookService.save(book);
//        System.out.println(bookService.findAll());

//        Book book1 = new BookBuilder()
//                .setTitle("Ion")
//                .setAuthor("Liviu Rebreanu")
//                .setPublishedDate(LocalDate.of(1910, 10, 20))
//                .build();
//
//        Book book2 = new BookBuilder()
//                .setTitle("Moara cu noroc")
//                .setAuthor("Ioan Slavici")
//                .setPublishedDate(LocalDate.of(1950, 2, 10))
//                .build();
//
//        bookService.save(book);
//        bookService.save(bookMoaraCuNoroc);

//        BookRepository bookRepository = new BookRepositoryCacheDecorator(
//                new BookRepositoryMySQL(DatabaseConnectionFactory.getConnectionWrapper(true).getConnection()),
//                new Cache<>()
//        );
//
//        BookService bookService = new BookServiceImpl(bookRepository);
//
//        Connection connection = DatabaseConnectionFactory.getConnectionWrapper(true).getConnection();
//
//        RightsRolesRepository rightsRolesRepository = new RightsRolesRepositoryMySQL(connection);
//        UserRepository userRepository = new UserRepositoryMySQL(connection, rightsRolesRepository);
//
//        AuthenticationService authenticationService = new AuthenticationServiceMySQL(userRepository, rightsRolesRepository);
//
//       if(userRepository.existsByUsername("Alex")){
//           System.out.println("Username already present into user table!");
//       } else {
//           authenticationService.register("Andreea", "parola123!");
//       }

       // authenticationService.register("Andreea", "parola123");

        //System.out.println(authenticationService.login("Andreea", "parola123!"));

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        final Connection connection = new JDBConnectionWrapper(PRODUCTION).getConnection();

        final RightsRolesRepository rightsRolesRepository = new RightsRolesRepositoryMySQL(connection);
        final UserRepository userRepository = new UserRepositoryMySQL(connection, rightsRolesRepository);

        final AuthenticationService authenticationService = new AuthenticationServiceMySQL(userRepository, rightsRolesRepository);

        final LoginView loginView = new LoginView(primaryStage);

        final UserValidator userValidator = new UserValidator(userRepository);

        new LoginController(loginView, authenticationService, userValidator);

    }
}
