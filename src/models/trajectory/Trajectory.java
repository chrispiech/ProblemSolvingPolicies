package models.trajectory;

import java.util.*;

import util.Edge;
import util.Warnings;
import models.ast.Program;

public class Trajectory {

	private List<String> programIds;
	private TrajectoryNode head;
	private int count;
	private String id;

	public Trajectory(String id, List<String> programIds, int count, Set<String> include) {
		this.programIds = programIds;
		this.count = count;
		this.id = id;

		head = null;
		for(int i = programIds.size() - 1; i >= 0; i--) {
			String nodeId = programIds.get(i);
			if(include == null || include.contains(nodeId)) {
				head = new TrajectoryNode(i, nodeId, head, this);
			}
		}
	}

	public Trajectory(String id, List<String> programIds, int count) {
		this.programIds = programIds;
		this.count = count;
		this.id = id;

		head = null;
		for(int i = programIds.size() - 1; i >= 0; i--) {
			String nodeId = programIds.get(i);
			head = new TrajectoryNode(i, nodeId, head, this);
		}
	}
	
	public List<Edge<String>> getEdges() {
		List<String> ids = getProgramIds();
		List<Edge<String>> list = new ArrayList<Edge<String>>();
		if(ids.size() <= 1) return list;
		for(int i = 1; i < ids.size(); i++) {
			String a = ids.get(i - 1);
			String b = ids.get(i);
			list.add(new Edge<String>(a, b, 1));
		}
		return list;
	}

	public List<String> getProgramIds() {
		return programIds;
	}

	public List<TrajectoryNode> getNodeList() {
		List<TrajectoryNode> nodes = new ArrayList<TrajectoryNode>();
		TrajectoryNode curr = head;
		while(curr != null) {
			nodes.add(curr);
			curr = curr.getNext();
		}
		return nodes;
	}

	public int getCount() {
		return count;
	}

	// eventually I could discount length?
	public int getReward() {
		if(perfect()) return Program.PERFECT_SCORE;
		if(passed()) return Program.PASSING_SCORE;
		return 1;
	}
	
	private boolean passed() {
		throw new RuntimeException("test");
	}

	public String getId() {
		return id;
	}

	public String toString() {
		return programIds.toString();
	}


	public boolean perfect() {
		if(isEmpty()) return false;
		return getLastNode().equals("0");
	}


	public boolean endsInState(String state) {
		String lastId = programIds.get(programIds.size() - 1);
		return lastId.equals(state);
	}

	public boolean isEmpty() {
		return programIds.size() == 0;
	}

	public int getNumHops() {
		return programIds.size();
	}

	public String getLastNode() {
		Warnings.check(!isEmpty(), "can't be empty");
		return programIds.get(programIds.size() - 1);
	}

}
