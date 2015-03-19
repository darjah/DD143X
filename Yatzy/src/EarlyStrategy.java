import java.util.*;

public class EarlyStrategy {
	/**
	 * calculates what value should be saved, does not consider straights since straight on first throw 
	 * @param card
	 * @param diceValueFrequency an int[AI.diceMaxValue] were each position holds an int representing the number of dices having that value
	 * @return 
	 */
	public static int play(Scorecard card, Hand hand) {
		LinkedList<Integer> freeCategories = card.getEmptyCategories();
		int valueToKeep = -1;
		int[] diceFrequency = new int[AI.diceMaxValue];
		diceFrequency = hand.diceFrequency(hand.getHandArray(), diceFrequency);

		//Räknar ner från 6 frekvensen för varje tärning, om >=3 och inte i protokollet -> lägg i  
		for (int diceValueTemp = AI.diceMaxValue; diceValueTemp >= 1; diceValueTemp--) {
			if ((diceFrequency[diceValueTemp-1] >= 3) && (freeCategories.contains(diceValueTemp-1))) {
				valueToKeep = diceValueTemp;
				return diceValueTemp;
			}
		}

		if(card.possibleToGetBonus()){
			//Om freq < 3 så finns det två fall
			//Över onPar, lägg i lägsta
			if (card.onPar()==1){

			}
			//Om onPar, lägg i lägsta
			if (card.onPar()==0){

			}
			//Ej onPar, offra nedre delen av protokollet
			else {
				//Fixa prioritetslista för vad som ska offras först
			}
		}
		//När vi inte längre kan få bonusen, använd resterande 1-6 som nollbrickor
		else
		{
			//MidStrategy.play();
		}
		return valueToKeep;
	}
}