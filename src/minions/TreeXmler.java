package minions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.ast.Tree;

public class TreeXmler {

	public static String treeToXml(Tree t) throws Exception {
		return getTreeXml(t, 0);
	}

	private static String getTreeXml(Tree t, int level) throws Exception {
		if(isType(t, "statementList")) {
			return getStatementListXml(t, level);
		}

		String xml = "";
		xml += getPreamble(t);
		for(Tree c : t.getChildren()) {
			xml += getTreeXml(c, level + 1);
		}
		xml += getPostamble(t);
		return xml;
	}

	private static String getStatementListXml(Tree t, int level) throws Exception {
		List<Tree> children = t.getChildren();

		// First create the last statement in the list
		Tree oldTail = children.get(children.size() - 1);
		Tree head = new Tree(
				oldTail.getType(), 
				oldTail.getName(), 
				oldTail.getChildren(),
				oldTail.getId());

		// Then create the linked list
		for(int i = children.size() - 2; i >= 0; i--) {
			// We need a new next node
			List<Tree> nextChildren = Collections.singletonList(head);
			Tree nextNode = new Tree("next", "", nextChildren, null); 

			// Then we need to create a new head
			Tree old = children.get(i);
			List<Tree> headChildren = old.getChildren();
			headChildren.add(nextNode);
			head = new Tree(old.getType(), old.getName(), headChildren, old.getId());
		}
		return getTreeXml(head, level);
	}

	private static String getPreamble(Tree t) throws Exception {
		if(isType(t, "program")) {
			return "<xml>";
		} else if(isType(t, "maze_moveForward")) {
			return getBlockXml(t, true);
		} else if(isType(t, "maze_turn")) {
			return getBlockXml(t, true);
		} else if(isType(t, "maze_ifElse")) {
			return getBlockXml(t, true);
		} else if(isType(t, "turnLeft")) {
			return getTitleXml(t);
		} else if(isType(t, "turnRight")) {
			return getTitleXml(t);
		} else if(isType(t, "isPathForward")) {
			return getTitleXml(t);
		} else if(isType(t, "isPathLeft")) {
			return getTitleXml(t);
		} else if(isType(t, "isPathRight")) {
			return getTitleXml(t);
		}else if(isType(t, "maze_forever")) {
			return getBlockXml(t, true);
		} else if(isType(t, "statementList")) {
			return "";
		}  else if(isType(t, "next")) {
			return "<next>";
		} else if(isType(t, "DO")) {
			return "<statement name=\"DO\">";
		} else if(isType(t, "ELSE")) {
			return "<statement name=\"ELSE\">";
		}
		throw new RuntimeException("unhandled: " + t.getType());
	}

	public static String getTitleXml(Tree t) {
		String type = t.getType();
		return "<title name=\"DIR\">" + type + "</title>";
	}

	private static String getBlockXml(Tree t, boolean addId) {
		String xml = "<block ";
		if(addId) {
			xml += getIdStr(t);
		}
		xml += "type=\""+ t.getType() +"\">";
		return xml;
	}

	private static String getIdStr(Tree t) {
		return "id=\"" + t.getId() + "\" ";
	}

	private static String getPostamble(Tree t) {
		if(isType(t, "program")) {
			return "</xml>";
		} else if(isType(t, "maze_moveForward")) {
			return "</block>";
		} else if(isType(t, "maze_turn")) {
			return "</block>";
		} else if(isType(t, "statementList")) {
			return "";
		} else if(isType(t, "turnLeft")) {
			return "";
		} else if(isType(t, "turnRight")) {
			return "";
		} else if(isType(t, "isPathForward")) {
			return "";
		} else if(isType(t, "isPathLeft")) {
			return "";
		} else if(isType(t, "isPathRight")) {
			return "";
		} else if(isType(t, "next")) {
			return "</next>";
		} else if(isType(t, "maze_forever")) {
			return "</block>";
		} else if(isType(t, "maze_ifElse")) {
			return "</block>";
		} else if(isType(t, "DO")) {
			return "</statement>";
		} else if(isType(t, "ELSE")) {
			return "</statement>";
		}
		throw new RuntimeException("unhandled: " + t.getType());
	}

	public static boolean isType(Tree t, String type) {
		return t.getType().equals(type);
	}

}
