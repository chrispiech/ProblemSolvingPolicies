package run.abilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import util.FileSystem;

class AbilityFeatures {

	private static final int FOLDS = 10;
	static double maxPathScore = 0;
	static double minPathScore = 0;

	int hops;
	double pathScore;
	int lastNode;
	boolean attemptNext;
	boolean perfectNext;

	public AbilityFeatures(String line) {
		String[] cols = line.split(",");
		hops = Integer.parseInt(cols[0]);
		double rawPathScore = Double.parseDouble(cols[1]);
		pathScore = rawPathScore;
		lastNode = Integer.parseInt(cols[2]);
		attemptNext = Boolean.parseBoolean(cols[3]);
		perfectNext = Boolean.parseBoolean(cols[4]);

		if(!Double.isInfinite(pathScore)) {
			if(pathScore < minPathScore) minPathScore = pathScore;
			if(pathScore > maxPathScore) maxPathScore = pathScore;
		}

	}

	public void normalize() {
		pathScore = (pathScore - minPathScore) / (maxPathScore - minPathScore);
	}

	public boolean getLabel() {
		return perfectNext;
	}
	
	public static List<List<AbilityFeatures>> loadAllFolds() {
		List<List<AbilityFeatures>> allFolds = 
				new ArrayList<List<AbilityFeatures>>();
		for(int i = 0; i < FOLDS; i++) {
			System.out.println("loading fold " + i);
			List<AbilityFeatures> fold = loadFold(i + ".txt");
			
			allFolds.add(fold);
		}
		return allFolds;
	}

	private static List<AbilityFeatures> loadFold(String fileName) {
		File nextDir = new File(FileSystem.getAssnDir(), "nextProblem");
		File foldDir = new File(nextDir, "prediction");
		File foldFile = new File(foldDir, fileName);

		List<AbilityFeatures> features = new ArrayList<AbilityFeatures>();
		for(String line : FileSystem.getFileLines(foldFile)){
			AbilityFeatures f = new AbilityFeatures(line);
			
			/*if(f.lastNode == 0 && f.hops == 1) {
				continue;
			}*/
			
			if(Double.isInfinite(f.pathScore)) {
				continue;
			}
			
			features.add(f);
			
		}
		return features;
	}
}
