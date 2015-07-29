package util;

import java.util.*;

public class Histogram {

	private double minBound;
	private double maxBound;
	private double binSize;
	
	List<Integer> counts;
	
	public Histogram(double minBound, double maxBound, double size) {
		
		this.binSize = size;
		this.maxBound = maxBound;
		this.minBound = minBound;
		this.counts = new ArrayList<Integer>();
		
		double diff = maxBound - minBound;
		int numBuckets = (int) (diff / size) + 1;
		for(int i = 0; i < numBuckets; i++) {
			counts.add(0);
		}
		
		
	}
	
	public void addPoint(double x, int amount) {
		int binIndex = getBinIndex(x);
		int newCount = counts.get(binIndex) + amount;
		counts.set(binIndex, newCount);
	}
	
	public void addPoint(double x) {
		addPoint(x, 1);
	}
	
	private int getBinIndex(double x) {
		double pos = (x - minBound) / binSize;
		return (int) pos;
	}
	
	public int getNumBuckets() {
		return counts.size();
	}
	
	public int getCount(int bucketIndex) {
		return counts.get(bucketIndex);
	}
	
	public double getBucketMax(int bucketIndex) {
		double lowerBound = minBound + bucketIndex * binSize;
		double upperBound = lowerBound + binSize;
		return upperBound;
	}
	
	public String toString() {
		String str = "";
		for(int i = 0; i < counts.size(); i++) {
			double lowerBound = minBound + i * binSize;
			double upperBound = lowerBound + binSize;
			int count = counts.get(i);
			String lowerStr = String.format("%.2f", lowerBound);
			String upperStr = String.format("%.2f", upperBound);
			//String boundStr = "[" + lowerStr + "," + upperStr + ")";
			String boundStr = upperStr;
			System.out.println(boundStr + "\t" + count);
		}
		return str;
	}

	public static void main(String[] args) {
		Histogram h = new Histogram(0, 2.5, 0.1);
		h.addPoint(0.00);
		h.addPoint(0.05);
		h.addPoint(0.10);
		h.addPoint(0.12);
		
		h.addPoint(0.45);
		System.out.println(h);
	}
	
}
