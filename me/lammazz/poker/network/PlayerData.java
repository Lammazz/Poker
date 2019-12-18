package me.lammazz.poker.network;

import java.io.Serializable;

public class PlayerData implements Serializable {

    private static final long serialVersionUID = 1L;

    public int id, seat, chips, betAmount, contributed;
    public String name;
    public boolean inPlay, dealer, isCurrentTurn, hasActed; // if inPlay = true, have not folded. if false, they have folded.
    public Card leftCard, rightCard;

    public PlayerData(int id, int seat, int chips, int betAmount, int contributed, String name, boolean inPlay, boolean dealer,
                      boolean isCurrentTurn, boolean hasActed, Card leftCard, Card rightCard) {
        this.id = id;
        this.seat = seat;
        this.chips = chips;
        this.betAmount = betAmount;
        this.contributed = contributed;
        this.name = name;
        this.inPlay = inPlay;
        this.dealer = dealer;
        this.isCurrentTurn = isCurrentTurn;
        this.hasActed = hasActed;
        this.leftCard = leftCard;
        this.rightCard = rightCard;
    }

    public void bet(int amount) {
        this.betAmount += amount;
        this.contributed += amount;
        this.chips -= amount;
    }

    public void roundReset() {
        this.betAmount = 0;
        this.contributed = 0;
        this.dealer = false;
        this.inPlay = false;
        this.isCurrentTurn = false;
        this.hasActed = false;
        this.leftCard = null;
        this.rightCard = null;
    }

}
