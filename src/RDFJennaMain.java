import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

public class RDFJennaMain {
	public static String ARG_HELP = "-help";
	public static String ARG_QUERIES = "-queries";
	public static String ARG_DATA = "-data";
	
	public static String FILEPATH_QUERIES = "";
	public static String FILEPATH_DATA = "";
	
	public static void setFilepath (List<String> arrayArgs){
		int indexQueries = arrayArgs.indexOf(ARG_QUERIES);
		FILEPATH_QUERIES = arrayArgs.get(indexQueries+1);
		
		int indexData = arrayArgs.indexOf(ARG_DATA);
		FILEPATH_DATA =  arrayArgs.get(indexData+1);
	}
	private static void displayHelpMessage() {
		String helpMessage = MessageFormat.format("java -jar "+System.getProperty("java.class.path")+ System.lineSeparator()
				+ "-queries \"{0}chemin{0}vers{0}requetes\"" + System.lineSeparator()
				+ "-data \"{0}chemin{0}vers{0}donnees\"" + System.lineSeparator(), File.separator);
		System.out.println(helpMessage);
		System.exit(0);
	}
	public static void main(String[] args) throws FileNotFoundException {	
		if(args.length==0) {
			displayHelpMessage();
		}
		//Arguments 
		List<String> arrayArgs = Collections.unmodifiableList(Arrays.asList(args));
		setFilepath(arrayArgs);
		//Parsing des queries
		ArrayList<Query> queries = new ArrayList<Query>();
		Path dir = FileSystems.getDefault().getPath(FILEPATH_QUERIES);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
		    for (Path file: stream) {
		    	queries.addAll(new QueryParser(file.toAbsolutePath().toString()).parse());
		    	
		    }
		} catch (IOException | DirectoryIteratorException e) {
		    System.err.println(e);
		    System.exit(-1);
		}
		/**
		 * 
		 * Create a data model and load file
		 * 
		 */

		Model model = ModelFactory.createDefaultModel();



		InputStream in = FileManager.get().open(FILEPATH_DATA);

		Long start = System.currentTimeMillis();

		model.read(in, null);
    	Collections.shuffle(queries);
		System.out.println("Lancement queries moteur JENNA data -> "+FILEPATH_DATA + " queries -> " + FILEPATH_QUERIES);
		start = System.currentTimeMillis();
		for(Query q : queries) {
			com.hp.hpl.jena.query.Query query = QueryFactory.create( " PREFIX  rdf: <http://db.uwaterloo.ca/~galuc/wsdbm/#>"
					+q.toString());

			QueryExecution qexec = QueryExecutionFactory.create(query, model);
			ResultSet rs =  qexec.execSelect();
		}
		long endTime = System.currentTimeMillis() - start;
		System.out.println("Temps écoulé : " + (int)endTime + "ms");
		System.exit((int)endTime);
	}
}
