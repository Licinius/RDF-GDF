import org.openrdf.model.Statement;
import org.openrdf.rio.helpers.RDFHandlerBase;

public class HexaStoreFactory extends RDFHandlerBase{

	private Dictionnary dictionnary;
	private POS POSIndex;
	private OPS OPSIndex;
	
	public HexaStoreFactory() {
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
