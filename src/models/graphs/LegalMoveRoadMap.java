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
import models.ast.Program;
import models.ast.Tree;
import models.trajectory.Trajectory;
import models.trajectory.TrajectoryNode;
import util.FileSystem;
import util.IdCounter;
import util.Warnings;

public class LegalMoveRoadMap {
	private static final String MAIN_ASSN_ID = "3";

	private static final String FILE_NAME = "legalMoveRoadMap.txt";

	private Set<String> contiguous;

	private Map<String, Program> programMap;

	// Maps from uniqueId to contiguousIndex
	// EditDistance matrix is saved in index space.
	private HashMap<String, Integer> contiguousMap;


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
		addSingleLinkEdges(programs, astMap, roadMap);

		System.out.println("unseen edges...");
		addUnseenEdges(programMap, roadMap);

		System.out.println("stats...");
		System.out.println(programMap.size());
		System.out.println(roadMap.getInConnected("0").size());
		
		DirectedGraph<String> connectedRoadMap = new DirectedGraph<String>();
		for(String node : roadMap.getInConnected("0")) {
			for(String next : roadMap.getOutgoing(node)) {
				connectedRoadMap.addEdge(node, next);
			}
		}

		System.out.println("saving road map...");
		//connectedRoadMap.saveEdgeMap(FILE_NAME, false);
		Warnings.error("chose where to save it!");

		System.out.println("done");

	}

	private void addSingleLinkEdges(List<Program> programs,
			Map<Tree, Program> astMap, DirectedGraph<String> roadMap) {

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

	private void addUnseenEdges(Map<String, Program> programMap,
			DirectedGraph<String> roadMap) {

		List<Trajectory> trajectories = TrajectoryLoader.load("uniqueTrajectories", "contiguous");
		TransitionGraph transGraph = new TransitionGraph(trajectories);
		int done = 0;

		for(String state : transGraph.getAllStates()) {
			// You can't move from a solution.
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
					roadMap.addEdge(state, next);
					if(!isPerfect(next)) {
						roadMap.addEdge(next, state);
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

	public static void main(String[] args) {
		System.out.println("starting");
		FileSystem.setAssnId(MAIN_ASSN_ID);
		new LegalMoveRoadMap().run();
	}
}
