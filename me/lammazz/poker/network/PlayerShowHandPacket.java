package me.lammazz.poker.network;

public class PlayerShowHandPacket extends Packet {

    public int id;
    public Card leftCard, rightCard;

    public PlayerShowHandPacket(int id, Card leftCard, Card rightCard) {
        this.id = id;
        this.leftCard = leftCard;
        this.rightCard = rightCard;
    }

}
