package me.lammazz.poker.network;

public class GamestatePreflopPacket extends Packet {

    public int dealer, bigBlindID, smallBlindID, firstTurn, bigBlindAmount, smallBlindAmount, currentBet, maximumBet, currentChips;
    public Card leftCard, rightCard;
    public int[] playing; // list of ids of players with hand

    public GamestatePreflopPacket(int dealer, int bigBlindID, int smallBlindID, int firstTurn, Card leftCard, Card rightCard, int[] playing,
                                  int bigBlindAmount, int smallBlindAmount, int currentBet, int maximumBet, int currentChips) {
        this.dealer = dealer;
        this.bigBlindID = bigBlindID;
        this.smallBlindID = smallBlindID;
        this.firstTurn = firstTurn;
        this.leftCard = leftCard;
        this.rightCard = rightCard;
        this.playing = playing;
        this.bigBlindAmount = bigBlindAmount;
        this.smallBlindAmount = smallBlindAmount;
        this.currentBet = currentBet; // how much first player has already put into table (either 0 or small/big blind depending on seat + table size)
        this.maximumBet = maximumBet; // max amount person can raise to. if player is chip leader, amnt = 2nd highest chips else amnt=all in
        this.currentChips = currentChips;
    }

}
