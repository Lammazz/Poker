package me.lammazz.poker.client;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class ControlDisplay {

    private Pane pane;

    private int chips, previewAmount, minimumBet, maximumBet, toCall;

    private Slider potPercent, totalPercent;
    private Button add10, sub10, add20, sub20, add50, sub50, add100, sub100, add250, sub250, callButton, bet, fold;
    private TextField toBetField;
    private Label chipsLabel1, chipsLabel2, toBetLabel, potPercentLabel, totalPercentLabel;

    private boolean sentAction;

    public ControlDisplay(Pane parentPane) {
        this.pane = parentPane;

        toBetLabel = new Label("To Bet:");
        toBetLabel.setStyle("-fx-text-fill: #000000;-fx-font-weight: bold;-fx-font-size: 12");
        toBetLabel.relocate(470, 578);
        toBetField = new TextField();
        toBetField.setText("0");
        toBetField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                String s = newValue;
                if (!s.matches("\\d*")) s = s.replaceAll("[^\\d]", "");
                int newPreview = 0;
                try{
                    newPreview = Integer.parseInt(s);
                } catch (NumberFormatException e) {}
                if (newPreview > maximumBet) newPreview = maximumBet;
                if (newPreview < minimumBet) newPreview = minimumBet;
                previewAmount = newPreview;
                updatePreviewAmount();
            }
        });
        toBetField.setMinWidth(70);
        toBetField.setMaxWidth(70);
        toBetField.relocate(526, 575);
        pane.getChildren().addAll(toBetLabel, toBetField);

        potPercentLabel = new Label("Pot:");
        potPercentLabel.relocate(409,654);
        potPercentLabel.setStyle("-fx-text-fill: #000000;-fx-font-weight: bold;-fx-font-size: 14");
        potPercent = new Slider();
        potPercent.setMin(0);
        potPercent.setMax(1);
        potPercent.setValue(0);
        potPercent.setShowTickLabels(true);
        potPercent.setShowTickMarks(true);
        potPercent.setBlockIncrement(0.1);
        potPercent.setMinWidth(200);
        potPercent.setMaxWidth(200);
        potPercent.relocate(464,650);
        potPercent.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                previewAmount = (int) potPercent.getValue();
                updatePreviewAmount();
            }
        });

        totalPercentLabel = new Label("Total:");
        totalPercentLabel.relocate(409,691);
        totalPercentLabel.setStyle("-fx-text-fill: #000000;-fx-font-weight: bold;-fx-font-size: 14");
        totalPercent = new Slider();
        totalPercent.setMin(0);
        totalPercent.setMax(1);
        totalPercent.setValue(0);
        totalPercent.setShowTickLabels(true);
        totalPercent.setShowTickMarks(true);
        totalPercent.setBlockIncrement(0.1);
        totalPercent.setMinWidth(200);
        totalPercent.setMaxWidth(200);
        totalPercent.relocate(464,685);
        totalPercent.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                previewAmount = (int) totalPercent.getValue();
                updatePreviewAmount();
            }
        });

        pane.getChildren().addAll(potPercentLabel, potPercent, totalPercentLabel, totalPercent);

        callButton = new Button();
        callButton.setText("Call\n0");
        callButton.setMinSize(89, 41);
        callButton.setMaxSize(89, 41);
        callButton.relocate(683, 585);
        callButton.setStyle("-fx-text-fill: #000000;-fx-font-weight: bold;-fx-font-size: 14;-fx-text-alignment: center;-fx-padding: 0");
        callButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (sentAction) return;
                sentAction = true;
                PokerClient.getInstance().sendBet(toCall);
            }
        });
        bet = new Button();
        bet.setText("Bet\n0");
        bet.setMinSize(89, 41);
        bet.setMaxSize(89, 41);
        bet.relocate(777, 585);
        bet.setStyle("-fx-text-fill: #000000;-fx-font-weight: bold;-fx-font-size: 14;-fx-text-alignment: center;-fx-padding: 0");
        bet.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (sentAction) return;
                sentAction = true;
                PokerClient.getInstance().sendBet(previewAmount);
            }
        });
        fold = new Button();
        fold.setText("Fold");
        fold.setMinSize(77, 23);
        fold.setMaxSize(77, 23);
        fold.relocate(736, 690);
        fold.setStyle("-fx-text-fill: #ff0000;-fx-font-weight: bold;-fx-font-size: 14;-fx-padding: 0");
        fold.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (sentAction) return;
                sentAction = true;
                PokerClient.getInstance().sendFold();
            }
        });
        pane.getChildren().addAll(callButton, bet, fold);

        chipsLabel1 = new Label("Your Chips:");
        Bounds b = getTextBounds(chipsLabel1);
        chipsLabel1.relocate(764 - (b.getWidth()/2), 639);
        chipsLabel1.setStyle("-fx-text-fill: #000000;-fx-font-weight: bold;-fx-font-size: 14");
        chipsLabel2 = new Label("0");
        chipsLabel2.relocate(738,657); // irrelevant
        chipsLabel2.setStyle("-fx-text-fill: #007700;-fx-font-weight: bold;-fx-font-size: 20");
        pane.getChildren().addAll(chipsLabel1, chipsLabel2);

        int buttonWidth = 52, buttonHeight = 21;

        add10 = new Button();
        add10.setText("+10");
        add10.setMinSize(buttonWidth, buttonHeight);
        add10.setMaxSize(buttonWidth, buttonHeight);
        add10.relocate(408, 604);
        add10.setStyle("-fx-text-fill: #007700;-fx-font-weight: bold;-fx-font-size: 14;-fx-text-alignment: center;-fx-padding: 0");
        add10.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                previewAmount += 10;
                if (previewAmount > maximumBet) previewAmount = maximumBet;
                updatePreviewAmount();
            }
        });
        sub10 = new Button();
        sub10.setText("-10");
        sub10.setMinSize(buttonWidth, buttonHeight);
        sub10.setMaxSize(buttonWidth, buttonHeight);
        sub10.relocate(408, 625);
        sub10.setStyle("-fx-text-fill: #ff0000;-fx-font-weight: bold;-fx-font-size: 14;-fx-text-alignment: center;-fx-padding: 0");
        sub10.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                previewAmount -= 10;
                if (previewAmount < minimumBet) previewAmount = minimumBet;
                updatePreviewAmount();
            }
        });

        add20 = new Button();
        add20.setText("+20");
        add20.setMinSize(buttonWidth, buttonHeight);
        add20.setMaxSize(buttonWidth, buttonHeight);
        add20.relocate(460, 604);
        add20.setStyle("-fx-text-fill: #007700;-fx-font-weight: bold;-fx-font-size: 14;-fx-text-alignment: center;-fx-padding: 0");
        add20.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                previewAmount += 20;
                if (previewAmount > maximumBet) previewAmount = maximumBet;
                updatePreviewAmount();
            }
        });
        sub20 = new Button();
        sub20.setText("-20");
        sub20.setMinSize(buttonWidth, buttonHeight);
        sub20.setMaxSize(buttonWidth, buttonHeight);
        sub20.relocate(460, 625);
        sub20.setStyle("-fx-text-fill: #ff0000;-fx-font-weight: bold;-fx-font-size: 14;-fx-text-alignment: center;-fx-padding: 0");
        sub20.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                previewAmount -= 20;
                if (previewAmount < minimumBet) previewAmount = minimumBet;
                updatePreviewAmount();
            }
        });

        add50 = new Button();
        add50.setText("+50");
        add50.setMinSize(buttonWidth, buttonHeight);
        add50.setMaxSize(buttonWidth, buttonHeight);
        add50.relocate(512, 604);
        add50.setStyle("-fx-text-fill: #007700;-fx-font-weight: bold;-fx-font-size: 14;-fx-text-alignment: center;-fx-padding: 0");
        add50.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                previewAmount += 50;
                if (previewAmount > maximumBet) previewAmount = maximumBet;
                updatePreviewAmount();
            }
        });
        sub50 = new Button();
        sub50.setText("-50");
        sub50.setMinSize(buttonWidth, buttonHeight);
        sub50.setMaxSize(buttonWidth, buttonHeight);
        sub50.relocate(512, 625);
        sub50.setStyle("-fx-text-fill: #ff0000;-fx-font-weight: bold;-fx-font-size: 14;-fx-text-alignment: center;-fx-padding: 0");
        sub50.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                previewAmount -= 50;
                if (previewAmount < minimumBet) previewAmount = minimumBet;
                updatePreviewAmount();
            }
        });

        add100 = new Button();
        add100.setText("+100");
        add100.setMinSize(buttonWidth, buttonHeight);
        add100.setMaxSize(buttonWidth, buttonHeight);
        add100.relocate(564, 604);
        add100.setStyle("-fx-text-fill: #007700;-fx-font-weight: bold;-fx-font-size: 14;-fx-text-alignment: center;-fx-padding: 0");
        add100.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                previewAmount += 100;
                if (previewAmount > maximumBet) previewAmount = maximumBet;
                updatePreviewAmount();
            }
        });
        sub100 = new Button();
        sub100.setText("-100");
        sub100.setMinSize(buttonWidth, buttonHeight);
        sub100.setMaxSize(buttonWidth, buttonHeight);
        sub100.relocate(564, 625);
        sub100.setStyle("-fx-text-fill: #ff0000;-fx-font-weight: bold;-fx-font-size: 14;-fx-text-alignment: center;-fx-padding: 0");
        sub100.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                previewAmount -= 100;
                if (previewAmount < minimumBet) previewAmount = minimumBet;
                updatePreviewAmount();
            }
        });

        add250 = new Button();
        add250.setText("+250");
        add250.setMinSize(buttonWidth, buttonHeight);
        add250.setMaxSize(buttonWidth, buttonHeight);
        add250.relocate(616, 604);
        add250.setStyle("-fx-text-fill: #007700;-fx-font-weight: bold;-fx-font-size: 14;-fx-text-alignment: center;-fx-padding: 0");
        add250.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                previewAmount += 250;
                if (previewAmount > maximumBet) previewAmount = maximumBet;
                updatePreviewAmount();
            }
        });
        sub250 = new Button();
        sub250.setText("-250");
        sub250.setMinSize(buttonWidth, buttonHeight);
        sub250.setMaxSize(buttonWidth, buttonHeight);
        sub250.relocate(616, 625);
        sub250.setStyle("-fx-text-fill: #ff0000;-fx-font-weight: bold;-fx-font-size: 14;-fx-text-alignment: center;-fx-padding: 0");
        sub250.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                previewAmount -= 250;
                if (previewAmount < minimumBet) previewAmount = minimumBet;
                updatePreviewAmount();
            }
        });
        pane.getChildren().addAll(add10, sub10, add20, sub20, add50, sub50, add100, sub100, add250, sub250);

        disable();

    }

    private void updatePreviewAmount() {
        toBetField.setText(previewAmount + "");
        bet.setText("Bet\n" + previewAmount);
        potPercent.setValue(previewAmount);
        totalPercent.setValue(previewAmount);
    }

    // min bet is the minimum raise a player can make. raise must be >= last raise
    // if player is chip leader, max bet is no. of chips of 2nd chip leader, else max bet = all in
    // call = amount that must be added by player to stay in hand
    // pot amount = total no. of chips in pot and is used exclusively for a bet size slider

    public void enable(int minBet, int maxBet, int call, int potAmount, int currentBet, int currentChips) {
        if (minBet > currentChips) minimumBet = currentChips;
        else minimumBet = minBet;
        maximumBet = maxBet - currentBet;
        toCall = call;
        chips = currentChips;
        if (toCall > chips) toCall = chips;

        String s;
        if (toCall == 0) s = "Check";
        else if (toCall == chips) s = "All In";
        else s = "Call\n" + toCall;

        callButton.setText(s);

        if (potAmount > maximumBet) potAmount = maximumBet;
        potPercent.setMin(minBet);
        potPercent.setMax(potAmount > chips ? chips : potAmount);
        potPercent.setBlockIncrement((potPercent.getMax() - potPercent.getMin()) / 10);
        totalPercent.setMin(minBet);
        totalPercent.setMax(maximumBet > chips ? chips : maximumBet);
        totalPercent.setBlockIncrement((totalPercent.getMax() - totalPercent.getMin()) / 10);

        previewAmount = minimumBet;
        updatePreviewAmount();

        add10.setDisable(false);
        sub10.setDisable(false);
        add20.setDisable(false);
        sub20.setDisable(false);
        add50.setDisable(false);
        sub50.setDisable(false);
        add100.setDisable(false);
        sub100.setDisable(false);
        add250.setDisable(false);
        sub250.setDisable(false);
        callButton.setDisable(false);
        bet.setDisable(false);
        fold.setDisable(toCall == 0);
        toBetField.setDisable(false);
        totalPercent.setDisable(false);
        potPercent.setDisable(false);
        sentAction = false;
    }

    public void disable() {
        sentAction = true;
        potPercent.setMin(0);
        potPercent.setMax(1);
        potPercent.setDisable(true);

        totalPercent.setMin(0);
        totalPercent.setMax(1);
        totalPercent.setDisable(true);

        previewAmount = 0;
        updatePreviewAmount();

        add10.setDisable(true);
        sub10.setDisable(true);
        add20.setDisable(true);
        sub20.setDisable(true);
        add50.setDisable(true);
        sub50.setDisable(true);
        add100.setDisable(true);
        sub100.setDisable(true);
        add250.setDisable(true);
        sub250.setDisable(true);
        callButton.setDisable(true);
        bet.setDisable(true);
        fold.setDisable(true);
        toBetField.setDisable(true);
    }

    public void setChips(int amount) {
        chips = amount;
        chipsLabel2.setText(chips + "");

        Bounds b = getTextBounds(chipsLabel2);
        chipsLabel2.relocate(764 - (b.getWidth()/2), 657);
    }

    private Bounds getTextBounds(Label label) {
        Text text = new Text(label.getText());
        text.setFont(label.getFont());
        return text.getBoundsInLocal();
    }

}
