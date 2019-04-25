package me.lammazz.poker.network;

public class UserDisconnectPacket extends Packet {

    private UserDisconnectPacket() {}

    public int id;

    public UserDisconnectPacket(int id) {
        this.id = id;
    }

}
