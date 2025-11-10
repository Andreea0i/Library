import model.Book;
import model.builder.BookBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import repository.BookRepository;
import repository.BookRepositoryMock;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookRepositoryMockTest {
    private static BookRepository bookRepository;

    @BeforeAll
    public static void setUp() {
        bookRepository = new BookRepositoryMock();
    }

    @Test
    public void findAll() {
        List<Book> books = bookRepository.findAll();
        assertEquals(0, books.size());
    }

    @Test
    public void findById() {
        final Optional<Book> books = bookRepository.findById(1L);
        assertTrue(books.isEmpty());
    }

    @Test
    public void save() {
        assertTrue(bookRepository.save(new BookBuilder().setTitle("Ion").setAuthor("Liviu Rebreanu").setPublishedDate(LocalDate.of(1980, 10, 2)).build()));
    }
}
