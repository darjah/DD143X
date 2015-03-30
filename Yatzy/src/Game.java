public class Game {
	public static void playGame() throws Exception {
		Scorecard card = new Scorecard();
		Hand hand;
		int roundCounter = 1;

		while(roundCounter<16){
			System.out.println("Roundcounter: " + roundCounter);
			hand = new Hand();

			AI.ai(card, hand);

			roundCounter ++;
		}

		for(int i = 0; i<= 15; i++){
			System.out.println(card.categories[i]);
		}

		System.out.println("Final score: " + card.finalScore());
		System.out.println("Obtained bonus: " + card.doWeHaveBonus());
		//Main.printer.writeInt(card.finalScore());
	}
}