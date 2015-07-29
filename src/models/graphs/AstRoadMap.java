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
import minions.ProgramLoader;
import minions.TrajectoryLoader;
import util.DirectedGraph;
import util.Edge;
import util.Warnings;
import models.ast.Program;
import models.ast.Tree;
import models.trajectory.Trajectory;
import models.trajectory.TrajectoryNode;
import util.FileSystem;
import util.IdCounter;

public class AstRoadMap {
	private static final String MAIN_ASSN_ID = "3";

	private TransitionGraph assnGraph;

	private Set<String> contiguous;

	private Map<String, Program> programMap;

	// Maps from uniqueId to contiguousIndex
	// EditDistance matrix is saved in index space.
	private HashMap<String, Integer> contiguousMap;

	public void run() {
		File assnDir = FileSystem.getAssnDir();
		File graphs = new File(assnDir, "graphs");
		File editFile = new File(graphs, "editDistanceMatrix.csv");

		System.out.println("load programs: ");
		List<Program> programs = ProgramLoader.loadPrograms("contiguous");
		contiguous = new HashSet<String>();
		contiguousMap = new HashMap<String, Integer>();
		programMap = new HashMap<String, Program>();
		IdCounter counter = new IdCounter();
		for(Program p : programs) {
			contiguous.add(p.getId());
			contiguousMap.put(p.getId(), counter.getNextId());
			programMap.put(p.getId(), p);
		}

		System.out.println("loading edit distances...");
		int[][] editMatrix = FileSystem.getFileMatrix(editFile);

		System.out.println(programs.get(3));
		System.out.println(programs.get(9));



		System.out.println("load trajectories: ");
		List<Trajectory> trajectories = TrajectoryLoader.loadUnique(contiguous);
		assnGraph = new TransitionGraph(trajectories);
		




		// ADDING IN ROAD MAD
		System.out.println("creating road map...");
		DirectedGraph<String> roadMap = new DirectedGraph<String>();
		for(String a : contiguous) {
			for(String b : contiguous) {
				int numEdits = getEditDistance(editMatrix, a, b);
				if(numEdits == 1) {
					addEdge(roadMap, a, b);
					addEdge(roadMap, b, a);
				}
			}
		}

		System.out.println("adding in extra edges...");
		for(int i = 2; i < 10; i++) {
			System.out.println("iteration " + i);
			for(String a : contiguous) {
				for(String b : contiguous) {
					int count = assnGraph.getCountPerfect(a, b);
					int editDistance = getEditDistance(editMatrix, a, b);
					if(count > 1 && editDistance == i) {
						Double roadMapDist = roadMap.getShortestPathCost(a, b);
						if(roadMapDist == null || editDistance < roadMapDist) {
							roadMap.addEdge(a, b);
						}
					}
				}
			}
		}

		List<Edge<String>> path = roadMap.getShortestPath("10", "0");
		System.out.println("shortest: ");
		for(Edge<String> e : path) {
			System.out.println(e);
		}

		System.out.println("saving roadMap...");
		
		Warnings.error("chose where to save it!");
		//roadMap.saveEdgeMap("roadMap.txt", false);
		
		System.out.println("done");
	}

	private void addEdge(DirectedGraph<String> roadMap, String a, String b) {
		Program p = programMap.get(a);
		if(!p.perfectOnUnitTests()) {
			roadMap.addEdge(a, b, 1.0);
		}
	}

	private void printEdit(int[][] editMatrix, String a, String b) {
		int e = getEditDistance(editMatrix, a, b);
		System.out.println(a + ", " + b + ": " + e);
	}

	private int getEditDistance(int[][] editMatrix, String a, String b) {
		int i = contiguousMap.get(a);
		int j = contiguousMap.get(b);
		int r = Math.min(i, j);
		int c = Math.max(i, j);
		int numEdits = editMatrix[r][c];
		return numEdits;
	}

	public static void main(String[] args) {
		System.out.println("starting");
		FileSystem.setAssnId(MAIN_ASSN_ID);
		new AstRoadMap().run();
	}
}
