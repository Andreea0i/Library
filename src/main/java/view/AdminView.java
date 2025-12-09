package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
    private final TextField titleField, authorField, quantityField, priceField;
    private final DatePicker datePicker;
    private final ObservableList<Book> bookData;

    public AdminView(Stage stage, User user, BookService bookService) {
        this.bookService = bookService;

        stage.setTitle("📖 Book Store - Admin Dashboard");

        // Container principal
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(25));
        mainLayout.getStyleClass().add("content-pane");

        // Header
        Label header = new Label("Book Store Library - Admin Panel");
        header.getStyleClass().add("header-text");

        Label welcome = new Label("Welcome, " + user.getUsername());
        welcome.getStyleClass().add("welcome-text");

        // Secțiunea de adăugare cărți
        VBox addBookSection = new VBox(10);
        addBookSection.setPadding(new Insets(15));
        addBookSection.getStyleClass().add("section-pane");

        Label sectionTitle = new Label("📚 Add New Book");
        sectionTitle.getStyleClass().add("section-title");

        HBox inputBox = new HBox(15);
        inputBox.setAlignment(Pos.CENTER_LEFT);

        titleField = new TextField();
        titleField.setPromptText("Book Title");
        titleField.setPrefWidth(120);

        authorField = new TextField();
        authorField.setPromptText("Author");
        authorField.setPrefWidth(120);

        quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        quantityField.setText("1");
        quantityField.setPrefWidth(80);

        priceField = new TextField();
        priceField.setPromptText("Price");
        priceField.setText("0.0");
        priceField.setPrefWidth(80);

        datePicker = new DatePicker();
        datePicker.setPromptText("Date");
        datePicker.setPrefWidth(120);

        Button addButton = new Button("➕ Add Book");

        inputBox.getChildren().addAll(
                new Label("Title:"), titleField,
                new Label("Author:"), authorField,
                new Label("Qty:"), quantityField,
                new Label("Price:"), priceField,
                new Label("Date:"), datePicker,
                addButton
        );

        // Tabel pentru cărți
        table = new TableView<>();
        bookData = FXCollections.observableArrayList();
        setupTableColumns();

        // Butoane de acțiune
        HBox actionButtons = new HBox(15);
        actionButtons.setAlignment(Pos.CENTER);

        Button deleteButton = new Button("🗑️ Delete Selected");
        Button refreshButton = new Button("🔄 Refresh");
        Button logoutButton = new Button("🚪 Logout");

        actionButtons.getChildren().addAll(deleteButton, refreshButton, logoutButton);

        // Asamblare layout
        addBookSection.getChildren().addAll(sectionTitle, inputBox);

        mainLayout.getChildren().addAll(
                header, welcome, addBookSection, table, actionButtons
        );

        // Event handlers
        addButton.setOnAction(e -> addBook());
        deleteButton.setOnAction(e -> deleteBook());
        refreshButton.setOnAction(e -> refreshBooks());
        logoutButton.setOnAction(e -> stage.close());

        Scene scene = new Scene(mainLayout, 1100, 700);

        String cssPath = "file:src/main/java/styles/application.css";
        scene.getStylesheets().add(cssPath);

        stage.setScene(scene);
        refreshBooks();
    }

    private void setupTableColumns() {
        TableColumn<Book, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(200);

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorCol.setPrefWidth(150);

        TableColumn<Book, LocalDate> dateCol = new TableColumn<>("Published");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));
        dateCol.setPrefWidth(120);

        TableColumn<Book, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setPrefWidth(80);

        // COLOANĂ PENTRU PRICE
        TableColumn<Book, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(80);

        table.getColumns().addAll(idCol, titleCol, authorCol, dateCol, quantityCol, priceCol);
        table.setItems(bookData);
    }

    private void addBook() {
        String title = titleField.getText();
        String author = authorField.getText();
        String quantityText = quantityField.getText();
        String priceText = priceField.getText();
        LocalDate date = datePicker.getValue();

        // VALIDARE COMPLETĂ CU PRICE
        if (title.isEmpty() || author.isEmpty() || quantityText.isEmpty() || priceText.isEmpty() || date == null) {
            showAlert("Error", "Please fill all fields including price!");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityText);
            double price = Double.parseDouble(priceText);

            if (quantity <= 0 || price < 0) {
                showAlert("Error", "Quantity must be > 0 and price must be >= 0!");
                return;
            }

            Book book = new BookBuilder()
                    .setTitle(title)
                    .setAuthor(author)
                    .setPublishedDate(date)
                    .setQuantity(quantity)
                    .setPrice(price)
                    .build();

            if (bookService.save(book)) {
                refreshBooks();
                clearFields();
                showAlert("Success", " Book added to library!\nQuantity: " + quantity + "\nPrice: " + price);
            } else {
                showAlert("Error", "Failed to add book!");
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Quantity and price must be valid numbers!");
        }
    }

    private void deleteBook() {
        Book selectedBook = table.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            if (bookService.delete(selectedBook)) {
                refreshBooks();
                showAlert("Success", " Book removed from library!");
            } else {
                showAlert("Error", "Failed to delete book!");
            }
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
        quantityField.clear();
        quantityField.setText("1");
        priceField.clear();
        priceField.setText("0.0");
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