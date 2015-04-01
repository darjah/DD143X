import java.util.LinkedList;

public class EarlyStrategy {
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

		//Då vi inte hittade ett vettigt värde (freq < 3)
		if(valueToKeep == -1){
			NullEntry.zeroDown(card);
			/*int[] evalScores = new int[card.categories.length];
			AI.evalScores(hand, evalScores);

			//Fånga kåk direkt
			if(AI.fullHouse(card, hand)){
				return;
			}*/
		}
		
		//Räkna ut slutgiltiga poängen för handen baserat på värdet vi satsade på
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

	//Räknar ut vilket värde att satsa på och returnerar det
	public static int valueToKeep(Scorecard card, Hand hand, int[] diceFreq) {
		LinkedList<Integer> freeCategories = card.getEmptyCategories();
		int valueToKeep = -1;

		//Första och andra kastet
		if(hand.getRoll() < 3){
			//Satsar på den hösta frekvensen på tärningarna 
			int highestFreq = 0;
			for(int diceValueTemp = AI.diceMaxValue; diceValueTemp > 0 ; diceValueTemp--){
				if((diceFreq[diceValueTemp-1] > highestFreq) && (freeCategories.contains(diceValueTemp-1))){
					highestFreq = diceFreq[diceValueTemp-1];
					valueToKeep = diceValueTemp;
				}
			}
			return valueToKeep;
		}

		//När vi har kastat om tärningarna 3 ggr
		else{
			//Om >=3 och inte i protokollet -> spara i valueToKeep och ret  
			for(int diceValueTemp = AI.diceMaxValue; diceValueTemp >= 1; diceValueTemp--){
				if((diceFreq[diceValueTemp-1] >= 3) && (freeCategories.contains(diceValueTemp-1))){
					valueToKeep = diceValueTemp;
					return valueToKeep;
				}
			}
			
			return valueToKeep;
			
			//Om diceFreq < 3 men fortfarande kan få bonusen, kolla om handen kan placeras i nerdre delen, annars nolla
			//if(card.possibleToGetBonus()){
				//TODO borde bara ret -1, kolla sedan i play om det är möjligt att få bonuen eller inte och hantera på samma sätt som beskrivet nedan
				//Över onPar, lägg i lägsta
				/*if(card.onPar()==1){
					for(int diceValueTemp = 1; diceValueTemp <= AI.diceMaxValue; diceValueTemp++){
						if(freeCategories.contains(diceValueTemp-1)){
							valueToKeep = diceValueTemp;
							return valueToKeep;
						}
					}
				}

				//Ej onPar eller onPar men fortfarande kan få bonusen, offra nedre delen av protokollet
				else if(card.onPar()==0 || card.onPar()==-1){
					return valueToKeep;
				}*/
				//TODO lägg in koll för handen om den kan placeras i nedre del
				//TODO om inte, nolla nedre delen
			//}

			//Då vi inte längre kan få bonusen, kolla om handen kan läggas i nedre delen annars lägg i det bästa i övre halvan
			//TODO lägg in koll för handen om den kan placeras i nedre del
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