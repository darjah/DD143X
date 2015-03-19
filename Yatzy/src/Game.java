import java.util.*;


public class Game {
	public static void main (String []args){
		Hand hand = new Hand();
		Scorecard card = new Scorecard();
		System.out.println(card.getEmptyCategories());

		System.out.println(hand + "\nTotal: " +  hand.getHandValue());

		int ones = 0, twos = 0, threes = 0, fours = 0, fives = 0, sixes = 0;

		for (int y = 0; y < 5; y++) {
			if(hand.getHandDice(y).equals(1)) {
				ones++;
			}
			if(hand.getHandDice(y).equals(2)) {
				twos++;
			}
			if(hand.getHandDice(y).equals(3)) {
				threes++;
			}
			if(hand.getHandDice(y).equals(4)) {
				fours++;
			}
			if (hand.getHandDice(y).equals(5)) {
				fives++;
			}
			if (hand.getHandDice(y).equals(6)) {
				sixes++;
			}
		}
		int[] i = {2, 4};
		if(sixes<3){
			hand.rethrowHand(i);
		}
		
		
		System.out.println(hand + "\nTotal: " +  hand.getHandValue());
	}
}