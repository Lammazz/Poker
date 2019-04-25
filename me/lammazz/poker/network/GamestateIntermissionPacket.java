package me.lammazz.poker.network;

public class GamestateIntermissionPacket extends Packet {

    public PlayerData[] data;

    public GamestateIntermissionPacket(PlayerData[] data) {
        this.data = data;
    }

}
