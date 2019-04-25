package me.lammazz.poker.client;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import me.lammazz.poker.network.Card;

public class CardDisplay {

    private Image source, faceDownCard;
    private ImageView view;
    private Pane pane;
    private int xPos, yPos;
    private boolean visible, faceDown;
    private Card currentCard;

    public CardDisplay(Image cards, Image faceDownCard, Pane pane, int xPos, int yPos, Card currentCard) {
        this.source = cards;
        this.faceDownCard = faceDownCard;
        this.pane = pane;
        this.xPos = xPos;
        this.yPos = yPos;
        this.visible = false;
        this.faceDown = false;
        this.currentCard = currentCard;

        view = new ImageView(source);
        view.setViewport(new Rectangle2D(currentCard.getViewPortX(), currentCard.getViewPortY(), 72, 96));
        view.relocate(xPos, yPos);
        view.setVisible(false);
        pane.getChildren().add(view);
    }

    public boolean isFaceDown() {
        return faceDown;
    }

    public Card getCurrentCard() {
        return currentCard;
    }

    public void setFaceDown() {
        if (faceDown) return;
        faceDown = true;
        currentCard = null;
        view.setImage(faceDownCard);
        view.setViewport(new Rectangle2D(0, 0, 72, 96));
        show();
    }

    public void setCard(Card card) {
        if (card == null) {
            currentCard = Card.ACE_CLUBS;
            faceDown = false;
            hide();
        } else {
            if (faceDown) view.setImage(source);
            faceDown = false;
            currentCard = card;
            view.setViewport(new Rectangle2D(currentCard.getViewPortX(), currentCard.getViewPortY(), 72, 96));
            show();
        }
    }

    public void setPosition(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        view.relocate(xPos, yPos);
    }

    public void show() {
        view.setVisible(true);
        visible = true;
    }

    public void hide() {
        view.setVisible(false);
        visible = false;
    }

    public boolean isVisible() {
        return visible;
    }

}
