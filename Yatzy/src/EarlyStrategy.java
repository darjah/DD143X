import java.util.LinkedList;

public class EarlyStrategy{
	public static void play(Scorecard card, Hand hand){
		int[] diceFrequency = new int[AI.diceMaxValue];
		diceFrequency = hand.diceFrequency(hand.getHandArray(), diceFrequency);
		
		//Första iterationen av att kolla vad som ska behållas och kasta om tärningarna där efter
		int valueToKeep = valueToKeep(card, hand, diceFrequency);
		GetCategories.allOfAKind(hand, valueToKeep);
		diceFrequency = hand.diceFrequency(hand.getHandArray(), diceFrequency);
		
		//Andra och sista iterationen
		valueToKeep = valueToKeep(card, hand, diceFrequency);
		GetCategories.allOfAKind(hand, valueToKeep);

		//Räkna ut slutgiltiga poängen för handen baserat på värdet vi satsade på
		int score = 0;
		for(int i : hand.getHandArray()){
			if(i == valueToKeep){
				score += i;
			}
		}

		//Fyll i protokollet
		card.categories[valueToKeep - 1] = score;
	}

	//Räknar ut vilket värde att satsa på och returnerar det
	public static int valueToKeep(Scorecard card, Hand hand, int[] diceFreq) {
		LinkedList<Integer> freeCategories = card.getEmptyCategories();
		int valueToKeep = -1;

		//Räknar ner från 6 frekvensen för varje tärning, om >=3 och inte i protokollet -> spara i valueToKeep och ret  
		for(int diceValueTemp = AI.diceMaxValue; diceValueTemp >= 1; diceValueTemp--){
			if((diceFreq[diceValueTemp-1] >= 3) && (freeCategories.contains(diceValueTemp-1))){
				valueToKeep = diceValueTemp;
				return valueToKeep;
			}
		}

		if(card.possibleToGetBonus()){
			//Om freq < 3 så finns det två fall
			//Över onPar, lägg i lägsta
			if(card.onPar()==1){
				for(int diceValueTemp = 1; diceValueTemp <= AI.diceMaxValue; diceValueTemp++){
					if(freeCategories.contains(diceValueTemp-1)){
						valueToKeep = diceValueTemp;
						return valueToKeep;
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