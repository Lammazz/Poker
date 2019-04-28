package me.lammazz.poker.network;

public class GamestatePregamePacket extends Packet {

    public TwoInt[] waiting; // <playerid,chips>

    public GamestatePregamePacket(TwoInt[] waiting) {
        this.waiting = waiting;
    }

}
