package me.lammazz.poker.server;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import me.lammazz.poker.network.*;

import java.util.*;

public class PokerServer {

    private static PokerServer instance;
    public static PokerServer getInstance() {
        return instance;
    }

    private Stage stage;
    private Scene scene;
    private Server server;

    private Button exitButton;
    private Label playerCount;

    private GameState currentState;
    private int dealerSeat = 0, currentTurnID, currentDeckIndex, bigBlind, smallBlind, smallBlindSeat;
    public int toStay, minimumRaise;
    public volatile int currentTurnCount;
    private Card tableCard1, tableCard2, tableCard3, tableCard4, tableCard5;
    private Random random;
    private Card[] deck;



    public int getCurrentTurnID() {
        return currentTurnID;
    }

    public Card getTableCard1() {
        return tableCard1;
    }

    public Card getTableCard2() {
        return tableCard2;
    }

    public Card getTableCard3() {
        return tableCard3;
    }

    public Card getTableCard4() {
        return tableCard4;
    }

    public Card getTableCard5() {
        return tableCard5;
    }

    public PokerServer(int port, Stage stage) {

        instance = this;

        server = new Server(this, port);
        server.start();

        stage.setTitle("Poker Server");

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();
                server.shutdown();
                stage.close();
            }
        });

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10,10,10,10));
        grid.setVgap(8);
        grid.setHgap(10);

        playerCount = new Label("Player Count: 0");
        GridPane.setConstraints(playerCount, 0, 0);

        exitButton = new Button("Exit");
        GridPane.setConstraints(exitButton, 0, 2);
        exitButton.setStyle("-fx-text-fill: #ff0000;-fx-font-weight: bold");
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                server.shutdown();
                stage.close();
                System.exit(0);
            }
        });

        grid.getChildren().addAll(playerCount, exitButton);

        Scene scene = new Scene(grid, 400, 300);

        stage.setScene(scene);

        deck = Card.values().clone();
        shuffleDeck();

        currentTurnCount = 0;
        bigBlind = 40;
        smallBlind = 20;// 2000 start
        currentTurnID = -1;
        toStay = bigBlind;
        minimumRaise = bigBlind;

        currentState = GameState.PREGAME;

        taskList = new ArrayList<ScheduledTask>();
        taskThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        long start = System.currentTimeMillis();
                        Iterator<ScheduledTask> it = taskList.iterator();
                        while (it.hasNext()) {
                            ScheduledTask task = it.next();
//                            System.out.println("Task waiting. To Run:" + task.systemTime + " Current:" + start);
                            if (task.systemTime <= start) {
//                                System.out.println("Running scheduled task.");
                                runTaskMainThread(task.runnable);
                                task.executed = true;
                                it.remove();
                            }
                        }
                        Thread.sleep(100 - (System.currentTimeMillis()-start));
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        taskThread.start();

        System.out.println("Server started.");

    }

    private Card nextCard() {
        Card card = deck[currentDeckIndex];
        currentDeckIndex++;
        return card;
    }

    private void shuffleDeck() {
        List<Card> list = Arrays.asList(deck);
        Collections.shuffle(list);
        deck = list.toArray(new Card[list.size()]);
        currentDeckIndex = 0;
        System.out.println("Deck shuffled " + currentDeckIndex);
    }

    public boolean isSeatOccupied(int seatID) {
        for (Connection connection : ConnectionHandler.connections) {
            if (connection.playerData == null) continue;
            if (connection.playerData.seat == seatID) return true;
        }
        return false;
    }

    public Connection playerAtSeat(int seatID) {
        for (Connection connection : ConnectionHandler.connections) {
            if (connection.playerData == null) continue;
            if (connection.playerData.seat == seatID) return connection;
        }
        return null;
    }

    public int nextOccupiedSeat(int from) {
        do from = ((from + 1) % 8);
        while (!isSeatOccupied(from));
        return from;
    }

    public boolean isSeatPlaying(int seatID) {
        for (Connection connection: ConnectionHandler.connections) {
            if (connection.playerData == null) continue;
            if (connection.playerData.seat == seatID &&
                connection.playerData.inPlay) return true;
        }
        return false;
    }

    public int nextPlayingSeat(int from) {
        System.out.println("NextPlayingSeat From:" + from);
        do from = ((from + 1) % 8);
        while (!isSeatPlaying(from));
        System.out.println("NextPlayingSeat To:" + from);
        return from;
    }

    public int countInPlay() {
        int amount = 0;
        for (Connection connection : ConnectionHandler.connections) {
            if (connection.playerData == null) continue;
            if (connection.playerData.inPlay) amount++;
        }
        return amount;
    }

    public void startPlayerTurn(PlayerData player) {
        currentTurnID = player.id;
        player.isCurrentTurn = true;
        currentTurnCount++;

        scheduleTask(400, new Runnable() {
            int turnTrack = currentTurnCount;
            @Override
            public void run() {
                if (turnTrack != currentTurnCount) return;
                currentTurnCount++;
                Connection nextTurn = playerAtSeat(nextPlayingSeat(player.seat));

                if (countInPlay() == 2) {
                    goQuickwin(nextTurn.playerData);
                } else {
                    player.inPlay = false;
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
                }


            }
        });

        // on send current turn to player, start task to send expire packet.
        // ^ STORE TURN NUMBER IN THIS CLASS, IF AFTER 40S TURN NUMBER IS SAME, CANCEL TURN. IF TURN NUMBER HAS CHANGED, CANCEL THE SCHEDULED TASK

    }

    public int potAmount() {
        int amount = 0;
        for (Connection connection : ConnectionHandler.connections) {
            if (connection.playerData == null) continue;
            amount += connection.playerData.contributed;
        }
        return amount;
    }

    public int maximumBet(int playerID) {
        PlayerData player = null;
        int high1 = 0, high2 = 0;
        for (Connection connection : ConnectionHandler.connections) {
            if (connection.playerData == null) continue;
            PlayerData pd = connection.playerData;
            if (pd.id == playerID) player = pd;
            if (pd.chips > high1) {
                high2 = high1;
                high1 = pd.chips;
            } else {
                if (pd.chips > high2) {
                    high2 = pd.chips;
                }
            }
        }
        if (player == null) return -1;
        if (player.chips == high1) return high2;
        return player.chips;
    }

    public void goQuickwin(PlayerData winner) {

        currentState = GameState.QUICKWIN;

        int amount = potAmount();
        winner.chips += amount;

        currentTurnCount++;
        currentTurnID = winner.id;

        ConnectionHandler.sendAll(new PlayerWinPacket(winner.id, amount, winner.chips));

        scheduleTask(100, new Runnable() {
            @Override
            public void run() {
                nextState();
            }
        });

    }

    public void nextState() {

        System.out.println("Next state method called. From " + currentState.toString());

        if (currentState == GameState.PREGAME || currentState == GameState.SHOWDOWN || currentState == GameState.QUICKWIN) { // go to intermission

            currentState = GameState.INTERMISSION;

            List<PlayerData> data = new ArrayList<PlayerData>();
            for (Connection connection : ConnectionHandler.connections) {
                if (connection.playerData == null) continue;
                connection.playerData.roundReset();
                connection.playerData.inPlay = false;
                data.add(connection.playerData);
            }
            PlayerData[] playerData = data.toArray(new PlayerData[data.size()]);

            ConnectionHandler.cleanup();

            GamestateIntermissionPacket packet = new GamestateIntermissionPacket(playerData);
            ConnectionHandler.sendAll(packet);

            scheduleTask(30, new Runnable() {
                @Override
                public void run() {
                    nextState();
                }
            });

        } else if (currentState == GameState.INTERMISSION) { // go to preflop

            currentState = GameState.PREFLOP;

            for (Connection connection : ConnectionHandler.connections) {
                if (connection.playerData == null) continue;
                connection.playerData.inPlay = connection.playerData.chips != 0;
            }

            List<Integer> inPlayList = new ArrayList<Integer>();
            for (Connection connection : ConnectionHandler.connections) {
                if (connection.playerData == null) continue;
                if (connection.playerData.inPlay) inPlayList.add(connection.playerData.id);
            }
            int[] inPlay = new int[inPlayList.size()];
            for (int i = 0; i < inPlay.length; i++) inPlay[i] = inPlayList.get(i);

            if (countInPlay() < 2) {
                // RETURN GAME TO PREGAME, NOT ENOUGH PLAYERS
            }

            shuffleDeck();

            dealerSeat = nextOccupiedSeat(dealerSeat);

            PlayerData dealer = playerAtSeat(dealerSeat).playerData;

            smallBlindSeat = nextOccupiedSeat(dealerSeat);
            PlayerData toPaySmallBlind = playerAtSeat(smallBlindSeat).playerData;
            int toPay = smallBlind;
            if (toPaySmallBlind.chips < smallBlind) toPay = toPaySmallBlind.chips;
            toPaySmallBlind.bet(toPay);

            int bigBlindSeat = nextOccupiedSeat(smallBlindSeat);
            PlayerData toPayBigBlind = playerAtSeat(bigBlindSeat).playerData;
            toPay = bigBlind;
            if (toPayBigBlind.chips < bigBlind) toPay = toPayBigBlind.chips;
            toPayBigBlind.bet(toPay);

            int utgSeat = nextOccupiedSeat(bigBlindSeat);
            Connection utg = playerAtSeat(utgSeat);

            PlayerData toDeal = toPayBigBlind;
            do {
                toDeal.leftCard = nextCard();
                toDeal.rightCard = nextCard();
                toDeal = playerAtSeat(nextPlayingSeat(toDeal.seat)).playerData;
            } while (toDeal.seat != bigBlindSeat);

            GamestatePreflopPacket packet = new GamestatePreflopPacket(dealer.id, toPayBigBlind.id, toPaySmallBlind.id, utg.id,
                    null, null, inPlay, bigBlind, smallBlind, utg.playerData.betAmount, maximumBet(utg.id));

            for (Connection connection : ConnectionHandler.connections) {
                if (connection.playerData == null) continue;
                PlayerData pd = connection.playerData;
                if (pd.inPlay) {
                    packet.leftCard = pd.leftCard;
                    packet.rightCard = pd.rightCard;
                } else {
                    packet.leftCard = null;
                    packet.rightCard = null;
                }
                connection.sendObject(packet);
            }

            toStay = bigBlind;

            startPlayerTurn(utg.playerData);

        } else if (currentState == GameState.PREFLOP) { // go to flop

            currentState = GameState.FLOP;

            tableCard1 = nextCard();
            tableCard2 = nextCard();
            tableCard3 = nextCard();
            for (Connection connection : ConnectionHandler.connections) {
                if (connection.playerData == null) continue;
                connection.playerData.betAmount = 0;
                connection.playerData.hasActed = false;
            }
            toStay = 0;
            minimumRaise = bigBlind;

            Connection nextTurn = playerAtSeat(nextPlayingSeat(dealerSeat));
            Packet packet = new GamestateFlopPacket(nextTurn.id, maximumBet(nextTurn.id), minimumRaise, potAmount(),
                    tableCard1, tableCard2, tableCard3);
            ConnectionHandler.sendAll(packet);

            startPlayerTurn(nextTurn.playerData);

        } else if (currentState == GameState.FLOP) { // go to turn

            currentState = GameState.TURN;

            tableCard4 = nextCard();

            for (Connection connection : ConnectionHandler.connections) {
                if (connection.playerData == null) continue;
                connection.playerData.betAmount = 0;
                connection.playerData.hasActed = false;
            }
            toStay = 0;
            minimumRaise = bigBlind;

            Connection nextTurn = playerAtSeat(nextPlayingSeat(dealerSeat));
            Packet packet = new GamestateTurnPacket(nextTurn.id, maximumBet(nextTurn.id), minimumRaise, potAmount(),
                    tableCard4);
            ConnectionHandler.sendAll(packet);

            startPlayerTurn(nextTurn.playerData);

        } else if (currentState == GameState.TURN) { // go to river

            currentState = GameState.RIVER;

            tableCard5 = nextCard();

            for (Connection connection : ConnectionHandler.connections) {
                if (connection.playerData == null) continue;
                connection.playerData.betAmount = 0;
                connection.playerData.hasActed = false;
            }
            toStay = 0;
            minimumRaise = bigBlind;

            Connection nextTurn = playerAtSeat(nextPlayingSeat(dealerSeat));
            Packet packet = new GamestateRiverPacket(nextTurn.id, maximumBet(nextTurn.id), minimumRaise, potAmount(),
                    tableCard5);
            ConnectionHandler.sendAll(packet);

            startPlayerTurn(nextTurn.playerData);

        } else if (currentState == GameState.RIVER) { // go to showdown

            currentState = GameState.SHOWDOWN;

            //determine hands for each inPlay player + rank hands

            Card[] baseCards = new Card[7];
            baseCards[0] = tableCard1;
            baseCards[1] = tableCard2;
            baseCards[2] = tableCard3;
            baseCards[3] = tableCard4;
            baseCards[4] = tableCard5;

            HandRank[][] hands = new HandRank[8][8]; // HandRank[a][b], a = hand ranking, lower is better. b = hands of same rank

            for (Connection connection : ConnectionHandler.connections) {
                if (connection.playerData == null) continue;
                if (!connection.playerData.inPlay) continue;
                Card[] cards = baseCards.clone();
                cards[5] = connection.playerData.leftCard;
                cards[6] = connection.playerData.rightCard;

                Hand hand = HandUtils.fromCards(cards);
                HandRank rank = new HandRank(connection.id, hand);

                for (int a = 0; a < hands.length; a++) {
                    HandRank[] curr = hands[a];
                    if (curr[0] == null) {
                        curr[0] = rank;
                        break;
                    } else {
                        Hand test = curr[0].hand;
                        HandComparison compare = rank.hand.compare(test); // compare rank.hand hand to test, return what test is relative to rank.hand
                        System.out.println(test.toString() + " " + compare.toString() + " " + rank.hand.toString());
                        if (compare == HandComparison.BETTER) { // test is better therefore continue
                            continue;
                        } else if (compare == HandComparison.EQUAL) { // test is equal therefore add rank.hand to same arr
                            for (int b = 1; b < curr.length; b++) {
                                if (curr[b] == null) {
                                    curr[b] = rank;
                                    break;
                                }
                            }
                            break;
                        } else { // test is worse therefore shift
                            for (int i = (hands.length - 1); i > a; i--) hands[i] = hands[i-1];
                            HandRank[] replacement = new HandRank[8];
                            replacement[0] = rank;
                            hands[a] = replacement;
                            break;
                        }
                    }
                }
            }

            for (int a = 0; a < hands.length; a++) {
                HandRank[] ha = hands[a];
                if (ha == null) continue;
                for (int b = 0; b < ha.length; b++) {
                    HandRank hr = ha[b];
                    if (hr == null) continue;
                    System.out.println("Rank:" + a + " id:" + hr.id + " Hand:" + hr.hand.toString());
                }
            }

            //generate pots

            int mainPot = 0;
            List<Integer> inPlay = new ArrayList<Integer>();
            for (Connection connection : ConnectionHandler.connections) {
                if (connection.playerData == null) continue;
                if (connection.playerData.inPlay) inPlay.add(connection.id);
                else mainPot += connection.playerData.contributed;
            }

            List<Pot> pots = new ArrayList<Pot>();
            pots.add(new Pot(mainPot, inPlay));

            while (true) {

                int remaining = 0;
                for (Connection connection : ConnectionHandler.connections) {
                    if (connection.playerData == null) continue;
                    if (connection.playerData.inPlay &&
                        connection.playerData.contributed > 0) remaining++;
                }
                if (remaining == 0) break;

                int contributed = -1; // lowest
                for (Connection connection : ConnectionHandler.connections) {
                    if (connection.playerData == null) continue;
                    if (!connection.playerData.inPlay) continue;
                    if (connection.playerData.contributed <= 0) continue;
                    if (contributed == -1) contributed = connection.playerData.contributed;
                    else {
                        if (contributed != connection.playerData.contributed) {
                            if (connection.playerData.contributed < contributed) contributed = connection.playerData.contributed;
                        }
                    }
                }

                int potAmount = 0;
                List<Integer> eligible = new ArrayList<Integer>();
                for (Connection connection : ConnectionHandler.connections) {
                    if (connection.playerData == null) continue;
                    if (connection.playerData.inPlay && connection.playerData.contributed > 0) {
                        potAmount += contributed;
                        connection.playerData.contributed -= contributed;
                        eligible.add(connection.id);
                    }
                }
                Pot pot = new Pot(potAmount, eligible);
                pots.add(pot);
                System.out.println("Created pot of size " + potAmount + " with " + eligible.size() + " eligible players.");

            }

            //loop through best hands, award eligible pots until no more pots

            List<TwoInt> winnings = new ArrayList<TwoInt>(); // TwoInt(PlayerID, AmountWon)
            for (int i = 0; i < inPlay.size(); i++) winnings.add(new TwoInt(inPlay.get(i), 0));

            for (int a = 0; a < hands.length; a++) {

                HandRank[] win = hands[a];

                Iterator<Pot> it = pots.iterator();

                while (it.hasNext()) {
                    Pot pot = it.next();
                    List<Integer> l = new ArrayList<Integer>(); // players that are eligible for pot that have won it
                    for (int i = 0; i < win.length; i++) {
                        HandRank hr = win[i];
                        if (hr == null) continue;
                        if (pot.eligible.contains(hr.id)) l.add(hr.id);
                    }
                    if (l.size() != 0) {
                        int toWin = pot.totalAmount / l.size();
                        for (Integer i : l) {
                            for (TwoInt ti : winnings) if (ti.x == i) ti.y += toWin;
                        }
                        it.remove();
                    }
                }

                if (pots.size() == 0) break;

            }

            //use winnings list

            List<PlayerShowdownData> data = new ArrayList<PlayerShowdownData>();
            for (TwoInt ti : winnings) {
                PlayerData playerData = ConnectionHandler.getConnection(ti.x).playerData;
                data.add(new PlayerShowdownData(ti.x, ti.y, playerData.chips + ti.y, playerData.leftCard, playerData.rightCard));
                playerData.chips += ti.y;
            }
            PlayerShowdownData[] dataArray = data.toArray(new PlayerShowdownData[]{});
            ConnectionHandler.sendAll(new GamestateShowdownPacket(dataArray));

            scheduleTask(100, new Runnable() {
                @Override
                public void run() {
                    nextState();
                }
            });

        }

    }

    private int[] seatOrder = {2, 5, 7, 0, 1, 3, 4, 6};
    public int nextSeat() {
        List<Connection> connections = ConnectionHandler.connections;
        for (int i = 0; i < seatOrder.length; i++) {
            boolean taken = false;
            int seat = seatOrder[i];
            for (Connection connection : connections) {
                if (connection.playerData == null) continue;
                if (connection.playerData.seat == seat) {
                    taken = true;
                    break;
                }
            }
            if (!taken) return seat;
        }
        return 6;
    }

    public void updatePlayerCountLabel() {
        final int players = ConnectionHandler.connectedPlayers();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                playerCount.setText("Player Count: " + players);
            }
        });
    }

    public volatile List<ScheduledTask> taskList;
    private Thread taskThread;

    public void runTaskMainThread(Runnable runnable) {
        Platform.runLater(runnable);
    }

    public void scheduleTask(int delay, Runnable runnable) { // delay in 0.1s ie delay=5 => 0.5s
        ScheduledTask task = new ScheduledTask(delay, runnable);
        taskList.add(task);
    }

    public GameState getCurrentState() {
        return currentState;
    }

}
