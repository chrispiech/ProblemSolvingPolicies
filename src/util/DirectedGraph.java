package util;

import java.io.File;

import org.jgrapht.alg.*;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.json.JSONObject;

import util.FileSystem;
import util.PQueue;

import java.util.*;

public class DirectedGraph<T> {

	DefaultDirectedWeightedGraph<T, DefaultWeightedEdge> graph;

	public DirectedGraph() {
		graph = new DefaultDirectedWeightedGraph<T, DefaultWeightedEdge>(DefaultWeightedEdge.class);
	}

	public void addVertex(T a) {
		graph.addVertex(a);
	}

	public void addVertex(T a, double weight) {
		throw new RuntimeException("not ready");
	}

	public void addEdge(T a, T b) {
		addEdge(a, b, 1.0);
	}

	public void addEdge(T a, T b, double weight) {
		if(!graph.containsVertex(a)) {
			graph.addVertex(a);
		}
		if(!graph.containsVertex(b)) {
			graph.addVertex(b);
		}
		if(!graph.containsEdge(a, b)) {
			DefaultWeightedEdge edge = graph.addEdge(a, b);
			graph.setEdgeWeight(edge, weight);
		} else {
			DefaultWeightedEdge edge = graph.getEdge(a, b);
			graph.setEdgeWeight(edge, weight);
		}
	}
	
	public void saveGml(File dir, String fileName, Map<String, Integer> countMap) {
		String nodeString = getVertexGml(countMap);
		String edgeString = getEdgeGml();

		String gml = "graph [ \n" + nodeString + edgeString + "\n]\n";
		FileSystem.createFile(dir, fileName, gml);
	}

	public void saveGml(File dir, String fileName) {
		String nodeString = getVertexGml();
		String edgeString = getEdgeGml();

		String gml = "graph [ \n" + nodeString + edgeString + "\n]\n";
		FileSystem.createFile(dir, fileName, gml);
	}

	public static DirectedGraph<String> loadEdgeMap(File graphFile) {
		DirectedGraph<String> graph = new DirectedGraph<String>();
		for(String s : FileSystem.getFileLines(graphFile)) {
			String[] cols = s.split(",");
			String source = cols[0];
			String target = cols[1];
			double weight = 1;

			if(cols.length == 3) {
				String weightStr = cols[2];
				weight = Double.parseDouble(weightStr);
			}

			graph.addEdge(source, target, weight);
		}
		return graph;
	}
	

	public void saveEdgeMap(File file, boolean weights) {
		String edgeMap = "";
		
		for(T source : graph.vertexSet()) {
			for(DefaultWeightedEdge edge : graph.outgoingEdgesOf(source)) {
				T target = graph.getEdgeTarget(edge);
				double weight = getWeight(source, target);
				if(weights) {
					edgeMap += source + "," + target + "," + weight + "\n";
				} else {
					edgeMap += source + "," + target + "\n";
				}
			}
		}

		FileSystem.createFile(file, edgeMap);
	}

	public Double getShortestPathCost(T start, T end) {
		if(!graph.containsVertex(start)) return null;
		if(!graph.containsVertex(end)) return null;
		List<DefaultWeightedEdge> path = 
				DijkstraShortestPath.findPathBetween(graph, start, end);
		if(path == null) return null;
		double cost = 0;
		for(DefaultWeightedEdge edge : path) {
			cost += graph.getEdgeWeight(edge);
		}
		return cost;
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
	
	public List<Edge<T>> getEasiestPath(T start, T end) {
		Set<T> closed = new HashSet<T>();
		PQueue<List<Edge<T>>> queue = new PQueue<List<Edge<T>>>(true);
		
		// Put in the first edge
		Edge<T> firstEdge = new Edge<T>(null, start, 0);
		queue.add(Collections.singletonList(firstEdge), 0);
		
		while(!queue.isEmpty()) {
			List<Edge<T>> currPath = queue.dequeue();
			Edge<T> curr = currPath.get(currPath.size() - 1);
			T lastNode = curr.getTarget();
			if(lastNode.equals(end)) {
				currPath.remove(0);
				return currPath;
			}
			if(closed.contains(lastNode)) {
				continue;
			}
			closed.add(lastNode);

			for(DefaultWeightedEdge e : graph.outgoingEdgesOf(lastNode)) {
				T next = graph.getEdgeTarget(e);
				double edgeCost = getWeight(lastNode, next);
				List<Edge<T>> newPath = new ArrayList<Edge<T>>();
				newPath.addAll(currPath);
				newPath.add(new Edge<T>(lastNode, next, edgeCost));
				
				Double minCost = null;
				for(Edge<T> edge : newPath) {
					if(minCost == null || edge.getWeight() > minCost) {
						minCost = edge.getWeight();
					}
				}
				queue.add(newPath, minCost);
			}
		}
		return null;

	}

	public List<Edge<T>> getShortestPath(T start, T end) {
		List<DefaultWeightedEdge> path = 
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
		return edges;
	}

	public boolean containsEdge(T a, T b) {
		return graph.containsEdge(a, b);
	}

	public Set<T> vertexSet() {
		return graph.vertexSet();
	}

	public Set<T> getOutgoing(T source) {
		if(!graph.containsVertex(source)) {
			return new HashSet<T>();
		}
		Set<T> outset = new HashSet<T>();
		for(DefaultWeightedEdge e : graph.outgoingEdgesOf(source)) {
			T target = graph.getEdgeTarget(e);
			outset.add(target);
		}
		return outset;
	}

	public double getWeight(T a, T b) {
		DefaultWeightedEdge e = graph.getEdge(a, b);
		return graph.getEdgeWeight(e);
	}

	private String getEdgeGml() {
		String edgeGml = "";
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
		return edgeGml;
	}
	
	private String getVertexGml(Map<String, Integer> countMap) {
		String nodeGml = "";
		for(T node : graph.vertexSet()) {
			nodeGml += "node [\n";
			nodeGml += "id " + node + "\n";
			nodeGml += "weight " + countMap.get(node) + "\n";
			nodeGml += "]\n";
		}
		return nodeGml;
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
	
	public void setWeight(T a, T b, double weight) {
		DefaultWeightedEdge edge = graph.getEdge(a, b);
		graph.setEdgeWeight(edge, weight);
		
	}

	public void increaseWeight(T a, T b, double amount) {
		if(!containsEdge(a, b)) {
			addEdge(a, b, amount);
		} else {
			DefaultWeightedEdge edge = graph.getEdge(a, b);
			double newWeight = graph.getEdgeWeight(edge) + amount;
			graph.setEdgeWeight(edge, newWeight);
		}

	}
	
	public Set<T> getInConnected(T start) {
		Queue<T> fringeStates = new LinkedList<T>();
		Set<T> visited = new HashSet<T>();
		fringeStates.add(start);
		while(!fringeStates.isEmpty()) {
			T curr = fringeStates.remove();
			if(visited.contains(curr)) continue;
			visited.add(curr);

			for(T next : getIncoming(curr)) {
				fringeStates.add(next);
			}
		}
		return visited;
	}

	public Set<T> getIncoming(T source) {
		Set<T> inset = new HashSet<T>();
		for(DefaultWeightedEdge e : graph.incomingEdgesOf(source)) {
			T target = graph.getEdgeSource(e);
			inset.add(target);
		}
		return inset;
	}

	public Set<T> getConnected(T start) {
		Queue<T> fringeStates = new LinkedList<T>();
		Set<T> visited = new HashSet<T>();
		fringeStates.add(start);
		while(!fringeStates.isEmpty()) {
			T curr = fringeStates.remove();
			if(visited.contains(curr)) continue;
			visited.add(curr);

			for(T next : getOutgoing(curr)) {
				fringeStates.add(next);
			}
		}
		return visited;
	}

	public double getPathCost(List<T> nodeList) {
		double cost = 0;
		if(nodeList.size() < 2) return 0;

		for(int i = 1; i < nodeList.size(); i++) {
			T previous = nodeList.get(i - 1);
			T curr = nodeList.get(i);
			double edgeCost = getWeight(previous, curr);
			cost += edgeCost;
		}
		return cost;
	}

	public DirectedGraph<T> getShortestPathTree(T target) {
		DirectedGraph<T> shortestPathTree = new DirectedGraph<T>();
		Set<T> closed = new HashSet<T>();
		PQueue<List<T>> queue = new PQueue<List<T>>(true);
		queue.add(Collections.singletonList(target), 0);
		while(!queue.isEmpty()) {
			List<T> currPath = queue.dequeue();
			T curr = currPath.get(0);
			if(closed.contains(curr)) {
				continue;
			}
			closed.add(curr);
			System.out.println("closed set: " + closed.size());
			if(currPath.size() > 1) {
				T next = currPath.get(1);
				shortestPathTree.addEdge(curr, next);
			}
			double pathCost = getPathCost(currPath);


			for(DefaultWeightedEdge e : graph.incomingEdgesOf(curr)) {
				T previous = graph.getEdgeSource(e);
				double edgeCost = getWeight(previous, curr);
				List<T> newPath = new ArrayList<T>();
				newPath.add(previous);
				newPath.addAll(currPath);
				double newCost = pathCost + edgeCost;
				queue.add(newPath, newCost);
			}
		}
		return shortestPathTree;

	}

	public void removeEdge(T a, T b) {
		graph.removeEdge(a, b);
	}

	public boolean containsVertex(T v) {
		return graph.containsVertex(v);
	}

	public List<Edge<T>> getEdges() {
		List<Edge<T>> edgeList = new ArrayList<Edge<T>>();
		for(DefaultWeightedEdge e : graph.edgeSet()) {
			T source = graph.getEdgeSource(e);
			T target = graph.getEdgeTarget(e);
			double weight = graph.getEdgeWeight(e);
			edgeList.add(new Edge<T>(source, target, weight));
		}
		return edgeList;
	}

	public Set<T> outgoingSet() {
		Set<T> outSet = new HashSet<T>();
		for(T node : vertexSet()) {
			if(getOutgoing(node).size() > 0) {
				outSet.add(node);
			}
		}
		return outSet;
	}



}
