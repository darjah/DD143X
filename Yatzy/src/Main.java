public class Main {
	public static void main(String[] args) {
		//Printer printer = new Printer();
		int nrGames = 5;
		
		for(int gameCounter = 1 ; gameCounter <= nrGames ; gameCounter ++){
			Game.playGame();
		}

		//printer.close();	
	}	
}