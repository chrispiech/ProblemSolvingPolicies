package models.graphs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import javax.management.RuntimeErrorException;

import edits.TreeEditDistance;
import minions.BlocklyHelper;
import minions.IllegalMoveMinion;
import minions.ProgramLoader;
import minions.TrajectoryLoader;
import minions.TreeJasonizer;
import util.DirectedGraph;
import util.Warnings;
import models.ast.Program;
import models.ast.Tree;
import models.trajectory.Trajectory;
import models.trajectory.TrajectoryNode;
import util.FileSystem;
import util.IdCounter;

public class IllegalMoveRoadMap {
	private static final String MAIN_ASSN_ID = "17";

	private static final String FILE_NAME = "illegalMoveRoadMap.txt";

	private Map<String, Program> programMap;

	private Set<String> contiguous;

	// Maps from uniqueId to contiguousIndex
	// EditDistance matrix is saved in index space.
	private HashMap<String, Integer> contiguousMap;

	// track all programs that we create (they should be illegal blocky code)...
	private Map<Tree, String> unseenAstMap = new HashMap<Tree, String>();

	// track how single hops map to paths through illegal node space...
	private Map<String, List<String>> unseenPathMap = new HashMap<String, List<String>>();

	// the road map that we are creating
	DirectedGraph<String> roadMap = new DirectedGraph<String>();

	public void run() {
		System.out.println("load programs: ");
		List<Program> programs = ProgramLoader.loadPrograms("contiguous");
		contiguous = new HashSet<String>();
		contiguousMap = new HashMap<String, Integer>();
		programMap = new HashMap<String, Program>();
		Map<Tree, Program> astMap = new HashMap<Tree, Program>();
		IdCounter counter = new IdCounter();
		for(Program p : programs) {
			contiguous.add(p.getId());
			contiguousMap.put(p.getId(), counter.getNextId());
			programMap.put(p.getId(), p);
			astMap.put(p.getAst(), p);
		}

		System.out.println("single link...");
		addSingleLinkEdges(programs, astMap);

		System.out.println("unseen edges...");
		addUnseenEdges(programMap);

		System.out.println("stats...");
		System.out.println(programMap.size());
		System.out.println(roadMap.getInConnected("0").size());

		System.out.println("saving new asts...");
		saveNewAsts();

		System.out.println("saving large jump map...");
		saveUnseenPaths();
		
		DirectedGraph<String> connectedRoadMap = new DirectedGraph<String>();
		for(String node : roadMap.getInConnected("0")) {
			for(String next : roadMap.getOutgoing(node)) {
				connectedRoadMap.addEdge(node, next);
			}
		}

		System.out.println("saving road map...");
		Warnings.error("chose where to save it!");
		//roadMap.saveEdgeMap(FILE_NAME, false);

		System.out.println("done");

	}

	private void addSingleLinkEdges(List<Program> programs,
			Map<Tree, Program> astMap) {

		int done = 0;
		for(Program p : programs) {
			Tree ast = p.getAst();
			Set<Tree> legalMoves = BlocklyHelper.getLegalMoves(ast);

			// You can't move from a solution.
			if(p.perfectOnUnitTests()) continue;

			for(Tree legalMove : legalMoves) {
				Program legalNext = astMap.get(legalMove);
				if(legalNext != null) {
					String a = p.getId();
					String b = legalNext.getId();

					if(a.equals(b)) continue;
					roadMap.addEdge(p.getId(), legalNext.getId());
				} 
			}

			done++;
			if(done % 100 == 0) System.out.println(done);
		}
	}

	private void addUnseenEdges(Map<String, Program> programMap) {

		IdCounter counter = new IdCounter();


		List<Trajectory> trajectories = TrajectoryLoader.load("uniqueTrajectories", "contiguous");
		TransitionGraph transGraph = new TransitionGraph(trajectories);
		int done = 0;

		for(String state : transGraph.getAllStates()) {

			if(isPerfect(state)) continue;

			for(String next : transGraph.getNextStates(state)) {
				if(state.equals(next)) continue;

				// Ignore those infrequent edges...
				if(transGraph.getCount(state, next) <= 3)  continue;

				if(!programMap.containsKey(state)) continue;
				if(!programMap.containsKey(next)) continue;
				if(roadMap.containsEdge(state, next)) continue;

				Program a = programMap.get(state);
				Program b = programMap.get(next);

				Tree astA = a.getAst();
				Tree astB = b.getAst();

				if(IllegalMoveMinion.isMinLegalMove(astA, astB)) {

					List<Tree> inter = IllegalMoveMinion.getItermediateMoves(astA, astB);
					List<String> illegalStates = new ArrayList<String>();
					for(int i = 0; i < inter.size(); i++) {
						Tree t = inter.get(i);
						if(!unseenAstMap.containsKey(t)) {
							String newId = "i" + counter.getNextId();
							unseenAstMap.put(t, newId);
						} 
						illegalStates.add(unseenAstMap.get(t));
					}
					String key = state + ":" + next;
					unseenPathMap.put(key, illegalStates);

					// now create a list of the node ids in the path, including legals
					List<String> pathStates = new ArrayList<String>();
					pathStates.add(state);
					pathStates.addAll(illegalStates);
					pathStates.add(next);

					// add the new large path to the road map
					for(int i = 1; i < pathStates.size(); i++) {
						String x = pathStates.get(i - 1);
						String y = pathStates.get(i);
						
						roadMap.addEdge(x, y);

						if(!isPerfect(y)){
							roadMap.addEdge(y, x);
						}
					}
				}
			}
			done += 1;
			if(done % 100 == 0) System.out.println(done);
		}
	}
	
	private boolean isPerfect(String state) {
		if(!programMap.containsKey(state)) return false;
		if(!programMap.get(state).perfectOnUnitTests()) return false;
		return true;
	}

	private void saveNewAsts() {
		File unseenDir = new File(FileSystem.getAssnDir(), "unseen");

		for(Tree t : unseenAstMap.keySet()) {
			String id = unseenAstMap.get(t);
			String treeString = TreeJasonizer.jsonify(t).toString(4);
			FileSystem.createFile(unseenDir, id + ".json", treeString);
		}
	}

	private void saveUnseenPaths() {
		File largeJumpDir = new File(FileSystem.getAssnDir(), "unseenPaths");

		for(String key : unseenPathMap.keySet()) {
			String fileText = "";
			for(String node : unseenPathMap.get(key)) {
				fileText += node + "\n";
			}
			FileSystem.createFile(largeJumpDir, key + ".txt", fileText);
		}
	}

	public static void main(String[] args) {
		System.out.println("starting");
		FileSystem.setAssnId(MAIN_ASSN_ID);
		new IllegalMoveRoadMap().run();
	}
}
