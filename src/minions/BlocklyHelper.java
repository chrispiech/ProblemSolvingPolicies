package minions;

import java.util.*;

import util.FileSystem;
import util.Graph;
import util.IdCounter;
import util.PQueue;
import models.ast.Program;
import models.ast.Tree;

public class BlocklyHelper {

	public static boolean isContiguous(Program p) {
		Tree ast = p.getAst();
		int numChildren = ast.getChildren().size();
		return numChildren <= 1;
	}

	public static boolean isBlock(Tree t) {
		String type = t.getType();
		if(checkType(t, "maze_turnLeft")) return true;
		if(checkType(t, "maze_turnRight")) return true;
		if(checkType(t, "maze_moveForward")) return true;
		if(checkType(t, "maze_ifElse")) return true;
		if(checkType(t, "maze_forever")) return true;

		return false;
	}
	
	public static boolean isCommand(Tree t) {
		if(checkType(t, "maze_turnLeft")) return true;
		if(checkType(t, "maze_turnRight")) return true;
		if(checkType(t, "maze_moveForward")) return true;
		return false;
	}


	public static String getBlocklyName(Tree tree) {
		if(tree == null) {
			return "null";
		}
		String type = tree.getType();
		if(isTurn(tree)) {
			Tree param = tree.getChildren().get(0);
			return param.getLabel();
		}
		if(isMove(tree)) {
			return "moveForward";
		}
		return type;
	}

	public static int getNumBlocks(Tree t) {
		int numBlocks = 0;
		if(isBlock(t)) {
			numBlocks++;
		}
		for(Tree child : t.getChildren()) {
			numBlocks += getNumBlocks(child);
		}
		return numBlocks;
	}
	
	public static int getNumBlocks(Tree t, String type) {
		int numBlocks = checkType(t, type) ? 1 : 0;
		for(Tree child : t.getChildren()) {
			numBlocks += getNumBlocks(child, type);
		}
		return numBlocks;
	}
	
	public static Tree makeContiguous(Tree node) {
		List<Tree> children = new ArrayList<Tree>();
		
		for(Tree child : node.getChildren()) {
			if(child.getType().equals("statementList")) {
				for(Tree slChild : child.getChildren()) {
					children.add(makeContiguous(slChild));
				}
			} else {
				children.add(makeContiguous(child));
			}
		}
		
		return new Tree(node.getType(), node.getName(), children, node.getId());
		
	}

	public static Tree simplify(Tree node) {
		List<Tree> children = new ArrayList<Tree>();

		if(node.getType().equals("maze_turn")) {
			String dirType = node.getLeftmostChild().getType();
			if(dirType.equals("turnLeft")) {
				return new Tree("maze_turnLeft", node.getName(), children, node.getId());
			}
			if(dirType.equals("turnRight")) {
				return new Tree("maze_turnRight", node.getName(), children, node.getId());
			}
		}

		if(node.getType().equals("maze_forever")) {
			Tree doBlock = node.getLeftmostChild();
			if(doBlock != null && BlocklyHelper.checkType(doBlock, "DO")) {
				Tree simpleDo = simplify(doBlock);
				return new Tree("maze_forever", node.getName(), simpleDo.getChildren(), node.getId());
			} 
		}


		for(Tree child : node.getChildren()) {
			if(child.getType().equals("statementList")) {
				for(Tree slChild : child.getChildren()) {
					children.add(simplify(slChild));
				}
			} else {
				Tree simpleChild = simplify(child);
				children.add(simpleChild);
			}
		}

		return new Tree(node.getType(), node.getName(), children, node.getId());
	}

	public static boolean isTurn(Tree node) {
		if(node == null) return false;
		if(node.getType().equals("maze_turnLeft")) return true;
		if(node.getType().equals("maze_turnRight")) return true;
		return node.getType().equals("maze_turn");
	}

	public static boolean isForever(Tree node) {
		if(node == null) return false;
		return node.getType().equals("maze_forever");
	}

	public static boolean isMove(Tree node) {
		if(node == null) return false;
		return node.getType().equals("maze_moveForward");
	}

	public static boolean isTurnParam(Tree node) {
		if(node == null) return false;
		String type = node.getType();
		if(type.equals("turnLeft")) return true;
		if(type.equals("turnRight")) return true;
		return false;
	}

	protected static boolean isCodeBlockParent(Tree node) {
		if(checkType(node, "program")) return true;
		if(checkType(node, "DO")) return true;
		if(checkType(node, "ELSE")) return true;
		if(checkType(node, "maze_forever")) return true;
		return false;
	}

	public static boolean isEditable(Tree node) {
		if(checkType(node, "program")) return false;
		if(checkType(node, "DO")) return false;
		if(checkType(node, "ELSE")) return false;
		return true;
	}

	public static boolean isIfElse(Tree node) {
		if(node == null) return false;
		return node.getType().equals("maze_ifElse");
	}

	public static boolean isCondition(Tree editNode) {
		String type = editNode.getType();
		if(type.equals("isPathForward")) return true;
		if(type.equals("isPathLeft")) return true;
		if(type.equals("isPathRight")) return true;
		return false;
	}

	private static List<String> getAllConditions() {
		List<String> allConditions = new ArrayList<String>();
		allConditions.add("isPathForward");
		allConditions.add("isPathLeft");
		allConditions.add("isPathRight");
		return allConditions;
	}

	public static boolean isElse(Tree node) {
		if(node == null) return false;
		return node.getType().equals("ELSE");
	}

	private static boolean isTurnLeft(Tree node) {
		return checkType(node, "maze_turnLeft");
	}

	public static boolean checkType(Tree node, String string) {
		return node.getType().equals(string);
	}

	public static Set<Tree> getLegalMoves(Tree ast) {

		Set<Tree> legalMoves = new HashSet<Tree>();

		// Deletes
		for(Tree node : ast.getPreorder()) {
			// delete the node
			if(node.isLeaf()) {
				Tree copy = new Tree(ast);
				Tree toChange = copy.getNodeById(node.getId());
				copy.removeSubtree(toChange);
				legalMoves.add(copy);
			}
		}

		// Modifications
		for(Tree node : ast.getPreorder()) {
			if(isTurn(node)) {
				Tree copy = new Tree(ast);
				Tree toChange = copy.getNodeById(node.getId());
				if(isTurnLeft(node)){
					toChange.setType("maze_turnRight");
				} else {
					toChange.setType("maze_turnLeft");
				}
				legalMoves.add(copy);
			}
			if(isCondition(node)) {
				for(String cond : getAllConditions()) {
					Tree copy = new Tree(ast);
					Tree toChange = copy.getNodeById(node.getId());
					toChange.setType(cond);
					legalMoves.add(copy);
				}
			}

		}

		// Inserts of new blocks (must be into a codeBlock)
		int maxId = ast.getMaxId();
		int nextId = maxId + 1;
		for(Tree parent : ast.getPreorder()) {
			if(isCodeBlockParent(parent)) {
				for(int i = 0; i < parent.getChildren().size() + 1; i++) {
					for(Tree newBlock : getAllLegalBlocks(nextId)) {
						Tree copy = new Tree(ast);
						Tree copyParent = copy.getNodeById(parent.getId());
						copyParent.addNodeBeforeIndex(i, newBlock);
						copy.reclaculateSize();
						legalMoves.add(copy);
					}

				}
			}
		}

		// Inserts of parent blocks
		if(!FileSystem.getAssnId().equals("3")) {
			for(Tree parent : ast.getPreorder()) {
				if(!FileSystem.getAssnId().equals("17")) {
					throw new RuntimeException("only designed for one assn");
				}
				if(!isCodeBlockParent(parent)) continue;
				for(int i = 0; i < parent.getChildren().size(); i++) {
					Tree tree = parent.getChildren().get(i);
					if(isBlock(tree)) {
						Tree copy = new Tree(ast);
						Tree toNest = copy.getNodeById(tree.getId());
						Tree parentCopy = copy.getNodeById(parent.getId());
						copy.removeSubtree(toNest);
						Tree forever = new Tree("maze_forever", "", 
								Collections.singletonList(toNest), nextId + "");
						parentCopy.addNodeBeforeIndex(i, forever);
						legalMoves.add(copy);
					}
				}
			}
		}

		/*// Change the order of two blocks
		for(Tree parent : ast.getPostorder()) {
			if(isCodeBlockParent(parent)) {
				for(int i = 1; i < parent.getChildren().size(); i++) {
					Tree node = parent.getChildren().get(i);
					Tree copy = new Tree(ast);

					Tree copyParent = copy.getNodeById(parent.getId());
					Tree toMove = copy.getNodeById(node.getId());
					copyParent.removeSubtree(toMove);
					copyParent.addNodeBeforeIndex(i - 1, toMove);
					copy.reclaculateSize();

					legalMoves.add(copy);
				}
			}
		}*/

		return legalMoves;
	}

	private static List<Tree> getAllLegalBlocks(int nextId) {
		List<Tree> legalBlocks = new ArrayList<Tree>();
		String assnId = FileSystem.getAssnId();
		if(assnId.equals("3")) {
			legalBlocks.add(new Tree("maze_moveForward", ""+nextId));
			legalBlocks.add(new Tree("maze_turnLeft", ""+nextId));
			legalBlocks.add(new Tree("maze_turnRight", ""+nextId));
		} else if(assnId.equals("17")) {
			legalBlocks.add(new Tree("maze_moveForward", ""+nextId));
			legalBlocks.add(new Tree("maze_turnLeft", ""+nextId));
			legalBlocks.add(new Tree("maze_turnRight", ""+nextId));
			legalBlocks.add(new Tree("maze_forever", ""+nextId));

			// Add an if/else block with isPathForward
			IdCounter ids = new IdCounter(nextId);
			Tree ifElse = makeIfElse(ids);

			legalBlocks.add(ifElse);

		} else {
			throw new RuntimeException("what are the legal blocks for this assn?");
		}
		return legalBlocks;
	}

	protected static Tree makeIfElse(IdCounter ids) {
		Tree cond = new Tree("isPathForward", ids.getNextIdStr());
		Tree doBlock = new Tree("DO", ids.getNextIdStr());
		Tree elseBlock = new Tree("ELSE", ids.getNextIdStr());
		List<Tree> ifElseChildren = new ArrayList<Tree>();
		ifElseChildren.add(cond);
		ifElseChildren.add(doBlock);
		ifElseChildren.add(elseBlock);
		Tree ifElse = new Tree("maze_ifElse", "", ifElseChildren, ids.getNextIdStr());
		return ifElse;
	}

	private static boolean isLegalAst(Tree tree) {
		for(Tree node : tree.getPreorder()) {
			if(blockRequiresChildren(node)) {
				if(node.getChildren().size() == 0){
					return false;
				}
			}
		}
		return true;
	}

	protected static boolean blockRequiresChildren(Tree t) {
		if(checkType(t, "DO")) return true;
		if(checkType(t, "ELSE")) return true;
		if(checkType(t, "maze_forever")) return true;
		return false;
	}

	public static void main(String[] args) {

		/*System.out.println("Assn 3 soln:");
		Tree assn3Soln = BlockyStringParser.parseBlockyString("MLMRM");
		for(Tree legalMove : getLegalMoves(assn3Soln)){
			System.out.println(legalMove);
		}*/

		System.out.println("Assn 17 soln:");
		FileSystem.setAssnId("17");
		Tree assn17Soln = BlockyStringParser.parseBlockyString("F[MI[a][M][M]]");
		System.out.println(assn17Soln);
		System.out.println("----");
		for(Tree legalMove : getLegalMoves(assn17Soln)){
			System.out.println(legalMove);
		}
	}




}
