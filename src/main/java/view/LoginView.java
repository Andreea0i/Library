package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginView {

    private TextField userTextField;
    private PasswordField passwordField;
    private Button signInButton;
    private Button loginButton;
    private Text actionTarget;

    public LoginView(Stage primaryStage) {
        primaryStage.setTitle("Book Store");

        GridPane gridPane = new GridPane();
        initializeGridPane(gridPane);

        Scene scene = new Scene(gridPane, 720, 480);
        primaryStage.setScene(scene);

        initializeSceneTitle(gridPane);
        initializeFields(gridPane);

        primaryStage.show();
    }

    private void initializeGridPane(GridPane gridPane) {
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));
    }

    private void initializeSceneTitle(GridPane gridPane) {
        Text sceneTitle = new Text("Welcome to our Book Store");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        gridPane.add(sceneTitle, 0, 0, 2, 1);
    }

    private void initializeFields(GridPane gridPane){
        Label userName = new Label("User Name:");
        gridPane.add(userName, 0, 1);

        userTextField = new TextField();
        gridPane.add(userTextField, 1, 1);

        Label password = new Label("Password");
        gridPane.add(password, 0, 2);

        passwordField = new PasswordField();
        gridPane.add(passwordField, 1, 2);

        signInButton = new Button("Sign In");
        HBox signInButtonHBox = new HBox(10);
        signInButtonHBox.setAlignment(Pos.BOTTOM_RIGHT);
        signInButtonHBox.getChildren().add(signInButton);
        gridPane.add(signInButtonHBox, 1, 4);

        loginButton = new Button("Log In");
        HBox loginButtonHBox = new HBox(10);
        loginButtonHBox.setAlignment(Pos.BOTTOM_LEFT);
        loginButtonHBox.getChildren().add(loginButton);
        gridPane.add(loginButtonHBox, 0, 4);

        actionTarget = new Text();
        actionTarget.setFill(Color.FIREBRICK);
        gridPane.add(actionTarget, 1, 6);
    }

    // Metodele publice trebuie să fie în afara metodelor private
    public String getUsername() {
        return userTextField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }

    public void setActionTargetText(String text) {
        this.actionTarget.setText(text);
    }

    public void addLoginButtonListener(EventHandler<ActionEvent> loginButtonListener) {
        loginButton.setOnAction(loginButtonListener);
    }

    public void addRegisterButtonListener(EventHandler<ActionEvent> signInButtonListener) {
        signInButton.setOnAction(signInButtonListener);
    }
}