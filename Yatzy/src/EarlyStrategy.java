import java.util.LinkedList;

public class EarlyStrategy{
	public static void play(Scorecard card, Hand hand){
		int[] diceFrequency = new int[AI.diceMaxValue];
		diceFrequency = hand.diceFrequency(hand.getHandArray(), diceFrequency);

		//F�rsta iterationen av att kolla vad som ska beh�llas och kasta om t�rningarna d�r efter
		int valueToKeep = valueToKeep(card, hand, diceFrequency);
		GetCategories.allOfAKind(hand, valueToKeep);
		diceFrequency = hand.diceFrequency(hand.getHandArray(), diceFrequency);

		//Andra och sista iterationen
		valueToKeep = valueToKeep(card, hand, diceFrequency);
		GetCategories.allOfAKind(hand, valueToKeep);

		//D� vi fortfarande kan f� bonusen men minst ligger onPar
		if(valueToKeep == -1){
			NullEntry.zeroDown(card);
		}
		//R�kna ut slutgiltiga po�ngen f�r handen baserat p� v�rdet vi satsade p�
		else{
			int score = 0;
			for(int i : hand.getHandArray()){
				if(i == valueToKeep){
					score += i;
				}
			}

			//Fyll i protokollet
			card.categories[valueToKeep - 1] = score;
		}
	}

	//R�knar ut vilket v�rde att satsa p� och returnerar det
	public static int valueToKeep(Scorecard card, Hand hand, int[] diceFreq) {
		LinkedList<Integer> freeCategories = card.getEmptyCategories();
		int valueToKeep = -1;

		//F�rsta och andra kastet
		if(hand.getRoll() < 3){
			//Satsar p� den h�sta frekvensen p� t�rningarna 
			int highestFreq = 0;
			for(int diceValueTemp = AI.diceMaxValue; diceValueTemp > 0 ; diceValueTemp--){
				if((diceFreq[diceValueTemp-1] > highestFreq) && (freeCategories.contains(diceValueTemp-1))){
					highestFreq = diceFreq[diceValueTemp-1];
					valueToKeep = diceValueTemp;
				}
			}
			return valueToKeep;
		}
		
		//N�r vi kastar om t�rningarna den sista g�ngen
		else{
			//Om >=3 och inte i protokollet -> spara i valueToKeep och ret  
			for(int diceValueTemp = AI.diceMaxValue; diceValueTemp >= 1; diceValueTemp--){
				if((diceFreq[diceValueTemp-1] >= 3) && (freeCategories.contains(diceValueTemp-1))){
					valueToKeep = diceValueTemp;
					return valueToKeep;
				}
			}
			
			//Om diceValue < 3 men fortfarande kan f� bonusen
			if(card.possibleToGetBonus()){
				//�ver onPar, l�gg i l�gsta
				if(card.onPar()==1){
					for(int diceValueTemp = 1; diceValueTemp <= AI.diceMaxValue; diceValueTemp++){
						if(freeCategories.contains(diceValueTemp-1)){
							valueToKeep = diceValueTemp;
							return valueToKeep;
						}
					}
				}

				//Ej onPar eller onPar men fortfarande kan f� bonusen, offra nedre delen av protokollet
				else if(card.onPar()==0 || card.onPar()==-1){
					return valueToKeep;
				}
			}

			//D� vi inte l�ngre kan f� bonusen
			int highestFreq = 0;
			for(int diceValueTemp = AI.diceMaxValue; diceValueTemp > 0 ; diceValueTemp--){
				if((diceFreq[diceValueTemp-1] > highestFreq) && (freeCategories.contains(diceValueTemp-1))){
					highestFreq = diceFreq[diceValueTemp-1];
					valueToKeep = diceValueTemp;
				}
			}
			return valueToKeep;
		}
	}
}