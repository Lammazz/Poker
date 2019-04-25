package me.lammazz.poker.network;

import java.io.Serializable;

public enum Card implements Serializable {

    ACE_CLUBS(CardValue.ACE, CardSuit.CLUB),
    TWO_CLUBS(CardValue.TWO, CardSuit.CLUB),
    THREE_CLUBS(CardValue.THREE, CardSuit.CLUB),
    FOUR_CLUBS(CardValue.FOUR, CardSuit.CLUB),
    FIVE_CLUBS(CardValue.FIVE, CardSuit.CLUB),
    SIX_CLUBS(CardValue.SIX, CardSuit.CLUB),
    SEVEN_CLUBS(CardValue.SEVEN, CardSuit.CLUB),
    EIGHT_CLUBS(CardValue.EIGHT, CardSuit.CLUB),
    NINE_CLUBS(CardValue.NINE, CardSuit.CLUB),
    TEN_CLUBS(CardValue.TEN, CardSuit.CLUB),
    JACK_CLUBS(CardValue.JACK, CardSuit.CLUB),
    QUEEN_CLUBS(CardValue.QUEEN, CardSuit.CLUB),
    KING_CLUBS(CardValue.KING, CardSuit.CLUB),

    ACE_SPADES(CardValue.ACE, CardSuit.SPADE),
    TWO_SPADES(CardValue.TWO, CardSuit.SPADE),
    THREE_SPADES(CardValue.THREE, CardSuit.SPADE),
    FOUR_SPADES(CardValue.FOUR, CardSuit.SPADE),
    FIVE_SPADES(CardValue.FIVE, CardSuit.SPADE),
    SIX_SPADES(CardValue.SIX, CardSuit.SPADE),
    SEVEN_SPADES(CardValue.SEVEN, CardSuit.SPADE),
    EIGHT_SPADES(CardValue.EIGHT, CardSuit.SPADE),
    NINE_SPADES(CardValue.NINE, CardSuit.SPADE),
    TEN_SPADES(CardValue.TEN, CardSuit.SPADE),
    JACK_SPADES(CardValue.JACK, CardSuit.SPADE),
    QUEEN_SPADES(CardValue.QUEEN, CardSuit.SPADE),
    KING_SPADES(CardValue.KING, CardSuit.SPADE),

    ACE_HEARTS(CardValue.ACE, CardSuit.HEART),
    TWO_HEARTS(CardValue.TWO, CardSuit.HEART),
    THREE_HEARTS(CardValue.THREE, CardSuit.HEART),
    FOUR_HEARTS(CardValue.FOUR, CardSuit.HEART),
    FIVE_HEARTS(CardValue.FIVE, CardSuit.HEART),
    SIX_HEARTS(CardValue.SIX, CardSuit.HEART),
    SEVEN_HEARTS(CardValue.SEVEN, CardSuit.HEART),
    EIGHT_HEARTS(CardValue.EIGHT, CardSuit.HEART),
    NINE_HEARTS(CardValue.NINE, CardSuit.HEART),
    TEN_HEARTS(CardValue.TEN, CardSuit.HEART),
    JACK_HEARTS(CardValue.JACK, CardSuit.HEART),
    QUEEN_HEARTS(CardValue.QUEEN, CardSuit.HEART),
    KING_HEARTS(CardValue.KING, CardSuit.HEART),

    ACE_DIAMONDS(CardValue.ACE, CardSuit.DIAMOND),
    TWO_DIAMONDS(CardValue.TWO, CardSuit.DIAMOND),
    THREE_DIAMONDS(CardValue.THREE, CardSuit.DIAMOND),
    FOUR_DIAMONDS(CardValue.FOUR, CardSuit.DIAMOND),
    FIVE_DIAMONDS(CardValue.FIVE, CardSuit.DIAMOND),
    SIX_DIAMONDS(CardValue.SIX, CardSuit.DIAMOND),
    SEVEN_DIAMONDS(CardValue.SEVEN, CardSuit.DIAMOND),
    EIGHT_DIAMONDS(CardValue.EIGHT, CardSuit.DIAMOND),
    NINE_DIAMONDS(CardValue.NINE, CardSuit.DIAMOND),
    TEN_DIAMONDS(CardValue.TEN, CardSuit.DIAMOND),
    JACK_DIAMONDS(CardValue.JACK, CardSuit.DIAMOND),
    QUEEN_DIAMONDS(CardValue.QUEEN, CardSuit.DIAMOND),
    KING_DIAMONDS(CardValue.KING, CardSuit.DIAMOND);

    private CardSuit suit;
    private CardValue value;

    private Card(CardValue value, CardSuit suit) {
        this.value = value;
        this.suit = suit;
    }

    public static Card get(CardValue value, CardSuit suit) {
        for (Card card : Card.values()) {
            if (card.getValue() == value && card.getSuit() == suit) return card;
        }
        return null;
    }

    public int getViewPortX() {
        return 72 * value.getID();
    }

    public int getViewPortY() {
        return 96 * suit.getID();
    }

    public CardSuit getSuit() {
        return suit;
    }

    public CardValue getValue() {
        return value;
    }

    public enum CardValue implements Serializable {

        ACE(0, 13), TWO(1, 1), THREE (2, 2), FOUR(3, 3), FIVE(4, 4), SIX(5,5),
        SEVEN(6,6), EIGHT(7,7), NINE(8,8), TEN(9,9), JACK(10, 10), QUEEN(11,11),
        KING(12,12);

        private int id, value;

        private CardValue(int id, int value) {
            this.id = id;
            this.value = value;
        }

        public int getID() {
            return id;
        }

        public int getValue() {
            return value;
        }

        public static CardValue fromID(int id) {
            for (int i = 0; i < CardValue.values().length; i++) {
                CardValue type = CardValue.values()[i];
                if (type.getID() == id) return type;
            }
            return null;
        }

        public static CardValue fromValue(int value) {
            for (int i = 0; i < CardValue.values().length; i++) {
                CardValue type = CardValue.values()[i];
                if (type.getValue() == value) return type;
            }
            return null;
        }

    }

    public enum CardSuit implements Serializable {

        CLUB(0), SPADE(1), HEART(2), DIAMOND(3);

        private int id;

        private CardSuit(int id) {
            this.id = id;
        }

        public int getID() {
            return id;
        }

        public static CardSuit fromID(int id) {
            for (int i = 0; i < CardSuit.values().length; i++) {
                CardSuit suit = CardSuit.values()[i];
                if (suit.getID() == id) return suit;
            }
            return null;
        }

    }

}
