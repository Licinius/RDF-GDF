import java.util.ArrayList;
import java.util.HashMap;

public class POS extends Index {

	@Override
	public void addElement(int s, int p, int o) {
		if (super.getIndex().get(p) == null) {
			super.getIndex().put(p, new HashMap<Integer, ArrayList<Integer>>());
			super.getIndex().get(p).put(o, new ArrayList<Integer>());
		}
		if (super.getIndex().get(p).get(o) == null) {
			super.getIndex().get(p).put(o, new ArrayList<Integer>());
		}
		super.getIndex().get(p).get(o).add(s);
	}

}
