import java.util.HashMap;
import java.util.HashSet;

public class POS extends Index {

	@Override
	public void addElement(int s, int p, int o) {
		if (super.getIndex().get(p) == null) {
			super.getIndex().put(p, new HashMap<Integer,HashSet<Integer>>());
			super.getIndex().get(p).put(o, new HashSet<Integer>());
		}
		if (super.getIndex().get(p).get(o) == null) {
			super.getIndex().get(p).put(o, new HashSet<Integer>());
		}
		super.getIndex().get(p).get(o).add(s);
	}

}
