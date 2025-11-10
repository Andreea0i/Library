package launcher;

import controller.BookController;
import database.DatabaseConnectionFactory;
import javafx.application.Application;
import javafx.stage.Stage;
import mapper.BookMapper;
import repository.BookRepositoryMySQL;
import service.BookService;
import service.BookServiceImpl;
import view.BookView;
import view.model.BookDTO;

import java.util.List;

public class Launcher extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ComponentFactory.getInstance(false, primaryStage).getBookView();

        BookService bookService = new BookServiceImpl(new BookRepositoryMySQL(DatabaseConnectionFactory.getConnectionWrapper(false).getConnection()));
        List<BookDTO> booksDTOs = BookMapper.convertBookListToBookDTOList(bookService.findAll());

        BookView bookView = new BookView(primaryStage, booksDTOs);
        new BookController(bookView, bookService);

    }


}
