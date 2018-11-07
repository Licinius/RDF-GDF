import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ThreadFile implements Runnable {

	private HexaStore hexastore;
	private ArrayList<Query> listQuery;
	private long totalTime;
	private LinkedHashMap<Query, List<String>> result;
	
	public ThreadFile(HexaStore hexastore, ArrayList<Query> listQuery) {
		super();
		this.hexastore = hexastore;
		this.listQuery = listQuery;
	}
	
	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		this.result = this.hexastore.execute(listQuery);
		long endTime = System.currentTimeMillis();
		this.totalTime = endTime-startTime;
	}
	
	public long getTotalTime() {
		return this.totalTime;
	}

	public LinkedHashMap<Query, List<String>> getResult() {
		return result;
	}

	

}
