package poker;

enum Suit {
	SPADES, HEARTS, CLUBS, DIAMONDS;
}

class Card {
	private int value;

	Suit cardSuit;

	Card(Suit cardSuit, int value) {
		this.value = value;
		this.cardSuit = cardSuit;
	}

	int getValue() {
		return this.value;
	}

	Suit getSuit() {
		return this.cardSuit;
	}

	public String toString() {
		String card = "";
		switch(cardSuit) {
			case SPADES:
				card = "Card: SPADES " + value;
				break;
			case CLUBS:
				card = "Card: CLUBS " + value;
				break;
			case DIAMONDS:
				card = "Card: DIAMONDS " + value;
				break;
			case HEARTS:
				card = "Card: HEARTS " + value;
				break;
		}
		return card;
	}
}