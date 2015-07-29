package run.policy;

import java.util.*;

import minions.GraphLoader;
import minions.TrajectoryLoader;
import models.graphs.TransitionGraph;
import models.trajectory.Trajectory;
import util.DirectedGraph;
import util.FileSystem;

public class PosteriorPSP {

	private static final String TRAJECTORY_DIR = "interpolated";
	private static String MAIN_ASSN_ID = "hoc18";
	private static int PRIOR = 3;
	
	TransitionGraph perfectGraph;
	DirectedGraph<String> roadMap;
	DirectedGraph<String> groundTruth;

	private void run() {
		System.out.println("loading data...");
		List<Trajectory> trajectories = TrajectoryLoader.load(TRAJECTORY_DIR);
		perfectGraph = new TransitionGraph(trajectories);
		
		//perfectGraph = GraphLoader.loadGraph("interpPerfect.txt");
		roadMap = GraphLoader.loadRoadMap();
		groundTruth = GraphLoader.loadGroundTruth();
		
		DirectedGraph<String> psp = new DirectedGraph<String>();
		for(String state : groundTruth.vertexSet()) {
			String bestNext = choseBestNext(state);
			System.out.println(state + ", " + bestNext);
			if(bestNext == null) continue;
			psp.addEdge(state, bestNext);
		}
		
		GraphLoader.savePolicy(psp, "postPsp2.txt");
		//psp.saveGml("postPsp.gml");
		
		//psp.saveEdgeMap("postPsp.txt", false);
		
	}
	
	private String choseBestNext(String state) {
		double bestReward = 0;
		String bestNext = null;
		
		for(String next : roadMap.getOutgoing(state)) {
			if(next.equals(state)) continue;
			
			if(!roadMap.containsEdge(state, next)) continue;
			
			int countPerfect = getCount(state, next);
			
			
			if(bestNext == null || countPerfect > bestReward) {
				bestNext = next;
				bestReward = countPerfect;
			}
		}
		
		return bestNext;
	}
	
	private int getCount(String a, String b) {
		return perfectGraph.getCount(a, b) + PRIOR;
	}
	
	public static void main(String[] args) {
		System.out.println("start");
		FileSystem.setAssnId(MAIN_ASSN_ID );
		new PosteriorPSP().run();
		System.out.println("done");
	}
}
