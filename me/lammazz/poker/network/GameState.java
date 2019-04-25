package me.lammazz.poker.network;

import java.io.Serializable;

public enum GameState implements Serializable {

    PREGAME(-1), // For when playercount < 2, just wait for 2 players then go intermission.
    INTERMISSION(0), // 5(ish) seconds no action to allow waiting players to join
    PREFLOP(1), // Move Dealer chip > Post Blinds > Deal Hands > Circle Turns
    FLOP(2), // 3 Cards to table > Circle turns
    TURN(3), // 1 Card to table > Circle turns
    RIVER(4), // 1 card to table > Circle turns
    SHOWDOWN(5), // Show cards > Determine winner(s) > Display amount won > wait 10s go intermission
    QUICKWIN(6); // If one person wins due to rest folding: Show amount won > Ask winner muck/show for 10s > go intermission

    private int id;

    private GameState(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public static GameState fromID(int id) {
        for (int i = 0; i < GameState.values().length; i++) {
            GameState state = GameState.values()[i];
            if (state.getID() == id) return state;
        }
        return null;
    }

}
