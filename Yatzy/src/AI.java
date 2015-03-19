import java.util.Arrays;
import java.util.LinkedList;

public class AI {
	final static public int diceMaxValue = 6;
	final static public int earlyGame = 6;
	final static public int midGame = 11;

	public static void ai(Scorecard card, Hand hand) {
		LinkedList<Integer> emptyCategories = card.getEmptyCategories();
		int turn = 15 - emptyCategories.size() + 1;

		if (turn <= earlyGame ){
			EarlyStrategy.play(card, hand);
			//Lägg till hantering för nollning/lägg in tärning<3
			return;
		}

		if (turn <= midGame){
			MidStrategy.play(hand, card);
			return;
		}	
		EndStrategy.play(card, hand);
	}

	public static void evalScores(int[] diceValues, int[] scoreScore) {
		// poäng för lika värden.
		for (int i = 1; i <= diceMaxValue; i++) {
			int score = numberScore(diceValues, i);
			scoreScore[i - 1] = score;
		}

		scoreScore[Scorecard.pair] = pairScore(diceValues);
		//////Printer.printArray(scoreScore);
		scoreScore[Scorecard.twoPair] = doublePairScore(diceValues);
		//////Printer.printArray(scoreScore);
		scoreScore[Scorecard.threeOfAKind] = checkTripleScore(diceValues);
		//		////Printer.printArray(scoreScore);
		scoreScore[Scorecard.fourOfAKind] = checkQuadruopleScore(diceValues);
		//		////Printer.printArray(scoreScore);
		scoreScore[Scorecard.smallStraight] = smallStraightScore(diceValues);
		//		////Printer.printArray(scoreScore);
		scoreScore[Scorecard.largeStraight] = largeStraightScore(diceValues);
		//		////Printer.printArray(scoreScore);
		scoreScore[Scorecard.fullHouse] = fullHouseScore(diceValues);
		//		////Printer.printArray(scoreScore);
		scoreScore[Scorecard.chance] = chansScore(diceValues);
		//		////Printer.printArray(scoreScore);
		scoreScore[Scorecard.yatzy] = yatzyScore(diceValues);
		//		////Printer.printArray(scoreScore);

	}

	/**
	 * beräkna poäng för #of a kind. summerar poängen för de antal // tärningar
	 * som // har värdet number
	 **/
	public static int numberScore(int[] dices, int number) {
		int score = 0;
		for (int i : dices) {
			if (i == number) {
				score += i;
			}
		}
		return score;
	}

	// alla 6:or är för att det finns 6 olika värden på tärningar.
	public static int pairScore(int[] dices) {
		int[] valueTimes = new int[diceMaxValue];
		int[] scores = new int[diceMaxValue];

		// [0] håller poängen
		// [1] håller vilken valör det var som gav poängen
		int returning = 0;

		// räknar de olika valörerna
		countValues(dices, valueTimes);

		// beäkna poängen för de olika paren,
		// måste vara par
		for (int j = 0; j < diceMaxValue; j++) {
			if (valueTimes[j] == 2) {
				scores[j] = (j + 1) * 2;
			}
		}

		// beräkna vilken poäng som är störst.
		for (int k = 0; k < diceMaxValue; k++) {
			if (scores[k] >= returning) {
				returning = scores[k];
			}
		}
		return returning;
	}
	public static void countValues(int[] dices, int[] valueTimes) {
		for (int i : dices) {
			// simply add 1 to the corresponding place in the answer array
			valueTimes[i - 1]++;
		}
	}
	public static int doublePairScore(int[] dices) {
		int returning = 0;
		int[] valueTimes = new int[diceMaxValue];
		countValues(dices, valueTimes);
		boolean firstPair = false;

		int eyeCounter = 1;
		int firstPairEyes = 0;

		for (int i : valueTimes) {
			if (i >= 2 && !firstPair) {
				firstPairEyes = eyeCounter;
				firstPair = true; // we have found our first pair
			} else if (i >= 2 && firstPair) {
				return firstPairEyes * 2 + eyeCounter * 2;
			}
			eyeCounter++;
		}
		// if not 2 pair return 0
		return returning;
	}

	public static int checkTripleScore(int[] dices) {
		int trippleDice = 1;
		int[] valueTimes = new int[6];
		countValues(dices, valueTimes);
		for (int i : valueTimes) {
			if (i >= 3) { // we have three of this dice, dice points indicated
				// by trippleDice
				return trippleDice * 3;
			}
			trippleDice++;
		}
		return 0;
	}

	public static int checkQuadruopleScore(int[] dices) {
		int trippleDice = 1;
		int[] valueTimes = new int[6];
		countValues(dices, valueTimes);
		for (int i : valueTimes) {
			if (i >= 4) { // we have three of this dice, dice points indicated
				// by trippleDice
				return trippleDice * 4;
			}
			trippleDice++;
		}
		return 0;
	}

	public static int smallStraightScore(int[] hand) {
		int returning = 0;
		Arrays.sort(hand);
		boolean smallStraightTrue = true;
		for (int i = 0; i < 5; i++) {
			if (hand[i] != i + 1) {
				// This will only be false iff we dont have a small straight
				// since dice i should have i as score, 1 index as is custom
				// with board games
				smallStraightTrue = false;
			}
		}

		if (smallStraightTrue) {
			returning = 15;
		}
		return returning;
	}

	public static int largeStraightScore(int[] hand) {
		int returning = 0;
		boolean smallStraightTrue = true;
		for (int i = 0; i < 5; i++) {
			if (hand[i] != i + 2) {
				// This will only be false iff we dont have a small straight
				// since dice i should have i as score, 1 index as is custom
				// with board games
				smallStraightTrue = false;
			}
		}

		if (smallStraightTrue) {
			returning = 20;
		}
		return returning;
	}

	public static int fullHouseScore(int[] hand) {
		int returning = 0;
		int pairEyes = 0;
		int trippleEyes = 0;
		int[] counted = new int[6];
		countValues(hand, counted);

		for (int i = 0; i < counted.length; i++) {
			if (counted[i] == 2) {
				pairEyes = i + 1;
			}
			if (counted[i] == 3) {
				trippleEyes = i + 1;
			}
		}

		if (pairEyes != 0 && trippleEyes != 0) {
			returning = pairEyes * 2 + trippleEyes * 3;
		}
		return returning;
	}

	public static int chansScore(int[] hand) {
		int sum = 0;

		for (int i : hand) {
			sum += i;
		}
		return sum;
	}

	public static int yatzyScore(int[] hand) {
		int[] evaluated = new int[diceMaxValue];
		countValues(hand, evaluated);
		for (int i : evaluated) {
			if (i == 5) {
				return 50;
			}
		}
		return 0;
	}

	public static boolean catchHand(Hand hand, Scorecard card){
		LinkedList<Integer> freeScores = card.getEmptyCategories();
		////System.out.println(freeScores.toString());

		// start with check if we have a straight.
		int smallStraightScore = AI.smallStraightScore(hand.getHandArray());
		////Printer.printArray(hand.getValueArray());
		int bigStraightScore = AI.largeStraightScore(hand.getHandArray());
		int weHaveYaatzy = AI.yatzyScore(hand.getHandArray());

		if ((smallStraightScore != 0)
				&& (freeScores.contains(Scorecard.smallStraight))) {
			card.categories[Scorecard.smallStraight] = smallStraightScore;
			return true;
		}
		if ((bigStraightScore != 0)
				&& (freeScores.contains(Scorecard.largeStraight))) {
			card.categories[Scorecard.largeStraight] = bigStraightScore;
			return true;
		}

		if ((weHaveYaatzy != 0) && (freeScores.contains(Scorecard.yatzy))) {
			card.categories[Scorecard.yatzy] = weHaveYaatzy;
			return true;
		}
		return false;
	}

	public static boolean fullHouse(Scorecard card , Hand hand){
		int score = fullHouseScore(hand.getHandArray());
		if (score != 0 && card.categories[Scorecard.fullHouse] == -1){
			card.categories[Scorecard.fullHouse] = score;
			return true;
		}
		return false;
	}

}