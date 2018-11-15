import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
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
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.PrintUtil;



public class QueryAndReasonOnLocalRDF {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		String pathToOntology = "res/1M.rdfxml";
		Reader reader = new FileReader(pathToOntology);
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
		Path dir = FileSystems.getDefault().getPath("res/queries/10000");
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
		/**
		 * 
		 * Create a data model and load file
		 * 
		 */

		Model model = ModelFactory.createDefaultModel();



		InputStream in = FileManager.get().open(pathToOntology);

		Long start = System.currentTimeMillis();

		model.read(in, null);

		System.out.println("Import time : " + (System.currentTimeMillis() - start));

		System.out.println("Lancement queries, si les resultats de jenna sont différents alors print la query ! ");
		for(Query q : queries) {
//			System.out.println("Query : " + q.toString());
//			System.out.println("Lancement queries");
	    	List<String> result = hexaStore.execute(q); //Execute toutes les queries du document
			com.hp.hpl.jena.query.Query query = QueryFactory.create( " PREFIX  rdf: <http://db.uwaterloo.ca/~galuc/wsdbm/#>"
					+q.toString());
			start = System.currentTimeMillis();

			QueryExecution qexec = QueryExecutionFactory.create(query, model);
			try {
				ResultSet rs =  ResultSetFactory.copyResults(qexec.execSelect());
				List<String> resJenna = ResultSetFormatter.toList(rs).stream().map(t -> t.get("v0").toString()).collect(Collectors.toList());
				result.removeAll(resJenna);
				if(result.size() > 0) {
					System.out.println(query);
					System.out.println(result.size() + " : " + result);
				}
			} finally {

				qexec.close();
			}
		}
	System.out.println("Done");	
	}
}
