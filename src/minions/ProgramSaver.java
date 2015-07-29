package minions;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MXBean;

import models.ast.Program;
import models.ast.Tree;
import util.FileSystem;

public class ProgramSaver {

	public static List<Program> saveUniques(Map<Program, List<Program>> programMap, String programDirName) {
		return new ProgramSaver().run(programMap, programDirName);
	}

	private Map<Program, Integer> newIdMap;
	private String programDir;
	private String countsStr;
	private String unitTestResultsStr;

	private List<Program> run(Map<Program, List<Program>> programMap, String programDir) {
		this.programDir = programDir;
		ValueComparator bvc =  new ValueComparator(programMap);
		List<Program> programList = new ArrayList<Program>();
		programList.addAll(programMap.keySet());
		Collections.sort(programList, bvc);

		System.out.println("saving uniques...");
		int id = 0;
		
		countsStr = "";
		unitTestResultsStr = "";

		newIdMap = new HashMap<Program, Integer>();
		List<Program> newPrograms = new ArrayList<Program>();
		for(Program p : programList) {

			// Get stats
			Integer count = 0;
			Integer maxUnitTest = 0;
			Boolean passed = null;
			Program original = null;
			for(Program same : programMap.get(p)) {
				count += same.getCount();
				if(passed == null) {
					passed = same.passedUnitTests();
					original = same;
				} else {

					if(passed != same.passedUnitTests()) {
						/*System.out.println("~~~~~~");
						System.out.println("discrepancy:");
						System.out.println("test A: " + same.getId());
						System.out.println("test B: " + original.getId());
						
						System.out.println("test A: " + same.getUnitTestResult());
						System.out.println("test B: " + original.getUnitTestResult());
						System.out.println("~~~~~~");*/
					}
				}
				maxUnitTest = Math.max(maxUnitTest, same.getUnitTestResult());
			}



			// Only save programs that showed up in the activities table!
			if(count > 0) {
				Program newP = new Program("" + id, p.getCode(), p.getAst(), count, maxUnitTest);
				newPrograms.add(newP);
				countsStr += id + "\t" + count +  "\n";
				unitTestResultsStr += id + "\t" + maxUnitTest + "\n";
				saveProgram(newP, id);
				newIdMap.put(p, id);
				id++;
			}
		}
		System.out.println("size " + id);
		saveCounts();
		saveUnitTestResults();
		saveMap(programMap);
		System.out.println("done!");
		return newPrograms;
	}

	private void saveMap(Map<Program, List<Program>> programMap) {
		Map<String, String> reverseMap = new HashMap<String, String>();
		for(Program newProgram : programMap.keySet()) {
			for(Program oldProgram : programMap.get(newProgram)) {
				String newId = "" + newIdMap.get(newProgram);
				String oldId = oldProgram.getId();
				reverseMap.put(oldId, newId);
			}
		}

		String text = "";
		for(String key : reverseMap.keySet()) {
			String value = reverseMap.get(key);
			text += key +","+value +"\n";
		}

		File uniqueDir = getUniqueDir();
		FileSystem.createFile(uniqueDir, "idMap.txt", text);
	}

	private void saveCounts() {
		File uniqueDir = getUniqueDir();
		FileSystem.createFile(uniqueDir, "counts.txt", countsStr);
	}

	private void saveUnitTestResults() {
		File uniqueDir = getUniqueDir();
		FileSystem.createFile(uniqueDir, "unitTestResults.txt", unitTestResultsStr);
	}

	private void saveProgram(Program p, int programId) {
		File uniqueDir = getUniqueDir();
		Tree ast = p.getAst();
		try {
			String json = TreeJasonizer.jsonify(ast).toString(4);
			FileSystem.createFile(uniqueDir, programId + ".json", json);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private File getUniqueDir() {
		File assnDir = FileSystem.getAssnDir();
		File uniqueDir = new File(assnDir, programDir);
		return uniqueDir;
	}

	class ValueComparator implements Comparator<Program> {

		Map<Program, List<Program>> programMap;
		public ValueComparator(Map<Program, List<Program>> programMap) {
			this.programMap = programMap;
		}

		// Note: this comparator imposes orderings that are inconsistent with equals.    
		public int compare(Program a, Program b) {
			if (getCount(a) < getCount(b)) {
				return 1;
			} else {
				return -1;
			} // returning 0 would merge keys
		}

		private int getCount(Program a) {
			int count = 0;
			for(Program p : programMap.get(a)) {
				count += p.getCount();
			}
			return count;
		}
	}

}
