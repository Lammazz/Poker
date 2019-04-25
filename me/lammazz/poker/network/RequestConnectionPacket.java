package me.lammazz.poker.network;

import java.io.Serializable;

public class RequestConnectionPacket extends Packet {

    private RequestConnectionPacket(){}

    public String name;

    public RequestConnectionPacket(String name) {
        this.name = name;
    }

}
