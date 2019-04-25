package me.lammazz.poker.client;

import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import me.lammazz.poker.network.Card;


public class SeatDisplay {

    private int id; // Seat id ranging from 0 to 7; NOT PLAYER ID
    private Pane pane;

    private ImageView namePlate, namePlateCurrent, dealerButton, betChips;
    private Label nameLabel, chipsLabel, betAmountLabel;
    private CardDisplay leftCard, rightCard;

    private String name;
    private boolean dealer, visible, isCurrentTurn;
    private int chips, betAmount;

    public SeatDisplay(int id, Pane pane, Image cardImage, Image faceDownCardImage, Image dealerButtonImage, Image namePlateImage,
                       Image namePlateCurrentImage, Image betChipsImage) {

        this.id = id;
        this.pane = pane;
        this.visible = false;
        this.dealer = false;
        this.name = "";
        this.chips = 0;
        this.betAmount = 0;
        this.dealer = false;
        this.isCurrentTurn = false;

//        CardDisplay cardDisplay2 = new CardDisplay(cardImage, faceDownCardImage, pane, 50, 10, Card.ACE_SPADES);
//        cardDisplay2.show();

        GraphicsCoordinates coords = GraphicsCoordinates.fromID(id);

        leftCard = new CardDisplay(cardImage, faceDownCardImage, pane, coords.getLeftCardX(), coords.getLeftCardY(), Card.ACE_CLUBS);
        rightCard = new CardDisplay(cardImage, faceDownCardImage, pane, coords.getRightCardX(), coords.getRightCardY(), Card.ACE_CLUBS);

//        view = new ImageView(source);

        namePlate = new ImageView(namePlateImage);
        namePlate.relocate(coords.getNamePlateX(), coords.getNamePlateY());
        namePlate.setVisible(false);
        pane.getChildren().add(namePlate);

        namePlateCurrent = new ImageView(namePlateCurrentImage);
        namePlateCurrent.relocate(coords.getNamePlateX(), coords.getNamePlateY());
        namePlateCurrent.setVisible(false);
        pane.getChildren().add(namePlateCurrent);

        dealerButton = new ImageView(dealerButtonImage);
        dealerButton.relocate(coords.getDealerChipX(), coords.getDealerChipY());
        dealerButton.setVisible(false);
        pane.getChildren().add(dealerButton);

        betChips = new ImageView(betChipsImage);
        betChips.relocate(coords.getBetImageX(), coords.getBetImageY());
        betChips.setVisible(false);
        pane.getChildren().add(betChips);

        nameLabel = new Label(name);
        nameLabel.setFont(new Font(16));
        nameLabel.setStyle("-fx-text-fill: #000000;-fx-font-weight: bold;");
        nameLabel.relocate(coords.getNamePlateNameX(), coords.getNamePlateNameY());
        nameLabel.setVisible(false);
        pane.getChildren().add(nameLabel);

        chipsLabel = new Label(chips + "");
        chipsLabel.setFont(new Font(28));
        chipsLabel.setStyle("-fx-text-fill: #007F0E;-fx-font-weight: bold");
        chipsLabel.relocate(coords.getBalanceX(), coords.getBalanceY());
        chipsLabel.setVisible(false);
        pane.getChildren().add(chipsLabel);

        betAmountLabel = new Label(betAmount + "");
        betAmountLabel.setFont(new Font(14));
        betAmountLabel.setStyle("-fx-text-fill: #FFFFFF;-fx-font-weight: bold");
        betAmountLabel.relocate(coords.getBetTextX(), coords.getBetTextY());
        betAmountLabel.setVisible(false);
        pane.getChildren().add(betAmountLabel);

    }

    private Bounds getTextBounds(Label label) {
        Text text = new Text(label.getText());
        text.setFont(label.getFont());
        return text.getBoundsInLocal();
    }

    public CardDisplay getLeftCard() {
        return leftCard;
    }

    public CardDisplay getRightCard() {
        return rightCard;
    }

    public void setBetAmount(int amount) {
        this.betAmount = amount;
        betAmountLabel.setText(betAmount + "");

        Bounds bounds = getTextBounds(betAmountLabel);
        GraphicsCoordinates coords = GraphicsCoordinates.fromID(id);
        betAmountLabel.relocate(coords.getBetTextX() - (bounds.getWidth()/2), coords.getBetTextY());

        if (visible) {
            boolean b = betAmount > 0;
            betAmountLabel.setVisible(b);
            betChips.setVisible(b);
        }
    }

    public void setDealer(boolean b) {
        this.dealer = b;
        if (visible) {
            dealerButton.setVisible(dealer);
        }
    }

    public void setCurrentTurn(boolean b) {
        this.isCurrentTurn = b;
        if (visible) {
            namePlate.setVisible(!isCurrentTurn);
            namePlateCurrent.setVisible(isCurrentTurn);
        }
    }

    public void setChips(int amount) {
        this.chips = amount;
        chipsLabel.setText(chips + "");

        Bounds b = getTextBounds(chipsLabel);
        GraphicsCoordinates coords = GraphicsCoordinates.fromID(id);
        chipsLabel.relocate(coords.getBalanceX() - (b.getWidth()/2), coords.getBalanceY() - (b.getHeight()));
    }

    public void setName(String name) {
        this.name = name;
        nameLabel.setText(name);

        Bounds b = getTextBounds(nameLabel);
        GraphicsCoordinates coords = GraphicsCoordinates.fromID(id);
        nameLabel.relocate(coords.getNamePlateNameX() - (b.getWidth()/2), coords.getNamePlateNameY() - (b.getHeight()));
    }

    public boolean isVisible() {
     return visible;
    }

    public void setVisible(boolean b) {
        this.visible = b;

        nameLabel.setVisible(visible);
        chipsLabel.setVisible(visible);

        if (!visible) {
            leftCard.hide();
            rightCard.hide();
        }

        if (isCurrentTurn) {
            namePlate.setVisible(false);
            namePlateCurrent.setVisible(visible);
        } else {
            namePlate.setVisible(visible);
            namePlateCurrent.setVisible(false);
        }

        if (dealer) {
            dealerButton.setVisible(visible);
        } else {
            dealerButton.setVisible(false);
        }

        if (betAmount > 0) {
            betChips.setVisible(visible);
            betAmountLabel.setVisible(visible);
        } else {
            betChips.setVisible(false);
            betAmountLabel.setVisible(false);
        }
    }

}


