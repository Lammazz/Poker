package me.lammazz.poker.network;

public class ClientBetPacket extends Packet {

    public int amount;

    public ClientBetPacket(int amount) { // sent by client, received by server
        this.amount = amount;
    }

}
