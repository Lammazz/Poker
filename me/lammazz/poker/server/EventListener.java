package me.lammazz.poker.server;

import me.lammazz.poker.network.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EventListener {

    public void received(Object p,Connection connection) {

        if (p instanceof RequestConnectionPacket) {
            RequestConnectionPacket packet = (RequestConnectionPacket)p;
            String requestedName = packet.name;

            System.out.println("Connection request: " + requestedName);

            String reason = null;
            for (int i = 0; i < ConnectionHandler.connections.size(); i++) {
                Connection c = ConnectionHandler.connections.get(i);
                if (c.name != null && c.name.equalsIgnoreCase(requestedName)) {
                    reason = "Name unavailable.";
                    break;
                }
            }

            if (ConnectionHandler.connectedPlayers() >= ConnectionHandler.MAX_PLAYERS) reason = "Table full.";

            if (reason == null) { // No reason to disallow therefore allow them to join

                List<PlayerData> playerData = new ArrayList<PlayerData>();
                for (Connection c : ConnectionHandler.connections) {
                    if (c.playerData != null) playerData.add(c.playerData);
                }
                PlayerData[] data = playerData.toArray(new PlayerData[playerData.size()]);

                PokerServer ps = PokerServer.getInstance();
                AcceptConnectionPacket acceptPacket = new AcceptConnectionPacket(connection.id, requestedName,
                        ps.getCurrentState(), data, ps.potAmount(), ps.getTableCard1(), ps.getTableCard2(), ps.getTableCard3(),
                        ps.getTableCard4(), ps.getTableCard5());
                connection.name = requestedName;
                connection.sendObject(acceptPacket);

                PlayerData newData = new PlayerData(connection.id, PokerServer.getInstance().nextSeat(), 2500,0, 0,
                        connection.name, false, false, false, false, null, null);
                connection.playerData = newData;
                UserConnectedPacket connectedPacket = new UserConnectedPacket(connection.playerData);
                for (Connection c : ConnectionHandler.connections) c.sendObject(connectedPacket);

                System.out.println("Connection for " + requestedName + " accepted.");

                int connectedPlayers = ConnectionHandler.connectedPlayers();
                System.out.println("Connected players: " + connectedPlayers);
                System.out.println("CurrentState: " + ps.getCurrentState().toString());
                if (ps.getCurrentState() == GameState.PREGAME && connectedPlayers >= 2) PokerServer.getInstance().nextState();

                PokerServer.getInstance().updatePlayerCountLabel();
            } else {
                RefuseConnectionPacket refusePacket = new RefuseConnectionPacket(reason);
                connection.sendObject(refusePacket);
                connection.close();
                ConnectionHandler.removeConnection(connection.id);
                System.out.println("Connection for " + requestedName + " refused: " + reason);
            }

        } else if (p instanceof UserDisconnectPacket) {
            UserDisconnectPacket disconnectPacket = (UserDisconnectPacket)p;
            Iterator<Connection> it = ConnectionHandler.connections.iterator();
            while (it.hasNext()) {
                Connection c = it.next();
                if (c.id == disconnectPacket.id) {
                    c.close();
                    it.remove();
                }
            }
            ConnectionHandler.cleanup();
            for (Connection c : ConnectionHandler.connections) {
                c.sendObject(disconnectPacket);
            }
        } else if (p instanceof ClientBetPacket) {
            ClientBetPacket packet = (ClientBetPacket) p;
            PokerServer ps = PokerServer.getInstance();
            if (ps.getCurrentTurnID() != connection.id) return;

            connection.playerData.isCurrentTurn = false;
            connection.playerData.hasActed = true;

            if (packet.amount < 0) packet.amount = 0;

            int raiseAmount = packet.amount - ps.toStay;
            if (raiseAmount != 0) ps.minimumRaise = raiseAmount;

            connection.playerData.bet(packet.amount);
            ps.toStay = connection.playerData.betAmount;

            Connection nextTurn = ps.playerAtSeat(ps.nextPlayingSeat(connection.playerData.seat));

            if (nextTurn.playerData.hasActed && nextTurn.playerData.betAmount == ps.toStay) {
                ConnectionHandler.sendAll(new PlayerBetPacket(connection.id, connection.playerData.betAmount, connection.playerData.chips,
                        -1, -1, -1, -1, -1,-1));
                ps.nextState();
            } else {
                ConnectionHandler.sendAll(new PlayerBetPacket(connection.id, connection.playerData.betAmount, connection.playerData.chips,
                        nextTurn.id, nextTurn.playerData.betAmount, ps.toStay, ps.minimumRaise, ps.maximumBet(nextTurn.id),
                        ps.potAmount()));
                ps.startPlayerTurn(nextTurn.playerData);
            }

        } else if (p instanceof ClientFoldPacket) {
            ClientFoldPacket packet = (ClientFoldPacket) p;
            PokerServer ps = PokerServer.getInstance();
            if (ps.getCurrentTurnID() != connection.id) return;

            connection.playerData.isCurrentTurn = false;
            connection.playerData.hasActed = true;

            Connection nextTurn = ps.playerAtSeat(ps.nextPlayingSeat(connection.playerData.seat));

            if (ps.countInPlay() == 2) {
                ps.goQuickwin(nextTurn.playerData);
            } else {
                connection.playerData.inPlay = false;
                if (nextTurn.playerData.betAmount == ps.toStay) { // next state
                    ConnectionHandler.sendAll(new PlayerFoldPacket(connection.id, -1, -1, -1, -1, -1,
                            -1));
                    ps.nextState();
                } else {
                    ConnectionHandler.sendAll(new PlayerFoldPacket(connection.id, nextTurn.id, nextTurn.playerData.betAmount, ps.toStay,
                            ps.minimumRaise, ps.maximumBet(nextTurn.id), ps.potAmount()));
                    ps.startPlayerTurn(nextTurn.playerData);
                }
            }

        } else if (p instanceof ClientShowHandPacket) {
            ClientShowHandPacket packet = (ClientShowHandPacket) p;
            PokerServer ps = PokerServer.getInstance();
            if (ps.getCurrentTurnID() != connection.id) return;
            if (ps.getCurrentState() != GameState.QUICKWIN) return;
            ConnectionHandler.sendAll(new PlayerShowHandPacket(connection.id, connection.playerData.leftCard, connection.playerData.rightCard));
        }

        /*
        currentTurnCount++;
                Connection nextTurn = playerAtSeat(nextPlayingSeat(player.seat));
                if (nextTurn.playerData.betAmount == toStay) { // would-be next player has already called therefore go to next state
                    PlayerFoldPacket packet = new PlayerFoldPacket(player.id, -1, -1, -1, -1, -1,
                            -1);
                    ConnectionHandler.sendAll(packet);
                    nextState();
                } else { // next player has not called yet therefore set current turn to theirs
                    PlayerFoldPacket packet = new PlayerFoldPacket(player.id, nextTurn.id, nextTurn.playerData.betAmount, toStay, minimumRaise,
                            maximumBet(nextTurn.id), potAmount());
                    ConnectionHandler.sendAll(packet);
                    startPlayerTurn(nextTurn.playerData);
                }
         */


    }

}