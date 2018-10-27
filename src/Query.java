import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Query {
	public class Triplet{
		private String sujet;
		private String predicate;
		private String object;
		private int stat;
		
		public int getStat() {
			return stat;
		}

		public void setStat(int stat) {
			this.stat = stat;
		}

		public Triplet(String[] splitTriplet) {
			this.sujet = splitTriplet[0].trim();
			this.predicate = splitTriplet[1].trim();
			this.object = splitTriplet[2].trim();
		}
		
		public String getSujet() {
			return sujet;
		}
		public String getPredicate() {
			return predicate;
		}
		public String getObject() {
			return object;
		}
		public String toString() {
			return sujet + " " + predicate + " " + object;
		}
		
		/**
		 * Retourne les parties du triplets non-inconnus
		 * @return une ArrayList contenant des triplets connus
		 */
		public ArrayList<String> getNotUnknown(){
			ArrayList<String> notUnknown = new ArrayList<>();
			if(!sujet.startsWith("?"))
				notUnknown.add(sujet);
			if(!predicate.startsWith("?"))
				notUnknown.add(predicate);
			if(!object.startsWith("?"))
				notUnknown.add(object);
			return notUnknown;
		}
		
	}
	private ArrayList<String> select;
	private ArrayList<Triplet> where;
	private ArrayList<Triplet> whereOrdered;
	/**
	 * Crée une instance de query basé sur une string bien formaté de query rdf
	 * @param query une chaine de préférence SELECT '[selection]' WHERE { [condition1] . [condition2]}  
	 */
	public Query(String query) {
		Pattern selectPattern = Pattern.compile("SELECT (\\?[A-z][0-9](\\,\\?[A-z][0-9])*) WHERE.*");
		Matcher matcher = selectPattern.matcher(query);
		matcher.matches();
		select = new ArrayList<>(Arrays.asList(matcher.group(1).split(",")));
		
		where = new ArrayList<>();
		Pattern logEntry = Pattern.compile("\\{(.*)\\}");
        Matcher matchPattern = logEntry.matcher(query);
        matchPattern.find();
        String condition = matchPattern.group(1);
        for(String triplet : condition.split(" \\.()+")) {
        	triplet = triplet.trim();
        	if(!triplet.isEmpty()) {
            	triplet = triplet.replaceAll(">|<" ,"");
            	String[] splitTriplet = triplet.split(" ");
            	where.add(new Triplet(splitTriplet));
        	}

        }        
	}
	
	/**
	 * Retourne une liste non modifiable correspondant à la selection
	 * @return List de variable (non modifiable)
	 */
	public List<String> getSelect() {
		return Collections.unmodifiableList(select);
	}
	/**
	 * Retourne une liste non modifiable correspondant aux conditions
	 * @return List de triplet (non modifiable)
	 */
	public List<Triplet> getWhere() {
		return Collections.unmodifiableList(where);
	}
	
	public void setOrderedWhere(ArrayList<Triplet> whereOrdered) {
		this.whereOrdered = whereOrdered;
	}
	
	public List<Triplet> getOrderedWhere(){
		return this.whereOrdered;
	}
	public String toString() {
		String res = "Select "+ String.join(",", select) + " WHERE {";
		res += where.stream()
				   .map(t -> t.toString())
				   .collect(Collectors.joining(" . "));
		res +="}";
		return res;
	}
}
