package me.lammazz.poker.server;

public abstract class Hand {

    private HandType type;

    public HandType getType() {
        return type;
    }

    public abstract HandComparison compare(Hand testHand);
//    public abstract Card[] getCards();

    public abstract String toString();

    protected Hand(HandType type) {
        this.type = type;
    }

}
