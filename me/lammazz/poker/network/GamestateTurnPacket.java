package me.lammazz.poker.network;

public class GamestateTurnPacket extends Packet {

    public int firstTurn, maximumBet, minimumRaise, potAmount;
    public Card tableCard4;

    public GamestateTurnPacket(int firstTurn, int maximumBet, int minimumRaise, int potAmount, Card tableCard4) {
        this.firstTurn = firstTurn;
        this.maximumBet = maximumBet;
        this.minimumRaise = minimumRaise;
        this.potAmount = potAmount;
        this.tableCard4 = tableCard4;
    }

}
