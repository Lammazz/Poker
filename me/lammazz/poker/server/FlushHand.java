package me.lammazz.poker.server;

import me.lammazz.poker.network.Card.CardSuit;
import me.lammazz.poker.network.Card.CardValue;

public class FlushHand extends Hand {

    private CardValue high;
    private CardSuit suit;

    public FlushHand(CardSuit suit, CardValue high) {
        super(HandType.FLUSH);

        this.suit = suit;
        this.high = high;
    }

    public CardValue getHigh() {
        return high;
    }

    public CardSuit getSuit() {
        return suit;
    }

    public HandComparison compare(Hand h) { // compare THIS hand to testHand, return what testHand is relative to this
        int handTypeID = h.getType().getID(); // lower is better
        if (handTypeID < getType().getID()) return HandComparison.BETTER;
        if (handTypeID == getType().getID()) {
            FlushHand testHand = (FlushHand) h;
            if (testHand.getHigh().getValue() > high.getValue()) return HandComparison.BETTER;
            if (testHand.getHigh().getValue() == high.getValue()) return HandComparison.EQUAL;
        }
        return HandComparison.WORSE;
    }

    public String toString() {
        return getType().toString() + " suit:" + suit.toString() + " high:" + high.toString();
    }

}
