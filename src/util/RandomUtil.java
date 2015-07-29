package util;

import java.util.NavigableMap;
import java.util.*;

public class RandomUtil {
	
	private static Random rg = new Random();
	
	
	
	public static double gauss(double mean, double std) {
		return rg.nextGaussian() * std + mean;
	}
	
	public static int randInt(int max) {
		return randInt(0, max);
	}
	
	public static int randInt(int min, int max){
		return min + (int)(Math.random()*max); 
	}
	
}
