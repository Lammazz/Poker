package me.lammazz.poker.network;

public class UserDisconnectPacket extends Packet {

    public int id;

    public UserDisconnectPacket(int id) {
        this.id = id;
    }

}
