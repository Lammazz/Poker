package me.lammazz.poker.network;

public class GamestateShowdownPacket extends Packet {

    public PlayerShowdownData[] data;

    public GamestateShowdownPacket(PlayerShowdownData[] data) {
        this.data = data;
    }

}
