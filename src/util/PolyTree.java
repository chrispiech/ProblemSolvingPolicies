package util;

import java.io.File;
import java.util.*;

public class PolyTree<T> extends DirectedGraph<T> {

	public static PolyTree<String> loadPolytree(String fileName) {
		File assnDir = FileSystem.getAssnDir();
		File graphs = new File(assnDir, "graphs");
		File graphFile = new File(graphs, fileName);
		PolyTree<String> graph = new PolyTree<String>();
		for(String s : FileSystem.getFileLines(graphFile)) {
			String[] cols = s.split(",");
			String source = cols[0];
			String target = cols[1];
			double weight = 0;

			if(cols.length == 3) {
				String weightStr = cols[2];
				weight = Double.parseDouble(weightStr);
			}

			graph.addEdge(source, target, weight);
		}
		return graph;
	}
	
	public T getOutedge(T state) {
		Set<T> outgoing = getOutgoing(state);
		if(outgoing.size() > 1) {
			throw new RuntimeException("what were you thinking?");
		}
		if(outgoing.isEmpty()) return null;
		for(T next : outgoing) {
			return next;
		}
		return null;
	}
	
}
