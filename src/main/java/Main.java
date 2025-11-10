import database.DatabaseConnectionFactory;
import model.Book;
import model.builder.BookBuilder;
import repository.BookRepository;
import repository.BookRepositoryMock;
import repository.BookRepositoryMySQL;
import service.BookService;
import service.BookServiceImpl;

import java.sql.Connection;


import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World");

        Book book = new BookBuilder()
                .setTitle("Ion")
                .setAuthor("Liviu Rebreanu")
                .setPublishedDate(LocalDate.of(1910, 10, 20))
                .build();
        System.out.println(book);
//
//        BookRepository bookRepository = new BookRepositoryMock();
//
//        bookRepository.save(book);
//        bookRepository.save(new BookBuilder().setAuthor("Ioan Slavici").setTitle("Moara cu noroc").setPublishedDate(LocalDate.of(1950, 2, 10)).build());
//        System.out.println(bookRepository.findAll());
//        bookRepository.removeAll();
//        System.out.println(bookRepository.findAll());

        //DatabaseConnectionFactory.getConnectionWrapper(false);

        Connection connection = DatabaseConnectionFactory.getConnectionWrapper(false).getConnection();
        BookRepository bookRepository = new BookRepositoryMySQL(connection);
        BookService bookService = new BookServiceImpl(bookRepository);


        bookService.save(book);
        System.out.println(bookService.findAll());
        Book bookMoaraCuNoroc = new BookBuilder().setAuthor("Ioan Slavici").setTitle("Moara cu noroc").setPublishedDate(LocalDate.of(1950, 2, 10)).build();
        bookService.save(bookMoaraCuNoroc);
        System.out.println(bookService.findAll());
        bookService.delete(bookMoaraCuNoroc);
        bookService.delete(book);
       // bookService.save(book);
        System.out.println(bookService.findAll());

        Book book1 = new BookBuilder()
                .setTitle("Ion")
                .setAuthor("Liviu Rebreanu")
                .setPublishedDate(LocalDate.of(1910, 10, 20))
                .build();

        Book book2 = new BookBuilder()
                .setTitle("Moara cu noroc")
                .setAuthor("Ioan Slavici")
                .setPublishedDate(LocalDate.of(1950, 2, 10))
                .build();

        bookService.save(book);
        bookService.save(bookMoaraCuNoroc);

    }
}
