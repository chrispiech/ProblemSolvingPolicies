package edits;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import minions.BlocklyHelper;
import models.ast.Tree;

public class TreeReplace extends TreeMove{

	private Tree replaced;
	private Tree replacing;

	public TreeReplace(
			List<TreeMove> origins, 
			Tree replaced,
			Tree replacing,
			int totalCost, 
			int cost) {
		this.node = replaced;
		this.replaced = replaced;
		this.replacing = replacing;
		this.totalCost = totalCost;
		this.cost = cost;
		this.operation = cost > 0 ? TreeMove.REPLACE : TreeMove.MATCH;
		this.origins = origins;
	}

	public Tree getReplaced() {
		return replaced;
	}

	public Tree getReplacing() {
		return replacing;
	}

	public Tree getNode() {
		return replaced;
	}

	@Override
	public String toString() {
		Tree replaced = getReplaced();
		Tree replacing = getReplacing();
		return operation + " a " + replaced.getType() + " with a " + replacing.getType();
	}

}
