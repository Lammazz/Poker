package me.lammazz.poker.server;

public enum HandType {

    STRAIGHT_FLUSH(0),
    FOUR_OF_A_KIND(1),
    FULL_HOUSE(2),
    FLUSH(3),
    STRAIGHT(4),
    THREE_OF_A_KIND(5),
    TWO_PAIR(6),
    ONE_PAIR(7),
    HIGH_CARD(8);

    private int id;
    private HandType(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public static HandType fromID(int id) {
        for (HandType type : HandType.values()) {
            if (type.getID() == id) return type;
        }
        return null;
    }

}
