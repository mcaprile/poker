package poker;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.Arrays;
import java.util.Collections;

class Hand {

	LinkedList<Card> allCards;
	Card[] hand;

	LinkedList<LinkedList<Card>> pairs;
	LinkedList<LinkedList<Card>> trips;
	Card[] quads;

	boolean flush;
	boolean straight;
	int[] suits; // 0 = spades, 1 = hearts, 2 = clubs, 3 = diamonds
	int[] straightValues;

	int straightEnd;
	int flushIndex;

	int numPairs;
	int numSets;
	int numQuads;

	int handValue;
	int allCardFill;

	Player player;

	HashMap<Integer,LinkedList<Card>> cardCollections;
	HashMap<Integer,LinkedList<Card>> suitCollections;

	Comparator<LinkedList<Card>> cardListComparator = new Comparator<LinkedList<Card>>() {
        @Override
        public int compare(LinkedList<Card> o1, LinkedList<Card> o2) {
        	try {
            	return o1.getFirst().getValue() - o2.getFirst().getValue();
            }
            catch (Exception e) {
            	return 0;
            }
        }
    };

    Comparator<Card> cardComparator = new Comparator<Card>() {
        @Override
        public int compare(Card o1, Card o2) {
            return o1.getValue() - o2.getValue();
        }
    };

	Hand(Player player) {
		this.player = player;

		allCards = new LinkedList<Card>(); // in case there is an Ace to store
		hand = new Card[5];
		suits = new int[4];

		flush = false;
		straight = false;

		numPairs = numSets = numQuads = 0;
		allCardFill = 0;
		handValue = 0;

		straightValues = new int[14];

		//pairs = new Card[3][2];
		//trips = new Card[2][3];
		pairs = new LinkedList<LinkedList<Card>>();
		trips = new LinkedList<LinkedList<Card>>();
		quads = new Card[4];

		cardCollections = new HashMap<Integer,LinkedList<Card>>();

		for (int i = 0; i < 14; i++) {
			cardCollections.put(i,new LinkedList<Card>());
		}
		suitCollections = new HashMap<Integer,LinkedList<Card>>();

		for (int i = 0; i < 4; i++) {
			suitCollections.put(i, new LinkedList<Card>());
		}
	}

	void addCard(Card card) {
		addToStraightValues(card);
		addToSuits(card);
		addToCollections(card);
		allCards.add(card);
		//allCardFill++;

		if (card.getValue() == 1) {
			Card aceHigh = new Card(card.getSuit(),14);
			addToStraightValues(aceHigh);
			addToSuits(aceHigh);
			addToCollections(aceHigh);
			allCards.add(aceHigh);
			//allCardFill++;
		}
	}

	void addToStraightValues(Card card) {
		//if (card.getValue() == 1) {
			straightValues[card.getValue()-1] = 1;
	//	}
		/*else
			straightValues[card.getValue()-1] = straightValues[card.getValue() - 2] + 1;

		if (straightValues[card.getValue()-1] >= 5) {
			straightEnd = card.getValue()-1;
			straight = true;
		}*/
	}

	void addToSuits(Card card) {
		int index = 0;

		switch(card.getSuit()) {
			case SPADES:
				index = 0;
				break;
			case HEARTS:
				index = 1;
				break;
			case CLUBS:
				index = 2;
				break;
			case DIAMONDS:
				index = 3;
				break;
		}

		suits[index]++;
		suitCollections.get(index).add(card);
		if (suits[index] == 5) {
			flushIndex = index;
			flush = true;
		}
	}

	void addToCollections(Card card) {
		if (cardCollections.get(card.getValue()) != null) {
			LinkedList<Card> cardList = cardCollections.get(card.getValue());
			cardList.add(card);

			switch(cardList.size()) {
				case 1:
					break;
				case 2:
					numPairs++;
					pairs.add(cardList);
					//pairs[numPairs-1][0] = cardList.get(0);
				//	pairs[numPairs-1][1] = cardList.get(1);
					break;
				case 3:
					numSets++;
					trips.add(cardList);
					//trips[numSets-1][0] = cardList.get(0);
					//trips[numSets-1][1] = cardList.get(1);
					//trips[numSets-1][2] = cardList.get(2);
					break;
				case 4: 
					numQuads++;
					quads[0] = cardList.get(0);
					quads[1] = cardList.get(1);
					quads[2] = cardList.get(2);
					quads[3] = cardList.get(3);
					break;
			}
		}
		else {
			cardCollections.put(card.getValue(),new LinkedList<Card>());
			cardCollections.get(card.getValue()).add(card);
		}
	}


	void evaluate() {
		if (checkStraight() && flush) {
			if(setStraightFlush())
				handValue = 9;
			else
				handValue = 6;
		}
		else if (numQuads == 1) {
			setQuads();
			handValue = 8;
		}
		else if (numSets >= 1 && numPairs >= 2) {
			setFullHouse();
			handValue = 7;
		}
		else if(flush) {
			setFlush();
			handValue = 6;
		}
		else if (checkStraight()) {		
			setStraight();
			handValue = 5;
		}
		else if(numSets >= 1) {
			setSet();
			handValue = 4;
		}
		else if (numPairs >= 2) {
			setTwoPair();
			handValue = 3;
		}
		else if (numPairs == 1) {
			setPair();
			handValue = 2;
		}
		else {
			Collections.sort(allCards,cardComparator);

			for (int i = 0; i < 5; i++) {
				hand[i] = allCards.get(i+2);
			}
			handValue = 1;
		}

		Arrays.sort(hand,cardComparator);
	}

	boolean checkStraight() {
		int numConsecutive = 0;
		int index = 13;
		while (index >= 0 && numConsecutive < 5) {
			if (straightValues[index] == 1) {
				numConsecutive++;
			}
			else
				numConsecutive = 0;
			index--;
		}

		if (numConsecutive == 5) {
			straight = true;
			straightEnd = index + 5;
		}
		return straight;
	}

	Card[] getHand() {
		return hand;
	}

	int getHandValue() {
		return handValue;
	}

	Player getPlayer() {
		return player;
	}

	int compareHands(Hand hand1, Hand hand2) {
		if (hand1.getHandValue() > hand2.getHandValue())
			return hand1.getPlayer().getId();
		else if (hand2.getHandValue() > hand1.getHandValue())
			return hand2.getPlayer().getId();
		else {
			Card[] cards1 = hand1.getHand();
			Card[] cards2 = hand2.getHand();

			int index = 4;

			while (cards1[index].getValue() == cards2[index].getValue()) {
				index--;
			}	

			if (cards1[index].getValue() > cards2[index].getValue())
				return hand1.getPlayer().getId();
			else if (cards1[index].getValue() > cards2[index].getValue())
				return hand2.getPlayer().getId();
		}

		return 0;

	}

	boolean setStraightFlush() {
		Card[] allInFlush = new Card[suitCollections.get(flushIndex).size()];
		int index = 0;
		boolean isStraightFlush = true;

		for (Card card : suitCollections.get(flushIndex)) {
			allInFlush[index] = card;
			index++;
		}

		Arrays.sort(allInFlush,cardComparator);
		int start = 0;

		while (start < allInFlush.length && allInFlush[start].getValue() != straightEnd - 3 && start < allInFlush.length) {
			start++;
		}

		if (start == allInFlush.length) {
			isStraightFlush = false;
		}
		else {
			for (int i = 1; i < 5; i++) {
				if (allInFlush[i+start].getValue() != allInFlush[i+start-1].getValue()+1) {
					isStraightFlush = false;
					break;
				}
			}
		}

		if (isStraightFlush) {
			for (int i = 0; i < 5; i++) {
				hand[i] = allInFlush[start+i];
			}
		}
		else {
			setFlush();
		}

		return isStraightFlush;
	}

	void setQuads() {
		int index = 6;

		if (allCards.get(index).getValue() == quads[0].getValue()) {
			index-=4;
		}

		for (int i = 0; i < 4; i++) {
			hand[i] = quads[i];
		}
		hand[4] = allCards.get(index);
	}

	void setFullHouse() {
		Collections.sort(pairs,cardListComparator);
		Collections.sort(trips,cardListComparator);

		for (int i = 0; i < 3; i++) {
			hand[i] = trips.get(numSets-1).get(i);
		}

		int index = numPairs-1;
		while (pairs.get(index).get(0).getValue() == trips.get(numSets-1).get(0).getValue()) {
			index--;
		}
		for (int i = 3; i < 5; i++) {
			hand[i] = pairs.get(index).get(i-3);
		}
	}

	void setFlush() {
		Card[] allInFlush = new Card[suitCollections.get(flushIndex).size()];
		int index = 0;
		for (Card card : suitCollections.get(flushIndex)) {
			allInFlush[index] = card;
			index++;
		}

		Arrays.sort(allInFlush,cardComparator);
		index = allInFlush.length-5;

		for (int i=0; i < 5; i++) {
			hand[i] = allInFlush[index+i];
		}
	}

	void setStraight() {
		Collections.sort(allCards,cardComparator);
		int start = 0;
		while(allCards.get(start).getValue() != straightEnd - 3) {
			start++;
		}
		for (int i = 0; i < 5; i++) {
			if ((i != 0 && allCards.get(start+i).getValue() != allCards.get(start+i-1).getValue()) || i==0)
				hand[i] = allCards.get(start+i);
		}
	}

	void setSet() {
		Collections.sort(trips,cardListComparator);
		Collections.sort(allCards,cardComparator);

		int additionalCards = 0;
		int index = 6;
		int start = 0;

		while (additionalCards < 2) {
			if (allCards.get(index).getValue() != trips.getLast().get(0).getValue()){
				hand[start] = allCards.get(index);
				index--;
				start++;
				additionalCards++;
			}
		}

		for (int i = start; i < 5; i++) {
			hand[i] = trips.getLast().get(i-start);
		}
	}

	void setTwoPair() {
		Collections.sort(pairs,cardListComparator);		
		Collections.sort(allCards,cardComparator);

		int kicker = 0;
		int start = 0;
		int index = 6;

		while (kicker < 1) {
			if ((allCards.get(index).getValue() != pairs.get(2).get(0).getValue()) && (allCards.get(index).getValue() != pairs.get(1).get(0).getValue())){
				hand[start] = allCards.get(index);
				index--;
				start++;
				kicker++;
			}
		}

		for (int i = 2; i >= 1; i--) {
			for (int j=0; j < 2; j++) {
				hand[start] = pairs.get(i).get(j);
				start++;
				if (start == 5)
					break;
			}
		}
	}

	void setPair() {
		Collections.sort(pairs,cardListComparator);			
		Collections.sort(allCards,cardComparator);
		int additionalCards = 0;
		int index = 6;
		int start = 0;

		while (additionalCards < 3) {
			if (allCards.get(index).getValue() != pairs.get(2).getFirst().getValue()){
				hand[start] = allCards.get(index);
				index--;
				start++;
				additionalCards++;
			}
		}

		for (int i = 0; i < 2; i++) {
			hand[start] = pairs.get(2).get(i);
			start++;
		}
	}

}