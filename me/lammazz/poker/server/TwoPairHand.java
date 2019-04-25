package me.lammazz.poker.server;

import me.lammazz.poker.network.Card.CardSuit;
import me.lammazz.poker.network.Card.CardValue;

public class TwoPairHand extends Hand {

    private CardValue pair1, pair2, kicker;// pair1 value > pair2 value

    public TwoPairHand(CardValue pair1, CardValue pair2, CardValue kicker) {
        super(HandType.TWO_PAIR);

        this.pair1 = pair1;
        this.pair2 = pair2;
        this.kicker = kicker;
    }

    public CardValue getPair1() {
        return pair1;
    }

    public CardValue getPair2() {
        return pair2;
    }

    public CardValue getKicker() {
        return kicker;
    }

    public HandComparison compare(Hand h) { // compare THIS hand to testHand, return what testHand is relative to this
        int handTypeID = h.getType().getID(); // lower is better
        if (handTypeID < getType().getID()) return HandComparison.BETTER;
        if (handTypeID == getType().getID()) {
            TwoPairHand testHand = (TwoPairHand) h;
            if (testHand.getPair1().getValue() > getPair1().getValue()) return HandComparison.BETTER;
            if (testHand.getPair1().getValue() == getPair1().getValue()) {
                if (testHand.getPair2().getValue() > getPair2().getValue()) return HandComparison.BETTER;
                if (testHand.getPair2().getValue() == getPair2().getValue()) {
                    if (testHand.getKicker().getValue() > getKicker().getValue()) return HandComparison.BETTER;
                    if (testHand.getKicker().getValue() == getKicker().getValue()) return HandComparison.EQUAL;
                }
            }
        }
        return HandComparison.WORSE;
    }

    public String toString() {
        return getType().toString() + " pair1:" + pair1.toString() + " pair2:" + pair2.toString() + " kicker:" + kicker.toString();
    }

}
