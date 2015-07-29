package util;

import java.util.*;

/*
 * PQueue:
 * -------
 * My pqueue. Higher numbers come out first.
 */
public class PQueue<T> {

	private PriorityQueue<T> pqueue;
	private Map<T, Double> priorityMap;

	public PQueue() {
		priorityMap = new HashMap<T, Double>(); 
		pqueue = new PriorityQueue<T>(10, new PQueueComparator(false));
	
	}
	
	public PQueue(boolean lessIsMore) {
		priorityMap = new HashMap<T, Double>(); 
		pqueue = new PriorityQueue<T>(10, new PQueueComparator(lessIsMore));
	}

	public void add(T obj, double priority) {
		if(priorityMap.containsKey(obj)) {
			throw new RuntimeException("can you really put the same obj in twice?");
		}
		priorityMap.put(obj, priority);
		pqueue.add(obj);
	}
	
	public T dequeue() {
		T obj = pqueue.remove();
		priorityMap.remove(obj);
		return obj;
	}
	
	public boolean isEmpty() {
		return pqueue.isEmpty();
	}
	
	/***************************************
	 * PRIVATE PARTS
	 ***************************************/

	private class PQueueComparator implements Comparator<T> {

		private boolean lessIsMore = false;
		
		public PQueueComparator(boolean lessIsMore) {
			this.lessIsMore = lessIsMore;
		}

		@Override
		public int compare(T x, T y) {
			double priorityX = priorityMap.get(x);
			double priorityY = priorityMap.get(y);
			double diff = priorityY - priorityX;
			
			int result = 0;
			if(diff < 0) {
				result = -1;
			} else if(diff > 0) {
				result = 1;
			}
			if(lessIsMore) {
				result *= -1;
			}
			return result;
		}
	}

	/***************************************
	 * TEST
	 ***************************************/

	public static void main(String[] args) {
		System.out.println("testing pqueue");
		PQueue<String> test = new PQueue<String>();
		test.add("chris", 20);
		test.add("laura", 100);
		test.add("tie", 20);
		test.add("bob", 0);
		test.add("joe", -5.2);
		
		while(!test.isEmpty()) {
			String x = test.dequeue();
			System.out.println(x);
		}
		
		test.add("chris", 200);
		test.add("laura", 5);
		test.add("joe", 400);
		
		while(!test.isEmpty()) {
			String x = test.dequeue();
			System.out.println(x);
		}
		
	}

}
