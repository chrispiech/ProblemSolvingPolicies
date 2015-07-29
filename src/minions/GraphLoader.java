package minions;

import java.io.File;
import java.util.Map;

import util.DirectedGraph;
import util.FileSystem;

public class GraphLoader {

	public static DirectedGraph<String> loadRoadMap() {
		//System.out.println("loading road map...");
		return loadGraph("roadMap.txt");
	}
	
	public static DirectedGraph<String> loadGraph(String name) {
		File graphDir = new File(FileSystem.getAssnDir(), "graphs");
		File graphFile = new File(graphDir, name);
		return DirectedGraph.loadEdgeMap(graphFile);
	}
	
	public static void saveGraph(DirectedGraph<String> graph, String name) {
		File assnDir = FileSystem.getAssnDir();
		File graphDir = new File(assnDir, "graphs");
		graphDir.mkdirs();
		File graphFile = new File(graphDir, name);
		graph.saveEdgeMap(graphFile, false);
	}
	
	public static void savePolicy(DirectedGraph<String> policy, String name) {
		File assnDir = FileSystem.getAssnDir();
		File policyDir = new File(assnDir, "policies");
		File policyFile = new File(policyDir, name);
		policy.saveEdgeMap(policyFile, false);
	}

	public static DirectedGraph<String> loadGroundTruth() {
		File groundDir = new File(FileSystem.getAssnDir(), "groundTruth");
		File groundFile = new File(groundDir, "groundTruth.txt");
		DirectedGraph<String> gt = DirectedGraph.loadEdgeMap(groundFile);
		for(int i = 1; i < 11; i++) {
			if(i == 8) continue;
			String next = gt.getOutgoing(i + "").iterator().next();
			System.out.println(i + " " + next);
		}
		return gt;
	}
	
	public static DirectedGraph<String> loadSuperRater() {
		File groundDir = new File(FileSystem.getAssnDir(), "groundTruth");
		File groundFile = new File(groundDir, "superRater.txt");
		return DirectedGraph.loadEdgeMap(groundFile);
	}

	public static DirectedGraph<String> loadPolicy(String name) {
		File assnDir = FileSystem.getAssnDir();
		File policyDir = new File(assnDir, "policies");
		File policyFile = new File(policyDir, name);
		return DirectedGraph.loadEdgeMap(policyFile);
	}

	
	
}
