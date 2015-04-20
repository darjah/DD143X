import java.io.IOException;
import java.util.ArrayList;

public class Main {
	public static void main(String[] args) {
		int nrGames = 100000;
		int nrYatzy = 0;
		int nrBonus = 0;
		int nrYatzyAndBonus = 0;
		
		ArrayList<Integer> results = new ArrayList<Integer>();
		
		for(int gameCounter = 1 ; gameCounter <= nrGames ; gameCounter ++){
			Game.bonus = false;
			Game.yatzyAndBonus = false;
			Game.yatzy = false;
			
			results.add(Game.playGame());
			
			if(Game.bonus == true){
				nrBonus++;
				if(Game.yatzyAndBonus == true){
					nrYatzyAndBonus++;
				}
			}
			if(Game.yatzy){
				nrYatzy++;
			}
		}
		
		System.out.println("We aquired the bonus " + nrBonus + " times");
		System.out.println("We aquired the bonus and a yatzy " + nrYatzyAndBonus + " times");
		System.out.println("We aquired yatzy " + nrYatzy + " times");
		
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