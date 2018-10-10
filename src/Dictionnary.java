import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Dictionnary {

	private HashMap<Integer, String> dico;

	public Dictionnary() {
		super();
		dico = new HashMap<Integer, String>();
	}

	public HashMap<Integer, String> getDico() {
		return dico;
	}
	
	public Dictionnary init(HashSet<String> mots) {
		int cpt = 0;
		List<String> sortedList = new ArrayList<String>(mots);
		Collections.sort(sortedList);
		for (String word : sortedList) {
			this.getDico().put(cpt++, word); 
		}
		return this;
	}
	
}
