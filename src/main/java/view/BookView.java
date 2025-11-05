package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import view.model.BookDTO;
import javafx.scene.control.TableView;

import java.awt.*;
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

    public BookView(Stage primaryStage, List<BookDTO> books) {
        primaryStage.setTitle("Library");

        GridPane gridPane = new GridPane();
        initializeGridPane(gridPane);

        Scene scene = new Scene(gridPane, 720, 480);
        primaryStage.setScene(scene);

        // Folosim BookDTO peste tot
        bookObservableList = FXCollections.observableArrayList(books);
        initTableView(gridPane);
        initSaveOptions(gridPane);

        primaryStage.show();
    }

    private void initializeGridPane(GridPane gridPane) {
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));
    }

    private void initTableView(GridPane gridPane) {
        bookTableView = new TableView<>();

        bookTableView.setPlaceholder(new Label("No books to display"));

        TableColumn<BookDTO, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<BookDTO, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));

        bookTableView.getColumns().addAll(titleColumn, authorColumn);
        bookTableView.setItems(bookObservableList);

        gridPane.add(bookTableView, 0, 0, 7, 1); // extindem pe 7 coloane pentru input-uri și butoane
    }

    private void initSaveOptions(GridPane gridPane) {
        titleLabel = new Label("Title");
        gridPane.add(titleLabel, 0, 1);

        titleTextField = new TextField();
        gridPane.add(titleTextField, 1, 1);

        authorLabel = new Label("Author");
        gridPane.add(authorLabel, 2, 1);

        authorTextField = new TextField();
        gridPane.add(authorTextField, 3, 1);

        saveButton = new Button("Save");
        gridPane.add(saveButton, 4, 1);

        deleteButton = new Button("Delete");
        gridPane.add(deleteButton, 5, 1);
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
}
