import java.util.HashMap;
import java.util.HashSet;

public class OPS extends Index {

	@Override
	public void addElement(int s, int p, int o) {
		if (getIndex().get(o) == null) {
			getIndex().put(o, new HashMap<Integer,HashSet<Integer>>());
			getIndex().get(o).put(p, new HashSet<Integer>());
		}
		if (getIndex().get(o).get(p) == null) {
			getIndex().get(o).put(p, new HashSet<Integer>());
		}
		getIndex().get(o).get(p).add(s);
	}

}
