public class Game {
	public static void playGame() throws Exception {
		Scorecard card = new Scorecard();
		Hand hand;
		int roundCounter = 1;

		while(!card.isScorecardFilled()){
			System.out.println("Roundcounter: " + roundCounter);
			hand = new Hand();

			AI.ai(card, hand);

			if (roundCounter == 15){
				//Printer.printArray(scoreCard.scoreValues);
				break;
				//throw new Exception("To many Rounds");
			}
			roundCounter ++;
		}
		
		System.out.println("Final score: " + card.finalScore());
		System.out.println("Obtained bonus: " + card.doWeHaveBonus());
		//Main.printer.writeInt(card.finalScore());
	}
}