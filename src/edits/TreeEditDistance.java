package edits;

import java.util.*;

import minions.BlocklyHelper;
import minions.BlockyStringParser;
import models.ast.Forest;
import models.ast.Tree;

public class TreeEditDistance {

	public static final int DELETE_COST = 1;
	public static final int INSERT_COST = 1;
	public static final int RENAME_COST = 1;

	private int maxCost = -1;
	private ForestPairMemoization queryCache;

	public static int calcMinDistance(Tree a, Tree b) {
		return new TreeEditDistance().getMinDistance(a, b);
	}
	
	public static List<TreeMove> calcMoves(Tree a, Tree b) {
		return new TreeEditDistance().getMoves(a, b);
	}
	
	public int getMinDistance(Tree a, Tree b) {

		Forest q1 = new Forest(a);
		Forest q2 = new Forest(b);

		queryCache = new ForestPairMemoization(q1, q2);

		// Calculate distances between query trees
		TreeMove finalMove = getDistanceBetweenQueries(q1, q2);

		return finalMove.getTotalCost();
	}
	
	public int getMinDistance(Tree a, Tree b, int max) {
		maxCost = max;
		return getMinDistance(a, b);
	}
	
	public List<TreeMove> getMoves(Tree a, Tree b) {
		Forest q1 = new Forest(a);
		Forest q2 = new Forest(b);

		queryCache = new ForestPairMemoization(q1, q2);

		// Calculate distances between query strings
		TreeMove finalMove = getDistanceBetweenQueries(q1, q2);

		// Back track the best move
		List<TreeMove> moves = finalMove.getMoveList();
		List<TreeMove> edits = new ArrayList<TreeMove>();
		for(TreeMove m : moves) {
			if(m.getCost() > 0) {
				edits.add(m);
			}
		}

		return edits;
		
	}

	/**************************************************************************
	 * Calculate distance between the query trees
	 *************************************************************************/

	private TreeMove getDistanceBetweenQueries(Forest f1, Forest f2) {
		if(queryCache.isCached(f1, f2)) {
			return queryCache.get(f1, f2);
		}

		List<TreeMove> options = new LinkedList<TreeMove>();
		if (f1 == null && f2 == null) {
			return new TreeMove();
		} else if (f1 == null) {
			options.add(calcInsertScore(f1, f2));
		} else if (f2 == null) {
			options.add(calcDeleteScore(f1, f2));
		} else {
			options.add(calcReplaceScore(f1, f2));
			options.add(calcInsertScore(f1, f2));
			options.add(calcDeleteScore(f1, f2));
		}
		TreeMove best = TreeMove.getBest(options);
		queryCache.put(f1, f2, best);
		return best;
	}

	private TreeMove calcReplaceScore(Forest f1, Forest f2) {
		// Compare the children of the matched nodes
		TreeMove s1 = getDistanceBetweenQueries(
				f1.getLastRootChildForest(), 
				f2.getLastRootChildForest());
		
		// Compare the forests (minus the trees rooted at v and w)
		TreeMove s2 = getDistanceBetweenQueries(
				f1.getForestMinusLastRootTree(), 
				f2.getForestMinusLastRootTree());
		
		// Compare v and w
		String l1 = f1.getLastRoot().getLabel();
		String l2 = f2.getLastRoot().getLabel();
		int renameCost = l1.equals(l2) ? 0 : RENAME_COST;
		Tree replaced = f1.getLastRoot();
		Tree replacing = f2.getLastRoot();
		
		// Return the sum
		int cost = s1.getTotalCost() + s2.getTotalCost() + renameCost;
		List<TreeMove> origins = new ArrayList<TreeMove>();
		origins.add(s1);
		origins.add(s2);
		TreeReplace move = new TreeReplace(origins, replaced, replacing, cost, renameCost);

		
		return move;
	}

	private TreeMove calcDeleteScore(Forest f1, Forest f2) {
		TreeMove old = getDistanceBetweenQueries(f1.getForestMinusLastRoot(), f2);
		int newCost = old.getTotalCost() + DELETE_COST;
		Tree toDelete = f1.getLastRoot();
		return new TreeMove(old, TreeMove.DELETE, toDelete, newCost, DELETE_COST);
	}
	
	private TreeMove calcInsertScore(Forest f1, Forest f2) {
		TreeMove old = getDistanceBetweenQueries(f1, f2.getForestMinusLastRoot());
		int cost =  old.getTotalCost() + INSERT_COST;
		Tree toInsert = f2.getLastRoot();
		
		return new TreeMove(old, TreeMove.INSERT, toInsert, cost, INSERT_COST);
	}
	
	public static final void main(String[] args) {
		Tree a = BlockyStringParser.parseBlockyString("F[MLI[a][M][M]]");
		Tree b = BlockyStringParser.parseBlockyString("F[MLI[a][MM][MR]]");
		
		System.out.println("a: " + a);
		System.out.println("b: " + b);
		
		List<TreeMove> moves = calcMoves(b, a);
		System.out.println("--");
		for(TreeMove move : moves) {
			/*if(!move.getNode().getType().equals(move.type)) {
				throw new RuntimeException("changed");
			}*/
			System.out.println(move.type);
			System.out.println(move);
			System.out.println(move.getNode().getId());
		}
		
	}

	


}
