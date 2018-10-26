import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.rio.helpers.RDFHandlerBase;

public class HexaStore extends RDFHandlerBase{

	private Dictionnary dictionnary;
	private POS POSIndex;
	private OPS OPSIndex;
	
	public HexaStore() {
		this.dictionnary = new Dictionnary();
		POSIndex = new POS();
		OPSIndex = new OPS();
	}
	
	@Override
	public void handleStatement(Statement st) {
		addToDictionnary(st);
		addToIndexes(st);
	}
	
	/**
	 * Ajoute les mots du triplé RDF au dictionnaire
	 * @param st
	 */
	private void addToDictionnary(Statement st) {
		String subject = st.getSubject().toString();
		String predicate = st.getPredicate().toString();
		String object = st.getObject().toString();
		dictionnary.put(subject)
				.put(predicate)
				.put(object);
	}
	/**
	 * Ajoute les mots du triplé RDF aux indexes
	 * @param st 
	 */
	private void addToIndexes(Statement st) {
		int s = dictionnary.getReverseDictionnary().get(st.getSubject().toString());
		int p = dictionnary.getReverseDictionnary().get(st.getPredicate().toString());
		int o = dictionnary.getReverseDictionnary().get(st.getObject().toString());

		POSIndex.addElement(s, p, o);
		OPSIndex.addElement(s, p, o);
	}
	
	
	public List<String> execute(Query query){
		ArrayList<Query.Triplet> triplets = new ArrayList<>(query.getWhere());
		Collections.sort(triplets,new Comparator<Query.Triplet>() {
			public int compare(Query.Triplet o1, Query.Triplet o2) {
				int o1Predicate = dictionnary.getValue(o1.getPredicate());
				int o1Object = dictionnary.getValue(o1.getObject());
				int o2Predicate = dictionnary.getValue(o2.getPredicate());
				int o2Object = dictionnary.getValue(o2.getObject());
				o1.setStat(POSIndex.getStat(o1Predicate,o1Object));
				o2.setStat(POSIndex.getStat(o2Predicate,o2Object));
				return  o1.getStat()- o2.getStat();
			}
		});
		if(triplets.get(0).getStat()==0) { //Si il y a pas de concordance
			return new ArrayList<String>();
		}else {
			HashSet<Integer> intermediateResult = this.execute(triplets.get(0));
			HashSet<Integer> tmpHashSet;
			for(int i = 1; i < triplets.size() ;i++) {
				if(intermediateResult.size() == 0) {
					return new ArrayList<String>();
				}
				tmpHashSet = this.execute(triplets.get(i));
				intermediateResult.retainAll(tmpHashSet);

			}
			return dictionnary.translate(intermediateResult);
		}
		
		
	}
	
	public HashSet<Integer> execute(Query.Triplet triplet){
		int tripletPredicate = dictionnary.getValue(triplet.getPredicate());
		int tripletObject = dictionnary.getValue(triplet.getObject());
		return POSIndex.getThirdColumn(tripletPredicate, tripletObject);
	}
	
	public Dictionnary getDictionnary() {
		return dictionnary;
	}
	public Index getPOS() {
		return POSIndex;
	}
	public Index getOPS() {
		return OPSIndex;
	}
		
}
