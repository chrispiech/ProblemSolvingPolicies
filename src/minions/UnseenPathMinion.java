package minions;

import java.io.File;
import java.util.*;

import util.FileSystem;

public class UnseenPathMinion {

	public static Map<String, List<String>> loadPathMap() {
		Map<String, List<String>> unseenPathMap = new HashMap<String, List<String>>();
		
		File unseenPathDir = new File(FileSystem.getAssnDir(), "unseenPaths");
		for(File f : FileSystem.listFiles(unseenPathDir)) {
			String name = FileSystem.getNameWithoutExtension(f.getName());
			List<String> lines = FileSystem.getFileLines(f);
			unseenPathMap.put(name, lines);
		}
		
		return unseenPathMap;
	}
	
}
