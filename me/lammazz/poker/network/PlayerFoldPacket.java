package me.lammazz.poker.network;

public class PlayerFoldPacket extends Packet {

    public int id, nextTurn, currentBet, toStay, minimumRaise, maximumBet, potAmount;
    // id = who folded
    public PlayerFoldPacket(int id, int nextTurn, int currentBet, int toStay, int minimumRaise, int maximumBet, int potAmount) {
        this.id = id;
        this.nextTurn = nextTurn;
        this.currentBet = currentBet;
        this.toStay = toStay;
        this.minimumRaise = minimumRaise;
        this.maximumBet = maximumBet;
        this.potAmount = potAmount;
    }

}
