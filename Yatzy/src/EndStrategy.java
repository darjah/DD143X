import java.util.LinkedList;

public class EndStrategy {
	public static void play(Scorecard card, Hand hand){
		agressive(card, hand);
	}

	public static void agressive(Scorecard card, Hand hand){
		LinkedList<Integer> emptyCategories = card.getEmptyCategories();
		int[] evalScores = new int[card.categories.length];
		AI.evalScores(hand, evalScores);

		//Fånga kåk direkt
		if(AI.fullHouse(card, hand)){
			return;
		}

		if(evalScores[Scorecard.twoPair] != 0 && (emptyCategories.contains(Scorecard.twoPair) || evalScores[Scorecard.twoPair] != 0 && emptyCategories.contains(Scorecard.fullHouse))) {
			MidStrategy.twoPairMid(card, hand, emptyCategories, evalScores);
			return;
		}

		boolean wentForStraight = false;

		int stege = stegCheck(card, hand);
		if (stege == 1) {
			//System.out.println("222");
			GetCategories.smallStraight(hand);
			GetCategories.smallStraight(hand);
			if (AI.catchHand(hand, card)) {
				return;
			}
			wentForStraight = true;
		}
		if (stege == 2) {
			//System.out.println("333");
			GetCategories.largeStraight(hand);
			GetCategories.largeStraight(hand);
			if (AI.catchHand(hand, card)) {
				return;
			}
			wentForStraight = true;
		}

		if (wentForStraight) {
			//System.out.println("4444");
			int[] evalScore = new int[15];
			AI.evalScores(hand, evalScore);
			for (int l = evalScore.length - 1; l >= 0; l--) {
				if (l != Scorecard.chance && evalScore[l] != 0
						&& emptyCategories.contains(l)) {
					card.categories[l] = evalScore[l];
					return;
				}
			}
			NullEntry.zeroDown(card);
			return;
		}

		if ((card.getEmptyCategories().size() == 1 && card.categories[Scorecard.smallStraight] == -1)
				|| (card.getEmptyCategories().size() == 2
				&& card.categories[Scorecard.smallStraight] == -1 && card.categories[Scorecard.chance] == -1)) {
			//System.out.println("5555");
			GetCategories.smallStraight(hand);
			GetCategories.smallStraight(hand);
			if (AI.catchHand(hand, card)) {
				return;
			}
			NullEntry.zeroDown(card);
			return;

		}

		if ((card.getEmptyCategories().size() == 1 && card.categories[Scorecard.largeStraight] == -1)
				|| (card.getEmptyCategories().size() == 2
				&& card.categories[Scorecard.largeStraight] == -1 && card.categories[Scorecard.chance] == -1)) {
			//System.out.println("6666");
			GetCategories.largeStraight(hand);
			GetCategories.largeStraight(hand);
			if (AI.catchHand(hand, card)) {
				return;
			}
			NullEntry.zeroDown(card);
			return;
		}

		if ((card.getEmptyCategories().size() == 2 && (card.categories[Scorecard.smallStraight] == -1 && card.categories[Scorecard.largeStraight] == -1))
				|| (card.getEmptyCategories().size() == 3 && (card.categories[Scorecard.smallStraight] == -1
				&& card.categories[Scorecard.largeStraight] == -1 && card.categories[Scorecard.chance] == -1))) {
			//System.out.println("7777");
			GetCategories.largeStraight(hand);
			if (AI.catchHand(hand, card)) {
				return;
			}
			GetCategories.largeStraight(hand);
			if (AI.catchHand(hand, card)) {
				return;
			}
			NullEntry.zeroDown(card);
			return;
		}

		if (AI.catchHand(hand, card)) {
			return;
		}
		if (AI.fullHouse(card, hand)) {
			return;
		}

		if ((emptyCategories.contains(Scorecard.fullHouse) || emptyCategories
				.contains(Scorecard.twoPair))
				&& AI.twoPairScore(hand) != 0) {
			//System.out.println("888");
			MidStrategy.twoPairMid(card, hand, emptyCategories, evalScores);
			return;
		}

		if (emptyCategories.contains(Scorecard.fullHouse)
				&& AI.threeOfAKindScore(hand) != 0) {

			//System.out.println("999");
			fullHouse(card, hand, emptyCategories, evalScores);
			return;
		}

		if ((emptyCategories.size() == 1 && emptyCategories.contains(Scorecard.fullHouse))
				|| (emptyCategories.size() == 2
				&& emptyCategories.contains(Scorecard.fullHouse) && emptyCategories
				.contains(Scorecard.chance))) {

			//System.out.println("aaaaa");
			fullHouse(card, hand, emptyCategories, evalScores);
		}

		if ((emptyCategories.size() == 1 && emptyCategories.contains(Scorecard.twoPair))
				|| (emptyCategories.size() == 2
				&& emptyCategories.contains(Scorecard.twoPair) && emptyCategories
				.contains(Scorecard.chance))) {
			//System.out.println("bbbb");
			GetCategories.getTwoPair(hand);
			GetCategories.getTwoPair(hand);

			int score = AI.twoPairScore(hand);
			if (score != 0){
				card.categories[Scorecard.twoPair] = score;
				return;
			}
			NullEntry.nullEntry(card);
			return;

		}

		if (emptyCategories.size() == 1 && emptyCategories.contains(Scorecard.chance)){
			//System.out.println("cccc");

			goForChans(card, hand);

			return;
		}


		xOfAKind(card, hand);

		//System.out.println("igenom late");
	}



	public static void xOfAKind(Scorecard card, Hand hand){
		MidStrategy.allOfAKindAgressive(card, hand, MidStrategy.betOnInt(card, hand));
	}


	public static void goForChans(Scorecard card, Hand hand){
		for (Dice dice : hand.getDices()){
			if (dice.faceValue < 4){
				dice.throwDice();
			}
		}
		for (Dice dice : hand.getDices()){
			if (dice.faceValue < 4){
				dice.throwDice();
			}
		}

		card.categories[Scorecard.chance] = AI.chansScore(hand);
	}

	/**
	 * 
	 * @param card
	 * @param hand
	 * @return 1 liten stege, 2 stor stege, 0 ingen stege
	 */
	public static int stegCheck(Scorecard card, Hand hand) {
		LinkedList<Integer> emptyCategories = card.getEmptyCategories();

		String s = new String();
		for (int k : hand.getHandArray()) {
			if (!s.contains("" + k)) {
				s += k;
			}
		}

		boolean first = s.contains("123");
		boolean second = s.contains("234");
		boolean third = s.contains("345");
		boolean forth = s.contains("456");

		if (emptyCategories.contains(Scorecard.smallStraight)
				&& (first || second || third)) {
			return 1;
		}

		if (emptyCategories.contains(Scorecard.largeStraight)
				&& (second || third || forth)) {
			return 2;
		}

		return 0;
	}

	public static void fullHouse(Scorecard card, Hand hand,
			LinkedList<Integer> emptyCategories, int[] evalScores) {
		if (emptyCategories.contains(Scorecard.fullHouse)) {

			GetCategories.getFullHouse(hand);
			AI.evalScores(hand, evalScores);

			// fÃ¥ngar kÃ¥k direkt om vi ligger under par, kan inte fÃ¥ par
			if (AI.fullHouse(card, hand)) {
				return;
			}

			if (AI.catchHand(hand, card)) {
				return;
			}

			GetCategories.getFullHouse(hand);

			AI.evalScores(hand, evalScores);

			if (AI.fullHouse(card, hand)) {
				return;
			}

			if (evalScores[Scorecard.fourOfAKind] != 0
					&& emptyCategories.contains(Scorecard.fourOfAKind)) {
				card.categories[Scorecard.fourOfAKind] = evalScores[Scorecard.fourOfAKind];
				return;
			}

			// vi har kak men kak ar upptagen.dvs fyller triss
			if (evalScores[Scorecard.threeOfAKind] != 0
					&& emptyCategories.contains(Scorecard.threeOfAKind)) {
				card.categories[Scorecard.threeOfAKind] = evalScores[Scorecard.threeOfAKind];
				return;
			}

			// vi har tva par och den platsen ar ledig
			if (card.categories[Scorecard.twoPair] == -1) {
				card.categories[Scorecard.twoPair] = evalScores[Scorecard.twoPair];
				return;
			}

			if (evalScores[Scorecard.pair] >= 8
					&& emptyCategories.contains(Scorecard.pair)) {
				card.categories[Scorecard.pair] = evalScores[Scorecard.pair];
				return;
			}

			for (int d = 0; d < 6; d++) {
				if (evalScores[d] != 0 && emptyCategories.contains(d)) {
					card.categories[d] = evalScores[d];
					return;
				}
			}

			if (emptyCategories.contains(Scorecard.pair)) {
				card.categories[Scorecard.pair] = evalScores[Scorecard.pair];
				return;
			}

			// nolla
			NullEntry.nullEntry(card);
			return;
		}
	}
}