//Part of the AI to caluculate which dices to rethrow and rethows them
public class GetCategories {
	//rethrows every dice in @hand that doesnt have the value @value
	public static void allOfAKind(Hand hand, int value){
		for(Dice dice : hand.getDices()){
			if(dice.faceValue != value){
				dice.throwDice();
			}
		}
		hand.rollCounter();
	}

	/** rethrow a hand that has pair, tripple or quardruple to the higest value if there are more then two pairs.
	 */
	public static void nrDices(Hand hand, int nrDices) {
		int[] countedDices = new int[6];
		for (Dice dice : hand.getDices()) {
			countedDices[dice.getDiceValue() - 1]++;
		}

		int valueToKeep = 1;

		for (int i = 0; i < 6; i++) {
			if (countedDices[i] == nrDices) {
				valueToKeep = i + 1;
			}
		}

		for (Dice dice : hand.getDices()) {
			if (dice.faceValue != valueToKeep) {
				dice.throwDice();
			}
		}
		hand.rollCounter();
	}

	/**
	 * requires a twoPair hand in to work correctly
	 */
	public static void twoPair(Hand hand) {
		int[] diceFreq = new int [AI.diceMaxValue];
		diceFreq = hand.diceFrequency(hand.getHandArray(), diceFreq);
				
		int valueToRethrow = 1;
		for (int i = 0; i < diceFreq.length; i++) {
			if (diceFreq[i] == 1) {
				valueToRethrow = i + 1;
			}
		}

		for (Dice dice : hand.getDices()) {
			if (dice.faceValue == valueToRethrow) {
				dice.throwDice();
			}
		}

		hand.rollCounter();
	}

	/**rethrows every surplus copy of a dice value in order to get a straight
	 **/
	public static void largeStraight(Hand hand) {
		boolean[] haveThisValue = { false, false, false, false, false, false };
		for (Dice dice : hand.getDices()) {
			if (haveThisValue[dice.getDiceValue() - 1] || dice.faceValue == 1) {
				dice.throwDice();
			} else {
				haveThisValue[dice.getDiceValue() - 1] = true;
			}
		}
		hand.rollCounter();

	}

	/**
	 * rethrows every surplus copy of a dice value in order to get a straight
	 */
	public static void smallStraight(Hand hand) {
		boolean[] haveThisValue = { false, false, false, false, false, false };
		for (Dice dice : hand.getDices()) {
			if (haveThisValue[dice.getDiceValue() - 1] || dice.faceValue == 6) {
				dice.throwDice();
			} else {
				haveThisValue[dice.getDiceValue() - 1] = true;
			}
		}
		hand.rollCounter();

	}

	/**
	 * Whatto do in case of full house on first or second throw. needs to know
	 * the status of the scorecard to do correct decission
	 */
	public static void fullHouse(Scorecard card, Hand hand) {
		int roll = hand.getRoll();

		int[] countedDice = new int[AI.diceMaxValue];
		int weHaveThree = 0;
		for (int i = 0; i < countedDice.length; i++) {
			if (countedDice[i] == 3) {
				weHaveThree = i + 1;
			}
		}

		if (weHaveThree == 0) {
			throw new IllegalArgumentException(
					"tried fullhouse rethrow without a tripple");
		}

		boolean upperFilled = false;
		boolean fourFilled = false;

		if (card.categories[weHaveThree - 1] != 0) {
			upperFilled = true;
		}

		if (card.categories[Scorecard.fourOfAKind] != 0) {
			fourFilled = true;
		}

		AI.evalScores(hand, countedDice);
		if (roll == 1) {
			if (!upperFilled) {
				for (Dice dice : hand.getDices()) {
					if (dice.faceValue != weHaveThree) {
						dice.throwDice();
					}
				}
			}

			if (upperFilled && !fourFilled) {
				for (Dice dice : hand.getDices()) {
					if (dice.faceValue != weHaveThree) {
						dice.throwDice();
					}
				}
			}

		}

		if (roll == 2) {
			if (!upperFilled && weHaveThree >= 4) {
				for (Dice dice : hand.getDices()) {
					if (dice.faceValue != weHaveThree) {
						dice.throwDice();
					}
				}
			}
		}

		hand.rollCounter();
	}

	public static void twoPairToHouse(Hand hand) {
		int i = 0;
		int j = 0;

		int[] diceFreq = new int [AI.diceMaxValue];
		diceFreq = hand.diceFrequency(hand.getHandArray(), diceFreq);

		for (int c = 0; c < diceFreq.length; c++) {
			if (diceFreq[c] == 2) {
				if (i == 0) {
					i = c + 1;
				} else {
					j = c + 1;
				}
			}
		}

		for (Dice dice : hand.getDices()) {
			if (dice.faceValue != i && dice.faceValue != j) {
				dice.throwDice();
			}
		}
		hand.rollCounter();
	}

	public static void getFullHouse(Hand hand) {
		int[] diceFreq = new int [AI.diceMaxValue];
		diceFreq = hand.diceFrequency(hand.getHandArray(), diceFreq);
		
		int valueToKeep;
		int trissScore = AI.threeOfAKindScore(hand);
		if (trissScore != 0) {
			valueToKeep = trissScore / 3;
			int otherValue = 0;
			for (int e = 5; e >= 2; e--) {
				if (diceFreq[e] == 1) {
					otherValue = e + 1;
					break;
				}
			}
			for (Dice dice : hand.getDices()) {
				if (dice.faceValue != valueToKeep && dice.faceValue != otherValue) {
					dice.throwDice();
				}
			}
			hand.rollCounter();
			return;

		}

		if (AI.twoPairScore(hand) != 0) {
			twoPairToHouse(hand);
			return;
		}

		int pairScore = AI.pairScore(hand);
		if (pairScore != 0) {
			valueToKeep = pairScore / 2;
			int otherValue = 0;
			for (int e = 5; e >= 2; e--) {
				if (diceFreq[e] == 1) {
					otherValue = e + 1;
					break;
				}
			}
			for (Dice dice : hand.getDices()) {
				if (dice.faceValue != valueToKeep && dice.faceValue != otherValue) {
					dice.throwDice();
				}
			}
			hand.rollCounter();
			return;
		}

		int i = 0;
		int j = 0;
		for (int c = diceFreq.length - 1; c >= 0; c--) {
			if (diceFreq[c] == 1) {
				if (i == 0) {
					i = c + 1;
				} else {
					j = c + 1;
					break;
				}
			}
		}

		for (Dice dice : hand.getDices()) {
			if (dice.faceValue != i && dice.faceValue != j) {
				dice.throwDice();
			}
		}
		hand.rollCounter();
	}

	public static void getTwoPair(Hand hand) {
		int[] diceFreq = new int [AI.diceMaxValue];
		diceFreq = hand.diceFrequency(hand.getHandArray(), diceFreq);

		int keep1 = 0;
		int keep2 = 0;

		for (int i = 5; i >= 0; i--) {
			if (diceFreq[i] > 0) {
				if (keep1 == 0) {
					keep1 = i + 1;
				} else if (keep2 == 0) {
					keep2 = i + 1;
				}

				if (keep1 != 0 && keep2 != 0) {
					if (keep1 > keep2) {
						if (diceFreq[keep2 - 1] < diceFreq[i]) {
							keep2 = i + 1;
						}
					} else {
						if (diceFreq[keep1 - 1] < diceFreq[i]) {
							keep1 = i + 1;
						}
					}
				}
			}
		}

		for (Dice dice : hand.getDices()){
			if(dice.faceValue != keep1 && keep2 != dice.faceValue){
				//	System.out.println("kastar om: "+ dice.value);
				dice.throwDice();
			}
			if (diceFreq[dice.faceValue -1] > 2){
				diceFreq[dice.faceValue-1] --;
				//System.out.println("kastar om: "+ dice.value);
				dice.throwDice();
			}

		}	
		hand.rollCounter();
	}
}