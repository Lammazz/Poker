package me.lammazz.poker.network;

public class GamestateTurnPacket extends Packet {

    public int firstTurn, maximumBet, minimumRaise, potAmount, currentChips;
    public Card tableCard4;

    public GamestateTurnPacket(int firstTurn, int maximumBet, int minimumRaise, int potAmount, Card tableCard4, int currentChips) {
        this.firstTurn = firstTurn;
        this.maximumBet = maximumBet;
        this.minimumRaise = minimumRaise;
        this.potAmount = potAmount;
        this.tableCard4 = tableCard4;
        this.currentChips = currentChips;
    }

}
