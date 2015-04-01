import java.util.Arrays;

public class Hand {
	Dice[] dices = new Dice[5];
	int roll = 1;

	public Hand(){
		for(int i = 0 ;i < 5; i ++ ){
			dices[i] = new Dice();
		}
	}

	public int getRoll(){
		return roll;
	}
	
	public void rollCounter(){
		roll++;
	}

	public Dice[] getDices(){
		return dices; 
	}

	public void rethrowHand(int[] dicesToRethrow){
		for(int i : dicesToRethrow){
			dices[i].throwDice();
		}
		roll ++;
	}

	public Dice getHandDice(int index){
		return dices[index];
	}

	public int getHandValue(){
		int score = 0 ;
		for(int i = 0; i < dices.length; i++){
			score = score + dices[i].getDiceValue();
		}
		return score;
	}

	public int[] getHandArray(){
		int[] hand = new int[dices.length];

		for(int i = 0; i < dices.length; i ++){
			hand[i] = dices[i].getDiceValue();
		}
		Arrays.sort(hand);
		return hand;
	}

	public int[] diceFrequency(int[] dices, int[] frequencyArray){
		//Add 1 to the corresponding place in the frequency array
		for(int i : dices) {
			frequencyArray[i-1]++;
		}
		return frequencyArray;
	}
	
	public String toString(){
		return(new StringBuilder(dices[0] + " " + dices[1] + " " + dices[2] + " " + dices[3]) + " " + dices[4]);
	}

	//For testing
	public void setDices(int[] values){
		for(int i = 0; i < 5; i++){
			dices[i].faceValue = values[i];
		}
	}
}