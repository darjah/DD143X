import java.util.Random;

public class Dice {	
	int faceValue;
	//private boolean diceState;
	Random random;

	public Dice() {
		random = new Random();
		throwDice();
		//diceState = false;
	}

	public void throwDice() {
		faceValue = random.nextInt(6) + 1;
	}

	public int getDiceValue() {
		return faceValue;
	}

	public String toString(){
		return Integer.toString(faceValue);
	}
	/*public void setDiceState(Boolean state) {
		diceState = state;
	}

	public boolean getDiceState() {
		return diceState;
	}*/
}