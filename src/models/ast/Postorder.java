package models.ast;

import java.util.List;

public class Postorder {

	private List<Tree> nodes;
	
	private Integer hash = null;
	
	public Postorder(List<Tree> nodes) {
		this.nodes = nodes;
	}
	
	public int size() {
		return nodes.size();
	}
	
	public List<Tree> getNodes() {
		return nodes;
	}
	
	public void setHashCode(int hash) {
		this.hash = hash;
	}
	
	@Override
	public boolean equals(Object o){
		Postorder other = (Postorder)o;
		if(hash != other.hashCode()){
			return false;
		}
		return nodes.equals(other.nodes);
	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
	@Override
	public String toString() {
		String str = "postorder: {\n";
		int index = nodes.size() - 1;
		while(index >= 0) {
			Tree curr = nodes.get(index);
			str += toString(curr, 1);
			str += "-----\n";
			index -= curr.size();
		}
		
		str += "}";
		return str;
	}
	
	private String toString(Tree node, int indentSize) {
		if(!hasNode(node)) return "";
		
		
		String treeString = "";
		String indent = "";
		for(int i = 0; i < indentSize; i++){
			indent += "  ";
		}
		treeString += indent + node.getLabel() + "\n";
		if(!node.getChildren().isEmpty()) {
			treeString += indent + "[\n";
			for(Tree child : node.getChildren()) {
				treeString += toString(child, indentSize + 1);
			}
			treeString += indent + "]\n";
		}
		return treeString;
	}
	
	private boolean hasNode(Tree node) {
		for(Tree n : nodes) {
			if(n == node) return true;
		}
		return false;
	}

	public boolean shiftEquals(Postorder other, int size, int otherSize) {
		if (hashCode() != other.hashCode())
			return false;
		if (size() != other.size())
			return false;

		for (int index = 0; index < size(); index++) {
			int shift = index - nodes.get(index).size() < -1 ? size : 0;
			int otherShift = index - other.nodes.get(index).size() < -1 ? otherSize : 0;
			if (!nodes.get(index).shiftEquals(other.nodes.get(index), shift, otherShift))
				return false;
		}
		return true;
	}
	
}
