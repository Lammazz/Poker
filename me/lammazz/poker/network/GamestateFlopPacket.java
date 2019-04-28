package me.lammazz.poker.network;

public class GamestateFlopPacket extends Packet {

    public int firstTurn, maximumBet, minimumRaise, potAmount, currentChips;
    public Card tableCard1, tableCard2, tableCard3;

    public GamestateFlopPacket(int firstTurn, int maximumBet, int minimumRaise, int potAmount, Card tableCard1, Card tableCard2, Card tableCard3,
                               int currentChips) {
        this.firstTurn = firstTurn;
        this.maximumBet = maximumBet;
        this.minimumRaise = minimumRaise;
        this.potAmount = potAmount;
        this.tableCard1 = tableCard1;
        this.tableCard2 = tableCard2;
        this.tableCard3 = tableCard3;
        this.currentChips = currentChips;
    }

}
