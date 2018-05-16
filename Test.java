package poker;

import java.util.Scanner;

class Test {
	public static void main(String[] args) {
		Card aceHearts = new Card(Suit.SPADES,1);
		Card kingHearts = new Card(Suit.SPADES,13);
		Card queenHearts = new Card(Suit.SPADES,12);
		Card jackHearts = new Card(Suit.SPADES,11);
		Card tenHearts = new Card(Suit.SPADES,10);
		Card twoClubs = new Card(Suit.CLUBS,2);
		Card sevenDiamonds = new Card(Suit.DIAMONDS,7);

		Player p1 = new Player(1);
		Hand p1Hand = new Hand(p1);

		p1Hand.addCard(aceHearts);
		p1Hand.addCard(kingHearts);
		p1Hand.addCard(queenHearts);
		p1Hand.addCard(jackHearts);
		p1Hand.addCard(tenHearts);
		p1Hand.addCard(twoClubs);
		p1Hand.addCard(sevenDiamonds);

		p1Hand.evaluate();

		System.out.println("hand value: " + p1Hand.getHandValue());
		for (int i = 0; i < 5; i++) {
			System.out.println(p1Hand.getHand()[i].toString());
		}

		boolean continueGettingHands = true;

		Scanner scan = new Scanner(System.in);

		while (continueGettingHands) {
			System.out.println("Would you like to enter a hand? (Y/N)");
			String response = scan.next();

			if (response.equals("N")) {
				continueGettingHands = false;
			}
			else {
				System.out.println("Please enter 7 cards by specifying the SUIT and card value and entering between each card:");
				p1Hand = new Hand(p1);
				for (int i = 0; i < 7; i++) {
					String suit = scan.next();
					int value = scan.nextInt();

					switch(suit) {
						case "SPADES":
							p1Hand.addCard(new Card(Suit.SPADES,value));
							break;
						case "CLUBS":
							p1Hand.addCard(new Card(Suit.CLUBS,value));
							break;
						case "DIAMONDS":
							p1Hand.addCard(new Card(Suit.DIAMONDS,value));
							break;
						case "HEARTS":
							p1Hand.addCard(new Card(Suit.HEARTS,value));
							break;
					}
				}
				p1Hand.evaluate();
				System.out.println("hand value: " + p1Hand.getHandValue());
				for (int i = 0; i < 5; i++) {
					System.out.println(p1Hand.getHand()[i].toString());
				}
			}

		}
	}
}

