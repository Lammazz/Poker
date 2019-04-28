package me.lammazz.poker.network;

public class GamestateRiverPacket extends Packet {

    public int firstTurn, maximumBet, minimumRaise, potAmount, currentChips;
    public Card tableCard5;

    public GamestateRiverPacket(int firstTurn, int maximumBet, int minimumRaise, int potAmount, Card tableCard5, int currentChips) {
        this.firstTurn = firstTurn;
        this.maximumBet = maximumBet;
        this.minimumRaise = minimumRaise;
        this.potAmount = potAmount;
        this.tableCard5 = tableCard5;
        this.currentChips = currentChips;
    }

}
