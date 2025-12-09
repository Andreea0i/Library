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
import model.validator.Notification;
import service.book.BookService;
import view.model.sale.SaleService;

import java.time.LocalDate;

import java.time.LocalDate;

public class EmployeeView {

    private final BookService bookService;
    private final SaleService saleService; // SCHIMBĂ TIPUL
    private final User currentUser; // ADAUGĂ ASTA PENTRU A ȘTI CINE VÂNDE

    private final TableView<Book> table;
    private final TextField titleField;
    private final TextField authorField;
    private final TextField priceField;
    private final DatePicker datePicker;
    private final TextField sellQuantityField;

    public EmployeeView(Stage stage, User user, BookService bookService, SaleService saleService) {
        this.bookService = bookService;
        this.saleService = saleService;
        this.currentUser = user; // SALVEAZĂ USERUL CURENT

        stage.setTitle("📖 Book Store - Employee Dashboard");

        // Container principal
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(25));
        mainLayout.getStyleClass().add("content-pane");

        // Header
        Label header = new Label("Book Store Library - Employee Panel");
        header.getStyleClass().add("header-text");

        Label welcome = new Label("Welcome, " + user.getUsername());
        welcome.getStyleClass().add("welcome-text");

        // Secțiunea pentru adăugare cărți
        VBox addBookSection = new VBox(10);
        addBookSection.setPadding(new Insets(15));
        addBookSection.getStyleClass().add("section-pane");

        Label sectionTitle = new Label("📚 Add New Book");
        sectionTitle.getStyleClass().add("section-title");

        // Câmpuri pentru introducerea datelor
        HBox inputLayout = new HBox(15);
        inputLayout.setAlignment(Pos.CENTER_LEFT);

        titleField = new TextField();
        titleField.setPromptText("Book Title");
        titleField.setPrefWidth(150);

        authorField = new TextField();
        authorField.setPromptText("Author");
        authorField.setPrefWidth(120);

        priceField = new TextField();
        priceField.setPromptText("Price");
        priceField.setPrefWidth(80);

        datePicker = new DatePicker();
        datePicker.setPromptText("Publish Date");
        datePicker.setPrefWidth(120);

        Button addButton = new Button("➕ Add Book");

        inputLayout.getChildren().addAll(
                new Label("Title:"), titleField,
                new Label("Author:"), authorField,
                new Label("Price:"), priceField,
                new Label("Date:"), datePicker,
                addButton
        );

        // Tabel pentru cărți
        table = new TableView<>();
        ObservableList<Book> bookData = FXCollections.observableArrayList();

        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(200);

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorCol.setPrefWidth(150);

        TableColumn<Book, LocalDate> dateCol = new TableColumn<>("Publish Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));
        dateCol.setPrefWidth(120);

        TableColumn<Book, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        qtyCol.setPrefWidth(60);

        // COLOANĂ PENTRU PRICE
        TableColumn<Book, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(80);

        table.getColumns().addAll(titleCol, authorCol, dateCol, qtyCol, priceCol);
        table.setItems(bookData);

        // Butoane de acțiune
        HBox actionButtons = new HBox(12);
        actionButtons.setAlignment(Pos.CENTER_LEFT);

        Button deleteButton = new Button("🗑️ Delete Selected");
        Button refreshButton = new Button("🔄 Refresh Books");
        Button logoutButton = new Button("🚪 Logout");

        // SELL controls
        sellQuantityField = new TextField();
        sellQuantityField.setPromptText("Qty");
        sellQuantityField.setPrefWidth(80);

        Button sellButton = new Button("💸 Sell Selected");

        actionButtons.getChildren().addAll(deleteButton, refreshButton, new Label("Sell qty:"), sellQuantityField, sellButton, logoutButton);

        // Asamblare layout
        addBookSection.getChildren().addAll(sectionTitle, inputLayout);

        mainLayout.getChildren().addAll(
                header,
                welcome,
                addBookSection,
                new Label("Available Books:"),
                table,
                actionButtons
        );

        // Event handlers
        addButton.setOnAction(e -> addBook());
        deleteButton.setOnAction(e -> deleteSelectedBook());
        refreshButton.setOnAction(e -> refreshBooks());
        logoutButton.setOnAction(e -> stage.close());

        sellButton.setOnAction(e -> sellSelectedBook());

        Scene scene = new Scene(mainLayout, 1000, 700);

        // CSS (dacă ai)
        String cssPath = "file:src/main/java/styles/application.css";
        scene.getStylesheets().add(cssPath);

        stage.setScene(scene);
        stage.show();

        refreshBooks();
    }

    private void addBook() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String priceText = priceField.getText().trim();
        LocalDate date = datePicker.getValue();

        if (title.isEmpty() || author.isEmpty() || priceText.isEmpty() || date == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill all fields including price!");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);

            Book book = new BookBuilder()
                    .setTitle(title)
                    .setAuthor(author)
                    .setPublishedDate(date)
                    .setQuantity(20)
                    .setPrice(price)
                    .build();

            if (bookService.save(book)) {
                refreshBooks();
                clearFields();
                showAlert(Alert.AlertType.INFORMATION, "Success", "📖 Book added successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add book!");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Price must be a valid number!");
        }
    }

    private void deleteSelectedBook() {
        Book selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a book to delete!");
            return;
        }

        // Confirmare ștergere
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete: " + selected.getTitle() + " ?");
        var result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        if (bookService.delete(selected)) {
            refreshBooks();
            showAlert(Alert.AlertType.INFORMATION, "Success", "🗑️ Book deleted successfully!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete book!");
        }
    }

    private void sellSelectedBook() {
        Book selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a book to sell!");
            return;
        }

        String qtyText = sellQuantityField.getText().trim();
        if (qtyText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a quantity to sell!");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(qtyText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Quantity must be a number!");
            return;
        }

        // DEBUG - afișează informații despre user
        System.out.println("=== DEBUG EmployeeView ===");
        System.out.println("currentUser: " + currentUser);
        System.out.println("currentUser.getId(): " + currentUser.getId());
        System.out.println("currentUser.getUsername(): " + currentUser.getUsername());

        // FIX CRITICAL: Dacă ID-ul este null, folosește un ID hardcodat pentru test
        Long employeeId = currentUser.getId();

        if (employeeId == null) {
            // ID-ul este NULL - folosește un ID hardcodat pentru test
            System.out.println("WARNING: User ID is null! Using default ID for testing.");

            // VERIFICĂ ÎN BAZA TA DE DATE CE ID ARE USERUL TĂU!
            // Rulează: SELECT id, username FROM user;

            // Exemplu: dacă andreea230604@gmail.com are id=2 în baza ta
            employeeId = 2L; // SCHIMBĂ ACEST NUMĂR CU ID-UL REAL DIN BAZA TA!

            showAlert(Alert.AlertType.WARNING, "System Notice",
                    "Using temporary employee ID for testing: " + employeeId + "\n" +
                            "To fix permanently, check UserRepositoryMySQL.findByUserNameAndPassword()");
        }


        double totalPrice = selected.getPrice() * qty;

        Notification<Boolean> result = saleService.sellBook(
                selected.getId(),        // bookId
                employeeId,             // employeeId (acum nu mai e null)
                qty,                     // quantity
                selected.getPrice()      // unitPrice
        );

        if (result == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Sale failed (no response)!");
            return;
        }

        if (result.hasErrors()) {
            showAlert(Alert.AlertType.ERROR, "Error", result.getFormattedErrors());
            return;
        }

        Boolean ok = result.getResult();
        if (Boolean.TRUE.equals(ok)) {
            refreshBooks();
            sellQuantityField.clear();

            //mesajul cand vinzi
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    " Book sold successfully!\n" +
                            " Book: " + selected.getTitle() + "\n" +
                            " Quantity: " + qty + "\n" +
                            " Unit Price: lei" + String.format("%.2f", selected.getPrice()) + "\n" +
                            " Total: lei" + String.format("%.2f", totalPrice));
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Sale failed!");
        }
    }

    private void refreshBooks() {
        table.getItems().clear();
        table.getItems().addAll(bookService.findAll());
    }

    private void clearFields() {
        titleField.clear();
        authorField.clear();
        priceField.clear();
        datePicker.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

       //pt css
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStyleClass().add("dialog-pane");

        alert.showAndWait();
    }
}