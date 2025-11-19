package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Book;
import model.User;
import service.book.BookService;

import java.time.LocalDate;

public class EmployeeView {

    private final BookService bookService;
    private final TableView<Book> table;
    private final TextField titleField;
    private final TextField authorField;
    private final DatePicker datePicker;

    public EmployeeView(Stage stage, User user, BookService bookService) {
        this.bookService = bookService;

        stage.setTitle("Employee Dashboard - Book Store");

        Label welcomeLabel = new Label("Welcome Employee: " + user.getUsername());

        // Butoane
        Button addButton = new Button("Add Book");
        Button deleteButton = new Button("Delete Selected");
        Button refreshButton = new Button("Refresh Books");
        Button logoutButton = new Button("Logout");

        // Câmpuri pentru introducerea datelor
        titleField = new TextField();
        titleField.setPromptText("Title");

        authorField = new TextField();
        authorField.setPromptText("Author");

        datePicker = new DatePicker();
        datePicker.setPromptText("Publish Date");

        // Tabel
        table = new TableView<>();
        ObservableList<Book> bookData = FXCollections.observableArrayList();

        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<Book, LocalDate> dateCol = new TableColumn<>("Publish Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));

        table.getColumns().addAll(titleCol, authorCol, dateCol);
        table.setItems(bookData);

        // Layout pentru introducere
        HBox inputLayout = new HBox(10, titleField, authorField, datePicker, addButton);
        inputLayout.setPadding(new Insets(10));

        // Layout general
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                welcomeLabel,
                new Label("Available Books:"),
                refreshButton,
                table,
                inputLayout,
                deleteButton,
                logoutButton
        );

        // Event handlers
        addButton.setOnAction(e -> addBook());
        deleteButton.setOnAction(e -> deleteSelectedBook());
        refreshButton.setOnAction(e -> refreshBooks());
        logoutButton.setOnAction(e -> stage.close());

        Scene scene = new Scene(layout, 850, 600);
        stage.setScene(scene);

        refreshBooks(); // încarcă la start
    }

    private void addBook() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        LocalDate date = datePicker.getValue();

        if (title.isEmpty() || author.isEmpty() || date == null) {
            showAlert(Alert.AlertType.WARNING, "Please fill all fields!");
            return;
        }

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setPublishedDate(date);

        bookService.save(book);
        refreshBooks();

        titleField.clear();
        authorField.clear();
        datePicker.setValue(null);

        showAlert(Alert.AlertType.INFORMATION, "Book added successfully!");
    }

    private void deleteSelectedBook() {
        Book selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a book to delete!");
            return;
        }

        bookService.delete(selected);
        refreshBooks();
        showAlert(Alert.AlertType.INFORMATION, "Book deleted successfully!");
    }

    private void refreshBooks() {
        table.getItems().clear();
        table.getItems().addAll(bookService.findAll());
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
