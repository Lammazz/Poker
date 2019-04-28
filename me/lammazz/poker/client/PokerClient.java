package me.lammazz.poker.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import me.lammazz.poker.network.*;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class PokerClient {

    private static PokerClient instance;
    public static PokerClient getInstance() {
        return instance;
    }

    private Stage stage;
    private Scene scene;
    private Client client;

    private PlayerHandler playerHandler;

    private SeatDisplay[] seatDisplays;
    private TableDisplay tableDisplay;

    private Button exitButton;

    private ControlDisplay controlDisplay;

    public int bigBlind, smallBlind;
    private long turnStart;

    public GameState currentState = GameState.PREGAME;

    public PokerClient(String ip, int port, String name, Stage stage) throws FileNotFoundException {

        this.instance = this;

        this.stage = stage;
        this.playerHandler = new PlayerHandler();

        client = new Client(ip, port, name);
        if (!(client.connect())) {
            client.close(false);
            stage.close();
            return;
        }


        stage.setTitle("Poker Client - " + name);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();
                close();
            }
        });

        Pane pane = new Pane();

        PokerClient.class.getResource("");
        InputStream tableIS = getResource("res/Table.png");
        Image tableImage = new Image(tableIS);
        BackgroundImage bgi = new BackgroundImage(tableImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);

        pane.setBackground(new Background(bgi));

        Image cardImage = loadImage("res/Cards.png");
        Image faceDownCardImage = loadImage("res/FaceDownCard.png");
        Image dealerButtonImage = loadImage("res/DealerButton.png");
        Image namePlateImage = loadImage("res/NamePlate.png");
        Image namePlateCurrentImage = loadImage("res/NamePlateCurrent.png");
        Image betChipsImage = loadImage("res/PlayerChips.png");
        Image potChipsImage = loadImage("res/PotChips.png");

        seatDisplays = new SeatDisplay[8];
        for (int i = 0; i < seatDisplays.length; i++) {
            SeatDisplay seat = new SeatDisplay(i, pane, cardImage, faceDownCardImage, dealerButtonImage, namePlateImage, namePlateCurrentImage,
                    betChipsImage);

//            seat.setVisible(true);
//            seat.setDealer(true);
//            seat.setName("Joel");
//            seat.setBetAmount(250);
//            seat.setChips(12345);
//            if (i == 3) seat.setCurrentTurn(true);

            seatDisplays[i] = seat;
        }

        tableDisplay = new TableDisplay(pane, cardImage, faceDownCardImage, potChipsImage);

//        tableDisplay.setPotAmount(567890);
//        tableDisplay.getCard1().show();
//        tableDisplay.getCard2().show();
//        tableDisplay.getCard3().show();
//        tableDisplay.getCard4().show();
//        tableDisplay.getCard5().show();

        exitButton = new Button();
        exitButton.setText("Exit");
        exitButton.relocate(4, 720 - 49);
        exitButton.setStyle("-fx-text-fill: #000000;-fx-font-weight: bold;-fx-background-color: #aa0000;-fx-font-size: 20");
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                close();
            }
        });
        pane.getChildren().add(exitButton);

        controlDisplay = new ControlDisplay(pane);

//        controlDisplay.setChips(2000);
//        controlDisplay.enable(40, 1000, 20, 450);

        scene = new Scene(pane, 1280, 720);
        stage.setScene(scene);

        client.sendObject(new RequestConnectionPacket(client.name));

    }

    public TableDisplay getTableDisplay() {
        return tableDisplay;
    }

    public ControlDisplay getControlDisplay() {
        return controlDisplay;
    }

    public void sendBet(int amount) {
        client.sendObject(new ClientBetPacket(amount));
    }

    public void sendFold() {
        client.sendObject(new ClientFoldPacket());
    }

    public void newTurnStart(int turnDuration) { // turn duration in seconds
        turnStart = System.currentTimeMillis();
        // Store current time ms, eventually used to display turn timer of 40s
    }

    public void setOtherPlayerTurn() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controlDisplay.disable();
            }
        });
    }

    public void setCurrentTurn(int minimumRaise, int maxBet, int call, int potAmount, int currentBet, int currentChips) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int minBet = call + minimumRaise;
                controlDisplay.enable(minBet,maxBet,call,potAmount,currentBet,currentChips);
            }
        });
    }

    public void updateDisplay() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < seatDisplays.length; i++) seatDisplays[i].setVisible(false);
                for (int i = 0; i < playerHandler.players.size(); i++) {
                    PlayerData data = playerHandler.players.get(i);

                    if (data.id == client.id) controlDisplay.setChips(data.chips);

                    SeatDisplay seat = seatDisplays[data.seat];

                    seat.setChips(data.chips);
                    seat.setBetAmount(data.betAmount);
                    seat.setName(data.name);
                    seat.setDealer(data.dealer);
                    if (data.inPlay && currentState != GameState.PREGAME) {
                        System.out.println("UPDATEDISPLAY id:" + data.id + " in play");
                        if (data.id == client.id || currentState == GameState.SHOWDOWN) {
                            seat.getLeftCard().setCard(data.leftCard);
                            seat.getRightCard().setCard(data.rightCard);
                        } else {
                            seat.getLeftCard().setFaceDown();
                            seat.getRightCard().setFaceDown();
                        }
                    } else {
                        System.out.println("UPDATEDISPLAY id:" + data.id + " out of play");
                        seat.getLeftCard().hide();
                        seat.getRightCard().hide();
                    }
                    seat.setCurrentTurn(data.isCurrentTurn);
                    seat.setVisible(true);
                }
            }
        });
    }

    public void updateTableDisplay(int potAmount, Card card1, Card card2, Card card3, Card card4, Card card5) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tableDisplay.setPotAmount(potAmount);
                tableDisplay.getCard1().setCard(card1);
                tableDisplay.getCard2().setCard(card2);
                tableDisplay.getCard3().setCard(card3);
                tableDisplay.getCard4().setCard(card4);
                tableDisplay.getCard5().setCard(card5);
            }
        });
    }

    public void showPlayerCards(int seatID, Card leftCard, Card rightCard) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                SeatDisplay seat = seatDisplays[seatID];
                seat.getLeftCard().setCard(leftCard);
                seat.getRightCard().setCard(rightCard);
            }
        });
    }

    public void handleShowdownData(PlayerShowdownData[] data) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (PlayerData pd : playerHandler.players) {
                    pd.betAmount = 0;
                    pd.isCurrentTurn = false;
                }
                for (int i = 0; i < data.length; i++) {
                    PlayerShowdownData d = data[i];
                    PlayerData playerData = playerHandler.getPlayer(d.id);
                    playerData.chips = d.newBalance;
                    playerData.betAmount = d.winnings;
                    playerData.leftCard = d.leftCard;
                    playerData.rightCard = d.rightCard;
//                    SeatDisplay seat = seatDisplays[playerData.seat];
//                    seat.getLeftCard().setCard(d.leftCard);
//                    seat.getRightCard().setCard(d.rightCard);
                }
                updateDisplay();
                setOtherPlayerTurn();
            }
        });
    }

    public PlayerHandler getPlayerHandler() {
        return playerHandler;
    }

    private Image loadImage(String path) {
        InputStream inputStream = getResource(path);
        Image image = new Image(inputStream);
        return image;
    }

    private InputStream getResource(String path) {
        return this.getClass().getClassLoader().getResourceAsStream(path);
    }

    public void close() {
        client.close();
        stage.close();
    }

}
