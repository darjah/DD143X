import java.util.LinkedList;

public class MidStrategy {
	public static void play(Scorecard card, Hand hand){
		//Fånga liten/stor stege eller yatzy direkt
		boolean checked = AI.catchHand(hand, card);
		if(checked){
			return;
		}
		
		//0 if the player is on par
		//else the player is over par
		if(card.onPar() == 0){
			onPar(card, hand);
			return;
		} 
		else{
			overPar(card, hand);
			return;
		}
	}

	public static void onPar(Scorecard card, Hand hand){
		LinkedList<Integer> emptyCategories = card.getEmptyCategories();

		if(AI.catchHand(hand, card)){
			return;
		}

		if(stegCheck(hand)){
			stegeKolla(card, hand, emptyCategories);
		}

		int betOn = uppCheck(card, hand);

		GetCategories.allOfAKind(hand, betOn);
		if(AI.catchHand(hand, card)){
			return;
		}
		if(AI.fullHouse(card, hand)){
			return;
		}

		GetCategories.allOfAKind(hand, betOn);

		allOfAKindDefensive(card, hand, betOn);
	}

	public static void overPar(Scorecard card, Hand hand) {
		agressive(card, hand);
	}

	public static void agressive(Scorecard card, Hand hand){
		//Hämta lediga kategorier
		LinkedList<Integer> freeCategories = card.getEmptyCategories();
		int[] evalScores = new int[card.categories.length];
		AI.evalScores(hand, evalScores);

		//Fånga kåk direkt
		if(AI.fullHouse(card, hand)){
			return;
		}

		//Om vi har två par och är ledig eller kåk är ledig, försök få en kåk
		if(evalScores[Scorecard.twoPair] != 0 && (freeCategories.contains(Scorecard.twoPair) || freeCategories.contains(Scorecard.fullHouse))){
			twoPairMid(card, hand, freeCategories, evalScores);
			return;
		}

		//Om vi har början på en stege (saknar endast en tärning)
		if(stegCheck(hand)){
			stegeKolla(card, hand, freeCategories);
			return;
		}

		int keep = betOnInt(card, hand);

		GetCategories.allOfAKind(hand, keep);
		if(AI.catchHand(hand, card)){
			return;
		}

		if(AI.fullHouse(card, hand)){
			return;
		}

		GetCategories.allOfAKind(hand, keep);
		allOfAKindAgressive(card, hand, keep);
	}

	public static int uppCheck(Scorecard card, Hand hand){
		LinkedList<Integer> emptyCategories = card.getEmptyCategories();
		int[] diceFreq = new int [AI.diceMaxValue];
		diceFreq = hand.diceFrequency(hand.getHandArray(), diceFreq);
		int keepThisValue = 0;
		int highestFreq = 0;
		for(int diceValueTemp = AI.diceMaxValue; diceValueTemp > 0; diceValueTemp--){
			if((diceFreq[diceValueTemp-1] > highestFreq) && (emptyCategories.contains(diceValueTemp-1))){
				highestFreq = diceFreq[diceValueTemp-1];
				keepThisValue = diceValueTemp;
			}
		}
		return keepThisValue;
	}

	//Används då vi har två par i handen och vill satsa på en kåk
	public static void twoPairMid(Scorecard card, Hand hand, LinkedList<Integer> freeCategories, int[] evalScores){
		GetCategories.twoPairToFullHouse(hand);
		AI.evalScores(hand, evalScores);

		//Fånga kåken
		if(AI.fullHouse(card, hand)){
			return;
		}

		GetCategories.twoPairToFullHouse(hand);
		AI.evalScores(hand, evalScores);

		//Fånga kåken
		if(AI.fullHouse(card, hand)){
			return;
		}

		//Fångade inte kåken/har redan fångat den men kan placera tvåPar om den är ledig
		if(freeCategories.contains(Scorecard.twoPair)){
			card.categories[Scorecard.twoPair] = evalScores[Scorecard.twoPair];
			return;
		}

		//Fångade inte kåken/har redan fångat den men kan fylla triss
		if(evalScores[Scorecard.threeOfAKind] != 0 && freeCategories.contains(Scorecard.threeOfAKind)){
			card.categories[Scorecard.threeOfAKind] = evalScores[Scorecard.threeOfAKind];
			return;
		}
		
		//Fångade inte kåken/har redan fångat den men kan fylla par (par i 4 minst)
		if(evalScores[Scorecard.pair] >= 8 && freeCategories.contains(Scorecard.pair)){
			card.categories[Scorecard.pair] = evalScores[Scorecard.pair];
			return;
		}
		
		//Ingen av ovastående kategorier var lediga, fyll i den som är ledig
		for(int d = 0; d < 6; d++){
			if(evalScores[d] != 0 && freeCategories.contains(d)){
				card.categories[d] = evalScores[d];
				return;
			}
		}
		
		//Om vi har ett par i <= 3or
		if(freeCategories.contains(Scorecard.pair)){
			card.categories[Scorecard.pair] = evalScores[Scorecard.pair];
			return;
		}

		//I värsta fall, nolla
		NullEntry.nullEntry(card);
		return;
	}

	public static int betOnInt(Scorecard card, Hand hand){
		LinkedList<Integer> freeCategories = card.getEmptyCategories();
		int[] diceFreq = new int [AI.diceMaxValue];
		diceFreq = hand.diceFrequency(hand.getHandArray(), diceFreq);
		int value = 1;
		
		// omedlbart satsa pa mer en fyra av en sort
		for(int l : diceFreq){
			if(l >= 4){
				return value;
			}
			value++;
		}

		int highestSum = 0;
		int highestDice = 1;

		for(int h = 0; h < diceFreq.length; h++){
			if(diceFreq[h] * (h + 1) > highestSum){
				highestSum = diceFreq[h] * (h + 1);
				highestDice = h + 1;
			}
		}

		boolean freeUpThere = freeCategories.contains(highestDice - 1);
		if(freeUpThere){
			return highestDice;
		}

		int highestCopy = highestDice;
		highestSum = 0;
		for(int h = 0; h < diceFreq.length; h++){
			if(diceFreq[h] * (h + 1) > highestSum && (h + 1) != highestCopy){
				highestSum = diceFreq[h] * (h + 1);
				highestDice = h + 1;
			}
		}

		freeUpThere = freeCategories.contains(highestDice - 1);
		if(freeUpThere){
			return highestDice;
		}
		return highestCopy;
	}

	public static void allOfAKindDefensive(Scorecard card, Hand hand, int kept){
		LinkedList<Integer> freeCategories = card.getEmptyCategories();

		boolean checked = AI.catchHand(hand, card);
		if(checked){
			return;
		}

		int[] evalScore = new int[15];
		AI.evalScores(hand, evalScore);

		if(freeCategories.contains(kept - 1) && evalScore[Scorecard.threeOfAKind] != 0){
			card.categories[kept - 1] = evalScore[kept - 1];
			return;
		}

		if(evalScore[Scorecard.fourOfAKind] != 0 && freeCategories.contains(Scorecard.fourOfAKind)){
			card.categories[Scorecard.fourOfAKind] = evalScore[Scorecard.fourOfAKind];
			return;
		}

		if(AI.fullHouse(card, hand)){
			return;
		}

		if(freeCategories.contains(kept - 1)){
			card.categories[kept - 1] = evalScore[kept - 1];
			return;
		}

		if(evalScore[Scorecard.threeOfAKind] != 0 && freeCategories.contains(Scorecard.threeOfAKind)){
			card.categories[Scorecard.threeOfAKind] = evalScore[Scorecard.threeOfAKind];
			return;
		}

		if(evalScore[Scorecard.twoPair] != 0 && freeCategories.contains(Scorecard.twoPair)){
			card.categories[Scorecard.twoPair] = evalScore[Scorecard.twoPair];
			return;
		}

		if(evalScore[Scorecard.pair] != 0 && freeCategories.contains(Scorecard.pair)){
			card.categories[Scorecard.pair] = evalScore[Scorecard.pair];
			return;
		}

		NullEntry.nullEntry(card);
	}


	public static void allOfAKindAgressive(Scorecard card, Hand hand, int kept){
		LinkedList<Integer> freeCategories = card.getEmptyCategories();

		boolean checked = AI.catchHand(hand, card);
		if(checked){
			return;
		}

		int[] evalScore = new int[15];
		AI.evalScores(hand, evalScore);

		if(evalScore[Scorecard.fourOfAKind] != 0 && freeCategories.contains(Scorecard.fourOfAKind)){
			card.categories[Scorecard.fourOfAKind] = evalScore[Scorecard.fourOfAKind];
			return;
		}

		if(AI.fullHouse(card, hand)){
			return;
		}

		if(freeCategories.contains(kept - 1) && evalScore[Scorecard.threeOfAKind] != 0){
			card.categories[kept - 1] = evalScore[kept - 1];
			return;
		}

		if(evalScore[Scorecard.threeOfAKind] != 0 && freeCategories.contains(Scorecard.threeOfAKind)){
			card.categories[Scorecard.threeOfAKind] = evalScore[Scorecard.threeOfAKind];
			return;
		}

		if(evalScore[Scorecard.twoPair] != 0 && freeCategories.contains(Scorecard.twoPair)){
			card.categories[Scorecard.twoPair] = evalScore[Scorecard.twoPair];
			return;
		}

		if(evalScore[Scorecard.pair] != 0 && freeCategories.contains(Scorecard.pair)){
			card.categories[Scorecard.pair] = evalScore[Scorecard.pair];
			return;
		}

		if(freeCategories.contains(kept - 1)){
			card.categories[kept - 1] = evalScore[kept - 1];
			return;
		}

		NullEntry.nullEntry(card);
	}

	public static void stegeKolla(Scorecard card, Hand hand, LinkedList<Integer> freeCategories){
		boolean[] straights = stegCheckArray(hand);
		int[] diceFreq = new int [AI.diceMaxValue];
		diceFreq = hand.diceFrequency(hand.getHandArray(), diceFreq);

		boolean hadeRedan = AI.catchHand(hand, card);
		if(hadeRedan){
			return;
		}

		//Gå för en liten stege om den är ledig
		if(freeCategories.contains(Scorecard.smallStraight)){
			if(straights[0] || straights[1]){
				GetCategories.smallStraight(hand);
				boolean caught = AI.catchHand(hand, card);
				if(caught){
					return;
				}

				GetCategories.smallStraight(hand);
				caught = AI.catchHand(hand, card);
				if(caught){
					return;
				}
			}
		}

		if(freeCategories.contains(Scorecard.largeStraight) && hand.getRoll() == 1){
			if(straights[1] || straights[2]){
				GetCategories.largeStraight(hand);
				boolean caught = AI.catchHand(hand, card);
				if(caught){
					return;
				}

				GetCategories.largeStraight(hand);
				caught = AI.catchHand(hand, card);
				if(caught){
					return;
				}
				// ingen stor stege
			}
		}

		int[] evalScore = new int[15];
		AI.evalScores(hand, evalScore);

		if(freeCategories.contains(Scorecard.twoPair) && evalScore[Scorecard.twoPair] != 0){
			card.categories[Scorecard.twoPair] = evalScore[Scorecard.twoPair];
			return;
		}

		if(freeCategories.contains(Scorecard.pair) && evalScore[Scorecard.pair] != 0){
			card.categories[Scorecard.pair] = evalScore[Scorecard.pair];
			return;
		}

		if(freeCategories.contains(Scorecard.threeOfAKind) && evalScore[Scorecard.threeOfAKind] != 0){
			card.categories[Scorecard.threeOfAKind] = evalScore[Scorecard.threeOfAKind];
			return;
		}

		boolean satteUppe = fillUpper(card, hand);
		if(!satteUppe){
			NullEntry.zeroUp(card);
		}
	}
	
	//Kollar om vi har del av en stege, saknar endast en till tärning
	public static boolean stegCheck(Hand hand){
		String s = new String();
		for(int k : hand.getHandArray()){
			if(!s.contains("" + k)){
				s += k;
			}
		}

		boolean first = s.contains("1234");
		boolean second = s.contains("2345");
		boolean third = s.contains("3456");

		if(first || second || third){
			return true;
		}
		return false;
	}

	public static boolean[] stegCheckArray(Hand hand){
		String s = new String();
		for(int k : hand.getHandArray()){
			if (!s.contains("" + k)) {
				s += k;
			}
		}

		boolean[] returning = new boolean[3];
		returning[0] = s.contains("1234");
		returning[1] = s.contains("2345");
		returning[2] = s.contains("3456");
		return returning;
	}

	public static boolean fillUpper(Scorecard card, Hand hand){
		for (int i = 0; i < 6; i++) {
			if(card.categories[i] == -1){
				card.categories[i] = AI.numberScore(hand.getHandArray(), i + 1);
				return true;
			}
		}
		return false;
	}
}