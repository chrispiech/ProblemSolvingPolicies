package models.graphs;

import java.util.*;

import models.trajectory.Trajectory;
import models.trajectory.TrajectoryNode;

public class TransitionGraph {

	private Map<String, Map<String, List<TrajectoryNode>>> transitionMap;

	public TransitionGraph(List<Trajectory> trajectories) {
		transitionMap = new HashMap<String, Map<String, List<TrajectoryNode>>>();

		for(Trajectory t : trajectories) {
			for(TrajectoryNode curr : t.getNodeList()) {
				TrajectoryNode next = curr.getNext();
				if(next == null) {
					continue;
				}
				String currId = curr.getProgramId();
				String nextId = next.getProgramId();

				if(!transitionMap.containsKey(currId)) {
					transitionMap.put(currId, new HashMap<String, List<TrajectoryNode>>());
				}
				Map<String, List<TrajectoryNode>> nextMap = transitionMap.get(currId);
				if(!nextMap.containsKey(nextId)) {
					nextMap.put(nextId, new ArrayList<TrajectoryNode>());
				}
				nextMap.get(nextId).add(next);
			}
		}
	}

	public Set<String> getAllStates() {
		return transitionMap.keySet();
	}

	public Set<String> getNextStates(String state) {
		Map<String, List<TrajectoryNode>> nextMap = transitionMap.get(state);
		if(nextMap == null) {
			return Collections.emptySet();
		}
		return transitionMap.get(state).keySet();
	}

	public int getTransitionReward(String start, String end) {
		List<String> path = new LinkedList<String>();
		path.add(start);
		path.add(end);
		return getTransitionReward(path);

		/*Map<String, List<TrajectoryNode>> nextMap = transitionMap.get(start);
		List<TrajectoryNode> transitions = nextMap.get(end);
		int rewardSum = 0;
		for(TrajectoryNode n : transitions) {
			int reward = n.getTrajectory().getReward();
			int count = n.getTrajectory().getCount();
			rewardSum += reward * count;
		}
		return rewardSum;*/
	}

	public int getTransitionReward(List<String> path) {
		List<TrajectoryNode> transitions = getTransitions(path);
		int rewardSum = 0;
		for(TrajectoryNode n : transitions) {
			int reward = n.getTrajectory().getReward();
			int count = n.getTrajectory().getCount();
			rewardSum += reward * count;
		}
		return rewardSum;
	}
	
	public List<TrajectoryNode> getTransitions(String a, String b) {
		List<String> path = new ArrayList<String>();
		path.add(a);
		path.add(b);
		return getTransitions(path);
	}

	public List<TrajectoryNode> getTransitions(List<String> path) {
		if(path.size() < 2) {
			System.out.println(path);
			throw new RuntimeException("path must be of length at least 2!");
		}
		String first = path.get(0);
		String second = path.get(1);
		Map<String, List<TrajectoryNode>> firstMap = transitionMap.get(first);
		if(firstMap == null) {
			return new ArrayList<TrajectoryNode>();
		}
		List<TrajectoryNode> nodes = new ArrayList<TrajectoryNode>();
		if(firstMap.containsKey(second)) {
			nodes.addAll(firstMap.get(second));
		}
		for(int i = 2; i < path.size(); i++) {
			String next = path.get(i);
			nodes = weedoutByNext(nodes, next);
		}
		return nodes;
	}

	private List<TrajectoryNode> weedoutByNext(List<TrajectoryNode> original, String nextState) {
		List<TrajectoryNode> nodes = new ArrayList<TrajectoryNode>();
		for(TrajectoryNode curr : original) {
			TrajectoryNode next = curr.getNext();
			if(next != null && next.getProgramId().equals(nextState)) {
				nodes.add(next);
			}
		}
		return nodes;
	}

	public double getExpectedScore(String stateId) {
		Map<String, List<TrajectoryNode>> nextMap = transitionMap.get(stateId);
		double rewardSum = 0;
		int num = 0;
		for(String nextId : nextMap.keySet()) {
			for(TrajectoryNode n : nextMap.get(nextId)) {
				int reward = n.getTrajectory().getReward();
				int count = n.getTrajectory().getCount();
				rewardSum += reward * count;
				num += count;
			}
		}
		return rewardSum / num;
	}

	public int getCount(String start, String end) {
		Map<String, List<TrajectoryNode>> nextMap = transitionMap.get(start);
		if(nextMap == null) return 0;
		if(!nextMap.containsKey(end)) return 0;
		List<TrajectoryNode> nexts = nextMap.get(end);
		if(nexts == null) return 0;
		int sum = 0;
		for(TrajectoryNode n : nexts) {
			sum += n.getTrajectory().getCount();
		}
		return sum;
	}

	public int getCountPerfect(String start, String end) {
		Map<String, List<TrajectoryNode>> nextMap = transitionMap.get(start);
		if(nextMap == null) return 0;
		List<TrajectoryNode> nexts = nextMap.get(end);
		if(nexts == null) return 0;
		int sum = 0;
		for(TrajectoryNode n : nexts) {
			if(n.getTrajectory().perfect()) {
				sum += n.getTrajectory().getCount();
			}
		}
		return sum;
	}

}
