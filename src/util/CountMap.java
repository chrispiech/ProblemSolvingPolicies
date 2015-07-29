package util;

import java.util.*;

public class CountMap<T> {

	private Map<T, Integer> countMap = new HashMap<T, Integer>();
	
	public void add(T key, int amount) {
		if(!countMap.containsKey(key)) {
			countMap.put(key, 0);
		}
		int newAmount = countMap.get(key) + amount;
		countMap.put(key, newAmount);
	}
	
	public Map<T, Integer> getMap() {
		return countMap;
	}
	
	public Set<T> keySet() {
		return countMap.keySet();
	}
	
	public int get(T key) {
		return countMap.get(key);
	}
}
