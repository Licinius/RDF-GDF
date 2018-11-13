import java.util.HashMap;
import java.util.HashSet;

public class POS extends Index {

	@Override
	public void addElement(int s, int p, int o) {
		if (getIndex().get(p) == null) {
			getIndex().put(p, new HashMap<Integer,HashSet<Integer>>());
			getIndex().get(p).put(o, new HashSet<Integer>());
		}
		if (getIndex().get(p).get(o) == null) {
			getIndex().get(p).put(o, new HashSet<Integer>());
		}
		getIndex().get(p).get(o).add(s);
	}

}
