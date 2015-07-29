package run.policy;

import java.util.*;

import minions.GraphLoader;
import util.DirectedGraph;
import util.FileSystem;

public class MakePriorPolicy {

	private static String MAIN_ASSN_ID = "hoc18";
	private static int PRIOR = 10;
	private static int PERFECT_REWARD = 100;
	private static int PASS_REWARD = 50;

	DirectedGraph<String> failGraph;
	DirectedGraph<String> passGraph;
	DirectedGraph<String> perfectGraph;
	DirectedGraph<String> roadMap;

	private void run() {
		System.out.println("start");
		FileSystem.setAssnId(MAIN_ASSN_ID );

		System.out.println("loading data...");
		failGraph = GraphLoader.loadGraph("interpFail.txt");
		passGraph = GraphLoader.loadGraph("interpPass.txt");
		perfectGraph = GraphLoader.loadGraph("interpPerfect.txt");
		roadMap = GraphLoader.loadRoadMap();

		Set<String> allNodes = new HashSet<String>();
		allNodes.addAll(failGraph.vertexSet());
		allNodes.addAll(passGraph.vertexSet());
		allNodes.addAll(perfectGraph.vertexSet());

		DirectedGraph<String> psp = new DirectedGraph<String>();
		for(String state : allNodes) {
			String bestNext = choseBestNext(state);
			if(bestNext == null) continue;
			System.out.println(state + ", " + bestNext);
			psp.addEdge(state, bestNext);
		}

		GraphLoader.savePolicy(psp, "priorPsp.txt");

		System.out.println("done");

	}

	private String choseBestNext(String state) {
		double bestReward = 0;
		String bestNext = null;

		for(String next : roadMap.getOutgoing(state)) {
			if(next.equals(state)) continue;

			int countPerfect = getCount(perfectGraph, state, next);
			int countPass = getCount(passGraph, state, next);
			int countFail = getCount(failGraph, state, next);

			double sum = countFail + countPass + countPerfect;

			double probPerfect = countPerfect / sum;
			double probPass = countPass / sum;

			double reward = 0;
			reward += probPerfect * PERFECT_REWARD;
			reward += probPass * PASS_REWARD;

			if(bestNext == null || reward > bestReward) {
				bestNext = next;
				bestReward = reward;
			}
		}

		return bestNext;
	}

	private int getCount(DirectedGraph<String> graph, String a, String b) {
		return (int) graph.getWeight(a, b) + PRIOR;
	}

	public static void main(String[] args) {
		new MakePriorPolicy().run();
	}
}
