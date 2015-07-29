package util;

public class IdCounter {

	private int nextId;
	
	public IdCounter() {
		nextId = 0;
	}
	
	public IdCounter(int i) {
		nextId = i;
	}

	public int getNextId() {
		int toReturn = nextId;
		nextId += 1;
		return toReturn;
	}
	
	public String getNextIdStr() {
		return getNextId() + "";
	}
	
	public void setNextId(int nextId) {
		this.nextId = nextId;
	}
	
}
