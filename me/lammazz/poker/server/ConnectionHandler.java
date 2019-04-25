package me.lammazz.poker.server;

import me.lammazz.poker.network.Packet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ConnectionHandler {

    public static final int MAX_PLAYERS = 8;

    public static volatile List<Connection> connections = new ArrayList<Connection>();

    public static int connectedPlayers() {
        int total = 0;
        for (int i = 0; i < connections.size(); i++) {
            Connection connection = connections.get(i);
            if (connection.playerData != null) total++;
        }
        return total;
    }

    public static void sendAll(Packet packet) {
        for (Connection connection : connections) connection.sendObject(packet);
    }

    public static Connection getConnection(int id) {
        for (Connection connection : connections) if (connection.id == id) return connection;
        return null;
    }

    public static void removeConnection(int id) {
        Iterator<Connection> it = connections.iterator();
        while (it.hasNext()) {
            Connection connection = it.next();
            if (connection.id == id) it.remove();
        }
    }

    public static void cleanup() {
        Iterator<Connection> it = connections.iterator();
        while (it.hasNext()) {
            Connection connection = it.next();
            if (!connection.running && !connection.playerData.inPlay) {
                connection.close();
                it.remove();
            }
        }
    }

}