import java.util.LinkedList;

public class EarlyStrategy {
	public static void play(Scorecard card, Hand hand){
		LinkedList<Integer> emptyCategories = card.getEmptyCategories();
		int[] evalScores = new int[card.categories.length];
		int[] diceFrequency = new int[AI.diceMaxValue];
		diceFrequency = hand.diceFrequency(hand.getHandArray(), diceFrequency);

		//Första iterationen av att kolla vad som ska behållas och kasta om tärningarna där efter
		int valueToKeep = valueToKeep(card, hand, diceFrequency);
		GetCategories.allOfAKind(hand, valueToKeep);
		diceFrequency = hand.diceFrequency(hand.getHandArray(), diceFrequency);

		//Andra och sista iterationen
		valueToKeep = valueToKeep(card, hand, diceFrequency);
		GetCategories.allOfAKind(hand, valueToKeep);
		diceFrequency = hand.diceFrequency(hand.getHandArray(), diceFrequency);

		//Då vi inte hittade ett vettigt värde (freq < 3)
		if(valueToKeep == -1){
			//Om vi fortfarande kan få bonusen, kolla om handen kan placeras i nerdre delen, annars nolla
			if(card.possibleToGetBonus()){
				//TODO lägg in koll för handen om den kan placeras i nedre del
				AI.evalScores(hand, evalScores);
				
				//Kollar om vi kan göra nåt med handen ändå
				if(canWeDoAnythingWithThisHand(card, hand, evalScores, emptyCategories)){
					return;
				}
				
				//I värsta fall, nolla
				NullEntry.nullEntry(card);
				return;
			}
			//Då vi inte längre kan få bonusen, kolla om handen kan läggas i nedre delen annars lägg i det bästa i övre halvan
			else{
				//Kollar om vi kan göra nåt med handen ändå
				if(canWeDoAnythingWithThisHand(card, hand, evalScores, emptyCategories)){
					return;
				}
				//Kollar om vi kan lägga det bästa värdet i övre halvan ändå
				else if(emptyCategories.contains(0) || emptyCategories.contains(1) || emptyCategories.contains(2) || emptyCategories.contains(3) || emptyCategories.contains(5) || emptyCategories.contains(5)){
					int highestValue = 0;
					for(int diceValueTemp = AI.diceMaxValue; diceValueTemp > 0 ; diceValueTemp--){
						if((diceFrequency[diceValueTemp-1]*diceValueTemp > highestValue) && (emptyCategories.contains(diceValueTemp-1))){
							highestValue = diceFrequency[diceValueTemp-1]*diceValueTemp;
						}
					}
					card.categories[valueToKeep - 1] = highestValue;
				}
				//I värsta fall, nolla
				else{
					NullEntry.nullEntry(card);
				}
			}
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
		LinkedList<Integer> emptyCategories = card.getEmptyCategories();
		int valueToKeep = -1;

		//Första och andra kastet
		if(hand.getRoll() < 3){
			//Satsar på den hösta frekvensen på tärningarna 
			int highestFreq = 0;
			for(int diceValueTemp = AI.diceMaxValue; diceValueTemp > 0; diceValueTemp--){
				if((diceFreq[diceValueTemp-1] > highestFreq) && (emptyCategories.contains(diceValueTemp-1))){
					highestFreq = diceFreq[diceValueTemp-1];
					valueToKeep = diceValueTemp;
				}
			}
			return valueToKeep;
		}

		//När vi har kastat om tärningarna 3 ggr
		else{
			//Om freq >=3 och inte i protokollet -> spara i valueToKeep och ret  
			for(int diceValueTemp = AI.diceMaxValue; diceValueTemp > 0; diceValueTemp--){
				if((diceFreq[diceValueTemp-1] >= 3) && (emptyCategories.contains(diceValueTemp-1))){
					valueToKeep = diceValueTemp;
					return valueToKeep;
				}
			}
			//Returnera annars -1
			return valueToKeep;		
		}
	}
	
	public static boolean canWeDoAnythingWithThisHand(Scorecard card, Hand hand, int[] evalScores, LinkedList<Integer> emptyCategories){
		//Kollar efter kåk
		if(AI.fullHouse(card, hand)){
			return true;
		}
		
		//Kollar efter yatzy, liten och storstege
		else if(AI.catchHand(hand, card)){
			return true;
		}
		
		else if(evalScores[Scorecard.fourOfAKind] != 0 && emptyCategories.contains(Scorecard.fourOfAKind)){
			card.categories[Scorecard.fourOfAKind] = evalScores[Scorecard.fourOfAKind];
			return true;
		}
		
		else if(evalScores[Scorecard.threeOfAKind] != 0 && emptyCategories.contains(Scorecard.threeOfAKind)){
			card.categories[Scorecard.threeOfAKind] = evalScores[Scorecard.threeOfAKind];
			return true;
		}
		
		//Fångade inte kåken/har redan fångat den men kan placera tvåPar om den är ledig
		else if(evalScores[Scorecard.twoPair] != 0 && emptyCategories.contains(Scorecard.twoPair)){
			card.categories[Scorecard.twoPair] = evalScores[Scorecard.twoPair];
			return true;
		}

		//Fångade inte kåken/har redan fångat den men kan fylla par (par i 4 minst)
		else if(evalScores[Scorecard.pair] != 0 && emptyCategories.contains(Scorecard.pair)){
			card.categories[Scorecard.pair] = evalScores[Scorecard.pair];
			return true;
		}
		return false;
	}
}