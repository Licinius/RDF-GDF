import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Query {
	public class Triplet{
		private String sujet;
		private String predicate;
		private String object;
		
		public Triplet(String[] splitTriplet) {
			this.sujet = splitTriplet[0];
			this.predicate = splitTriplet[1];
			this.object = splitTriplet[2];
		}
		public String toString() {
			return sujet + " " + predicate + " " + object;
		}
		
	}
	private ArrayList<String> select;
	private ArrayList<Triplet> where;
	
	public Query(String query) {
		query = query.replace("\n", "");
		Pattern selectPattern = Pattern.compile("SELECT (\\?[A-z][0-9](\\,\\?[A-z][0-9])*) WHERE .*");
		Matcher matcher = selectPattern.matcher(query);
		matcher.matches();
		select = new ArrayList<>(Arrays.asList(matcher.group(1).split(",")));
		
		where = new ArrayList<>();
		Pattern logEntry = Pattern.compile("\\{(.*)\\}");
        Matcher matchPattern = logEntry.matcher(query);
        matchPattern.find();
        String condition = matchPattern.group(1);
        for(String triplet : condition.split(" \\. ")) {
        	String[] splitTriplet = triplet.split(" ");
        	where.add(new Triplet(splitTriplet));
        }        
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
