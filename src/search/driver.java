package search;

import java.io.FileNotFoundException;

public class driver {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub

		LittleSearchEngine test = new LittleSearchEngine();
		test.makeIndex("AliceCh1.txt" , "noisewords.txt");
		System.out.println(test.getKeyWord("hello!.1"));
	}

}
