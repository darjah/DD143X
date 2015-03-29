import java.util.LinkedList;

public class EarlyStrategy{
	public static int play(Scorecard card, Hand hand){
		LinkedList<Integer> freeCategories = card.getEmptyCategories();
		int valueToKeep = -1;
		int[] diceFrequency = new int[AI.diceMaxValue];
		diceFrequency = hand.diceFrequency(hand.getHandArray(), diceFrequency);

		//R�knar ner fr�n 6 frekvensen f�r varje t�rning, om >=3 och inte i protokollet -> l�gg i  
		for(int diceValueTemp = AI.diceMaxValue; diceValueTemp >= 1; diceValueTemp--){
			if((diceFrequency[diceValueTemp-1] >= 3) && (freeCategories.contains(diceValueTemp-1))){
				valueToKeep = diceValueTemp;
				return diceValueTemp;
			}
		}

		if(card.possibleToGetBonus()){
			//Om freq < 3 s� finns det tv� fall
			//�ver onPar, l�gg i l�gsta
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
}