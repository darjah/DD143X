import java.util.LinkedList;

public class MidStrategy {

	public static void play(Hand hand, Scorecard card) {
		boolean[] betOnThis = new boolean[15];

		for (int i = 0; i < betOnThis.length; i++) {
			betOnThis[i] = false;
		}

		// start with check if we have small/large straight or yatzy
		boolean checked = AI.catchHand(hand, card);
		if (checked) {
			return;
		}		
	}

	public static void onPar(Scorecard card, Hand hand) {

		LinkedList<Integer> emptyCategories = card.getEmptyCategories();
		
		if (AI.catchHand(hand, card)) {
			return;
		}

		if (stegCheck(hand)) {
			stegeKolla(card, hand, emptyCategories);
		
			
		}
		
		int betOn = uppCheck(card, hand);
		
		GetCategories.allOfAKind(hand, betOn);
		if (AI.catchHand(hand, card)) {
			return;
		}
		if (AI.fullHouse(card, hand)) {
			return;
		}

		GetCategories.allOfAKind(hand, betOn);
		
		allOfAKindDefensive(card, hand, betOn);

	}

	public static int uppCheck(Scorecard card, Hand hand) {
		LinkedList<Integer> freeScores = card.getEmptyCategories();
		int[] countedDices = new int[6];
		AI.countValues(hand.getHandArray(), countedDices);

		LinkedList<Integer> checkedAllready = new LinkedList<Integer>();

		int betOn = 1;
		int num = 0;
		for (int v = 0; v < 6; v++) {
			num = 0;
			for (int e = 5; e >= 0; e--) {
				if (countedDices[e] > num && !checkedAllready.contains(e +1)) {
					num = countedDices[e];
					betOn = e +1;
					////System.out.println(betOn);
				}
			}

			if (freeScores.contains(betOn - 1)) {
				
				return betOn;
			}

			checkedAllready.add(betOn);
		}

		for (int e = 5; e >= 0; e--) {
			if (countedDices[e] > num) {
				num = countedDices[e];
				betOn = e;
			}
		}
		return betOn;
	}

	public static void agressive(Scorecard card, Hand hand) {
		LinkedList<Integer> freeScores = card.getEmptyCategories();
		// the player need every point possible

		int[] evalScores = new int[card.categories.length];
		AI.evalScores(hand.getHandArray(), evalScores);

		// fångar kåk direkt om vi ligger under par, kan inte få par
		if (AI.fullHouse(card, hand)) {
			return;
		}

		if (evalScores[Scorecard.twoPair] != 0
				&& (freeScores.contains(Scorecard.twoPair) || freeScores
						.contains(Scorecard.fullHouse))) {
			twoPairMid(card, hand, freeScores, evalScores);
			return;

		}

		if (stegCheck(hand)) {
			stegeKolla(card, hand, freeScores);
			return;
		}

		int keep = betOnInt(card, hand);

		GetCategories.allOfAKind(hand, keep);
		if (AI.catchHand(hand, card)) {
			return;
		}
		if (AI.fullHouse(card, hand)) {
			return;
		}

		GetCategories.allOfAKind(hand, keep);

		allOfAKindAgressive(card, hand, keep);
	}

	public static void twoPairMid(Scorecard card, Hand hand,
			LinkedList<Integer> freeScores, int[] evalScores) {
			// TODO vi har tva par, kak ledigt

			GetCategories.twoPairToHouse(hand);
			AI.evalScores(hand.getHandArray(), evalScores);

			// fångar kåk direkt om vi ligger under par, kan inte få par
			if (AI.fullHouse(card, hand)) {
				////System.out.println("aaaa");
				return;
			}

			GetCategories.twoPairToHouse(hand);

			AI.evalScores(hand.getHandArray(), evalScores);

			// fångar kåk direkt om vi ligger under par, kan inte få par
			if (AI.fullHouse(card, hand)) {
				////System.out.println("bbbb");
				return;
			}

			// vi har tva par och den platsen ar ledig
			if (freeScores.contains(Scorecard.twoPair)) {
				////System.out.println("ccc");
				card.categories[Scorecard.twoPair] = evalScores[Scorecard.twoPair];
				return;
			}

			// vi har kak men kak ar upptagen.dvs fyller triss
			if (evalScores[Scorecard.threeOfAKind] != 0
					&& freeScores.contains(Scorecard.threeOfAKind)) {
				////System.out.println("dddd");
				card.categories[Scorecard.threeOfAKind] = evalScores[Scorecard.threeOfAKind];
				return;
			}


			if (evalScores[Scorecard.pair] >= 8
					&& freeScores.contains(Scorecard.pair)) {
				////System.out.println("eeeee");
				card.categories[Scorecard.pair] = evalScores[Scorecard.pair];
				return;
			}

			for (int d = 0; d < 6; d++) {
				////System.out.println("ffffff");
				if (evalScores[d] != 0 && freeScores.contains(d)) {
					card.categories[d] = evalScores[d];
					return;
				}
			}

			if (freeScores.contains(Scorecard.pair)) {
				////System.out.println("gggggg");
				card.categories[Scorecard.pair] = evalScores[Scorecard.pair];
				return;
			}

		

			// NullEntry
			////System.out.println("NullEntry");
			NullEntry.nolla(card, hand);
			
			return;

	}

	public static int betOnInt(Scorecard card, Hand hand) {
		LinkedList<Integer> freeScores = card.getEmptyCategories();

		int[] countedDices = new int[6];
		AI.countValues(hand.getHandArray(), countedDices);
		int value = 1;

		// omedlbart satsa pa mer en fyra av en sort
		for (int l : countedDices) {
			if (l >= 4) {
				return value;
			}
			value++;
		}

		int highestSum = 0;
		int highestDice = 1;

		for (int h = 0; h < countedDices.length; h++) {
			if (countedDices[h] * (h + 1) > highestSum) {
				highestSum = countedDices[h] * (h + 1);
				highestDice = h + 1;
			}
		}

		boolean freeUpThere = freeScores.contains(highestDice - 1);
		if (freeUpThere) {
			return highestDice;
		}

		int highestCopy = highestDice;
		highestSum = 0;
		for (int h = 0; h < countedDices.length; h++) {
			if (countedDices[h] * (h + 1) > highestSum
					&& (h + 1) != highestCopy) {
				highestSum = countedDices[h] * (h + 1);
				highestDice = h + 1;
			}
		}

		freeUpThere = freeScores.contains(highestDice - 1);
		if (freeUpThere) {
			return highestDice;
		}

		return highestCopy;

	}
	
	
	
	public static void allOfAKindDefensive(Scorecard card, Hand hand, int kept){
		LinkedList<Integer> freeScores = card.getEmptyCategories();

		boolean checked = AI.catchHand(hand, card);
		if (checked) {
			return;
		}
		int[] evalScore = new int[15];
		AI.evalScores(hand.getHandArray(), evalScore);

		if (freeScores.contains(kept - 1)
				&& evalScore[Scorecard.threeOfAKind] != 0) {
			card.categories[kept - 1] = evalScore[kept - 1];
			return;
		}

		if (evalScore[Scorecard.fourOfAKind] != 0
				&& freeScores.contains(Scorecard.fourOfAKind)) {
			card.categories[Scorecard.fourOfAKind] = evalScore[Scorecard.fourOfAKind];
			return;
		}

		if (AI.fullHouse(card, hand)) {
			return;
		}

		if (freeScores.contains(kept - 1)) {
			card.categories[kept - 1] = evalScore[kept - 1];
			return;
		}

		if (evalScore[Scorecard.threeOfAKind] != 0
				&& freeScores.contains(Scorecard.threeOfAKind)) {
			card.categories[Scorecard.threeOfAKind] = evalScore[Scorecard.threeOfAKind];
			return;
		}

		if (evalScore[Scorecard.twoPair] != 0
				&& freeScores.contains(Scorecard.twoPair)) {
			card.categories[Scorecard.twoPair] = evalScore[Scorecard.twoPair];
			return;
		}

		if (evalScore[Scorecard.pair] != 0
				&& freeScores.contains(Scorecard.pair)) {
			card.categories[Scorecard.pair] = evalScore[Scorecard.pair];
			return;
		}


		NullEntry.nolla(card, hand);

	}
	

	public static void allOfAKindAgressive(Scorecard card, Hand hand, int kept) {
		LinkedList<Integer> freeScores = card.getEmptyCategories();

		boolean checked = AI.catchHand(hand, card);
		if (checked) {
			return;
		}
		int[] evalScore = new int[15];
		AI.evalScores(hand.getHandArray(), evalScore);

		if (evalScore[Scorecard.fourOfAKind] != 0
				&& freeScores.contains(Scorecard.fourOfAKind)) {
			card.categories[Scorecard.fourOfAKind] = evalScore[Scorecard.fourOfAKind];
			return;
		}

		if (AI.fullHouse(card, hand)) {
			return;
		}

		if (freeScores.contains(kept - 1)
				&& evalScore[Scorecard.threeOfAKind] != 0) {
			card.categories[kept - 1] = evalScore[kept - 1];
			return;
		}

		if (evalScore[Scorecard.threeOfAKind] != 0
				&& freeScores.contains(Scorecard.threeOfAKind)) {
			card.categories[Scorecard.threeOfAKind] = evalScore[Scorecard.threeOfAKind];
			return;
		}

		if (evalScore[Scorecard.twoPair] != 0
				&& freeScores.contains(Scorecard.twoPair)) {
			card.categories[Scorecard.twoPair] = evalScore[Scorecard.twoPair];
			return;
		}

		if (evalScore[Scorecard.pair] != 0
				&& freeScores.contains(Scorecard.pair)) {
			card.categories[Scorecard.pair] = evalScore[Scorecard.pair];
			return;
		}

		if (freeScores.contains(kept - 1)) {
			card.categories[kept - 1] = evalScore[kept - 1];
			return;
		}

		NullEntry.nolla(card, hand);

	}

	public static void stegeKolla(Scorecard card, Hand hand,
			LinkedList<Integer> freeScores) {
		int[] countedDices = new int[6];
		boolean[] stegarKollade = stegCheckArray(hand);
		AI.countValues(hand.getHandArray(), countedDices);

		boolean hadeRedan = AI.catchHand(hand, card);
		if (hadeRedan) {
			return;
		}

		////Printer.printArraybOOL(stegarKollade);

		// vvi kan fa liten stege och den ar ledig
		if (freeScores.contains(Scorecard.smallStraight)) {
			////////System.out.println("small straight free");
			if (stegarKollade[0] || stegarKollade[1]) {
				GetCategories.smallStraight(hand);
				boolean caught = AI.catchHand(hand, card);
				if (caught) {
					return;
				}
				GetCategories.smallStraight(hand);
				caught = AI.catchHand(hand, card);
				if (caught) {
					return;
				}

				// ingen liten stege
			}

		}

		if (freeScores.contains(Scorecard.largeStraight) && hand.getRoll() == 1) {
			if (stegarKollade[1] || stegarKollade[2]) {
				GetCategories.largeStraight(hand);
				boolean caught = AI.catchHand(hand, card);
				if (caught) {
					return;
				}
				GetCategories.largeStraight(hand);
				caught = AI.catchHand(hand, card);
				if (caught) {
					return;
				}

				// ingen stor stege
			}
		}

		int[] evalScore = new int[15];
		AI.evalScores(hand.getHandArray(), evalScore);

		if (freeScores.contains(Scorecard.twoPair)
				&& evalScore[Scorecard.twoPair] != 0) {
			card.categories[Scorecard.twoPair] = evalScore[Scorecard.twoPair];
			return;
		}
		if (freeScores.contains(Scorecard.pair)
				&& evalScore[Scorecard.pair] != 0) {
			card.categories[Scorecard.pair] = evalScore[Scorecard.pair];
			return;
		}

		if (freeScores.contains(Scorecard.threeOfAKind)
				&& evalScore[Scorecard.threeOfAKind] != 0) {
			card.categories[Scorecard.threeOfAKind] = evalScore[Scorecard.threeOfAKind];
			return;
		}

		boolean satteUppe = fillUpper(card, hand);
		if (!satteUppe) {
			NullEntry.nollaUppe(card);
		}

	}

	public static boolean stegCheck(Hand hand) {
		String s = new String();
		for (int k : hand.getHandArray()) {
			if (!s.contains("" + k)) {
				s += k;
			}
		}

		boolean first = s.contains("1234");
		boolean second = s.contains("2345");
		boolean third = s.contains("3456");

		if (first || second || third) {
			return true;
		}
		return false;
	}

	public static boolean[] stegCheckArray(Hand hand) {

		String s = new String();
		for (int k : hand.getHandArray()) {
			if (!s.contains("" + k)) {
				s += k;
			}
		}

		boolean[] returning = new boolean[3];
		returning[0] = s.contains("1234");
		returning[1] = s.contains("2345");
		returning[2] = s.contains("3456");

		return returning;
	}

	public static boolean fillUpper(Scorecard card, Hand hand) {
		for (int i = 0; i < 6; i++) {
			if (card.categories[i] == -1) {
				card.categories[i] = AI.numberScore(hand.getHandArray(),
						i + 1);
				return true;
			}
		}
		return false;
	}

}