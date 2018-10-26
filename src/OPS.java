import java.util.HashMap;

public class OPS extends Index {

	@Override
	public void addElement(int s, int p, int o) {
		if (getIndex().get(o) == null) {
			getIndex().put(o, new HashMap<Integer,HashMap<Integer,Integer>>());
			getIndex().get(o).put(p, new HashMap<Integer,Integer>());
		}
		if (getIndex().get(o).get(p) == null) {
			getIndex().get(o).put(p, new HashMap<Integer,Integer>());
		}
		getIndex().get(o).get(p).put(s,s);
	}

}
