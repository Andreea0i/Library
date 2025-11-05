package service;

import model.Book;
import repository.BookRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BookServiceImplementation implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImplementation(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> findAll() {
        return List.of();
    }

    @Override
    public Book findById(int id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book with id: %d was not found.".formatted(id)));
    }

    @Override
    public boolean save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public boolean delete(Book book) {
        return bookRepository.delete(book);
    }

    @Override
    public int getAgeOfBook(Long id) {
        Book book = this.findById(id.intValue());
        LocalDate now = LocalDate.now();

        return (int) ChronoUnit.YEARS.between(book.getPublishedDate(), now) ;
    }
}
