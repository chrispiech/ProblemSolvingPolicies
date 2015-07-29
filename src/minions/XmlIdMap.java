package minions;

import java.io.File;
import java.util.*;

import util.FileSystem;

public class XmlIdMap {

	Map<String, List<String>> xmlIdMap = new HashMap<String, List<String>>();
	
	public XmlIdMap(List<String> dirs) {
		File assnDir = FileSystem.getAssnDir();;
		
		List<Map<String, List<String>>> reverseMaps = new ArrayList<Map<String,List<String>>>();
		
		for(String dirName : dirs) {
			File uniqueDir = new File(assnDir, dirName);
			File uniqueMapFile = new File(uniqueDir, "idMap.txt");
			Map<String, String> uniqueMap = FileSystem.getFileMapString(uniqueMapFile);
			Map<String, List<String>> reverseMap = reverseMap(uniqueMap);

			reverseMaps.add(0, reverseMap);
		}
		
		Map<String, List<String>> lastReverse = reverseMaps.get(0);
		
		for(String key : lastReverse.keySet()) {
			List<String> ids = new ArrayList<String>();
			ids.add(key);
			for(Map<String, List<String>> map : reverseMaps) {
				List<String> nextIds = new ArrayList<String>();
				for(String id : ids) {
					nextIds.addAll(map.get(id));
				}
				ids = nextIds;
			}
			xmlIdMap.put(key, ids);
		}	
	}
	
	public List<String> getXmlIds(String astId) {
		return xmlIdMap.get(astId);
	}
	
	private Map<String, List<String>> reverseMap(Map<String, String> map) {
		Map<String, List<String>> reverseMap = new HashMap<String, List<String>>();
		for(String key : map.keySet()) {
			String value = map.get(key);
			if(!reverseMap.containsKey(value)) {
				reverseMap.put(value, new ArrayList<String>());
			}
			reverseMap.get(value).add(key);
		}
		return reverseMap;
	}
	
}
