import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.rio.helpers.RDFHandlerBase;

public class HexaStore extends RDFHandlerBase{

	private Dictionary dictionary;
	private POS POSIndex;
	private OPS OPSIndex;
	
	public HexaStore() {
		this.dictionary = new Dictionary();
		POSIndex = new POS();
		OPSIndex = new OPS();
	}
	
	@Override
	public void handleStatement(Statement st) {
		addToDictionnary(st);
		addToIndexes(st);
	}
	
	/**
	 * Ajoute les mots du tripl� RDF au dictionnaire
	 * @param st
	 */
	private void addToDictionnary(Statement st) {
		String subject = st.getSubject().toString();
		String predicate = st.getPredicate().toString();
		String object = st.getObject().toString();
		dictionary.put(subject)
				.put(predicate)
				.put(object);
	}
	/**
	 * Ajoute les mots du tripl� RDF aux indexes
	 * @param st 
	 */
	private void addToIndexes(Statement st) {
		int s = dictionary.getReverseDictionnary().get(st.getSubject().toString());
		int p = dictionary.getReverseDictionnary().get(st.getPredicate().toString());
		int o = dictionary.getReverseDictionnary().get(st.getObject().toString());

		POSIndex.addElement(s, p, o);
		OPSIndex.addElement(s, p, o);
	}
	
	public LinkedHashMap<Query ,List<String>> execute(List<Query> queries){
		LinkedHashMap<Query,List<String>> res = new LinkedHashMap<>();
		for(Query query : queries) {
			res.put(query,this.execute(query));
		}
		return res;
	}
	/**
	 * Initialise les statistiques des triplets de la liste afin de les trier
	 * @param triplets
	 */
	public void setStats(List<Query.Triplet> triplets) {
		for(Query.Triplet triplet:triplets) {
			int tripletPredicate = dictionary.getValue(triplet.getPredicate());
			int tripletObject = dictionary.getValue(triplet.getObject());
			triplet.setStat(POSIndex.getStat(tripletPredicate,tripletObject));
		}
	}
	public List<String> execute(Query query){
		ArrayList<Query.Triplet> triplets = new ArrayList<>(query.getWhere());
		this.setStats(triplets);
		Collections.sort(triplets,new Comparator<Query.Triplet>() {
			public int compare(Query.Triplet o1, Query.Triplet o2) {
				return  o1.getStat()- o2.getStat();
			}
		});
		query.setOrderedWhere(triplets);
		if(triplets.get(0).getStat()==0) { //Si il y a pas de concordance
			return new ArrayList<String>();
		}else {
			HashSet<Integer> intermediateResult = new HashSet<Integer>(this.execute(triplets.get(0)));			
			HashSet<Integer> tmpHashSet;
			for(int i = 1; i < triplets.size() ;i++) {
				if(intermediateResult.size() == 0) {
					return new ArrayList<String>();
				}
				tmpHashSet = this.execute(triplets.get(i));
				intermediateResult.retainAll(tmpHashSet);
			}
			return dictionary.getValues(intermediateResult);
		}
		
		
	}
	
	public HashSet<Integer> execute(Query.Triplet triplet){
		int tripletPredicate = dictionary.getValue(triplet.getPredicate());
		int tripletObject = dictionary.getValue(triplet.getObject());
		return POSIndex.getThirdColumn(tripletPredicate, tripletObject);
	}
	
	public Dictionary getDictionnary() {
		return dictionary;
	}
	public Index getPOS() {
		return POSIndex;
	}
	public Index getOPS() {
		return OPSIndex;
	}
		
}
