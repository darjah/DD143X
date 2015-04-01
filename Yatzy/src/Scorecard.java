import java.util.LinkedList;

public class Scorecard {
	public static final int ones = 0;
	public static final int twos = 1;
	public static final int threes = 2;
	public static final int fours = 3;
	public static final int fives = 4;
	public static final int sixes = 5;	
	public static final int bonus = 50;
	public static final int pointsToBonus = 63;

	public static final int pair = 6;
	public static final int twoPair = 7;
	public static final int threeOfAKind = 8;
	public static final int fourOfAKind = 9;
	public static final int smallStraight = 10;
	public static final int largeStraight = 11;
	public static final int fullHouse = 12;
	public static final int chance = 13;
	public static final int yatzy = 14;

	int[] categories = new int[15];

	public Scorecard(){
		for(int i = 0; i < categories.length; i++){
			categories[i] = -1;
		}
	}

	public boolean isScorecardFilled(){
		return getEmptyCategories().isEmpty();
	}

	public LinkedList<Integer> getEmptyCategories(){
		LinkedList<Integer> emptyCategories = new LinkedList<Integer>();
		for(int i = 0; i < categories.length; i++){
			if(categories[i] == -1){
				emptyCategories.add(i);
			}
		}
		return emptyCategories;
	}	

	public int onPar(){
		int parScore = 0;

		for(int i = 0; i < sixes; i++){
			if(categories[i] >= 0){
				parScore += categories[i];
			} 
			else{
				parScore += i * 3;
			}
		}
		
		//Om över onPar, ret 1
		if(parScore > pointsToBonus){
			return 1;
		}
		//Om onPar, ret 0
		if(parScore == pointsToBonus){
			return 0;
		}
		//Om under onPar, ret -1
		return -1;
	}
	
	public boolean possibleToGetBonus(){
		int score = 0;

		for(int i = 0; i <= sixes; i++){
			if(categories[i] >= 0){
				score += categories[i];
			} 
			else{
				score += i * 5;
			}
		}

		if(score >= pointsToBonus){
			return true;
		}
		return false;
	}

	public boolean doWeHaveBonus(){
		int score = 0;

		for(int i = 0; i < sixes; i++){
			if(categories[i] >= 0){
				score += categories[i];
			}
		}

		if(score >= pointsToBonus){
			return true;
		}
		return false;
	}

	public int finalScore(){
		int total = 0;
		for(int i = 0; i < categories.length; i++){
			total += categories[i];
		}
		if(doWeHaveBonus()){
			total += bonus;
		}
		return total;
	}

	//For testing
	public void setScorecard(int[] array){
		categories = array;
	}
}