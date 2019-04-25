package me.lammazz.poker.network;

import java.io.Serializable;

public class PlayerShowdownData implements Serializable {
    private static final long serialVersionUID = 1L;

    public int id, winnings, newBalance;
    public Card leftCard, rightCard;

    public PlayerShowdownData(int id, int winnings, int newBalance, Card leftCard, Card rightCard) {
        this.id = id;
        this.winnings = winnings;
        this.newBalance = newBalance;
        this.leftCard = leftCard;
        this.rightCard = rightCard;
    }

}
