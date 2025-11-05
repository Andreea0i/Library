

import database.DatabaseConnectionFactory;
import model.Book;
import org.junit.jupiter.api.*;
import repository.BookRepositoryMySQL;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookRepositoryMySQLTest {

    private static BookRepositoryMySQL bookRepository;
    private static Connection connection;

    @BeforeAll
    static void setUp() {
        // presupunem că ai o clasă DatabaseConnectionFactory care îți oferă conexiunea
        connection = DatabaseConnectionFactory.getConnectionWrapper(true).getConnection();
        bookRepository = new BookRepositoryMySQL(connection);
    }

    @Test
    void testSaveBook() {
        Book book = new Book("Test Title", "Test Author");
        boolean result = bookRepository.save(book);
        assertTrue(result, "Cartea ar trebui să se salveze cu succes");
    }

    @Test
    void testFindAllBooks() {
        List<Book> books = bookRepository.findAll();
        assertNotNull(books, "Lista nu trebuie să fie null");
    }

    @Test
    void testDeleteBook() {
        Book book = new Book("ToDelete", "Author");
        bookRepository.save(book);
        boolean result = bookRepository.delete(book);
        assertTrue(result, "Cartea ar trebui să fie ștearsă cu succes");
    }
}
