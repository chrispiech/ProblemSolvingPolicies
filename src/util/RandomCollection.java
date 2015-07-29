package util;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class RandomCollection<E> {
    private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
    private final Random random;
    private double total = 0;

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random random) {
        this.random = random;
    }

    public void add(E result, double weight) {
        if (weight <= 0) return;
        total += weight;
        map.put(total, result);
    }

    public E next() {
        double value = random.nextDouble() * total;
        return map.ceilingEntry(value).getValue();
    }
    
    public static void main(String[] args) {
		RandomCollection<String> c = new RandomCollection<String>();
		c.add("chris", 10);
		c.add("laura", 10);
		
		for(int i = 0; i < 10; i++) {
			System.out.println(c.next());
		}
	}
}
