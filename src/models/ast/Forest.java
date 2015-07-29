package models.ast;

import java.util.*;


public class Forest {

	private List<Tree> roots;

	private int size;

	public static final Forest NULL = new Forest(Collections.<Tree> emptyList());

	// we cache the hash code and the postorder (if computed)
	private Integer hashCode = null;
	private List<Tree> postorder = null;
	
	public static Forest createForest(List<Tree> roots) {
		if(roots.isEmpty()) {
			return null;
		}
		return new Forest(roots);
	}
	
	public Forest(Tree singleRoot) {
		this.roots = Collections.singletonList(singleRoot);
		size = singleRoot.size();
	}

	public Forest(List<Tree> roots) {
		this.roots = roots;
		size = 0;
		for(Tree root : roots) {
			size += root.size();
		}
	}

	public List<Tree> getPostorder() {
		if(postorder != null) return postorder;
		postorder = new ArrayList<Tree>();

		// I have committed to putting the first root last,
		// and have stuck to that commitment :).
		for(int i = roots.size() - 1; i >= 0; i--) {
			Tree root = roots.get(i);
			postorder.addAll(root.getPostorder());
		}

		return postorder;
	}

	public int size() {
		return size;
	}

	public List<Tree> getRoots() {
		return roots;
	}

	public Tree getLastRoot() {
		if(roots.isEmpty()) return null;
		return roots.get(roots.size() - 1);
	}

	public Tree getHead() {
		if(roots.isEmpty()) return null;
		return roots.get(0);
	}

	@Override
	public String toString() {
		String str = "forest: {\n";
		for(Tree t : roots) {
			str += t.toString(1);
			str += "-----\n";
		}
		str += "}";
		return str;
	}
	
	@Override
	public boolean equals(Object o) {
		Forest other = (Forest)o;
		if(size != other.size) {
			return false;
		}
		return roots.equals(other.roots);
	}
	
	@Override
	public int hashCode() {
		if(roots.isEmpty()) {
			return 0;
		}
		return hashCode;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}

	/*******************************************************************
	 * Methods useful for finding the distance between forests. These
	 * Are the basic operations that we need.
	 *******************************************************************/

	/**
	 * Get Last Root Child Forest
	 * --------------------------
	 * Returns the children of the last root as a forest. Let v be the
	 * last root. This method returns F(v).
	 */
	public Forest getLastRootChildForest() {
		Tree lastRoot = getLastRoot();
		if(lastRoot == null) {
			return null;
		}
		List<Tree> children = lastRoot.getChildren();
		if(children.isEmpty()) {
			return null;
		}
		return new Forest(lastRoot.getChildren());
	}

	/**
	 * Get Forest Minus Last Root
	 * --------------------------
	 * Let F be the forest and v is the lass root. This method returns
	 * F - v. Note that this is different than F - T(v). The children
	 * of v are promoted to be roots.
	 */
	public Forest getForestMinusLastRoot() {
		List<Tree> newRoots = new ArrayList<Tree>();

		// Add all the previous roots
		newRoots.addAll(roots.subList(0, roots.size() - 1));

		// Add the last roots children
		Tree lastRoot = getLastRoot();
		newRoots.addAll(lastRoot.getChildren());

		if(newRoots.isEmpty()){
			return null;
		}
		return new Forest(newRoots);
	}

	/**
	 * Get Forest Minus Last Root's Tree.
	 * ---------------------------------
	 * Let F be the forest and v is the lass root. This method returns
	 * F - T(v). Note that this is different than F - v. The children
	 * of v are also removed.
	 */
	public Forest getForestMinusLastRootTree() {		
		List<Tree> newRoots = new ArrayList<Tree>();

		// Add all the previous roots
		if(roots.size() <= 1) {
			return null;
		}
		newRoots.addAll(roots.subList(0, roots.size() - 1));

		if(newRoots.isEmpty()) {
			return null;
		}
		Forest toReturn = new Forest(newRoots);
		return toReturn;
	}

	/**
	 * Forest From Postorder
	 * ---------------------
	 * Given a sublist of nodes from the postorder, turn it into a rooted subforest
	 * @param subforestPostorder
	 * @return
	 */
	public static Forest forestFromPostorder(List<Tree> postorder) {
		List<Tree> roots = new ArrayList<Tree>();
		int currIndex = postorder.size() - 1;

		while(currIndex >= 0) {
			Tree root = postorder.get(currIndex);
			roots.add(root);
			currIndex -= root.size();
		}

		throw new RuntimeException("weary");
		//return new Forest(roots);
	}

	/**
	 * Get All Suffix Subforests
	 * -------------------------
	 * What is a suffix subforest you may ask? Is all the right most nodes
	 * in a forest, plus all the suffix subforests of each root. Its useful
	 * for the recursive definition of distance with equivalence between two
	 * queries. If there are n nodes in a forest, there are exactly n suffix
	 * subforests.
	 */
	public List<Forest> getAllSuffixSubforests() {
		List<Forest> subforests = new ArrayList<Forest>();

		for(int i = 0; i < roots.size(); i++) {
			List<Tree> suffixRoots = roots.subList(i, roots.size());
			subforests.add(new Forest(suffixRoots));
		}

		for(Tree root : roots) {
			List<Tree> children = root.getChildren();
			if(!children.isEmpty()){
				Forest childForest = new Forest(children);
				subforests.addAll(childForest.getAllSuffixSubforests());
			}
		}
		return subforests;
	}


}
