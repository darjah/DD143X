import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Printer {
	private File outputFile;
	private FileWriter outStream;
	private BufferedWriter writer;
	private String dateTime;
	private int sum = 0;
	private int best = 0;
	private int worst = 375;

	public Printer(ArrayList<Integer> results) throws IOException{
		dateTime = new SimpleDateFormat("yyyyMMdd_HHmm").format(Calendar.getInstance().getTime());
		outputFile = new File(dateTime +".txt");
		try {
			outputFile.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println("Output file found at:" + outputFile.getAbsolutePath());
		try {
			outStream = new FileWriter(outputFile);
			writer = new BufferedWriter(outStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Skriver alla slutpoäng till filen
		for(int i = 0; i < results.size(); i++){
			writer.write(results.get(i)+"\n");
		}
	}

	public void close(ArrayList<Integer> results, int nrGames){
		for(int i = 0; i < results.size(); i++){
			writeInt(results.get(i));
		}
		
		double sumOfAllGames = sum;
		double nrOfGames = nrGames;
		
		double meanValue = sumOfAllGames/nrOfGames;
		
		System.out.println("Avg.score: "  + meanValue);
		System.out.println("Highest score: " + best);
		System.out.println("Lowest score: " + worst );
		
		try {
			writer.close();
			outStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeInt(int input){
		sum += input;
		if (input > best){
			best = input;
		}
		if (input < worst){
			worst = input;
		}
	}		
}