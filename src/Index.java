import java.util.HashMap;

public abstract class Index {
	
	private HashMap<Integer, HashMap<Integer, HashMap<Integer,Integer>>> index;
	
	public Index() {
		index = new HashMap<>();
	}
	
	public HashMap<Integer, HashMap<Integer, HashMap<Integer,Integer>>> getIndex() {
		return index;
	}
	
	public abstract void addElement(int a, int b, int c);
	
	/**
	 * Return the thirdColumn
	 * @param n First column
	 * @param m Second Column
	 * @return
	 */
	public HashMap<Integer,Integer> getThirdColumn(int n,int m){
		if(index.get(n)==null)
			return null;
		else
			return index.get(n).get(m);
	}
	
	public int getStat(int n){
		int res = 0;
		for(HashMap<Integer,Integer> value: index.get(n).values()){
			res += value.size();
		}
		return res;
	}
	
	public int getStat(int n,int m){
		if(index.get(n)==null)
			return 0;
		else if(getThirdColumn(n, m)==null)
			return 0;
		return getThirdColumn(n,m).size();
		
	}
	
	public String toString() {
		return index.toString();
	}
	
}
