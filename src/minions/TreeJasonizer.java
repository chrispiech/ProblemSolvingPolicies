package minions;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import models.ast.Context;
import models.ast.Forest;
import models.ast.Program;
import models.ast.Tree;

/**
 * Class Jasonizer
 * The logic behind taking a tree-structure and returning the corresponding 
 * json tree.
 * I image this will be used to save both subforests and contexts.
 */
public class TreeJasonizer {
	
	public static JSONObject jsonify(Forest forest) {
		JSONObject json = new JSONObject();
		
		JSONArray roots = new JSONArray();
		for(Tree root : forest.getRoots()) {
			JSONObject rootJson = jsonify(root);
			roots.put(rootJson);
		}
		json.append("roots", roots);
		
		return json;
	}

	public static JSONObject jsonify(Tree tree) {
		JSONObject json = new JSONObject();
		json.put("type", tree.getType());
		if(tree.getId() != null) {
			json.put("id", tree.getId());
		}
		if(tree.hasName()) {
			json.append("name", tree.getName());
		}

		JSONArray children = new JSONArray();
		for(Tree c : tree.getChildren()) {
			JSONObject childJson = jsonify(c);
			children.put(childJson);
		}
		if(children.length() > 0) {
			json.put("children", children);
		}
		return json;
	}
	
	public static JSONObject jsonify(Context context) {
		throw new RuntimeException("not quite ready");
	}

}
