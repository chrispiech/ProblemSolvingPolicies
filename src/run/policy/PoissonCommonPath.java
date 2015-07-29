package run.policy;

import java.io.File;
import java.util.*;

import minions.GraphLoader;
import minions.ProgramLoader;
import minions.TrajectoryLoader;
import minions.UnseenPathMinion;
import models.trajectory.Trajectory;
import util.DirectedGraph;
import util.Edge;
import util.FileSystem;
import util.MapSorter;

public class PoissonCommonPath {

	private static final String MAIN_ASSN_ID = "hoc2";
	private static final String TRAJECTORY_DIR = "interpolated";
	private static final double EPSILON = 0.001;
	private static final String OUTPUT_FILE = "nodeRank.txt";

	private DirectedGraph<String> roadMap;
	private DirectedGraph<String> groundTruth;

	public void run() {
		System.out.println("node rank");
		// Get the number of students who visit each node.
		List<Trajectory> trajectories = TrajectoryLoader.load(TRAJECTORY_DIR);
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
				int newCount = nodeCount.get(v) + t.getCount();
				nodeCount.put(v, newCount);
			}
		}
		roadMap = GraphLoader.loadRoadMap();
		groundTruth = GraphLoader.loadGroundTruth();

		// Make a graph where each edge has cost = coherence of the next node
		System.out.println("make coherenceCost");
		DirectedGraph<String> coherenceCost = new DirectedGraph<String>();
		for(String node : roadMap.vertexSet()) {
			double count = EPSILON;
			if(nodeCount.containsKey(node)) {
				count = nodeCount.get(node);
			}

			for(String previous : roadMap.getIncoming(node)) {
				
				double cost = 1.0 / (count);

				coherenceCost.addEdge(previous, node, cost);
			}
		}
		


		// Turn it all into a prediction
		System.out.println("predict");
		DirectedGraph<String> bestNext = new DirectedGraph<String>();
		String soln = "0";
		
		for(String node : groundTruth.vertexSet()) {
			if(node.equals(soln)) continue;
			if(groundTruth.getOutgoing(node).size() == 0) continue;

			
			List<Edge<String>> path = coherenceCost.getShortestPath(node, soln);
			if(path != null) {

				Edge<String> firstEdge = path.get(0);
				String next = firstEdge.getTarget();
				bestNext.addEdge(node, next);
			}
		}

		System.out.println("connected: " + roadMap.getInConnected("0").size());
		System.out.println("node count size: " + nodeCount.size());
		System.out.println("best next size: " + bestNext.vertexSet().size());

		DirectedGraph<String> toSave = new DirectedGraph<String>();

		for(int i = 0; i < 1000; i++) {
			String node = "" + i;
			if(!bestNext.containsVertex(node)) continue;
			String next = null;
			for(String s : bestNext.getOutgoing(node)) {
				next = s;
				break;
			}
			if(next != null) {
				toSave.addEdge(node, next);
			}
		}

		GraphLoader.savePolicy(toSave, OUTPUT_FILE);
		
		System.out.println("done");
	}

	public static void main(String[] args) {
		FileSystem.setAssnId(MAIN_ASSN_ID);
		new PoissonCommonPath().run();
	}
}
