import java.io.IOException;
import java.util.ArrayList;

public class Main {
	public static void main(String[] args) {
		int nrGames = 100000;
		ArrayList<Integer> results = new ArrayList<Integer>();
		
		for(int gameCounter = 1 ; gameCounter <= nrGames ; gameCounter ++){
			results.add(Game.playGame());
		}
		
		Printer printer = null;
		try {
			printer = new Printer(results);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		printer.close(results, nrGames);	
	}	
}