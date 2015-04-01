//Part of the AI to caluculate which dices to rethrow and rethows them
public class GetCategories {
	//rethrows every dice in @hand that doesnt have the value @value
	public static void allOfAKind(Hand hand, int value){
		for(Dice dice : hand.getDices()){
			if(dice.faceValue != value){
				dice.throwDice();
			}
		}
		hand.rollCounter();
	}

	//rethrow a hand that has pair, tripple or quardruple to the higest value if there are more than two pairs.
	public static void nrDices(Hand hand, int nrDices){
		int[] diceFreq = new int [AI.diceMaxValue];
		diceFreq = hand.diceFrequency(hand.getHandArray(), diceFreq);
		int valueToKeep = 1;

		for(int i = 0; i < 6; i++){
			if(diceFreq[i] == nrDices) {
				valueToKeep = i + 1;
			}
		}

		for(Dice dice : hand.getDices()){
			if(dice.faceValue != valueToKeep){
				dice.throwDice();
			}
		}
		hand.rollCounter();
	}

	//requires a twoPair hand in to work correctly
	public static void twoPair(Hand hand){
		int[] diceFreq = new int [AI.diceMaxValue];
		diceFreq = hand.diceFrequency(hand.getHandArray(), diceFreq);

		int valueToRethrow = 1;
		for(int i = 0; i < diceFreq.length; i++){
			if(diceFreq[i] == 1){
				valueToRethrow = i + 1;
			}
		}

		for(Dice dice : hand.getDices()){
			if(dice.faceValue == valueToRethrow){
				dice.throwDice();
			}
		}

		hand.rollCounter();
	}

	//Kastar om t�rningarna f�r att kunna f� en liten stege
	public static void smallStraight(Hand hand){
		boolean[] straight = { false, false, false, false, false, false };
		for (Dice dice : hand.getDices()){
			if(straight[dice.getDiceValue() - 1] || dice.faceValue == 6){
				dice.throwDice();
			} 
			else{
				straight[dice.getDiceValue() - 1] = true;
			}
		}
		hand.rollCounter();
	}
	
	//Kastar om t�rningarna f�r att kunna f� en stor stege
	public static void largeStraight(Hand hand) {
		boolean[] straight = { false, false, false, false, false, false };
		for (Dice dice : hand.getDices()){
			if(straight[dice.getDiceValue() - 1] || dice.faceValue == 1){
				dice.throwDice();
			} 
			else{
				straight[dice.getDiceValue() - 1] = true;
			}
		}
		hand.rollCounter();
	}
	
	/////VAD VARF�R EXAKT//Whatto do in case of full house on first or second throw. needs to know the status of the scorecard to do correct decission
	public static void fullHouse(Scorecard card, Hand hand){
		int roll = hand.getRoll();
		int[] diceFreq = new int [AI.diceMaxValue];
		diceFreq = hand.diceFrequency(hand.getHandArray(), diceFreq);
		int weHaveThree = 0;
		
		for(int i = 0; i < diceFreq.length; i++){
			if(diceFreq[i] == 3){
				weHaveThree = i + 1;
			}
		}

		if(weHaveThree == 0){
			throw new IllegalArgumentException("tried fullhouse rethrow without a tripple");
		}

		boolean fourFilled = false;

		if(card.categories[Scorecard.fourOfAKind] != 0){
			fourFilled = true;
		}

		AI.evalScores(hand, diceFreq);
		if(roll == 1){
			if(!fourFilled){
				for(Dice dice : hand.getDices()){
					if(dice.faceValue != weHaveThree){
						dice.throwDice();
					}
				}
			}
		}

		if(roll == 2){
			if(weHaveThree >= 4) {
				for(Dice dice : hand.getDices()){
					if(dice.faceValue != weHaveThree){
						dice.throwDice();
					}
				}
			}
		}
		hand.rollCounter();
	}
	
	//Kommer kolla om man har triss f�rst, sen tv� par, ett par och sedan enstaka t�rningar
	public static void getFullHouse(Hand hand){
		int[] diceFreq = new int [AI.diceMaxValue];
		diceFreq = hand.diceFrequency(hand.getHandArray(), diceFreq);

		int valueToKeep;
		int trissScore = AI.threeOfAKindScore(hand);
		if(trissScore != 0){
			valueToKeep = trissScore / 3;
			int otherValue = 0;
			for(int e = 5; e >= 2; e--){
				if(diceFreq[e] == 1){
					otherValue = e + 1;
					break;
				}
			}
			
			for(Dice dice : hand.getDices()){
				if(dice.faceValue != valueToKeep && dice.faceValue != otherValue){
					dice.throwDice();
				}
			}
			hand.rollCounter();
			return;
		}

		if(AI.twoPairScore(hand) != 0){
			twoPairToFullHouse(hand);
			return;
		}

		int pairScore = AI.pairScore(hand);
		if(pairScore != 0){
			valueToKeep = pairScore / 2;
			int otherValue = 0;
			for(int e = 5; e > 0; e--){ //�ndrade e>2 till e>0
				if(diceFreq[e] == 1){
					otherValue = e + 1;
					break;
				}
			}
			
			for(Dice dice : hand.getDices()){
				if(dice.faceValue != valueToKeep && dice.faceValue != otherValue){
					dice.throwDice();
				}
			}
			hand.rollCounter();
			return;
		}

		int i = 0;
		int j = 0;
		for(int c = diceFreq.length - 1; c >= 0; c--){
			if(diceFreq[c] == 1){
				if(i == 0){
					i = c + 1;
				} 
				else {
					j = c + 1;
					break;
				}
			}
		}

		for(Dice dice : hand.getDices()){
			if(dice.faceValue != i && dice.faceValue != j){
				dice.throwDice();
			}
		}
		hand.rollCounter();
	}
	
	//Om vi sitter p� tv� par och vill ha en k�k, anv�nds i getFullHouse
	public static void twoPairToFullHouse(Hand hand){
		int i = 0;
		int j = 0;

		int[] diceFreq = new int [AI.diceMaxValue];
		diceFreq = hand.diceFrequency(hand.getHandArray(), diceFreq);

		for(int c = diceFreq.length - 1; c >= 0; c--){
			if(diceFreq[c] == 2){
				if(i == 0){
					i = c + 1;
				}
				else{
					j = c + 1;
				}
			}
		}

		for(Dice dice : hand.getDices()){
			if(dice.faceValue != i && dice.faceValue != j){
				dice.throwDice();
			}
		}
		hand.rollCounter();
	}
	
	//F�r att kunna f� tv� olika par
	public static void getTwoPair(Hand hand){
		int[] diceFreq = new int [AI.diceMaxValue];
		diceFreq = hand.diceFrequency(hand.getHandArray(), diceFreq);

		int keep1 = 0;
		int keep2 = 0;

		for(int i = 5; i >= 0; i--){
			if(diceFreq[i] > 0){
				if(keep1 == 0){
					keep1 = i + 1;
				}
				else if (keep2 == 0){
					keep2 = i + 1;
				}
				
				//Om vi har hittat tv� v�rden att beh�lla kommer vi kolla om freq �r h�gre f�r mindre val�rer och i s�dana fall beh�lla den
				if(keep1 != 0 && keep2 != 0){
					if(keep1 > keep2){
						if(diceFreq[keep2 - 1] < diceFreq[i]){ //Kommer dock kanske ta ett l�gre v�rde med h�gre freq?
							keep2 = i + 1;
						}
					} 
					else{
						if(diceFreq[keep1 - 1] < diceFreq[i]){
							keep1 = i + 1;
						}
					}
				}
			}
		}

		for(Dice dice : hand.getDices()){
			//Om t�rningarna inte har de v�rden vi har best�mt att beh�lla
			if(dice.faceValue != keep1 && dice.faceValue != keep2){
				dice.throwDice();
			}
			//Om vi har mer �n 2 t�rningar med samma v�rde, kan vi kasta om de andra
			if(diceFreq[dice.faceValue - 1] > 2){
				diceFreq[dice.faceValue - 1] --;
				dice.throwDice();
			}

		}	
		hand.rollCounter();
	}
}