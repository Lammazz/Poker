package me.lammazz.poker.network;

import java.io.Serializable;

public class TwoInt implements Serializable {
    private static final long serialVersionUID = 1L;

    public int x,y;
    public TwoInt(int x, int y) {
        this.x = x;
        this.y = y;
    }

}
