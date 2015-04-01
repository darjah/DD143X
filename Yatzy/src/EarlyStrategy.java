import java.util.LinkedList;

public class EarlyStrategy {
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

		//D� vi inte hittade ett vettigt v�rde (freq < 3)
		if(valueToKeep == -1){
			NullEntry.zeroDown(card);
			/*int[] evalScores = new int[card.categories.length];
			AI.evalScores(hand, evalScores);

			//F�nga k�k direkt
			if(AI.fullHouse(card, hand)){
				return;
			}*/
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

		//N�r vi har kastat om t�rningarna 3 ggr
		else{
			//Om >=3 och inte i protokollet -> spara i valueToKeep och ret  
			for(int diceValueTemp = AI.diceMaxValue; diceValueTemp >= 1; diceValueTemp--){
				if((diceFreq[diceValueTemp-1] >= 3) && (freeCategories.contains(diceValueTemp-1))){
					valueToKeep = diceValueTemp;
					return valueToKeep;
				}
			}
			
			return valueToKeep;
			
			//Om diceFreq < 3 men fortfarande kan f� bonusen, kolla om handen kan placeras i nerdre delen, annars nolla
			//if(card.possibleToGetBonus()){
				//TODO borde bara ret -1, kolla sedan i play om det �r m�jligt att f� bonuen eller inte och hantera p� samma s�tt som beskrivet nedan
				//�ver onPar, l�gg i l�gsta
				/*if(card.onPar()==1){
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
				}*/
				//TODO l�gg in koll f�r handen om den kan placeras i nedre del
				//TODO om inte, nolla nedre delen
			//}

			//D� vi inte l�ngre kan f� bonusen, kolla om handen kan l�ggas i nedre delen annars l�gg i det b�sta i �vre halvan
			//TODO l�gg in koll f�r handen om den kan placeras i nedre del
			/*int highestValue = 0;
			for(int diceValueTemp = AI.diceMaxValue; diceValueTemp > 0 ; diceValueTemp--){
				if((diceFreq[diceValueTemp-1]*diceValueTemp > highestValue) && (freeCategories.contains(diceValueTemp-1))){
					highestValue = diceFreq[diceValueTemp-1]*diceValueTemp;
					valueToKeep = diceValueTemp;
				}
			}*/
		}
	}
}