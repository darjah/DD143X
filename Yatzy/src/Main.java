public class Main {
	//public static Printer printer;
	
	public static void main(String[] args) {
		//printer = new Printer();
		int errors = 0;
		int nrGames = 1;
		for(int gameCounter = 1 ; gameCounter <= nrGames ; gameCounter ++){
			try {
				Game.playGame();
			} catch (Exception e) {
				gameCounter --;
				errors ++;
			}
		}
		System.out.println("errors: " + errors);
		//printer.close();	
	}	
}