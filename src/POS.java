import java.util.HashMap;

public class POS extends Index {

	@Override
	public void addElement(int s, int p, int o) {
		if (super.getIndex().get(p) == null) {
			super.getIndex().put(p, new HashMap<Integer,HashMap<Integer,Integer>>());
			super.getIndex().get(p).put(o, new HashMap<Integer,Integer>());
		}
		if (super.getIndex().get(p).get(o) == null) {
			super.getIndex().get(p).put(o, new HashMap<Integer,Integer>());
		}
		super.getIndex().get(p).get(o).put(s,s);
	}

}
