package me.lammazz.poker.server;

import me.lammazz.poker.network.Card.CardSuit;
import me.lammazz.poker.network.Card.CardValue;

public class StraightFlushHand extends Hand {

    private CardSuit suit;
    private CardValue value;

    public StraightFlushHand(CardSuit suit, CardValue value) {
        super(HandType.STRAIGHT_FLUSH);

        this.suit = suit;
        this.value = value;

    }

    public CardSuit getSuit() {
        return suit;
    }

    public CardValue getValue() {
        return value;
    }

    public HandComparison compare(Hand h) { // compare THIS hand to testHand, return what testHand is relative to this
        if (h.getType() != getType()) return HandComparison.WORSE;

        StraightFlushHand testHand = (StraightFlushHand) h;

        if (testHand.getValue().getValue() > value.getValue()) return HandComparison.BETTER;
        if (testHand.getValue().getValue() == value.getValue()) return HandComparison.EQUAL;
        return HandComparison.WORSE;
    }

    public String toString() {
        return getType().toString() + " suit:" + suit.toString() + " high:" + value.toString();
    }

//    public Card[] getCards() {
//        Card[] cards = new Card[5];
//        int maxValue = value.getValue();
//        for (int i = maxValue; i > maxValue-5; i--) {
//            cards[maxValue - i] = Card.get(CardValue.fromValue(i), suit);
//        }
//        return cards;
//    }

}
