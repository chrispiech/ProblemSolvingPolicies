package run.policy;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import minions.GraphLoader;
import minions.ProgramLoader;
import minions.TrajectoryLoader;
import models.ast.Program;
import models.trajectory.Trajectory;
import util.DirectedGraph;
import util.FileSystem;

public class MostPopularPath {

	private static final String MAIN_ASSN_ID = "hoc18";

	private static final String TRAJECTORY_DIR = "interpolated";

	private static final String OUTPUT_FILE = "mostPopularPath.txt";

	private HashMap<String, Program> programMap;
	
	List<Trajectory> trajectories;

	public void run() {
		DirectedGraph<String> roadMap = GraphLoader.loadRoadMap();
		DirectedGraph<String> groundTruth = GraphLoader.loadGroundTruth();
		trajectories = TrajectoryLoader.load(TRAJECTORY_DIR);

		DirectedGraph<String> toSave = new DirectedGraph<String>();
		for(String node : groundTruth.vertexSet()) {
			if(node.equals("0")) continue;
			String next = getMostPopularPathNext(node);
			toSave.addEdge(node, next);
		}
		
		GraphLoader.savePolicy(toSave, OUTPUT_FILE);
	}

	private String getMostPopularPathNext(String node) {
		Map<List<String>, Integer> countMap = new HashMap<List<String>, Integer>();
		for(Trajectory tj : trajectories) {
			if(!tj.endsInState("0")) continue;
			List<String> path = tj.getProgramIds();
			for(int i = 0; i < path.size(); i++) {
				if(path.get(i).equals(node)) {
					List<String> toSoln = path.subList(i, path.size());
					if(!countMap.containsKey(toSoln)) {
						countMap.put(toSoln, 0);
					}
					int oldValue = countMap.get(toSoln);
					int newValue = oldValue + tj.getCount();
					countMap.put(toSoln, newValue);
				}
			}
		}
		int max = 0;
		List<String> argMax = null;
		for(List<String> path : countMap.keySet()) {
			int count = countMap.get(path);
			if(argMax == null || count > max) {
				max = count;
				argMax = path;
			}
		}
		return argMax.get(1);
	}

	public static void main(String[] args) {
		FileSystem.setAssnId(MAIN_ASSN_ID);
		new MostPopularPath().run();
	}
}
