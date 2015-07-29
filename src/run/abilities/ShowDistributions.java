package run.abilities;

import java.util.*;

import util.FileSystem;
import util.Histogram;

public class ShowDistributions {

	private void run() {
		FileSystem.setAssnId("hoc3");
		List<List<AbilityFeatures>> allFolds = AbilityFeatures.loadAllFolds();
		
		Histogram hPerfect = new Histogram(-20, 20, 1.0);
		Histogram hFalse = new Histogram(-20, 20, 1.0);
		
		int count = 0;
		for(List<AbilityFeatures> fold : allFolds) {
			count += fold.size();
			for(AbilityFeatures f : fold) {
				//if(f.lastNode != 0) continue;
				
				double logScore = Math.log(f.pathScore);
				logScore = Math.max(logScore, -3);
				
				if(f.perfectNext) {
					hPerfect.addPoint(logScore);
				} else {
					hFalse.addPoint(logScore);
				}
			}
		}
		
		System.out.println("NUM: " + count);
		
		System.out.println(hPerfect);
		System.out.println(hFalse);
		
		for(int i = 0; i < hPerfect.getNumBuckets(); i++) {
			double max = hPerfect.getBucketMax(i);
			int numPerfect = hPerfect.getCount(i);
			int numNotPerfect = hFalse.getCount(i);
			
			int total = numNotPerfect + numPerfect;
			if(total > 5) {
				double prob = (double)(numPerfect) / total;
				double percent = 100.0 * prob;
				double variance = prob * (1 - prob);
				double sampleVariance = variance / total;
				double sampleStd = 100.0 * Math.sqrt(sampleVariance);
				System.out.println(max + "\t" + percent + "\t" + total + "\t" + sampleStd);
			}
			
		}
	}
	
	public static void main(String[] args) {
		new ShowDistributions().run();
	}
}
