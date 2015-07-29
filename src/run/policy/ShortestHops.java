package run.policy;

import java.util.*;

import edits.TreeEditDistance;
import minions.GraphLoader;
import minions.ProgramLoader;
import minions.TrajectoryLoader;
import models.ast.Program;
import models.ast.Tree;
import models.trajectory.Trajectory;
import util.DirectedGraph;
import util.FileSystem;

public class ShortestHops {

	private static String MAIN_ASSN_ID = "hoc18";

	Map<String, Integer> countMap = null;
	DirectedGraph<String> roadMap = null;
	DirectedGraph<String> truth = null;
	
	private void run() {
		System.out.println("start");
		FileSystem.setAssnId(MAIN_ASSN_ID );
		
		//countMap = nodeCount();
		roadMap = GraphLoader.loadRoadMap();
		truth = GraphLoader.loadGroundTruth();
		
		double wFreq = 1;
		double wEdit = 1;
	
		DirectedGraph<String> policy = getPolicy(wFreq, wEdit);
		
		GraphLoader.savePolicy(policy, "shortestPath.txt");

	}

	private DirectedGraph<String> getPolicy(double wFreq, double wEdit) {
		DirectedGraph<String> policy = new DirectedGraph<String>();
		int done = 0;
		for(String node : truth.outgoingSet()) {
			String argMax = null;
			double max = 0;
			for(String next : roadMap.getOutgoing(node)) {
				double frequency = 0;// getFrequency(countMap, next);
				if(!roadMap.hasPath(next, "0")) continue;
				double editDist = roadMap.getShortestPathCost(next, "0");
				double score = frequency * wFreq - editDist * wEdit;
				if(argMax == null || score > max) {
					argMax = next;
					max = score;
				}
			}
			policy.addEdge(node, argMax);
			System.out.println("policy made: " + ++done);
		}
		return policy;
	}

	private Integer getFrequency(Map<String, Integer> countMap, String next) {
		if(!countMap.containsKey(next)) return 0;
		return countMap.get(next);
	}
	
	public Map<String, Integer> nodeCount() {
		List<Trajectory> trajectories = TrajectoryLoader.load();
		System.out.println("next");
		Map<String, Integer> nodeCount = new HashMap<String, Integer>();
		for(Trajectory t : trajectories) {
			List<String> visited = t.getProgramIds();
			List<String> noCycle = new ArrayList<String>();
			for(int i = 0; i < visited.size(); i++) {
				String state = visited.get(i);
				noCycle.add(state);
				int lastIndex = visited.lastIndexOf(state);
				i = lastIndex;
			}

			for(String v : noCycle) {
				if(!nodeCount.containsKey(v)) {
					nodeCount.put(v, 0);
				}
				int newCount = getFrequency(nodeCount, v) + t.getCount();
				nodeCount.put(v, newCount);
			}
		}
		return nodeCount;
	}

	public static void main(String[] args) {
		new ShortestHops().run();
	}
}
