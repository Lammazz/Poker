package me.lammazz.poker.launch;

import me.lammazz.poker.network.Card;
import me.lammazz.poker.server.Hand;
import me.lammazz.poker.server.HandUtils;

public class Test {

    public static void main(String[] args) {

        Card[] cards = new Card[] {
                Card.FOUR_DIAMONDS,
                Card.EIGHT_CLUBS,
                Card.TEN_CLUBS,
                Card.ACE_DIAMONDS,
                Card.TEN_HEARTS,
                Card.TWO_SPADES,
                Card.TWO_HEARTS
        };

        Hand hand = HandUtils.fromCards(cards);

        System.out.println("\n" + hand.toString());


    }

}
