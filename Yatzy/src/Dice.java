import java.util.Random;

public class Dice {	
	int faceValue;
	Random random;

	public Dice() {
		random = new Random();
		throwDice();
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
}