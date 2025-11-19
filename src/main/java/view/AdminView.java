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
import model.builder.BookBuilder;
import service.book.BookService;

import java.time.LocalDate;

public class AdminView {

    private final BookService bookService;
    private final TableView<Book> table;
    private final TextField titleField, authorField;
    private final DatePicker datePicker;
    private final ObservableList<Book> bookData;

    public AdminView(Stage stage, User user, BookService bookService) {
        this.bookService = bookService;

        stage.setTitle("Admin Dashboard - Book Store");

        // Componente pentru adăugare carte
        Label titleLabel = new Label("Title:");
        titleField = new TextField();

        Label authorLabel = new Label("Author:");
        authorField = new TextField();

        Label dateLabel = new Label("Publish Date:");
        datePicker = new DatePicker();

        Button addButton = new Button("Add Book");
        Button deleteButton = new Button("Delete Selected");
        Button refreshButton = new Button("Refresh");
        Button logoutButton = new Button("Logout");

        // Tabel pentru afișare cărți
        table = new TableView<>();
        bookData = FXCollections.observableArrayList();

        // Coloane tabel
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<Book, LocalDate> dateCol = new TableColumn<>("Publish Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));

        table.getColumns().addAll(titleCol, authorCol, dateCol);
        table.setItems(bookData);

        // Layout
        HBox inputBox = new HBox(10);
        inputBox.getChildren().addAll(titleLabel, titleField, authorLabel, authorField,
                dateLabel, datePicker, addButton);

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(deleteButton, refreshButton, logoutButton);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                new Label("Welcome Admin: " + user.getUsername()),
                new Label("Book Management:"),
                inputBox,
                buttonBox,
                table
        );

        // Event handlers
        addButton.setOnAction(e -> addBook());
        deleteButton.setOnAction(e -> deleteBook());
        refreshButton.setOnAction(e -> refreshBooks());
        logoutButton.setOnAction(e -> stage.close());

        Scene scene = new Scene(layout, 900, 600);
        stage.setScene(scene);

        refreshBooks(); // Încarcă cărțile la start
    }

    private void addBook() {
        String title = titleField.getText();
        String author = authorField.getText();
        LocalDate date = datePicker.getValue();

        if (title.isEmpty() || author.isEmpty() || date == null) {
            showAlert("Error", "Please fill all fields!");
            return;
        }

        Book book = new BookBuilder()
                .setTitle(title)
                .setAuthor(author)
                .setPublishedDate(date)
                .build();

        if (bookService.save(book)) {
            refreshBooks();
            clearFields();
            showAlert("Success", "Book added successfully!");
        } else {
            showAlert("Error", "Failed to add book!");
        }
    }

    private void deleteBook() {
        Book selectedBook = table.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            bookService.delete(selectedBook);
            refreshBooks();
            showAlert("Success", "Book deleted successfully!");
        } else {
            showAlert("Error", "Please select a book to delete!");
        }
    }

    private void refreshBooks() {
        bookData.clear();
        bookData.addAll(bookService.findAll());
    }

    private void clearFields() {
        titleField.clear();
        authorField.clear();
        datePicker.setValue(null);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}