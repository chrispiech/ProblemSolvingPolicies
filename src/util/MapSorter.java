package util;

import java.util.*;


public class MapSorter<T> {

	public List<T> sortInt(Map<T, Integer> map) {
		IntValueComparator bvc =  new IntValueComparator(map);
		List<T> list = new ArrayList<T>();
		list.addAll(map.keySet());
		Collections.sort(list, bvc);
		return list;
	}
	
	public List<T> sortDouble(Map<T, Double> map) {
		DoubleValueComparator bvc =  new DoubleValueComparator(map);
		List<T> list = new ArrayList<T>();
		list.addAll(map.keySet());
		Collections.sort(list, bvc);
		return list;
	}
	
	class IntValueComparator implements Comparator<T> {

		Map<T, Integer> map;
		public IntValueComparator(Map<T, Integer> map) {
			this.map = map;
		}

		// Note: this comparator imposes orderings that are inconsistent with equals.    
		public int compare(T a, T b) {
			if (map.get(a) < map.get(b)) {
				return 1;
			} else {
				return -1;
			} // returning 0 would merge keys
		}
	}
	
	class DoubleValueComparator implements Comparator<T> {

		Map<T, Double> map;
		public DoubleValueComparator(Map<T, Double> map) {
			this.map = map;
		}

		// Note: this comparator imposes orderings that are inconsistent with equals.    
		public int compare(T a, T b) {
			if (map.get(a) < map.get(b)) {
				return 1;
			} else {
				return -1;
			} // returning 0 would merge keys
		}
	}
	
	public static Map<String, List<String>> reverseMap(Map<String, String> toReverse) {
		Map<String, List<String>> reverseMap = new HashMap<String, List<String>>();
		for(String key : toReverse.keySet()) {
			String value = toReverse.get(key);
			if(!reverseMap.containsKey(value)) {
				reverseMap.put(value, new ArrayList<String>());
			}
			reverseMap.get(value).add(key);
		}
		return reverseMap;
	}

	public static Map<String, String> reverse1To1Map(Map<String, String> toReverse) {
		Map<String, String> reverseMap = new HashMap<String, String>();
		for(String key : toReverse.keySet()) {
			String value = toReverse.get(key);
			if(reverseMap.containsKey(value)) {
				throw new RuntimeException("assumes 1 to 1 mapping");
			}
			reverseMap.put(value, key);
		}
		return reverseMap;
	}
	
}
