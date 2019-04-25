package me.lammazz.poker.network;

public class RefuseConnectionPacket extends Packet {

    private RefuseConnectionPacket() {}

    public String reason;

    public RefuseConnectionPacket(String reason) {
        this.reason = reason;
    }

}
