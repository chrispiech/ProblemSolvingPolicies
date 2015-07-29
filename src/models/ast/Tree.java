package models.ast;

import java.util.*;

public class Tree {

	// What a user thinks we store
	private String name;
	private String type;
	private List<Tree> children;
	private String id;

	// What we also store
	private int size;
	
	// Users can optinally tag trees
	private String tag = "";

	public static final Tree NULL = new Tree("null", "", Collections.<Tree> emptyList(), null);

	public Tree(String type, String nodeId) {
		this.type = type;
		this.name = "";
		this.children = new ArrayList<Tree>();
		this.id = nodeId;
		size = 1;
	}
	
	public Tree(String type, String name, List<Tree> children, String nodeId) {
		this.type = type;
		this.name = name;
		if(children == null) {
			this.children = new ArrayList<Tree>();
		} else {
			this.children = children;
		}
		this.id = nodeId;
		size = 1;
		for(Tree child : this.children) {
			size += child.size;
		}
	}

	// Deep copy
	public Tree(Tree ast) {
		this.type = ast.type;
		this.name = ast.name;
		this.id = ast.id;
		this.size = ast.size;

		children = new ArrayList<Tree>();
		for(Tree c : ast.children) {
			Tree copy = new Tree(c);
			children.add(copy);
		}
	}

	public List<Tree> getChildren() {
		return children;
	}

	public int size() {
		return size;
	}

	public boolean isLeaf() {
		return children.isEmpty();
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public String getTag() {
		return tag;
	}

	public List<Tree> getPostorder() {
		List<Tree> postorder = new ArrayList<Tree>();
		for(Tree child : children) {
			postorder.addAll(child.getPostorder());
		}
		postorder.add(this);
		return postorder;
	}
	
	public List<Tree> getPreorder() {
		List<Tree> preorder = new ArrayList<Tree>();
		preorder.add(this);
		for(Tree child : children) {
			preorder.addAll(child.getPreorder());
		}
		return preorder;
	}

	public String getType() {
		return type;
	}

	public String getLabel() {
		if(name.isEmpty()) { 
			return type;
		}
		return type + ":" + name;
	}

	public String getName() {
		return name;
	}

	public boolean hasName() {
		return !name.isEmpty();
	}

	public String getId() {
		return id;
	}
	
	public boolean hasSubtree(Tree subtree) {
		if(this == subtree) return true;
		for(Tree child : children) {
			if(child.hasSubtree(subtree)) {
				return true;
			}
		}
		return false;
	}

	public Tree getPreviousTree(Tree subtree) {
		Tree parent = getParent(subtree);
		if(parent == null) return null;
		List<Tree> siblings = parent.children;
		int index = 0;
		// Can't use indexOf because need == not .equals
		for(int i = 0; i < siblings.size(); i++) {
			Tree child = siblings.get(i);
			if(child == subtree) {
				index = i;
			}
		}
		if(index == 0) return null;
		return siblings.get(index - 1);
	}

	public boolean isRightmostSubtree(Tree subtree) {
		Tree curr = this;
		while(curr != null) {
			if(curr == subtree) {
				return true;
			}
			curr = curr.getRightmostChild();
		}
		return false;
	}

	public boolean isLeftmostSubtree(Tree subtree) {
		Tree curr = this;
		while(curr != null) {
			if(curr == subtree) {
				return true;
			}
			curr = curr.getLeftmostChild();
		}
		return false;
	}

	public Tree getRightmostChild() {
		if(children.isEmpty()) return null;
		return children.get(children.size() - 1);
	}

	public Tree getLeftmostChild() {
		if(children.isEmpty()) return null;
		return children.get(0);
	}

	public boolean shiftEquals(Tree other, int shift, int otherShift) {
		this.size -= shift;
		other.size -= otherShift;
		boolean result = this.equals(other);
		this.size += shift;
		other.size += otherShift;
		return result;
	}

	public Tree getParent(Tree subtree) {
		for(Tree child : children) {
			if(child == subtree) {
				return this;
			}
			Tree childParent = child.getParent(subtree);
			if(childParent != null) {
				return childParent;
			}
		}
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}
		Tree other = (Tree) o;
		String thisStr = this.toString();
		String otherStr = other.toString();
		return thisStr.equals(otherStr);
	}

	public int getRootHash() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {
		return toString(0);
	}

	protected String toString(int indentSize) {
		String treeString = "";
		String indent = "";
		for(int i = 0; i < indentSize; i++){
			indent += "  ";
		}
		treeString += indent + getLabel() + "\n";
		if(!children.isEmpty()) {
			treeString += indent + "[\n";
			for(Tree child : children) {
				treeString += child.toString(indentSize + 1);
			}
			treeString += indent + "]\n";
		}
		return treeString;
	}

	public boolean isType(String name) {
		return type.equals(name);
	}

	public Tree getNodeById(String id) {
		if(this.id == id) return this;
		for(Tree c : children) {
			Tree node = c.getNodeById(id);
			if(node != null) return node;
		}
		return null;
	}

	////////////// EDITABLE TREES ONLY //////////////////

	public void setType(String newType) {
		this.type = newType;
	}


	public boolean removeSubtree(Tree toRemove) {
		Integer removeIndex = null;
		for(int i = 0; i < children.size(); i++) {
			if(children.get(i) == toRemove) {
				removeIndex = i;
			}
		}
		if(removeIndex != null) {
			children.remove((int)removeIndex);
			reclaculateSize();
			return true;
		}

		for(Tree c : children) {
			if(c.removeSubtree(toRemove)) {
				reclaculateSize();
				return true;
			}
		}

		return false;
	}

	public void reclaculateSize() {
		for(Tree c : children) {
			c.reclaculateSize();
		}
		size = 1;
		for(Tree c : children) {
			size += c.size;
		}

	}

	public void addNodeBeforeIndex(int i, Tree block) {
		children.add(i, block);
		reclaculateSize();
	}

	public int getMaxId() {
		int max = Integer.parseInt(id);
		for(Tree c : children) {
			int m = c.getMaxId();
			if(m > max) {
				max = m;
			}
		}
		return max;
	}

	public Tree getChild(int i) {
		return children.get(i);
	}

	public int getNumChildren() {
		return children.size();
	}

}
