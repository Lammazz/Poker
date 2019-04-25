package me.lammazz.poker.client;

import me.lammazz.poker.network.*;

import java.util.Iterator;

public class EventListener {

    public void received(Object p, Client client) {

        System.out.println("Received Packet: " + p.toString());

        if (p instanceof AcceptConnectionPacket) {
            AcceptConnectionPacket packet = (AcceptConnectionPacket) p;
            PokerClient pc = PokerClient.getInstance();
            PlayerHandler ph = pc.getPlayerHandler();
            client.id = packet.id;
            client.name = packet.name;
            client.connected = true;
            pc.currentState = packet.currentState;
            for (int i = 0; i < packet.playerData.length; i++) ph.players.add(packet.playerData[i]);
            pc.updateTableDisplay(packet.potAmount, packet.tableCard1, packet.tableCard2, packet.tableCard3,
                    packet.tableCard4, packet.tableCard5);
        } else if (p instanceof RefuseConnectionPacket) {
            System.out.println("Connection Refused. Reason: '" + ((RefuseConnectionPacket)p).reason + "'");
            PokerClient.getInstance().close();
        } else if (p instanceof UserConnectedPacket) {
            UserConnectedPacket packet = (UserConnectedPacket) p;
            PokerClient pc = PokerClient.getInstance();
            pc.getPlayerHandler().players.add(packet.data);
            pc.updateDisplay();
        } else if (p instanceof UserDisconnectPacket) {
            UserDisconnectPacket packet = (UserDisconnectPacket) p;
            PokerClient pc = PokerClient.getInstance();
            PlayerHandler ph = pc.getPlayerHandler();
            Iterator<PlayerData> it = ph.players.iterator();
            while (it.hasNext()) {
                PlayerData data = it.next();
                if (data.id == packet.id) it.remove();
            }
            pc.updateDisplay();
        } else if (p instanceof GamestateIntermissionPacket) {
            GamestateIntermissionPacket packet = (GamestateIntermissionPacket) p;
            PokerClient pc = PokerClient.getInstance();
            pc.currentState = GameState.INTERMISSION;
            PlayerHandler ph = pc.getPlayerHandler();
            ph.players.clear();
            for (int i = 0; i < packet.data.length; i++) {
                packet.data[i].betAmount = 0;
                ph.players.add(packet.data[i]);
            }
//            for (PlayerData playerData : ph.players) playerData.inPlay = false;
            pc.updateTableDisplay(0, null, null, null, null, null);
            pc.updateDisplay();
        } else if (p instanceof GamestatePreflopPacket) {
            GamestatePreflopPacket packet = (GamestatePreflopPacket) p;
            PokerClient pc = PokerClient.getInstance();
            pc.currentState = GameState.PREFLOP;
            pc.bigBlind = packet.bigBlindAmount;
            pc.smallBlind = packet.smallBlindAmount;

            for (PlayerData data : pc.getPlayerHandler().players) {
                if (data.id == client.id) {
                    data.leftCard = packet.leftCard;
                    data.rightCard = packet.rightCard;
                }
                if (Utils.arrayContains(packet.playing, data.id)) data.inPlay = true;
                data.dealer = (packet.dealer == data.id);
                if (packet.bigBlindID == data.id) {
                    if (data.chips < pc.bigBlind) {
                        data.betAmount = data.chips;
                        data.chips = 0;
                    } else {
                        data.betAmount = pc.bigBlind;
                        data.chips -= pc.bigBlind;
                    }
                }
                if (packet.smallBlindID == data.id) {
                    if (data.chips < pc.smallBlind) {
                        data.betAmount = data.chips;
                        data.chips = 0;
                    } else {
                        data.betAmount = pc.smallBlind;
                        data.chips -= pc.smallBlind;
                    }
                }
                if (packet.firstTurn == data.id) data.isCurrentTurn = true;
            }
            pc.newTurnStart(40);
            pc.updateDisplay();
            pc.updateTableDisplay(pc.smallBlind + pc.bigBlind, null, null, null, null, null);
            if (pc.getPlayerHandler().getPlayer(client.id).isCurrentTurn) pc.setCurrentTurn(pc.bigBlind,packet.maximumBet,
                    pc.bigBlind-packet.currentBet,pc.bigBlind + pc.smallBlind);
            else pc.setOtherPlayerTurn();
        } else if (p instanceof PlayerBetPacket) {
            PlayerBetPacket packet = (PlayerBetPacket) p;
            PokerClient pc = PokerClient.getInstance();

            PlayerData prev = pc.getPlayerHandler().getPlayer(packet.id);
            prev.isCurrentTurn = false;
            prev.betAmount = packet.newBetAmount;
            prev.chips = packet.newBalance;

            if (packet.nextTurn == -1) {
                pc.updateDisplay();
                return; // No next turn, ie. waiting for gamestate change packet
            }
            if (prev.id == client.id) pc.setOtherPlayerTurn();

            PlayerData next = pc.getPlayerHandler().getPlayer(packet.nextTurn);
            next.isCurrentTurn = true;
            next.betAmount = packet.currentBet;

            pc.newTurnStart(40);
            pc.updateDisplay();
            if (next.id == client.id) pc.setCurrentTurn(packet.minimumRaise, packet.maximumBet, packet.toStay - packet.currentBet,
                    packet.potAmount);
        } else if (p instanceof PlayerFoldPacket) {
            PlayerFoldPacket packet = (PlayerFoldPacket) p;
            PokerClient pc = PokerClient.getInstance();

            PlayerData prev = pc.getPlayerHandler().getPlayer(packet.id);
            prev.isCurrentTurn = false;
            prev.inPlay = false;

            System.out.println("id:" + prev.id + " folded. inPlay:" + prev.inPlay);

            if (packet.nextTurn == -1) {
                pc.updateDisplay();
                return; // No next turn, ie. waiting for gamestate change packet
            }
            if (prev.id == client.id) pc.setOtherPlayerTurn();

            PlayerData next = pc.getPlayerHandler().getPlayer(packet.nextTurn);
            next.isCurrentTurn = true;
            next.betAmount = packet.currentBet;

            pc.newTurnStart(40);
            pc.updateDisplay();
            if (next.id == client.id) pc.setCurrentTurn(packet.minimumRaise, packet.maximumBet, packet.toStay - packet.currentBet,
                    packet.potAmount);
        } else if (p instanceof PlayerShowHandPacket) {
            PlayerShowHandPacket packet = (PlayerShowHandPacket) p;
            PokerClient pc = PokerClient.getInstance();
            if (pc.currentState != GameState.QUICKWIN) return;
            PlayerData playerData = pc.getPlayerHandler().getPlayer(packet.id);
            pc.showPlayerCards(playerData.seat, packet.leftCard, packet.rightCard);
        } else if (p instanceof PlayerWinPacket) {
            PlayerWinPacket packet = (PlayerWinPacket) p;
            PokerClient pc = PokerClient.getInstance();
            pc.currentState = GameState.QUICKWIN;
            PlayerData playerData = pc.getPlayerHandler().getPlayer(packet.id);
            playerData.betAmount = packet.amount;
            playerData.chips = packet.chips;

            for (PlayerData d : pc.getPlayerHandler().players) {
                if (d.id != packet.id) {
                    d.betAmount = 0;
                    d.inPlay = false;
                    d.isCurrentTurn = false;
                }
            }

            if (playerData.id == client.id) {
                // Display the "Show Hand" Button which sends a "PlayerShowHandPacket" packet.
            }

            pc.updateDisplay();
        } else if (p instanceof GamestateFlopPacket) {
            GamestateFlopPacket packet = (GamestateFlopPacket) p;
            PokerClient pc = PokerClient.getInstance();
            pc.currentState = GameState.FLOP;

            for (PlayerData pd : pc.getPlayerHandler().players) pd.betAmount = 0;

            pc.updateTableDisplay(packet.potAmount, packet.tableCard1, packet.tableCard2, packet.tableCard3, null, null);

            PlayerData next = pc.getPlayerHandler().getPlayer(packet.firstTurn);
            next.isCurrentTurn = true;
            pc.newTurnStart(40);
            pc.updateDisplay();
            if (next.id == client.id) pc.setCurrentTurn(packet.minimumRaise, packet.maximumBet, 0, packet.potAmount);
            else pc.setOtherPlayerTurn();
        } else if (p instanceof GamestateTurnPacket) {
            GamestateTurnPacket packet = (GamestateTurnPacket) p;
            PokerClient pc = PokerClient.getInstance();
            pc.currentState = GameState.TURN;

            for (PlayerData pd : pc.getPlayerHandler().players) pd.betAmount = 0;

            pc.updateTableDisplay(packet.potAmount, pc.getTableDisplay().getCard1().getCurrentCard(),
                    pc.getTableDisplay().getCard2().getCurrentCard(), pc.getTableDisplay().getCard3().getCurrentCard(),
                    packet.tableCard4, null);

            PlayerData next = pc.getPlayerHandler().getPlayer(packet.firstTurn);
            next.isCurrentTurn = true;
            pc.newTurnStart(40);
            pc.updateDisplay();
            if (next.id == client.id) pc.setCurrentTurn(packet.minimumRaise, packet.maximumBet, 0, packet.potAmount);
            else pc.setOtherPlayerTurn();
        } else if (p instanceof GamestateRiverPacket) {
            GamestateRiverPacket packet = (GamestateRiverPacket) p;
            PokerClient pc = PokerClient.getInstance();
            pc.currentState = GameState.RIVER;

            for (PlayerData pd : pc.getPlayerHandler().players) pd.betAmount = 0;

            pc.updateTableDisplay(packet.potAmount, pc.getTableDisplay().getCard1().getCurrentCard(),
                    pc.getTableDisplay().getCard2().getCurrentCard(), pc.getTableDisplay().getCard3().getCurrentCard(),
                    pc.getTableDisplay().getCard4().getCurrentCard(), packet.tableCard5);

            PlayerData next = pc.getPlayerHandler().getPlayer(packet.firstTurn);
            next.isCurrentTurn = true;
            pc.newTurnStart(40);
            pc.updateDisplay();
            if (next.id == client.id) pc.setCurrentTurn(packet.minimumRaise, packet.maximumBet, 0, packet.potAmount);
            else pc.setOtherPlayerTurn();
        } else if (p instanceof GamestateShowdownPacket) {
            GamestateShowdownPacket packet = (GamestateShowdownPacket) p;
            PokerClient pc = PokerClient.getInstance();
            pc.currentState = GameState.SHOWDOWN;
            pc.handleShowdownData(packet.data);
            pc.newTurnStart(10);
        }

//        if(p instanceof AddConnectionPacket) {
//            AddConnectionPacket packet = (AddConnectionPacket)p;
//            ConnectionHandler.connections.put(packet.id,new Connection(packet.id));
//            System.out.println(packet.id + " has connected");
//        }else if(p instanceof RemoveConnectionPacket) {
//            RemoveConnectionPacket packet = (RemoveConnectionPacket)p;
//            System.out.println("Connection: " + packet.id + " has disconnected");
//            ConnectionHandler.connections.remove(packet.id);
//        }
    }

}