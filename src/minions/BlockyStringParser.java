package minions;
import java.util.*;

import util.IdCounter;
import models.ast.Tree;

public class BlockyStringParser {

	public static Tree parseBlockyString(String cmd) {
		return new BlockyStringParser(cmd).parse();
	}
	
	private IdCounter counter = null;
	private Queue<Character> tokens = null;
	
	private BlockyStringParser(String blocklyString) {
		counter = new IdCounter();
		tokens = new LinkedList<Character>();
		for(int i = 0; i < blocklyString.length(); i++) {
			char ch = blocklyString.charAt(i);
			tokens.add(ch);
		}
		
	}
	
	private Tree parse() {
		List<Tree> body = getStatementList();
		return new Tree("program", "", body, nextId());
	}
	
	private List<Tree> getStatementList() {
		List<Tree> cmds = new ArrayList<Tree>();
		while(!tokens.isEmpty() && tokens.peek() != ']') {
			char ch = eatNext();
			Tree next = null;
			
			if(ch == 'I') {
				next = getIfTree();
			} else if(ch == 'F') {
				next = getForeverTree();
			} else if(ch == 'M') {
				next = getMoveTree();
			} else if(ch == 'L') {
				next = getTurnLeftTree();
			} else if(ch == 'R') {
				next = getTurnRightTree();
			}
			cmds.add(next);
		}
		
		return cmds;
	}

	private Tree getIfTree() {
		eatNext('[');
		Tree cond = getCondition();
		eatNext(']');
		eatNext('[');
		List<Tree> ifBlock = getStatementList();
		eatNext(']');
		eatNext('[');
		List<Tree> elseBlock = getStatementList();
		eatNext(']');
		
		Tree doTree = makeTree("DO", ifBlock);
		Tree elseTree = makeTree("ELSE", elseBlock);
		
		List<Tree> children = new ArrayList<Tree>();
		children.add(cond);
		children.add(doTree);
		children.add(elseTree);
		
		return makeTree("maze_ifElse", children);
	}

	private Tree getCondition() {
		char ch = tokens.remove();
		if(ch == 'a') {
			return makeTree("isPathForward");
		} else if(ch == 'l') {
			return makeTree("isPathLeft");
		} else if(ch == 'r') {
			return makeTree("isPathRight");
		} 
		throw new RuntimeException("didn't find it");
	}

	private Tree getTurnRightTree() {
		return makeTree("maze_turnRight");
	}

	private Tree getTurnLeftTree() {
		return makeTree("maze_turnLeft");
	}

	private Tree getMoveTree() {
		return makeTree("maze_moveForward");
	}

	private Tree getForeverTree() {
		eatNext('[');
		List<Tree> body = getStatementList();
		eatNext(']');
		
		return makeTree("maze_forever", body);
	}
	
	private char eatNext() {
		char next = tokens.remove();
		return next;
	}
	
	private void eatNext(char expected) {
		char next = tokens.remove();
		if(next != expected) {
			throw new RuntimeException("Expected " + expected + ". Got " + next);
		}
	}
	
	private Tree makeTree(String nodeType) {
		return makeTree(nodeType, new ArrayList<Tree>());
	}
	
	private Tree makeTree(String nodeType, List<Tree> children) {
		return new Tree(nodeType, "", children, nextId());
	}
	
	private String nextId() {
		return counter.getNextId() + "";
	}
	
}
