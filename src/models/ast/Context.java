package models.ast;


/**
 * Class: Context
 * This is a piece of code around a subforest. By definition it is the original
 * tree (from which the subforest came) with a symbolic node replacing the
 * subforest. In practice, we represent it as the list of postorder nodes
 * before and after the subforest. This representation is not perfect in that
 * it is possible for two different contexts to have the same left and right
 * forests.
 */
public class Context {

	private Postorder left;
	private Postorder right;

	// Cache this. But be wary
	private Integer hash;
	private int size;

	public Context(Postorder left, Postorder right, Program program,
			int start, int end) {
		this.left = left;
		this.right = right;
		this.size = end - start;
	}

	@Override
	public boolean equals(Object obj) {
		Context other = (Context) obj;
		
		return toString().equals(other.toString());
		/*if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		if (hashCode() != obj.hashCode())
			return false;

		Context other = (Context) obj;

		if (!left.equals(other.left))
			return false;
		return right.shiftEquals(other.right, size, other.size);*/
	}

	public void setHashCode(int hash) {
		this.hash = hash;
	}

	@Override
	public int hashCode() {
		if (hash != null) {
			return hash;
		}
		throw new RuntimeException("should be set");
	}

	/**
	 * TODO: Context could use a much more useful toString (one which
	 * shows the context tree, with the symbolic node where the
	 * replacement goes)
	 */
	public String toString() {
		String str = "";
		str += "LEFT: " + left + "\n";
		str += "RIGHT: " + right + "\n";
		return str;
	}
}
