package me.lammazz.poker.network;

public class PlayerWinPacket extends Packet {

    public int id, amount, chips;

    public PlayerWinPacket(int id, int amount, int chips) { // happens when all other players sendFold >> set state to quickwin for 10s, ask winner show
        this.id = id;
        this.amount = amount;
        this.chips = chips;
    }

}
