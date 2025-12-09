package repository.book;

import model.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepositoryMock implements BookRepository {
    private List<Book> books;

    public BookRepositoryMock() {
        books = new ArrayList<>();
    }

    @Override
    public List<Book> findAll() {
        return books;
    }

    @Override
    public Optional<Book> findById(Long id) {
        return books.stream().filter(it -> it.getId().equals(id)).findFirst();
    }

    @Override
    public boolean save(Book book) {
        return books.add(book);
    }

    @Override
    public boolean update(Book book) {
        return books.stream()
                .filter(b -> b.getId().equals(book.getId()))
                .findFirst()
                .map(existingBook -> {
                    existingBook.setAuthor(book.getAuthor());
                    existingBook.setTitle(book.getTitle());
                    existingBook.setPublishedDate(book.getPublishedDate());
                    existingBook.setPrice(book.getPrice());
                    existingBook.setQuantity(book.getQuantity());
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean delete(Book book) {
        return books.remove(book);
    }

    @Override
    public void removeAll() {
        books.clear();
    }
}