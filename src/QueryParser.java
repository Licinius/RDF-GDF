import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class QueryParser {
	File queriesFile;
	public QueryParser(String filepath) {
		queriesFile = new File(filepath);
	}
	
	public ArrayList<Query> parse() throws IOException{
		ArrayList<Query> queries = new ArrayList<>();
		FileReader fileReader = new FileReader(queriesFile);
		int codeRead;
		String query = "";
		char charactedRead;
		while(( codeRead= fileReader.read())!=-1) {
			charactedRead = (char) codeRead;
			query += charactedRead;
			if(charactedRead == '}') {
				query = query.trim();
				query = query.replace("\n", "");
				queries.add(new Query(query));
				query = "";
			}
		}
		fileReader.close();
		return queries;
		
	}
}
