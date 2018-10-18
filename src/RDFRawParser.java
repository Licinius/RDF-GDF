import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;

public final class RDFRawParser {


	public static void main(String args[]) throws FileNotFoundException {

		Reader reader = new FileReader("500K.rdfxml");
		RDFParser rdfParser = Rio.createParser(RDFFormat.RDFXML);
		HexaStoreFactory hexaStoreFactory = new HexaStoreFactory();
		rdfParser.setRDFHandler(hexaStoreFactory);
		try {
			rdfParser.parse(reader, "");
		} catch (Exception e) {

		}
		finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//System.out.println(hexaStoreFactory.getDictionnary());
		//System.out.println(hexaStoreFactory.getDictionnary().getDictionnary().lastEntry());
		//Too slow, peut-être trop de collision et donc Bucket trop lent
		System.out.println(hexaStoreFactory.getOPS().getThirdColumn(3, 2));
	}

}