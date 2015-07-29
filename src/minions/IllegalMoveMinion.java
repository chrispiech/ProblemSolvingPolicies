package minions;

import java.util.*;

import edits.TreeEditDistance;
import edits.TreeMove;
import util.FileSystem;
import util.Graph;
import util.IdCounter;
import util.PQueue;
import models.ast.Program;
import models.ast.Tree;

public class IllegalMoveMinion {

	public static boolean isMinLegalMove(Tree a, Tree b) {
		List<TreeMove> moves = TreeEditDistance.calcMoves(a, b);

		// maybe you replace a single node in a required block...
		if(isRequireBlockSwap(a, moves)) {
			return true;
		}

		// maybe there is a whole tree that must be inserted as one move...
		if(isMinLegalTreeInsert(moves)) {
			return true;
		}

		return false;
	}

	/**
	 * Get Intermediate Moves
	 * ----------------------
	 * Returns the illegal moves inbetween a and b. Note that the list
	 * does not include the trees a or b... and all trees should be illegal
	 * blocky...
	 */
	public static List<Tree> getItermediateMoves(Tree a, Tree b) {
		List<TreeMove> moves = TreeEditDistance.calcMoves(a, b);

		// maybe you replace a single node in a required block...
		if(isRequireBlockSwap(a, moves)) {
			return getRequireBlockSwapPath(a, b, moves);
		}

		// maybe there is a whole tree that must be inserted as one move...
		if(isMinLegalTreeInsert(moves)) {
			return getMinLegalTreeInsertPath(a, b, moves);
		}

		return null;

	}
	
	public static int getNumMoves(Tree a, Tree b) {
		return getItermediateMoves(a, b).size() + 1;
	}


	/*********************************************************************************
	 *   REQUIRE BLOCK SWAP
	 *********************************************************************************/

	private static boolean isRequireBlockSwap(Tree a, List<TreeMove> moves) {
		if(moves.size() == 1) {
			TreeMove move = moves.get(0);
			if(move.isReplace()) {
				Tree node = move.getNode(); // gives the replaced node
				Tree parent = a.getParent(node);
				int parentChildren = parent.getChildren().size();
				
				return BlocklyHelper.blockRequiresChildren(parent) && parentChildren == 1;
			}
		}
		return false;
	}

	private static List<Tree> getRequireBlockSwapPath(Tree a, Tree b,
			List<TreeMove> moves) {
		TreeMove move = moves.get(0);
		List<Tree> path = new ArrayList<Tree>();

		Tree intermediate = new Tree(a);
		Tree example = move.getNode();
		Tree toRemove = intermediate.getNodeById(example.getId());
		intermediate.removeSubtree(toRemove);
		path.add(intermediate);

		return path;
	}


	/*********************************************************************************
	 *   MIN LEGAL TREE INSERT
	 *********************************************************************************/

	/**
	 * returns null if there is no such insert. Else returns the
	 * parent of
	 */
	private static boolean isMinLegalTreeInsert(List<TreeMove> moves) {
		Tree parentInsert = getParentNodeInserted(moves);
		if(parentInsert == null) {
			return false;
		}
		if (!isMinLegalInsert(parentInsert)) {
			return false;
		}
		return true;
	}

	private static Tree getParentNodeInserted(List<TreeMove> moves) {
		// first, get the parent of all the insert calls...
		Tree parentInsert = null;
		for(TreeMove m : moves) {
			if(!m.isInsert()) {
				return null;
			}
			Tree node = m.getNode();
			if(parentInsert == null) {
				parentInsert = node;
			} else if(node.hasSubtree(parentInsert)) {
				parentInsert = node;
			} 
		}
		if(parentInsert == null) return null;

		// only a real parent if it is parent to all edits...
		for(TreeMove m : moves) {
			Tree node = m.getNode();
			if(!parentInsert.hasSubtree(node)) {
				return null;
			}
		}
		
		// ... and all the nodes that it is parent to are edits
		for(Tree t : parentInsert.getPreorder()) {
			boolean hasMove = false;
			for(TreeMove m : moves) {
				if(m.getNode() == t) {
					hasMove = true;
					break;
				}
			}
			if(!hasMove) {
				return null;
			}
		}
		return parentInsert;
	}

	private static List<Tree> getMinLegalTreeInsertPath(Tree a, Tree b,
			List<TreeMove> moves) {
		Tree parentInsert = getParentNodeInserted(moves);

		List<Tree> intermediate = new ArrayList<Tree>();
		Tree reconstructed = new Tree(a);
		int nextId = a.size() + 1;
		IdCounter idCounter = new IdCounter(nextId);
		for(Tree n : parentInsert.getPreorder()) {
			Tree parent = b.getParent(n);
			Tree recoParent = getParallelNode(b, parent, reconstructed);
			Tree newLeaf = null;
			if(BlocklyHelper.checkType(n, "maze_ifElse")) {
				newLeaf = BlocklyHelper.makeIfElse(idCounter);
			} else {
				newLeaf = new Tree(n.getType(), idCounter.getNextIdStr());
			}

			for(int i = 0; i < parent.getChildren().size(); i++) {
				if(parent.getChildren().get(i) == n) {
					Tree toAdd = parent.getChildren().get(i);
					if(isIfStructure(toAdd)) break;
					recoParent.addNodeBeforeIndex(i, newLeaf);
					recoParent.reclaculateSize();
					intermediate.add(new Tree(reconstructed));
				}
			}
		}
		// the last tree should be the full formed tree b
		intermediate.remove(intermediate.size() - 1);
		return intermediate;
	}

	private static boolean isIfStructure(Tree tree) {
		if(BlocklyHelper.checkType(tree, "isPathForward")) return true;
		if(BlocklyHelper.checkType(tree, "ELSE")) return true;
		if(BlocklyHelper.checkType(tree, "DO")) return true;
		return false;
	}

	/**
	 * root1 is to example, 
	 * as 
	 * root2 is to ?
	 */
	private static Tree getParallelNode(Tree root1, Tree example, Tree root2) {
		if(example.equals(root1)) return root2;
		if(!root1.hasSubtree(example)) return null;
		
		for(int i = 0; i < root1.getChildren().size(); i++) {
			Tree bChild = root1.getChildren().get(i);
			Tree aChild = root2.getChildren().get(i);

			Tree parallel = getParallelNode(bChild, example, aChild);
			if(parallel != null) {
				return parallel;
			}
		}

		return null;
	}

	private static boolean isMinLegalInsert(Tree t) {
		if(BlocklyHelper.checkType(t, "maze_moveForward")) return true;
		if(BlocklyHelper.checkType(t, "maze_turnLeft")) return true;
		if(BlocklyHelper.checkType(t, "maze_turnRight")) return true;

		if(BlocklyHelper.isCodeBlockParent(t)) {
			boolean singleChild = t.getChildren().size() <= 1;
			return singleChild && isMinLegalInsert(t.getLeftmostChild());
		}

		if(BlocklyHelper.checkType(t, "maze_ifElse")) {
			Tree doBlock = null;
			Tree elseBlock = null;
			Tree condBlock = null;
			for(Tree child : t.getChildren()) {
				if(BlocklyHelper.checkType(child, "DO")) {
					doBlock = child;
				} else if(BlocklyHelper.checkType(child, "ELSE")) {
					elseBlock = child;
				} else {
					condBlock = child;
				}
			}
			
			boolean legalDo = isMinLegalInsert(doBlock);
			boolean legalElse = isMinLegalInsert(elseBlock);
			boolean legalCond = BlocklyHelper.checkType(condBlock, "isPathForward");
			return legalDo && legalElse && legalCond; 

		}
		return false;
	}

	public static void main(String[] args) {

		FileSystem.setAssnId("17");

		/*Tree astA = BlockyStringParser.parseBlockyString("");
		Tree astB = BlockyStringParser.parseBlockyString("F[I[a][M][L]]");

		System.out.println(isMinLegalMove(astA, astB));
		for(Tree i : IllegalMoveMinion.getItermediateMoves(astA, astB)) {
			System.out.println(i);
		}*/
		
		

		Tree astA = BlockyStringParser.parseBlockyString("I[a][M][L]");
		Tree astB = BlockyStringParser.parseBlockyString("I[a][M][L]I[a][M][L]");


		boolean truth = isMinLegalMove(astA, astB);
		System.out.println(truth);
		if(truth) {
			for(Tree i : IllegalMoveMinion.getItermediateMoves(astA, astB)) {
				System.out.println(i);
			}
		}


	}

}
