import java.util.LinkedList;

public class NullEntry {
	final static int[] nullUp = {0, 1, 2, 3, 4, 5, 6, Scorecard.yatzy, Scorecard.smallStraight, Scorecard.largeStraight, Scorecard.fourOfAKind, 
								Scorecard.fullHouse, Scorecard.threeOfAKind, Scorecard.twoPair, Scorecard.pair};
	
	final static int[] nullDown = {Scorecard.yatzy, Scorecard.smallStraight, Scorecard.largeStraight, Scorecard.fourOfAKind, 
									Scorecard.fullHouse, Scorecard.threeOfAKind, Scorecard.twoPair, Scorecard.pair, 0, 1, 2, 3, 4, 5, 6};
	
	//Nolla första halvan
	public static void zeroUp(Scorecard card){
		LinkedList<Integer> emptyCategories = card.getEmptyCategories();
		for(int i : nullUp){
			if(emptyCategories.contains(i)){
				card.categories[i] = 0;
				return;
			}
		}
	}

	//Nolla andra halvan
	public static void zeroDown(Scorecard card){
		LinkedList<Integer> emptyCategories = card.getEmptyCategories();
		for(int i : nullDown){
			if(emptyCategories.contains(i)){
				card.categories[i] = 0;
				return;
			}
		}
	}

	public static void nullEntry(Scorecard card){	
		//När vi redan har bonusen eller det inte längre är möjligt att få den
		if(card.doWeHaveBonus() || !card.possibleToGetBonus()){
			NullEntry.zeroUp(card);
		}
		//Om vi ligger under onPar men fortfarande möjligt att få bounsen
		else if(card.onPar() == -1 && card.possibleToGetBonus()){
			NullEntry.zeroDown(card);
		}
		else if(card.onPar() == -1 && !card.possibleToGetBonus()){
			NullEntry.zeroUp(card);
		}
		//När vi ligger onPar
		else if(card.onPar() == 0){
			NullEntry.zeroDown(card);
		}
		//När vi ligger över onPar
		else if(card.onPar() == 1){
			NullEntry.zeroUp(card);
		}
		//Alla andra fall
		else{
			NullEntry.zeroDown(card);
		}
	}
}