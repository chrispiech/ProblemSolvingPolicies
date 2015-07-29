package run.evaluation;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.math3.util.Pair;

import minions.GraphLoader;
import minions.ProgramLoader;
import models.ast.Program;
import util.DirectedGraph;
import util.FileSystem;
import util.MapSorter;
import util.Warnings;

/**
 * Class: Evaluate Policy
 * ----------------------
 * Given a ground truth graph, and a policy graph, test how well the policy
 * does at recreating the ground truth. Give stats for number of edges correct
 * and also number of edges, weighted by student counts.
 * @author Chris Piech
 */
public class EvaluateAllPolicies {

	// constants
	private static final String MAIN_ASSN_ID = "hoc4";

	// instance variables;
	private DirectedGraph<String> groundTruth = null;
	private Map<String, Integer> countMap = null;
	private Map<String, Pair<String, Double>> statMax; 

	private void run() {
		FileSystem.setAssnId(MAIN_ASSN_ID);

		// load the necessary graphs
		loadGraphs();
		statMax = new TreeMap<String, Pair<String, Double>>();

		// test the policy graph against the ground truth
		File policyDir = new File(FileSystem.getAssnDir(), "policies");
		for(File policyFile : FileSystem.listFiles(policyDir)) {
			String ext = FileSystem.getExtension(policyFile.getName());
			if(!ext.equals("txt")) continue;
			String name = policyFile.getName();
			Map<String, String> policy =  FileSystem.getFileMapString(policyFile);
			generateStats(name, policy);
		}
		
		System.out.println("");
		System.out.println("Best:");
		for(String test : statMax.keySet()) {
			System.out.println(test + ": " + statMax.get(test).getFirst());
		}
	}

	private void loadGraphs() {
		groundTruth = GraphLoader.loadGroundTruth();
		countMap = ProgramLoader.loadCountMap();
	}

	private void generateStats(String name, Map<String, String> policy) {
		System.out.println("\n" + name + ":");
		presentResult(name, "weighted", percentCorrect(policy, true));
	}
	
	private void presentResult(String algName, String testName, double stat) {
		System.out.println(stat);
		Pair<String, Double> curr = new Pair<String, Double>(algName, stat);
		if(!statMax.containsKey(testName)) {
			statMax.put(testName, curr);
		}
		double best = statMax.get(testName).getSecond();
		if(stat > best) {
			statMax.put(testName, curr);
		}
	}

	

	private double percentCorrect(Map<String, String> policy, 
			boolean weighted) {
		int numTested = 0;
		int numCorrect = 0;
		for(String key : groundTruth.vertexSet()) {
			if(key.equals("0")) continue;
			
			if(groundTruth.getOutgoing(key).size() == 0) continue;
			
			String guess = policy.get(key);
			int weight = weighted ? countMap.get(key) : 1;
			if(groundTruth.getOutgoing(key).contains(guess)) {
				numCorrect += weight;
			}
			numTested += weight;
		}
		System.out.println("(" + numTested + ")");
		return 100.0 * numCorrect / numTested;
	}

	public static void main(String[] args) {
		new EvaluateAllPolicies().run();
	}

}
