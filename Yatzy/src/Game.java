public class Game {
	public static int playGame(){
		Scorecard card = new Scorecard();
		Hand hand;
	
		while(!card.getEmptyCategories().isEmpty()){
			hand = new Hand();
			AI.play(card, hand);
		}

		for(int i = 0; i< 15; i++){
			System.out.println(card.categories[i]);
		}
		int finalScore = card.finalScore();
		System.out.println("Final score: " + finalScore);
		System.out.println("Obtained bonus: " + card.doWeHaveBonus());
		
		return finalScore;
	}
}