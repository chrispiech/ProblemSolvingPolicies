package run.policy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import minions.GraphLoader;
import minions.ProgramLoader;
import minions.TrajectoryLoader;
import models.graphs.TransitionGraph;
import models.trajectory.Trajectory;
import util.DirectedGraph;
import util.FileSystem;
import util.Warnings;

/**
 * Class: Value Iteration
 * ---------------------
 * This class implements the algorithm at:
 * http://artint.info/html/ArtInt_227.html
 */
public class ValueIteration {

	private static final double GAMMA = 0.99;
	private static final double NODE_REWARD = -10;
	private static final double GOAL_REWARD = 100;
	private static final String OUTPUT_FILE = "mdp.txt";
	
	// The legal move graph
	private DirectedGraph<String> roadMap = null;
	
	// The counts of how many students made each transition
	private TransitionGraph transP = null;
	
	// The latent variables learned in value iteration.
	private Map<String, Double> values = new HashMap<String, Double>();
	
	// End value iteration when change is less than epsilon
	double epsilon = 1e-6;

	/**
	 * Method: Solve
	 * -------------
	 * This is the main method in the Value Iteration class. It loads the
	 * data, runs the algorithm, and saves the result.
	 */
	public void solve() {
		roadMap = GraphLoader.loadRoadMap();
		List<Trajectory> trajectories = TrajectoryLoader.load();
		transP = new TransitionGraph(trajectories);
		
		initializeValues();
		runValueIteration();
		savePolicy();
	}

	/**
	 * Method: Run Value Iteration
	 * -------------
	 * Runs the algorithm.
	 */
	private void runValueIteration() {
		while(true) {
			double maxDiff = 0;
			// Make a copy of the value map, to be consistent with
			// the defined algorithm (which uses V_k to compute V_k-1)
			Map<String, Double> nextValueMap = initNextMap();
			
			// Compute a new value for each node in the graph
			for(String s : roadMap.outgoingSet()) {
				double oldValue = values.get(s);
				double newValue = getNewValue(s);
				nextValueMap.put(s, newValue);
				double diff = Math.abs(oldValue - newValue);
				maxDiff = Math.max(diff, maxDiff);
			}
			System.out.println(maxDiff);
			if(maxDiff < epsilon) {
				break;
			}
			// Make V_k+1 into the new V_k
			values = nextValueMap;
		}
	}

	/**
	 * Method: Get New Value
	 * -------------
	 * Calculate V_k(S)
	 */
	private double getNewValue(String curr) {
		Warnings.check(!curr.equals("0"), "shouldn't update goal");
		double max = 0;
		String argMax = null;
		for(String next : roadMap.getOutgoing(curr)) {
			double p = getProbability(curr, next);
			double nextValue = values.get(next);
			double reward = p * (NODE_REWARD + GAMMA * nextValue);
			if(argMax == null || reward > max) {
				max = reward;
				argMax = next;
			}
		}
		return max;
	}

	/**
	 * Method: Get Probability
	 * ---------
	 * Calculate probability of a student making the transition from 
	 * curr to next.
	 */
	private double getProbability(String curr, String next) {
		double transCount = transP.getCount(curr, next);
		double nodeCount = epsilon;
		for(String otherNext : roadMap.getOutgoing(curr)) {
			nodeCount += transP.getCount(curr, otherNext);
		}
		return transCount / nodeCount;
	}

	/**
	 * Method: Save Policy
	 * ---------
	 * For each node, chose the argmax node and then save the policy
	 * to file.
	 */
	private void savePolicy() {
		DirectedGraph<String> policy = new DirectedGraph<String>();
		DirectedGraph<String> truth = GraphLoader.loadGroundTruth();
		for(String s : truth.outgoingSet()) {
			String n = argMax(s);
			policy.addEdge(s, n);
		}
		GraphLoader.savePolicy(policy, OUTPUT_FILE);
	}
	
	/**
	 * Method: Arg Max
	 * ---------
	 * See line 22 in the algorithm
	 */
	private String argMax(String s) {
		double max = 0;
		String argMax = null;
		for(String next : roadMap.getOutgoing(s)) {
			double value = values.get(next);
			if(argMax == null || value > max) {
				max = value;
				argMax = next;
			}
		}
		return argMax;
	}

	/**********************************************************
	 * Initialization
	 **********************************************************/
	
	private Map<String, Double> initNextMap() {
		Map<String, Double> tempMap = new HashMap<String, Double>();
		for(String s : values.keySet()) {
			tempMap.put(s, values.get(s));
		}
		return tempMap;
	}
	
	private void initializeValues() {
		for(String s : roadMap.vertexSet()) {
			values.put(s, 1.0);
		}
		values.put("0", GOAL_REWARD);
	}
	
	public static void main(String[] args) {
		FileSystem.setAssnId("hoc3");
		new ValueIteration().solve();
	}
}


