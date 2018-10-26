import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;


public final class RDFRawParser {

	public static String ARG_HELP = "-help";
	public static String ARG_QUERIES = "-queries";
	public static String ARG_DATA = "-data";
	public static String ARG_OUTPUT = "-output";
	public static String ARG_VERBOSE = "-verbose";
	public static String ARG_EXPORT_RESULTS = "-export_results";
	public static String ARG_EXPORT_STATS = "-export_stats";
	public static String ARG_WORKLOAD_TIME = "-workload_time";
	
	public static String FILEPATH_QUERIES = "";
	public static String FILEPATH_DATA = "";
	public static String FILEPATH_OUTPUT = "";
	/**
	 * Check if required argument are specified and initialize FILEPATH_QUERIES,FILEPATH_DATA, FILEPATH_OUTPUT
	 * @param arrayArgs
	 * @return true if the arguments are invalid
	 */
	public static boolean invalidArguments(List<String> arrayArgs) {
		if(arrayArgs.contains(ARG_HELP)) {
			return true;
		}
		
		int indexQueries = arrayArgs.indexOf(ARG_QUERIES);
		FILEPATH_QUERIES = arrayArgs.get(indexQueries+1);
		if(indexQueries<0 || !new File(FILEPATH_QUERIES).exists()) 
			return true;
		
		int indexData = arrayArgs.indexOf(ARG_DATA);
		FILEPATH_DATA =  arrayArgs.get(indexData+1);
		if(indexData<0 || !new File(arrayArgs.get(indexData+1)).exists())
			return true;
		
		int indexOutput = arrayArgs.indexOf(ARG_OUTPUT);
		FILEPATH_DATA =  arrayArgs.get(indexOutput+1);
		if(indexOutput<0 || !new File(arrayArgs.get(indexOutput+1)).exists())
			return true;
		return false;
	}
	public static void main(String args[]) throws FileNotFoundException {
		//Arguments 
		List<String> arrayArgs = Collections.unmodifiableList(Arrays.asList(args));
		//-verbose
		boolean verbose = arrayArgs.contains(ARG_VERBOSE);
		//-export_results
		boolean exportResults = arrayArgs.contains(ARG_EXPORT_RESULTS);
		//-export_stats
		boolean exportStats = arrayArgs.contains(ARG_EXPORT_STATS);
		//-workload_time
		boolean workloadTime = arrayArgs.contains(ARG_WORKLOAD_TIME);
		
		if(invalidArguments(arrayArgs)) {
			String helpMessage = MessageFormat.format("java -jar "+System.getProperty("java.class.path")+ System.lineSeparator()
					+ "-queries \"{0}chemin{0}vers{0}requetes\"" + System.lineSeparator()
					+ "-data \"{0}chemin{0}vers{0}donnees\"" + System.lineSeparator()
					+ "-output \"{0}chemin{0}vers{0}dossier{0}sortie\"" + System.lineSeparator()
					+ "-verbose" + System.lineSeparator()
					+ "-export_results" + System.lineSeparator()
					+ "-export_stats" + System.lineSeparator()
					+ "-workload_time", File.separator);
			System.out.println(helpMessage);
			System.exit(0);
		}else {
			Reader reader = new FileReader(FILEPATH_DATA);
			RDFParser rdfParser = Rio.createParser(RDFFormat.RDFXML);
			HexaStore hexaStore = new HexaStore();
			rdfParser.setRDFHandler(hexaStore);
			try {
				rdfParser.parse(reader, "");
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
			finally {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(0);
				}
			}
			
			//Parsing des queries
			ArrayList<Query> queries = null;
			try {
				queries = new QueryParser(FILEPATH_QUERIES).parse();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
			for(Query q : queries) {
				System.out.println("---" +hexaStore.execute(q));
			}
			
		}
	}

}