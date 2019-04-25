package me.lammazz.poker.network;

public class UserConnectedPacket extends Packet {

    public PlayerData data;

    public UserConnectedPacket(PlayerData data) {
        this.data = data;
    }

}
