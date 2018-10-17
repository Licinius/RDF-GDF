import org.openrdf.model.Statement;
import org.openrdf.rio.helpers.RDFHandlerBase;

public class IndexFactory extends RDFHandlerBase{

	private static IndexFactory INSTANCE;
	private static POS POS;
	private static OPS OPS;
	
	public void handleStatement(Statement st) {
		getInstance().getPOS();
	}
	public static IndexFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new IndexFactory();
		}
		return INSTANCE;
	}
	
	public static POS getPOS() {
		if (POS == null) {
			POS = new POS();
		}
		return POS;
	}
	
	public static OPS getOPS() {
		if (OPS == null) {
			OPS = new OPS();
		}
		return OPS;
	}
	
}
