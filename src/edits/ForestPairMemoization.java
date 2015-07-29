package edits;

import java.util.HashMap;

import util.IdCounter;
import minions.BlockyStringParser;
import minions.TreeJasonizer;
import models.ast.Forest;
import models.ast.Tree;

/**
 * Class: Forest Pair Memoization
 * ------------------------------
 * In the recurrence relation, many sub-expressions are repeated. To
 * reduce the complexity, we cache each solved sub expression, using
 * a hash that is forest1Key:forest2Key. The key for a single forest
 * is a tuple of the start and end node from the postordering. I
 * assume that these two values uniquely identify a subforest in the
 * original query.
 */
public class ForestPairMemoization {

	HashMap<String, TreeMove> cache; 

	/**
	 * Class: Forest Pair Memoization
	 * ------------------------------
	 * The order of trees is important.
	 */
	public ForestPairMemoization(Forest a, Forest b) {
		// each node in each tree gets a special number
		cache = new HashMap<String, TreeMove>();
		IdCounter postorderId = new IdCounter();
		for(Tree t : a.getPostorder()) {
			t.setTag(postorderId.getNextIdStr());
		}
		for(Tree t : b.getPostorder()) {
			t.setTag(postorderId.getNextIdStr());
		}
	}

	public boolean isCached(Forest a, Forest b) {
		String key = getKey(a, b);
		return cache.containsKey(key);
	}

	public TreeMove get(Forest a, Forest b) {
		String key = getKey(a, b);
		return cache.get(key);
	}

	public void put(Forest a, Forest b, TreeMove move) {
		String key = getKey(a, b);
		cache.put(key, move);
	}

	private String getKey(Forest a, Forest b) {
		String key = "";
		key += getForestKey(a);
		key += ":";
		key += getForestKey(b);
		return key;
	}

	public String getForestKey(Forest f) {
		if(f == null) {
			return "null";
		}
		int end = getEnd(f);
		int start = end - f.size();
		return "(" + start + "," + end + ")";
	}

	private int getEnd(Forest a) {
		Tree firstRoot = a.getHead();
		return Integer.parseInt(firstRoot.getTag());
	}
}
