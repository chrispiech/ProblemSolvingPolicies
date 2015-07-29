package edits;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import minions.BlocklyHelper;
import models.ast.Tree;

public class TreeMove {

	public static final String INSERT = "Insert";
	public static final String DELETE = "Delete";
	public static final String REPLACE = "Replace";
	public static final String MATCH = "Match";
	
	protected int totalCost;
	protected int cost;
	protected String operation;
	protected List<TreeMove> origins;
	protected Tree node;

	
	String type;
	
	public TreeMove() {
		origins = null;
		this.operation = "empty";
		this.node = null;
		this.totalCost = 0;
		this.cost = 0;
	}

	public TreeMove(
			TreeMove origin, 
			String operation,
			Tree node,
			int totalCost, 
			int cost) {
		this.node = node;
		this.totalCost = totalCost;
		this.cost = cost;
		this.operation = operation;
		this.origins = Collections.singletonList(origin);
		this.type = node.getType();
	}

	public TreeMove(
			List<TreeMove> origins, 
			String operation, 
			Tree node,
			int totalCost, 
			int cost) {
		this.node = node;
		this.totalCost = totalCost;
		this.cost = cost;
		this.operation = operation;
		this.origins = origins;
		this.type = node.getType();
	}

	public List<TreeMove> getOrigin() {
		return origins;
	}
	
	public Tree getTreeNode(){
		return node;
	}
	
	public boolean isEndBlock(Tree original, Tree next) {
		Tree ast = isInsert() ? next : original;
		return ast.isRightmostSubtree(node);
	}
	
	public boolean isStartBlock(Tree original, Tree next) {
		Tree ast = isInsert() ? next : original;
		return ast.isLeftmostSubtree(node);
	}
	
	public Tree previousBlock(Tree original, Tree next) {
		Tree ast = isInsert() ? next : original;
		return ast.getPreviousTree(node);
	}
	
	public boolean isInsert() {
		return operation.equals(INSERT);
		
	}
	
	public boolean isReplace() {
		return operation.equals(REPLACE);
		
	}
	
	public boolean isDelete() {
		return operation.equals(DELETE);
		
	}

	public int getTotalCost() {
		return totalCost;
	}

	public int getCost() {
		return cost;
	}

	public String getOperation() {
		return operation;
	}

	public Tree getNode() {
		return node;
	}

	public static TreeMove getBest(List<TreeMove> options) {
		TreeMove best = null;
		for(TreeMove move : options) {
			if(move == null) {
				continue;
			}
			if(best == null || move.getTotalCost() < best.getTotalCost()) {
				best = move;
			}
		}
		return best;
	}

	public List<TreeMove> getMoveList() {
		List<TreeMove> moveList = new LinkedList<TreeMove>();
		if(origins != null) {
			for(TreeMove m : origins) {
				moveList.addAll(m.getMoveList());
			}
			moveList.add(this);
		}
		return moveList;
	}

	@Override
	public String toString() {
		String op = getOperation();

		Tree t = getNode();
		return op + " a " + t.getType();

	}

}
