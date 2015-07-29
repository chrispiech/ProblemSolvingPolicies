package models.graphs;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import models.ast.Program;
import util.FileSystem;
import util.IdCounter;

public class EditGraph {
	private int[][] editMatrix = null;
	private HashMap<String, Integer> contiguousMap;

	public EditGraph(List<Program> programs) {
		File assnDir = FileSystem.getAssnDir();
		File graphs = new File(assnDir, "graphs");
		File editFile = new File(graphs, "editDistanceMatrix.csv");
		
		contiguousMap = new HashMap<String, Integer>();
		IdCounter c = new IdCounter();
		for(Program p : programs) {
			contiguousMap.put(p.getId(), c.getNextId());
		}
		
		editMatrix = FileSystem.getFileMatrix(editFile);
	}
	
	public int getEditDistance(String a, String b) {
		int i = contiguousMap.get(a);
		int j = contiguousMap.get(b);
		int r = Math.min(i, j);
		int c = Math.max(i, j);
		return editMatrix[r][c];
	}
}
