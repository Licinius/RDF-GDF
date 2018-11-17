import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;


public final class RDFRawParser {

	public static String ARG_HELP = "-help";
	public static String ARG_QUERIES = "-queries";
	public static String ARG_DATA = "-data";
	public static String ARG_OUTPUT = "-output";
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
		if(indexQueries<0 || !new File(FILEPATH_QUERIES).isDirectory()) {
			return true;
		}
		int indexData = arrayArgs.indexOf(ARG_DATA);
		FILEPATH_DATA =  arrayArgs.get(indexData+1);
		if(indexData<0 || !new File(FILEPATH_DATA).exists()) {
			return true;
		}
		return false;
	}
	
	public static void setFilepath (List<String> arrayArgs){
		int indexQueries = arrayArgs.indexOf(ARG_QUERIES);
		FILEPATH_QUERIES = arrayArgs.get(indexQueries+1);
		
		int indexData = arrayArgs.indexOf(ARG_DATA);
		FILEPATH_DATA =  arrayArgs.get(indexData+1);
		
		int indexOutput = arrayArgs.indexOf(ARG_OUTPUT);
		if(indexOutput<0 || new File(arrayArgs.get(indexOutput+1)).isDirectory())
			FILEPATH_OUTPUT =  arrayArgs.get(indexOutput+1);
	}
	
	public static void main(String args[]) throws FileNotFoundException {
		if(args.length==0) {
			displayHelpMessage();
		}
		//Arguments 
		List<String> arrayArgs = Collections.unmodifiableList(Arrays.asList(args));
		//-export_results
		boolean exportResults = arrayArgs.contains(ARG_EXPORT_RESULTS);
		//-export_stats
		boolean exportStats = arrayArgs.contains(ARG_EXPORT_STATS);
		//-workload_time
		boolean workloadTime = arrayArgs.contains(ARG_WORKLOAD_TIME);
		
		if(invalidArguments(arrayArgs)) {
			displayHelpMessage();
		}else {
			setFilepath(arrayArgs);
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
			ArrayList<Query> queries = new ArrayList<Query>();
			Path dir = FileSystems.getDefault().getPath(FILEPATH_QUERIES);
			boolean isEmptyStats = false;
			boolean isEmptyResults = false;
			long totalTime = 0;
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			    for (Path file: stream) {
			    	queries.addAll(new QueryParser(file.toAbsolutePath().toString()).parse());
			    	
			    }
			} catch (IOException | DirectoryIteratorException e) {
			    System.err.println(e);
			    System.exit(-1);
			}
			Collections.shuffle(queries);
			System.out.println("Lancement queries moteur RDF data -> "+FILEPATH_DATA + " queries -> " + FILEPATH_QUERIES);
	    	long startTime = System.currentTimeMillis();
	    	LinkedHashMap<Query,List<String>> result = hexaStore.execute(queries); //Execute toutes les queries du document
		    long endTime = System.currentTimeMillis();
		    long timeQueries = endTime-startTime;
	    	totalTime += timeQueries;
		    if(!FILEPATH_OUTPUT.isEmpty()) {
    		    if(exportStats) {
    		    	try {
						isEmptyStats = exportStats(new ArrayList<Query>(result.keySet()), isEmptyStats);
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(-1);
					}
    		    }
    		    if(exportResults) {
    		    	try {
						isEmptyResults = exportResult(result,isEmptyResults);
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(-1);
					}
    		    }
		    }
	    	queries.clear();
		    if(workloadTime) {
		    	System.out.println("Temps total : " + (int)totalTime + "ms");
		    }
			System.exit((int)totalTime);
		}
	}

	private static void displayHelpMessage() {
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
	}
	private static boolean exportExecutionTime(String path, long timeQueries, boolean isEmptyExecution) throws IOException {
		File yourFile = new File(FILEPATH_OUTPUT + File.separator +"exportExecutionTime.csv");
		yourFile.createNewFile(); // if file already exists will do nothing
		FileOutputStream oFile = new FileOutputStream(yourFile, isEmptyExecution);
		BufferedOutputStream bos = new BufferedOutputStream(oFile);
		if(!isEmptyExecution) {
			String header = "Requete;Execution Time(ms)" +System.lineSeparator();
			bos.write(header.getBytes());
			isEmptyExecution = true;
		}
		bos.write((path + ";" + timeQueries+System.lineSeparator()).getBytes());
		bos.close();
		oFile.close();
		return isEmptyExecution;
	}

	private static boolean exportResult(LinkedHashMap<Query, List<String>> result, boolean isEmpty) throws IOException {
		File yourFile = new File(FILEPATH_OUTPUT + File.separator +"exportResults.csv");
		yourFile.createNewFile(); // if file already exists will do nothing
		FileOutputStream oFile = new FileOutputStream(yourFile, isEmpty);
		BufferedOutputStream bos = new BufferedOutputStream(oFile);
		if(!isEmpty) {
			String header = "Requete;Resultats" +System.lineSeparator();
			bos.write(header.getBytes());
			isEmpty = true;
		}
		String queryString, resultString;
		for(Query query : result.keySet()) {
			queryString= query.toString() +";";
			bos.write(queryString.getBytes());
			for(String res : result.get(query)) {
				resultString = res + ";";
				bos.write(resultString.getBytes());
			}
			bos.write(System.lineSeparator().getBytes());
		}
		bos.close();
		oFile.close();
		return isEmpty;
	}

	/**
	 * Export les statistiques r�alis� sur les querys 
	 * @param queries Les query qui seront export� dans le fichier exportStats.csv
	 * @param isEmpty si le fichier est vide (le d�but)
	 * @return return true if the file is not empty anymore
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static boolean exportStats(ArrayList<Query> queries, boolean isEmpty)
			throws IOException {
		File yourFile = new File(FILEPATH_OUTPUT + File.separator +"exportStats.csv");
		yourFile.createNewFile(); // if file already exists will do nothing
		FileOutputStream oFile = new FileOutputStream(yourFile, isEmpty);
		BufferedOutputStream bos = new BufferedOutputStream(oFile);
		if(!isEmpty) {
			String header = "Requete;Triplet_1;Triplet_2;Triplet_3;Triplet_4;Estimation_1;Estimation_2;Estimation_3;Estimation_4" +System.lineSeparator();
			bos.write(header.getBytes());
			isEmpty = true;
		}
		for(Query query : queries) {
			String queryString = query.toString() +";";
			bos.write(queryString.getBytes());
			for(int i=0; i<4;i++) {
				if(i < query.getOrderedWhere().size())
					bos.write(query.getOrderedWhere().get(i).toString().getBytes());
				bos.write(";".getBytes());
			}
			for(int i=0; i<4;i++) {
				if(i < query.getOrderedWhere().size())
					bos.write(Integer.toString(query.getOrderedWhere().get(i).getStat()).getBytes());
				bos.write(";".getBytes());
			}
			bos.write(System.lineSeparator().getBytes());
		}
		bos.close();
		oFile.close();
		return isEmpty;
	}

}