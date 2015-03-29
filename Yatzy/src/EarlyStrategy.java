import java.util.LinkedList;

public class EarlyStrategy{
	public static int play(Scorecard card, Hand hand){
		LinkedList<Integer> freeCategories = card.getEmptyCategories();
		int valueToKeep = -1;
		int[] diceFrequency = new int[AI.diceMaxValue];
		diceFrequency = hand.diceFrequency(hand.getHandArray(), diceFrequency);

		//Räknar ner från 6 frekvensen för varje tärning, om >=3 och inte i protokollet -> lägg i  
		for(int diceValueTemp = AI.diceMaxValue; diceValueTemp >= 1; diceValueTemp--){
			if((diceFrequency[diceValueTemp-1] >= 3) && (freeCategories.contains(diceValueTemp-1))){
				valueToKeep = diceValueTemp;
				return diceValueTemp;
			}
		}

		if(card.possibleToGetBonus()){
			//Om freq < 3 så finns det två fall
			//Över onPar, lägg i lägsta
			if(card.onPar()==1){
				for(int diceValueTemp = 0; diceValueTemp < AI.diceMaxValue; diceValueTemp++){
					if(freeCategories.contains(diceValueTemp)){
						valueToKeep = diceValueTemp;
						return diceValueTemp;
					}
				}

			}

			//Ej onPar eller onPar, offra nedre delen av protokollet
			else if(card.onPar()==0 || card.onPar()==-1){
				NullEntry.zeroDown(card);
			}
		}
		else{
			NullEntry.zeroUp(card);
		}
		return valueToKeep;
	}
	public static void play(Hand hand, Scorecard card) {
		LinkedList<Integer> freeScores = card.getEmptyCategories();
		int[] tempScore = new int[15];

		int[] diceFrequency = new int[AI.diceMaxValue];
		diceFrequency = hand.diceFrequency(hand.getHandArray(), diceFrequency);

		// we start with 3 or 4 of a kind and we havnt filled that value
		// throws 2 more times to collect those and fills in the score card
		// if we happen upon a straight or yatze that is filled in.

		int valueToKeep = valueToKeep(card, diceFrequency);
		
		GetCategories.allOfAKind(hand, valueToKeep);
		AI.evalScores(hand, tempScore);
		diceFrequency = hand.diceFrequency(hand.getHandArray(), diceFrequency);

		// this is done 2 times, no more no less, so no own method
		valueToKeep = valueToKeep(card, diceFrequency);
		GetCategories.allOfAKind(hand, valueToKeep);
		AI.evalScores(hand, tempScore);

		int[] howManyDoWeHave = new int[AI.diceMaxValue];
		diceFrequency = hand.diceFrequency(hand.getHandArray(), diceFrequency);
		if (freeScores.contains(Scorecard.fourOfAKind) && !freeScores.contains(valueToKeep -1) && (valueToKeep > 3)){

			if (howManyDoWeHave[valueToKeep -1] >= 4){
				card.categories[Scorecard.fourOfAKind] = 4*valueToKeep;
				return;
			}
		}

		//större än 3, större än indexet för 3 på tärningarna
		int highestPair = 0;
		boolean weHaveMore = false;
		for(int c = howManyDoWeHave.length-1; c  >=0; c --){
			if (howManyDoWeHave[c] == 2){
				if (highestPair ==0){
					highestPair = c+1;
				}
			}
			if (howManyDoWeHave[c] > 2){
				weHaveMore = true;
			}
		}


		if (highestPair >= 4 && !weHaveMore && freeScores.contains(Scorecard.pair)){
			card.categories[Scorecard.pair] = highestPair*2;
			return;
		}

		// calc score to set
		int score = 0;
		for (int i : hand.getHandArray()) {
			if (i == valueToKeep) {
				score += i;
			}
		}

		// fill in the one that should now have 3, 4 or 5 of a kind
		card.categories[valueToKeep - 1] = score;
	}

	/**
	 * calculates what value should be saved, does not consider straights since straight on first throw 
	 * should alredy have been caught, fullHous is split upp
	 * @param card
	 * @param countedDices an int[AI.diceMaxValue] were each position holds an int representing the number of dices having that value
	 * @return the value to go for
	 */
	public static int valueToKeep(Scorecard card, int[] diceFreq) {
		int valueToKeep = -1;

		LinkedList<Integer> freeScores = card.getEmptyCategories();
		// find 3 or 4 of a kind and set so aim for that if not already filled
		for (int diceValueTemp = AI.diceMaxValue; diceValueTemp >= 1; diceValueTemp--) {
			if ((diceFreq[diceValueTemp - 1] >= 3) && (freeScores.contains(diceValueTemp - 1))) {
				valueToKeep = diceValueTemp;
				return diceValueTemp;
			}
		}

		// catch pair or broken straight and decide what to go for
		// starights already caught
		if (valueToKeep == -1) {
			for (int isThisARealNumber = diceFreq.length - 1; isThisARealNumber >= 0; isThisARealNumber--) {
				if ((diceFreq[isThisARealNumber] == 2) && (card.categories[isThisARealNumber] == -1)) {
					return isThisARealNumber+1;
				}
			}

			for (int anotherFakkingInt = AI.diceMaxValue; anotherFakkingInt >= 1; anotherFakkingInt--) {
				if (freeScores.contains(anotherFakkingInt - 1) && (diceFreq[anotherFakkingInt - 1] != 0)) {
					valueToKeep = anotherFakkingInt;
					return anotherFakkingInt;
				}
			}
		}

		for(int q = 1; q <=AI.diceMaxValue ; q++){
			if (card.categories[q-1] == -1){
				return q;
			}
		}
		return 0;
	}
}