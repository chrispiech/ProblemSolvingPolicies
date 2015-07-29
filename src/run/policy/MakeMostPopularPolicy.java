package run.policy;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import minions.GraphLoader;
import minions.ProgramLoader;
import minions.TrajectoryLoader;
import models.ast.Program;
import util.DirectedGraph;
import util.FileSystem;

public class MakeMostPopularPolicy {

	private static final String MAIN_ASSN_ID = "17";

	private HashMap<String, Program> programMap;

	public void run() {
		DirectedGraph<String> roadMap = GraphLoader.loadRoadMap();
		List<Program> programs = ProgramLoader.loadPrograms("contiguous");
		programMap = new HashMap<String, Program>();
		for(Program p : programs) {
			String id = p.getId();
			programMap.put(id, p);
		}

		Map<String, String> popularBestNext = new HashMap<String, String>();
		for(String node : roadMap.vertexSet()) {
			String best = null;
			int bestCount = 0;
			for(String option : roadMap.getOutgoing(node)){
				int optionCount = programMap.get(option).getCount();
				if(best == null || optionCount > bestCount) {
					bestCount = optionCount;
					best = option;
				}
			}
			if(best != null) {
				popularBestNext.put(node, best);
				System.out.println(node + " -> " +best);
			}
		}
		File graphDir = new File(FileSystem.getAssnDir(), "graphs");
		FileSystem.createMapFile(graphDir, "popularNext.txt", popularBestNext);
	}

	public static void main(String[] args) {
		FileSystem.setAssnId(MAIN_ASSN_ID);
		new MakeMostPopularPolicy().run();
	}
}
