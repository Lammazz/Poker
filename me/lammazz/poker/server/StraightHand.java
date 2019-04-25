package me.lammazz.poker.server;

import me.lammazz.poker.network.Card.CardSuit;
import me.lammazz.poker.network.Card.CardValue;

public class StraightHand extends Hand {

    private CardValue high;

    public StraightHand(CardValue high) {
        super(HandType.STRAIGHT);

        this.high = high;
    }

    public CardValue getHigh() {
        return high;
    }

    public HandComparison compare(Hand h) { // compare THIS hand to testHand, return what testHand is relative to this
        int handTypeID = h.getType().getID(); // lower is better
        if (handTypeID < getType().getID()) return HandComparison.BETTER;
        if (handTypeID == getType().getID()) {
            StraightHand testHand = (StraightHand) h;
            if (testHand.getHigh().getValue() > high.getValue()) return HandComparison.BETTER;
            if (testHand.getHigh().getValue() == high.getValue()) return HandComparison.EQUAL;
        }
        return HandComparison.WORSE;
    }

    public String toString() {
        return getType().toString() + " high:" + high.toString();
    }

}
