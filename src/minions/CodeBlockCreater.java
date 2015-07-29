package minions;

import java.util.*;

import util.IdCounter;
import models.ast.CodeBlock;
import models.ast.Context;
import models.ast.Forest;
import models.ast.Postorder;
import models.ast.Program;
import models.ast.Tree;

/**
 * CAUTION: Since the last version I have moved from having Forests 
 * constructed by postorder, to having forests constructed by roots. 
 * Everything works, but right now Contexts contain forests which
 * were given a postorder as roots. 
 * @author cpiech
 */
public class CodeBlockCreater {

	/**
	 * Method: Create Code Blocks
	 * --------------------------
	 * The public interface to the minion.
	 */
	public static List<CodeBlock> createCodeBlocks(Program p) {
		return new CodeBlockCreater(p).run();
	}

	// Make sure all blocks get a unique id;
	private static IdCounter blockIdCounter = new IdCounter();
	private static boolean INCLUDE_EMPTY_TREE = true;

	// The program whos code blocks we are creating
	private Program p;
	private Tree ast;
	private List<Tree> postorder;

	// Precomputed and cached hash codes
	int[] hashes = null;
	int[] pow = null;


	private CodeBlockCreater(Program p) {
		this.p = p;
		this.ast = p.getAst();
		this.postorder = ast.getPostorder();
	}

	private List<CodeBlock> run() {
		/*System.out.println("The AST:");
		System.out.println(ast);
		System.out.println("@@@@@\n");*/
		precomputeHashArrays();
		List<CodeBlock> codeBlocks = new ArrayList<CodeBlock>();
		for (int j = 0; j < ast.size(); j++) {
			List<CodeBlock> blocks = makeCodeBlocks(j);
			codeBlocks.addAll(blocks);
		}

		/*for(CodeBlock block : codeBlocks) {
			Forest f = block.getForest();
			Context c = block.getContext();
			if(f.size() < 2) {
				System.out.println(f.size());
				System.out.println(f);
				System.out.println("context: ");
				System.out.println(c);
				System.out.println("\n");
				
			}
		}*/

		return codeBlocks;
	}

	private List<CodeBlock> makeCodeBlocks(int root) {
		// Get the single code block rooted at root.
		CodeBlock treeBlock = makeCodeBlock(root);

		// Get the list of code blocks including all lines that start at root.
		List<CodeBlock> multiCodeBlocks = makeMultilineCodeBlocks(root);

		// Put them all into a list.
		List<CodeBlock> codeBlocks = new ArrayList<CodeBlock>();
		codeBlocks.add(treeBlock);
		codeBlocks.addAll(multiCodeBlocks);
		return codeBlocks;
	}

	private Tree getTree(int index) {
		return postorder.get(index);
	}

	private void precomputeHashArrays() {
		if (hashes != null) return;
		int treeSize = ast.size();
		hashes = new int[treeSize + 1];
		int hashCode = 1;
		for (int i = 0; i <= treeSize; i++) {
			hashes[i] = hashCode;
			if (i < treeSize) {
				hashCode = 31 * hashCode + getTree(i).getRootHash();
			}
		}
		pow = new int[treeSize + 2];
		pow[0] = 1;
		for (int i = 1; i < treeSize + 2; i++) pow[i] = pow[i-1] * 31;
	}

	private CodeBlock makeCodeBlock(int root) {
		Forest subforest = makeForest(root);
		Context context = makeComplement(root);
		int id = blockIdCounter.getNextId();
		return new CodeBlock(p, subforest, context, id);
	}

	private List<CodeBlock> makeMultilineCodeBlocks(int i) {
		List<CodeBlock> ans = new ArrayList<CodeBlock>();
		Tree curr = postorder.get(i);
		if (shouldMakeMultiFromChildren(curr)) {

			ArrayList<Integer> partialSums = new ArrayList<Integer>();
			int partial = 0;
			partialSums.add(partial);
			while (1 + partial < postorder.get(i).size()) {
				partial += postorder.get(i-1-partial).size();
				partialSums.add(partial);
			}
			for (int j = 0; j < partialSums.size(); j++) {
				for (int k = j + 2; k < partialSums.size(); k++) {
					int start = i - partialSums.get(k);
					int end = i - partialSums.get(j);

					ans.add(makeMultilineCodeBlock(start, end));
				}

				int loc = i - partialSums.get(j);
				if (INCLUDE_EMPTY_TREE) {
					ans.add(makeEmptyLineCodeBlock(loc));
				}
			}

		}
		return ans;
	}

	private boolean shouldMakeMultiFromChildren(Tree curr) {
		if(curr.getType().equals("statementList")) return true;
		if(curr.getType().equals("program")) return true;
		return false;
	}

	private CodeBlock makeEmptyLineCodeBlock(int loc) {
		Forest forest = Forest.NULL;
		Context complement = makeComplement(loc, loc);
		int id = blockIdCounter.getNextId();
		return new CodeBlock(p, forest, complement, id);
	}

	private CodeBlock makeMultilineCodeBlock(int start, int end) {
		Forest forest = forestFromPostorder(postorder.subList(start, end));
		forest.setHashCode(hashes[end] - pow[end - start] * (hashes[start] - 1));
		Context complement = makeComplement(start, end);
		int id = blockIdCounter.getNextId();
		return new CodeBlock(p, forest, complement, id);
	}

	private Context makeContext(int upperStart, int upperEnd, int lowerStart, int lowerEnd) {

		Postorder left = new Postorder(postorder.subList(upperStart, lowerStart));
		Postorder right = new Postorder(postorder.subList(lowerEnd, upperEnd));
		
		int leftHash = hashes[lowerStart] - pow[lowerStart - upperStart] * (hashes[upperStart] - 1);
		int rightHash = hashes[upperEnd] - pow[upperEnd - lowerEnd] * (hashes[lowerEnd] - 1);
		
		Context c = new Context(left, right, p, lowerStart, lowerEnd);
		c.setHashCode(pow[upperEnd - lowerEnd + 1] * leftHash + rightHash);
		return c;

	}

	private Context makeComplement(int root) {
		int start = root - postorder.get(root).size() + 1;
		int end = root + 1;
		return makeComplement(start, end);
	}

	private Context makeComplement(int start, int end) {
		return makeContext(0, postorder.size(), start, end);
	}

	private Forest makeForest(int root) {
		
		Tree rootedTree = postorder.get(root);
		
		int start = root - rootedTree.size() + 1;
		int end = root + 1;
		Forest forest = forestFromPostorder(postorder.subList(start, end));
		forest.setHashCode(hashes[end] - pow[end - start] * (hashes[start] - 1));
	
		
		return forest;
	}
	
	private Forest forestFromPostorder(List<Tree> postorder) {
		List<Tree> roots = new ArrayList<Tree>();
		int index = postorder.size() - 1;
		while(index >= 0) {
			Tree curr = postorder.get(index);
			roots.add(0, curr);
			index -= curr.size();
		}
		return new Forest(roots);
	}
	

}
