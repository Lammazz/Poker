package me.lammazz.poker.network;

import java.util.List;

public class AcceptConnectionPacket extends Packet {

    public int id, potAmount;
    public String name;
    public GameState currentState;

    public PlayerData[] playerData;
    public Card tableCard1, tableCard2, tableCard3, tableCard4, tableCard5;

    public AcceptConnectionPacket(int id, String name, GameState currentState, PlayerData[] playerData, int potAmount,
                                  Card tableCard1, Card tableCard2, Card tableCard3, Card tableCard4, Card tableCard5) {
        this.id = id;
        this.name = name;
        this.currentState = currentState;
        this.playerData = playerData;
        this.potAmount = potAmount;
        this.tableCard1 = tableCard1;
        this.tableCard2 = tableCard2;
        this.tableCard3 = tableCard3;
        this.tableCard4 = tableCard4;
        this.tableCard5 = tableCard5;
    }

}
