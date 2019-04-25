package me.lammazz.poker.client;

import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import me.lammazz.poker.network.Card;

import java.awt.*;


public class TableDisplay {

    private Pane pane;

    private ImageView potChips;
    private Label potLabel;
    private CardDisplay card1, card2, card3, card4, card5;

    private int potAmount;

    public TableDisplay(Pane pane, Image cardImage, Image faceDownCardImage, Image potChipsImage) {

        this.pane = pane;
        this.potAmount = 0;

        potChips = new ImageView(potChipsImage);
        potChips.relocate(GraphicsCoordinates.POT_IMAGE_X, GraphicsCoordinates.POT_IMAGE_Y);
        potChips.setVisible(false);
        pane.getChildren().add(potChips);

        potLabel = new Label("POT: " + potAmount);
        potLabel.setFont(new Font(28));
        potLabel.setStyle("-fx-text-fill: #FFFFFF;-fx-font-weight: bold;");
        potLabel.relocate(GraphicsCoordinates.POT_TEXT_X, GraphicsCoordinates.POT_TEXT_Y);
        potLabel.setVisible(false);
        pane.getChildren().add(potLabel);

        card1 = new CardDisplay(cardImage, faceDownCardImage, pane, GraphicsCoordinates.TABLE_CARD1_X, GraphicsCoordinates.TABLE_CARD_Y, Card.ACE_CLUBS);
        card2 = new CardDisplay(cardImage, faceDownCardImage, pane, GraphicsCoordinates.TABLE_CARD2_X, GraphicsCoordinates.TABLE_CARD_Y, Card.ACE_CLUBS);
        card3 = new CardDisplay(cardImage, faceDownCardImage, pane, GraphicsCoordinates.TABLE_CARD3_X, GraphicsCoordinates.TABLE_CARD_Y, Card.ACE_CLUBS);
        card4 = new CardDisplay(cardImage, faceDownCardImage, pane, GraphicsCoordinates.TABLE_CARD4_X, GraphicsCoordinates.TABLE_CARD_Y, Card.ACE_CLUBS);
        card5 = new CardDisplay(cardImage, faceDownCardImage, pane, GraphicsCoordinates.TABLE_CARD5_X, GraphicsCoordinates.TABLE_CARD_Y, Card.ACE_CLUBS);

    }

    public void setPotAmount(int amount) {
        this.potAmount = amount;
        if (potAmount > 0) {
            potChips.setVisible(true);

            potLabel.setText("POT: " + potAmount);
            Text text = new Text(potLabel.getText());
            text.setFont(potLabel.getFont());
            Bounds bounds = text.getBoundsInLocal();
            potLabel.relocate(GraphicsCoordinates.POT_TEXT_X - (bounds.getWidth()/2), GraphicsCoordinates.POT_TEXT_Y);
            potLabel.setVisible(true);
        } else {
            potChips.setVisible(false);
            potLabel.setVisible(false);
        }
    }

    public CardDisplay getCard1() {
        return card1;
    }

    public CardDisplay getCard2() {
        return card2;
    }

    public CardDisplay getCard3() {
        return card3;
    }

    public CardDisplay getCard4() {
        return card4;
    }

    public CardDisplay getCard5() {
        return card5;
    }
}
