package me.lammazz.poker.launch;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import me.lammazz.poker.client.PokerClient;
import me.lammazz.poker.server.PokerServer;

import java.io.FileNotFoundException;


public class Poker extends Application implements EventHandler<ActionEvent> {

    private final int port = 2612;

    private Button playButton, hostButton;
    private TextField ipInput, nameInput;
    private String defaultTextFieldStyle;
    private String errorTextFieldStyle;

    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Poker");
        primaryStage.setResizable(false);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10,10,10,10));
        grid.setVgap(8);
        grid.setHgap(10);

        Label ipLabel = new Label("Server IP:");
        GridPane.setConstraints(ipLabel, 0, 0);
        ipInput = new TextField();
        ipInput.setPromptText("127.0.0.1");
        GridPane.setConstraints(ipInput, 1, 0);

        defaultTextFieldStyle = ipInput.getStyle();
        errorTextFieldStyle = "-fx-text-box-border: #ff0000; -fx-focus-color: #ff0000";

        Label nameLabel = new Label("Name:");
        GridPane.setConstraints(nameLabel, 0, 1);
        nameInput = new TextField();
        GridPane.setConstraints(nameInput, 1, 1);

        playButton = new Button("Play");
        GridPane.setConstraints(playButton, 1, 2);
        playButton.setOnAction(this);
//        playButton.setStyle("-fx-text-fill: #ff0000;-fx-font-weight: bold");

        hostButton = new Button("Host");
        GridPane.setConstraints(hostButton, 0, 4);
        hostButton.setOnAction(this);
//        hostButton.setStyle("-fx-text-fill: #00aa00;-fx-font-weight: bold");

        grid.getChildren().addAll(ipLabel, ipInput, nameLabel, nameInput, playButton, hostButton);

        Scene scene = new Scene (grid, 300, 250);

        primaryStage.setScene(scene);
        primaryStage.show();

        this.stage = primaryStage;
    }

    @Override
    public void handle(ActionEvent event) {
        if (event.getSource() == playButton) {
            String name = nameInput.getText();
            String ip = ipInput.getText();
            if (!(ip.isEmpty() || name.isEmpty() || name.length() > 16)) {
                System.out.println("Player: " + nameInput.getText() + " wants to connect to ip: " + ipInput.getText());
                ipInput.setStyle(defaultTextFieldStyle);
                nameInput.setStyle(defaultTextFieldStyle);

                PokerClient client = null;
                try {
                    client = new PokerClient(ip, port, name, stage);
                } catch (FileNotFoundException e) {
                    System.out.println("Could not load resources.");
                    e.printStackTrace();
                    if (client != null) client.close();
                }

            } else {
                if (ip.isEmpty()) ipInput.setStyle(errorTextFieldStyle);
                else ipInput.setStyle(defaultTextFieldStyle);
                if (name.isEmpty() || name.length() > 16) nameInput.setStyle(errorTextFieldStyle);
                else nameInput.setStyle(defaultTextFieldStyle);
            }
        } else if (event.getSource() == hostButton) {
            System.out.println("User wants to host");

            new PokerServer(port, stage);

        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
