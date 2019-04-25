package me.lammazz.poker.server;

import me.lammazz.poker.network.Card.CardSuit;
import me.lammazz.poker.network.Card.CardValue;

public class OnePairHand extends Hand {

    private CardValue pair, kicker1, kicker2, kicker3; // kicker1 value > kicker2 value > kicker3 value

    public OnePairHand(CardValue pair, CardValue kicker1, CardValue kicker2, CardValue kicker3) {
        super(HandType.ONE_PAIR);

        this.pair = pair;
        this.kicker1 = kicker1;
        this.kicker2 = kicker2;
        this.kicker3 = kicker3;
    }

    public CardValue getPair() {
        return pair;
    }

    public CardValue getKicker1() {
        return kicker1;
    }

    public CardValue getKicker2() {
        return kicker2;
    }

    public CardValue getKicker3() {
        return kicker3;
    }

    public HandComparison compare(Hand h) { // compare THIS hand to testHand, return what testHand is relative to this
        int handTypeID = h.getType().getID(); // lower is better
        if (handTypeID < getType().getID()) return HandComparison.BETTER;
        if (handTypeID == getType().getID()) {
            OnePairHand testHand = (OnePairHand) h;
            if (testHand.getPair().getValue() > getPair().getValue()) return HandComparison.BETTER;
            if (testHand.getPair().getValue() == getPair().getValue()) {
                if (testHand.getKicker1().getValue() > getKicker1().getValue()) return HandComparison.BETTER;
                if (testHand.getKicker1().getValue() == getKicker1().getValue()) {
                    if (testHand.getKicker2().getValue() > getKicker2().getValue()) return HandComparison.BETTER;
                    if (testHand.getKicker2().getValue() == getKicker2().getValue()) {
                        if (testHand.getKicker3().getValue() > getKicker3().getValue()) return HandComparison.BETTER;
                        if (testHand.getKicker3().getValue() == getKicker3().getValue()) return HandComparison.EQUAL;
                    }
                }
            }
        }
        return HandComparison.WORSE;
    }

    public String toString() {
        return getType().toString() + " pair:" + pair.toString() + " kicker1:" + kicker1.toString() + " kicker2:" + kicker2.toString() +
                " kicker3:" + kicker3.toString();
    }

}
