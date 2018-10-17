import java.util.ArrayList;
import java.util.HashMap;

public class OPS extends Index {

	@Override
	public void addElement(int s, int p, int o) {
		if (getIndex().get(o) == null) {
			getIndex().put(o, new HashMap<Integer, ArrayList<Integer>>());
			getIndex().get(o).put(p, new ArrayList<Integer>());
		}
		if (getIndex().get(o).get(p) == null) {
			getIndex().get(o).put(p, new ArrayList<Integer>());
		}
		getIndex().get(o).get(p).add(s);
	}

}
