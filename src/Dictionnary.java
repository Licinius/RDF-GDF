import java.util.HashMap;

public class Dictionnary {

	private HashMap <Integer, String> dictionnary;
	private HashMap <String,Integer> reverseDictionnary;
	private int wordCounter;
	
	public Dictionnary() {
		super();
		dictionnary = new HashMap<Integer, String>();
		reverseDictionnary = new HashMap<String, Integer>();
		wordCounter = 0;
	}

	public HashMap<Integer, String> getDictionnary() {
		return dictionnary;
	}
	public HashMap<String, Integer> getReverseDictionnary() {
		return reverseDictionnary;
	}
	
	public Dictionnary put(String word) {
		if(!reverseDictionnary.containsKey(word)) {
			wordCounter +=1;
			reverseDictionnary.put(word, wordCounter);
			dictionnary.put(wordCounter,word);
		}
		return this;
	}
	public String toString() {
		return dictionnary.toString();
	}
}
