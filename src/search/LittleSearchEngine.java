package search;

import java.io.*;
import java.util.*;

/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author Sesh Venugopal
 * 
 */
class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.put(word,word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeyWords(docFile);
			mergeKeyWords(kws);
		}
		
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeyWords(String docFile) 
	throws FileNotFoundException {
		// COMPLETE THIS METHOD
		Scanner doc = new Scanner(new File(docFile));
		HashMap<String,Occurrence> loadedWords = new HashMap<String, Occurrence>();
		while(doc.hasNext()){
			String kw = getKeyWord(doc.next());
			if(!loadedWords.containsKey(kw)){
				loadedWords.put(kw,new Occurrence(docFile,1));
			}
			if(loadedWords.containsKey(kw)){
				loadedWords.get(kw).frequency+=1;
			}
		}
		return loadedWords;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeyWords(HashMap<String,Occurrence> kws) {
		// COMPLETE THIS METHOD
		ArrayList<Occurrence> merge = new ArrayList<Occurrence>();
		Occurrence temp;
		for(String word : kws.keySet()){
			merge.clear();
			temp = kws.get(word);
			if(keywordsIndex.containsKey(word)){
				merge = keywordsIndex.get(temp);
				merge.add(temp);
				insertLastOccurrence(merge);
				keywordsIndex.put(word, merge);
			}
			else{
				merge.add(temp);
				keywordsIndex.put(word, merge);
			}
			merge.clear();
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * TRAILING punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyWord(String word) {
		// COMPLETE THIS METHOD
		String temp = "";
		for(int i = 0; i< word.length(); i++){
			if(Character.isLetter(word.charAt(i))){
				temp+=Character.toString(word.charAt(i));
			}
			if(!Character.isLetter(word.charAt(i)) && word.length()-1>=i+1 && Character.isLetter(word.charAt(i+1)))
				return null;
		}
		word=temp.toLowerCase();
		if(noiseWords.containsKey(word)){
			return null;
		}
		return word;
	}

	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		// COMPLETE THIS METHOD
		ArrayList<Integer> mIndex = new ArrayList<Integer>();
		Occurrence toAdd = occs.remove(occs.size()-1);
		int hi = occs.size()-1;
		int lo = 0;
		while(lo<=hi){
			int m = (hi+lo)/2;
			Occurrence compare = occs.get(m);
			if(compare.frequency == toAdd.frequency){
				mIndex.add(m);
				occs.add(m,toAdd);
			}
			else if(compare.frequency<toAdd.frequency){
				mIndex.add(m);
				lo = m-1;
			}
			else if(compare.frequency> toAdd.frequency){
				mIndex.add(m);
				m++;
				hi=m+1;
			}
				
		}
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		return mIndex;
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		// COMPLETE THIS METHOD
		ArrayList<String> finalResult = new ArrayList<String>();
		ArrayList<Occurrence> kw1Result = new ArrayList<Occurrence>();
		ArrayList<Occurrence> kw2Result = new ArrayList<Occurrence>();
		kw1 = kw1.toLowerCase();
		kw2 = kw2.toLowerCase();
		kw1Result = keywordsIndex.get(kw1);
		kw2Result = keywordsIndex.get(kw2);
		for (Occurrence e : kw1Result){
			if (finalResult.size() < 5){
				String kw1Value = e.document;
				int kw1Freq = e.frequency;
				
				for (Occurrence i : kw2Result){
					String kw2Value = i.document;
					int kw2Freq = i.frequency;
					
					if (kw2Freq <= kw1Freq){
						if (!finalResult.contains(kw1Value) && finalResult.size() < 5)
							finalResult.add(kw1Value);
					}
					else if (kw2Freq > kw1Freq){
						if (!finalResult.contains(kw2Value) && finalResult.size() < 5)
							finalResult.add(kw2Value);
					}
				}
			}
		}
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		return finalResult;
	}
}
