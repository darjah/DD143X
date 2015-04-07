import java.util.LinkedList;

public class EarlyStrategy {
	public static void play(Scorecard card, Hand hand){
		LinkedList<Integer> emptyCategories = card.getEmptyCategories();
		int[] evalScores = new int[card.categories.length];
		int[] diceFrequency = new int[AI.diceMaxValue];
		diceFrequency = hand.diceFrequency(hand.getHandArray(), diceFrequency);

		//F�rsta iterationen av att kolla vad som ska beh�llas och kasta om t�rningarna d�r efter
		int valueToKeep = valueToKeep(card, hand, diceFrequency);
		GetCategories.allOfAKind(hand, valueToKeep);
		diceFrequency = hand.diceFrequency(hand.getHandArray(), diceFrequency);

		//Andra och sista iterationen
		valueToKeep = valueToKeep(card, hand, diceFrequency);
		GetCategories.allOfAKind(hand, valueToKeep);
		diceFrequency = hand.diceFrequency(hand.getHandArray(), diceFrequency);

		//D� vi inte hittade ett vettigt v�rde (freq < 3)
		if(valueToKeep == -1){
			//Om vi fortfarande kan f� bonusen, kolla om handen kan placeras i nerdre delen, annars nolla
			if(card.possibleToGetBonus()){
				//TODO l�gg in koll f�r handen om den kan placeras i nedre del
				AI.evalScores(hand, evalScores);
				
				//Kollar om vi kan g�ra n�t med handen �nd�
				if(canWeDoAnythingWithThisHand(card, hand, evalScores, emptyCategories)){
					return;
				}
				//testkommentar f�r att se opm git funkar
				//I v�rsta fall, nolla
				NullEntry.nullEntry(card);
				return;
			}
			//D� vi inte l�ngre kan f� bonusen, kolla om handen kan l�ggas i nedre delen annars l�gg i det b�sta i �vre halvan
			else{
				//Kollar om vi kan g�ra n�t med handen �nd�
				if(canWeDoAnythingWithThisHand(card, hand, evalScores, emptyCategories)){
					return;
				}
				//Kollar om vi kan l�gga det b�sta v�rdet i �vre halvan �nd�
				else if(emptyCategories.contains(0) || emptyCategories.contains(1) || emptyCategories.contains(2) || emptyCategories.contains(3) || emptyCategories.contains(5) || emptyCategories.contains(5)){
					int highestValue = 0;
					for(int diceValueTemp = AI.diceMaxValue; diceValueTemp > 0 ; diceValueTemp--){
						if((diceFrequency[diceValueTemp-1]*diceValueTemp > highestValue) && (emptyCategories.contains(diceValueTemp-1))){
							highestValue = diceFrequency[diceValueTemp-1]*diceValueTemp;
						}
					}
					card.categories[valueToKeep - 1] = highestValue;
				}
				//I v�rsta fall, nolla
				else{
					NullEntry.nullEntry(card);
				}
			}
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
		LinkedList<Integer> emptyCategories = card.getEmptyCategories();
		int valueToKeep = -1;

		//F�rsta och andra kastet
		if(hand.getRoll() < 3){
			//Satsar p� den h�sta frekvensen p� t�rningarna 
			int highestFreq = 0;
			for(int diceValueTemp = AI.diceMaxValue; diceValueTemp > 0; diceValueTemp--){
				if((diceFreq[diceValueTemp-1] > highestFreq) && (emptyCategories.contains(diceValueTemp-1))){
					highestFreq = diceFreq[diceValueTemp-1];
					valueToKeep = diceValueTemp;
				}
			}
			return valueToKeep;
		}

		//N�r vi har kastat om t�rningarna 3 ggr
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
		//Kollar efter k�k
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
		
		//F�ngade inte k�ken/har redan f�ngat den men kan placera tv�Par om den �r ledig
		else if(evalScores[Scorecard.twoPair] != 0 && emptyCategories.contains(Scorecard.twoPair)){
			card.categories[Scorecard.twoPair] = evalScores[Scorecard.twoPair];
			return true;
		}

		//F�ngade inte k�ken/har redan f�ngat den men kan fylla par (par i 4 minst)
		else if(evalScores[Scorecard.pair] != 0 && emptyCategories.contains(Scorecard.pair)){
			card.categories[Scorecard.pair] = evalScores[Scorecard.pair];
			return true;
		}
		return false;
	}
}