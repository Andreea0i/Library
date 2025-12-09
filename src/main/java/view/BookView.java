package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import service.book.BookService;
import view.model.BookDTO;
import java.util.List;

public class BookView {

    private TableView<BookDTO> bookTableView;
    private final ObservableList<BookDTO> bookObservableList;

    private TextField authorTextField;
    private TextField titleTextField;
    private Label authorLabel;
    private Label titleLabel;
    private Button saveButton;
    private Button deleteButton;
    private DatePicker publishedDatePicker;
    private TextField quantityField;

    private final BookService bookService; // ADAUGĂ ASTA

    public BookView(Stage primaryStage, List<BookDTO> books, BookService bookService) {
        this.bookService = bookService;
        primaryStage.setTitle("📖 Book Library");

        GridPane gridPane = new GridPane();
        initializeGridPane(gridPane);

        bookObservableList = FXCollections.observableArrayList(books);

        initTableView(gridPane);
        initSaveOptions(gridPane);

        Scene scene = new Scene(gridPane, 800, 600);

        // pt EmployeeView, AdminView, BookView:
        String cssPath = "file:src/main/java/styles/application.css";
        scene.getStylesheets().add(cssPath);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeGridPane(GridPane gridPane) {
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));
        gridPane.getStyleClass().add("content-pane"); // CLASA CSS
    }

    private void initTableView(GridPane gridPane) {
        bookTableView = new TableView<>();
        bookTableView.setPlaceholder(new Label("No books to display"));

        TableColumn<BookDTO, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.setPrefWidth(300);

        TableColumn<BookDTO, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorColumn.setPrefWidth(250);

        bookTableView.getColumns().addAll(titleColumn, authorColumn);
        bookTableView.setItems(bookObservableList);

        gridPane.add(bookTableView, 0, 0, 6, 1);
    }

    private void initSaveOptions(GridPane gridPane) {
        // Section title
        Label sectionTitle = new Label("📚 Manage Books");
        sectionTitle.getStyleClass().add("section-title");
        gridPane.add(sectionTitle, 0, 1, 6, 1);

        // Title input
        titleLabel = new Label("Title:");
        titleLabel.getStyleClass().add("label");
        gridPane.add(titleLabel, 0, 2);

        titleTextField = new TextField();
        titleTextField.setPromptText("Enter book title");
        gridPane.add(titleTextField, 1, 2);

        // Author input
        authorLabel = new Label("Author:");
        authorLabel.getStyleClass().add("label");
        gridPane.add(authorLabel, 2, 2);

        authorTextField = new TextField();
        authorTextField.setPromptText("Enter author name");
        gridPane.add(authorTextField, 3, 2);

        quantityField = new TextField(); // INITIALIZEAZĂ
        quantityField.setPromptText("Enter quantity");
        quantityField.setText("1"); // Valoare default
        gridPane.add(quantityField, 3, 3);

        // Buttons
        saveButton = new Button("💾 Save Book");
        gridPane.add(saveButton, 4, 2);

        deleteButton = new Button("🗑️ Delete Selected");
        gridPane.add(deleteButton, 5, 2);
    }

    public DatePicker getPublishedDatePicker() {
        return publishedDatePicker;
    }

    public TextField getQuantityField() {
        return quantityField;
    }

    // Listener pentru butonul Save
    public void addSaveButtonListener(EventHandler<ActionEvent> saveButtonListener) {
        saveButton.setOnAction(saveButtonListener);
    }

    // Listener pentru butonul Delete
    public void addDeleteButtonListener(EventHandler<ActionEvent> deleteButtonListener) {
        deleteButton.setOnAction(deleteButtonListener);
    }

    // Afișare mesaj alert
    public void addDisplayAlertMessage(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Stilizare alertă
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStyleClass().add("dialog-pane");

        alert.showAndWait();
    }

    // Getter pentru inputuri
    public String getTitle() {
        return titleTextField.getText();
    }

    public String getAuthor() {
        return authorTextField.getText();
    }

    // Adaugă carte în listă
    public void addBookObservableList(BookDTO bookDTO) {
        this.bookObservableList.add(bookDTO);
    }

    // Șterge carte din listă
    public void removeBookFromObservableList(BookDTO bookDTO) {
        this.bookObservableList.remove(bookDTO);
    }

    public TableView<BookDTO> getBookTableView() {
        return bookTableView;
    }

    public void clearInputFields() {
        titleTextField.clear();
        authorTextField.clear();
        quantityField.clear();
        quantityField.setText("1");
        publishedDatePicker.setValue(null);
    }
}