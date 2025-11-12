package launcher;

import controller.BookController;
import database.DatabaseConnectionFactory;
import javafx.stage.Stage;
import mapper.BookMapper;
import repository.book.BookRepository;
import repository.book.BookRepositoryMySQL;
import service.book.BookService;
import service.book.BookServiceImpl;
import view.BookView;
import view.model.BookDTO;

import java.sql.Connection;
import java.util.List;

public class ComponentFactory {

    // instanța unică (marcată volatile pentru siguranță între thread-uri)
    private static volatile ComponentFactory instance;

    // dependențele
    private final BookView bookView;
    private final BookController bookController;
    private final BookRepository bookRepository;
    private final BookService bookService;

    // constructorul este PRIVATE — nu se poate apela din afară
    private ComponentFactory(Boolean componentsForTest, Stage primaryStage) {
        // Obține conexiunea corectă
        Connection connection = DatabaseConnectionFactory
                .getConnectionWrapper(componentsForTest)
                .getConnection();

        // Inițializează repository și service
        this.bookRepository = new BookRepositoryMySQL(connection);
        this.bookService = new BookServiceImpl(bookRepository);

        // Obține lista de cărți și convertește la DTO
        List<BookDTO> bookDTOs = BookMapper.convertBookListToBookDTOList(bookService.findAll());

        // Creează view + controller
        this.bookView = new BookView(primaryStage, bookDTOs);
        this.bookController = new BookController(bookView, bookService);
    }

    // implementare singleton thread-safe cu double-checked locking
    public static ComponentFactory getInstance(Boolean componentsForTest, Stage primaryStage) {
        if (instance == null) { // prima verificare fără lock (rapidă)
            synchronized (ComponentFactory.class) {
                if (instance == null) { // verificare dublă cu lock
                    instance = new ComponentFactory(componentsForTest, primaryStage);
                }
            }
        }
        return instance;
    }

    // Gettere pentru componente
    public BookView getBookView() {
        return bookView;
    }

    public BookController getBookController() {
        return bookController;
    }

    public BookRepository getBookRepository() {
        return bookRepository;
    }

    public BookService getBookService() {
        return bookService;
    }
}
