package util;

import java.io.File;


import org.jgrapht.alg.*;
import org.jgrapht.*;
import org.jgrapht.graph.*;

import util.FileSystem;
import util.PQueue;

import java.util.*;

public class Graph<T> {

	SimpleGraph<T, DefaultEdge> graph;

	public Graph() {
		graph = new SimpleGraph<T, DefaultEdge>(DefaultEdge.class);
	}

	public void addVertex(T a) {
		graph.addVertex(a);
	}

	public void addVertex(T a, double weight) {
		throw new RuntimeException("not ready");
	}


	public void addEdge(T a, T b) {
		if(!graph.containsVertex(a)) {
			graph.addVertex(a);
		}
		if(!graph.containsVertex(b)) {
			graph.addVertex(b);
		}
		graph.addEdge(a, b);
	}

	public void saveGml(String fileName) {
		String nodeString = getVertexGml();
		String edgeString = getEdgeGml();

		File assnDir = FileSystem.getAssnDir();
		File graphs = new File(assnDir, "graphs");
		String gml = "graph [ \n" + nodeString + edgeString + "\n]\n";
		FileSystem.createFile(graphs, fileName, gml);
	}

	public static Graph<String> loadEdgeMap(String fileName) {
		File assnDir = FileSystem.getAssnDir();
		File graphs = new File(assnDir, "graphs");
		File graphFile = new File(graphs, fileName);
		Graph<String> graph = new Graph<String>();
		for(String s : FileSystem.getFileLines(graphFile)) {
			String[] cols = s.split(",");
			String source = cols[0];
			String target = cols[1];

			graph.addEdge(source, target);
		}
		return graph;
	}

	public void saveEdgeMap(String fileName, boolean weights) {
		/*String edgeMap = "";
		for(DefaultWeightedEdge edge : graph.edgeSet()) {
			T source = graph.getEdgeSource(edge);
			T target = graph.getEdgeTarget(edge);

			double weight = getWeight(source, target);
			if(weights) {
				edgeMap += source + "," + target + "," + weight + "\n";
			} else {
				edgeMap += source + "," + target + "\n";
			}
		}

		File assnDir = FileSystem.getAssnDir();
		File graphs = new File(assnDir, "graphs");
		FileSystem.createFile(graphs, fileName, edgeMap);*/
		throw new RuntimeException("not thought through...");
	}

	public Double getShortestPathCost(T start, T end) {
		/*if(!graph.containsVertex(start)) return null;
		if(!graph.containsVertex(end)) return null;
		List<DefaultWeightedEdge> path = 
				DijkstraShortestPath.findPathBetween(graph, start, end);
		if(path == null) return null;
		double cost = 0;
		for(DefaultWeightedEdge edge : path) {
			cost += graph.getEdgeWeight(edge);
		}
		return cost;*/
		throw new RuntimeException("not thought through...");
	}
	
	public boolean hasPath(T start, T end) {
		if(!graph.containsVertex(start)) return false;
		if(!graph.containsVertex(end)) return false;
		return DijkstraShortestPath.findPathBetween(graph, start, end) != null;
	}
	
	/*public DirectedGraph<T> getMinimumSpanningTree() {
		KruskalMinimumSpanningTree<T, DefaultWeightedEdge> mst = 
				new KruskalMinimumSpanningTree<T, DefaultWeightedEdge>(graph); 
		DirectedGraph<T> newGraph = new DirectedGraph<T>();
		for(DefaultWeightedEdge e : mst.getMinimumSpanningTreeEdgeSet()) {
			T source = graph.getEdgeSource(e);
			T target = graph.getEdgeTarget(e);
			double weight = graph.getEdgeWeight(e);
			newGraph.addEdge(source, target, weight);
		}
		return newGraph;
	}*/
	

	public List<Edge<T>> getShortestPath(T start, T end) {
		/*List<DefaultWeightedEdge> path = 
				DijkstraShortestPath.findPathBetween(graph, start, end);
		if(path == null) return null;
		List<Edge<T>> edges = new ArrayList<Edge<T>>();
		for(DefaultWeightedEdge edge : path) {
			T source = graph.getEdgeSource(edge);
			T target = graph.getEdgeTarget(edge);
			double weight = graph.getEdgeWeight(edge);
			Edge<T> e = new Edge<T>(source, target, weight);
			edges.add(e);
		}
		return edges;*/
		throw new RuntimeException("not thought through...");
	}

	public boolean containsEdge(T a, T b) {
		return graph.containsEdge(a, b);
	}

	public Set<T> vertexSet() {
		return graph.vertexSet();
	}

	private String getEdgeGml() {
		/*String edgeGml = "";
		for(DefaultWeightedEdge edge : graph.edgeSet()) {
			T source = graph.getEdgeSource(edge);
			T target = graph.getEdgeTarget(edge);
			double weight = graph.getEdgeWeight(edge);
			edgeGml += "edge [\n";
			edgeGml += "source " + source + "\n";
			edgeGml += "target " + target + "\n";
			edgeGml += "weight " + weight + "\n";
			edgeGml += "]\n";
		}
		return edgeGml;*/
		throw new RuntimeException("not thought through...");
	}

	private String getVertexGml() {
		String nodeGml = "";
		for(T node : graph.vertexSet()) {
			nodeGml += "node [\n";
			nodeGml += "id " + node + "\n";
			nodeGml += "]\n";
		}
		return nodeGml;
	}

	public void removeEdge(T a, T b) {
		graph.removeEdge(a, b);
	}

	public boolean containsVertex(T v) {
		return graph.containsVertex(v);
	}

	public List<Set<T>> getConnectedComponents() {
		ConnectivityInspector<T,DefaultEdge> inspector = 
				new ConnectivityInspector<T, DefaultEdge>(graph);
		return inspector.connectedSets();
	}




}
