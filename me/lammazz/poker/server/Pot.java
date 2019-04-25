package me.lammazz.poker.server;

import java.util.List;

public class Pot {

    public int totalAmount;
    public List<Integer> eligible;

    public Pot(int totalAmount, List<Integer> eligible) {
        this.totalAmount = totalAmount;
        this.eligible = eligible;
    }

}
