import database.DatabaseConnectionFactory;
import model.Book;
import model.builder.BookBuilder;
import org.junit.jupiter.api.*;
import repository.book.BookRepositoryMySQL;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookRepositoryMySQLTest {

    private static BookRepositoryMySQL bookRepository;

    @BeforeAll
    static void setup() {
        Connection connection = DatabaseConnectionFactory.getConnectionWrapper(true).getConnection(); // test schema
        bookRepository = new BookRepositoryMySQL(connection);
        bookRepository.removeAll(); // curățare tabele
    }

    @Test
    void testSaveAndFindAll() {
        Book book = new BookBuilder()
                .setTitle("Test Title")
                .setAuthor("Test Author")
                .setPublishedDate(LocalDate.of(2020, 1, 1))
                .build();

        boolean saved = bookRepository.save(book);
        assertTrue(saved);

        List<Book> books = bookRepository.findAll();
        assertEquals(1, books.size());
        assertEquals("Test Title", books.get(0).getTitle());
    }

    @Test
    void testDelete() {
        Book book = new BookBuilder()
                .setTitle("Delete Title")
                .setAuthor("Delete Author")
                .setPublishedDate(LocalDate.of(2019, 1, 1))
                .build();

        bookRepository.save(book);
        boolean deleted = bookRepository.delete(book);
        assertTrue(deleted);
    }
}
