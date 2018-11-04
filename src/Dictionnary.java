import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
	public String getValue(int key) {
		return dictionnary.get(key);
	}
	/**
	 * Retourne la valeur trouvé dans le dictionnaire inversé ou -1 
	 * @param key la clef à rechercher
	 * @return la valeur trouvé ou -1
	 */
	public int getValue(String key) {
		return reverseDictionnary.getOrDefault(key, -1);
	}
	public String toString() {
		return dictionnary.toString();
	}

	public List<String> getValues(Set<Integer> keySet) {
		return keySet.stream().map(key -> dictionnary.get(key)).collect(Collectors.toList());
	}
}
