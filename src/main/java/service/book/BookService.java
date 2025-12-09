package service.book;

import model.Book;

import java.util.List;
//primeste comenzi de la controller - the brain
public interface BookService {
    List<Book> findAll();
    Book findById(Long id);
    boolean save(Book book);
    boolean delete(Book book);
    int getAgeOfBook(Long id); //repository e pt citire, logica se face in service
}
