package me.lammazz.poker.server;

import me.lammazz.poker.network.Card;
import me.lammazz.poker.network.Card.CardValue;
import me.lammazz.poker.network.Card.CardSuit;

import java.util.List;

public class HandUtils {

    public static int[] toArray(List<Integer> list) {
        int[] arr = new int[list.size()];
        for (int i = 0; i < arr.length; i++) arr[i] = list.get(i);
        return arr;
    }

    public static Hand fromCards(Card[] cards) {

        //SORT CARDS[] BY VALUE
        Card[] sortedCards = new Card[cards.length];
        sortedCards[0] = cards[0];
        for (int i = 1; i < cards.length; i++) {
            Card toSort = cards[i];
            for (int j = 0; j < sortedCards.length; j++) {
                Card compare = sortedCards[j];
                if (compare == null) {
                    sortedCards[j] = toSort;
                    break;
                } else {
                    if (toSort.getValue().getValue() > compare.getValue().getValue()) { //shift right, insert toSort at j
                        for (int k = (sortedCards.length - 1); k > j; k--) sortedCards[k] = sortedCards[k-1];
                        sortedCards[j] = toSort;
                        break;
                    } else { // try next slot
                        continue;
                    }
                }
            }
        }
        cards = sortedCards;

        int[] cardCount = new int[13]; // index = CardValue id, cardCount[index] = number of cards with given value id
        for (int i = 0; i < cardCount.length; i++) cardCount[i] = 0;
        for (int i = 0; i < cards.length; i++) cardCount[cards[i].getValue().getID()]++;

        int[] suitCount = new int[4]; // index = CardSuit id, suitCount[index] = number of cards with given suit id
        for (int i = 0; i < suitCount.length; i++) suitCount[i] = 0;
        for (int i = 0; i < cards.length; i++) suitCount[cards[i].getSuit().getID()]++;

        System.out.println("CardCount:");
        for (int i = 0; i < cardCount.length; i++) if (cardCount[i] != 0) System.out.println(CardValue.fromID(i).toString() + " - " + cardCount[i]);

        System.out.println("\nSuitCount:");
        for (int i = 0; i < suitCount.length; i++) if (suitCount[i] != 0) System.out.println(CardSuit.fromID(i).toString() + " - " + suitCount[i]);

        // Check for straight flush

        for (int i = 0; i < cards.length; i++) {
            Card high = cards[i];
            if (high.getValue().getValue() < 4) continue; // Lowest straight is 5 high

            int nextCardsFound = 0;
            for (int j = 1; j <= 3; j++) {
                boolean foundNext = false;
                for (int k = 0; k < cards.length; k++) {
                    Card c = cards[k];
                    if ((c.getSuit() == high.getSuit()) && (c.getValue().getValue() == (high.getValue().getValue() - j))) {
                        foundNext = true;
                        break;
                    }
                }
                if (!foundNext) break;
                nextCardsFound++;
            }

            if (nextCardsFound == 3) {

                int lastValue = high.getValue().getValue() - 4;
                if (high.getValue() == Card.CardValue.FIVE) lastValue = CardValue.ACE.getValue();

                for (int j = 0; j < cards.length; j++) {
                    Card c = cards[j];
                    if (c.getSuit() == high.getSuit() && c.getValue().getValue() == lastValue) {
                        return new StraightFlushHand(high.getSuit(), high.getValue());
                    }
                }

            }
            continue;
        }

        // Check for four of a kind

        for (int i = 0; i < cards.length; i++) {
            CardValue value = cards[i].getValue();
            int amount = 0;
            for (int j = 0; j < cards.length; j++) {
                Card c = cards[j];
                if (c.getValue() == value) amount++;
            }
            if (amount == 4) { // has 4 of kind, determine kicker
                CardValue kicker = CardValue.TWO;
                for (int j = 0; j < cards.length; j++) {
                    Card c = cards[j];
                    if (c.getValue() == value) continue;
                    if (c.getValue().getValue() > kicker.getValue()) kicker = c.getValue();
                }
                return new FourOfKindHand(value, kicker);
            }
        }

        // Check for full house

        int three = -1, two = -1;
        for (int i = 0; i < cardCount.length; i++) {
            if (cardCount[i] == 3 && three == -1) three = i;
            if (cardCount[i] >= 2 && two == -1) {
                if (i == three) continue;
                two = i;
            }
        }

        if (three != -1 && two != -1) return new FullHouseHand(CardValue.fromID(three), CardValue.fromID(two));

        // Check for flush

        for (int i = 0; i < suitCount.length; i++) {
            if (suitCount[i] >= 5) {
                CardSuit suit = Card.CardSuit.fromID(i);
                CardValue high = CardValue.TWO;
                for (int j = 0; j < cards.length; j++) {
                    Card c = cards[j];
                    if (c.getSuit() != suit) continue;
                    if (c.getValue().getValue() > high.getValue()) high = c.getValue();
                }
                return new FlushHand(suit, high);
            }
        }

        // Check for straight

        for (int i = 0; i < cards.length; i++) {
            CardValue high = cards[i].getValue();
            if (high.getValue() < 4) continue; // Lowest straight is 5 high

            int nextCardsFound = 0;
            for (int j = 1; j <= 3; j++) {
                boolean foundNext = false;
                for (int k = 0; k < cards.length; k++) {
                    Card c = cards[k];
                    if (c.getValue().getValue() == (high.getValue() - j)) {
                        foundNext = true;
                        break;
                    }
                }
                if (!foundNext) break;
                nextCardsFound++;
            }

            if (nextCardsFound == 3) {

                int lastValue = high.getValue() - 4;
                if (high == Card.CardValue.FIVE) lastValue = CardValue.ACE.getValue();

                for (int j = 0; j < cards.length; j++) {
                    Card c = cards[j];
                    if (c.getValue().getValue() == lastValue) {
                        return new StraightHand(high);
                    }
                }

            }
            continue;
        }

        // Check for three of a kind

        three = -1;
        for (int i = 0; i < cardCount.length; i++) {
            if (cardCount[i] == 3) {
                three = i;
                break;
            }
        }
        if (three != -1) { // has 3 of kind, determine 2 kickers
            CardValue threeValue = CardValue.fromID(three);
            CardValue kicker1 = CardValue.TWO, kicker2 = CardValue.TWO; // kicker1 >= kicker2
            for (int i = 0; i < cards.length; i++) {
                Card c = cards[i];
                if (c.getValue() == threeValue) continue;
                if (c.getValue().getValue() > kicker1.getValue()) {
                    kicker2 = kicker1;
                    kicker1 = c.getValue();
                } else {
                    if (c.getValue().getValue() > kicker2.getValue()) kicker2 = c.getValue();
                }
            }
            return new ThreeOfAKindHand(threeValue, kicker1, kicker2);
        }

        // Check for two pair

        int pair1 = -1, pair2 = -1, pair3 = -1;
        for (int i = 0; i < cardCount.length; i++) {
            if (cardCount[i] == 2) {
                if (pair1 == -1) pair1 = i;
                else {
                    if (pair2 == -1) pair2 = i;
                    else {
                        pair3 = i;
                        break;
                    }
                }
            }
        }
        if (pair1 != -1 && pair2 != -1) {
            CardValue pair1Value = CardValue.fromID(pair1);
            CardValue pair2Value = CardValue.fromID(pair2);
            CardValue sortedValue1, sortedValue2;

            if (pair1Value.getValue() > pair2Value.getValue()) {
                sortedValue1 = pair1Value;
                sortedValue2 = pair2Value;
            } else {
                sortedValue1 = pair2Value;
                sortedValue2 = pair1Value;
            }

            CardValue pair3Value;
            if (pair3 != -1) {
                pair3Value = CardValue.fromID(pair3);
                if (pair3Value.getValue() > sortedValue1.getValue()) {
                    sortedValue2 = sortedValue1;
                    sortedValue1 = pair3Value;
                } else if (pair3Value.getValue() > sortedValue2.getValue()) {
                    sortedValue2 = pair3Value;
                }
            }

            CardValue kicker = CardValue.TWO;
            for (int i = 0; i < cards.length; i++) {
                Card c = cards[i];
                if (c.getValue().getValue() == sortedValue1.getValue() || c.getValue().getValue() == sortedValue2.getValue()) continue;
                if (c.getValue().getValue() > kicker.getValue()) kicker = c.getValue();
            }
            return new TwoPairHand(sortedValue1, sortedValue2, kicker);
        }

        // Check for one pair

        int pair = -1;
        for (int i = 0; i < cardCount.length; i++) {
            if (cardCount[i] == 2) {
                pair = i;
                break;
            }
        }
        if (pair != -1) {
            CardValue pairValue = CardValue.fromID(pair);
            CardValue kicker1 = CardValue.TWO, kicker2 = CardValue.TWO, kicker3 = CardValue.TWO;
            for (int i = 0; i < cards.length; i++) {
                Card c = cards[i];
                if (c.getValue() == pairValue) continue;
                if (c.getValue().getValue() > kicker1.getValue()) {
                    kicker3 = kicker2;
                    kicker2 = kicker1;
                    kicker1 = c.getValue();
                } else {
                    if (c.getValue().getValue() > kicker2.getValue()) {
                        kicker3 = kicker2;
                        kicker2 = c.getValue();
                    } else {
                        if (c.getValue().getValue() > kicker3.getValue()) {
                            kicker3 = c.getValue();
                        }
                    }
                }
            }
            return new OnePairHand(pairValue, kicker1, kicker2, kicker3);
        }

        // Check for high card

        CardValue[] best = new CardValue[5];
        for (int i = 0; i < best.length; i++) best[i] = CardValue.TWO;
        for (int i = 0; i < cards.length; i++) {
            CardValue c = cards[i].getValue();
            for (int j = 0; j < best.length; j++) {
                if (c.getValue() > best[j].getValue()) {
                    for (int k = (best.length - 1); k > j; k--) best[k] = best[k-1];
                    best[j] = c;
                    break;
                }
            }
        }
        return new HighCardHand(best[0], best[1], best[2], best[3], best[4]);

    }

}
