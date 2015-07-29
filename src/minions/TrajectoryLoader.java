package minions;

import java.io.File;
import java.util.*;

import util.FileSystem;
import util.Warnings;
import models.trajectory.Trajectory;

public class TrajectoryLoader {

	public static List<Trajectory> loadUnique() {
		throw new RuntimeException("depricated");
	}

	public static List<Trajectory> loadUnique(Set<String> contiguous) {
		throw new RuntimeException("depricated");
	}

	public static List<Trajectory> load(String dirName, String astDir) {
		return load(-1, null, dirName, astDir);
	}

	public static List<Trajectory> load(String dirName, String astDir, int max) {
		return load(max, null, dirName, astDir);
	}

	public static List<Trajectory> load(String dirName) {
		return load(-1, null, dirName, null);
	}
	
	public static List<Trajectory> load(String dirName, int max) {
		return load(max, null, dirName, null);
	}
	
	public static List<Trajectory> load() {
		return load("interpolated");
	}


	public static List<Trajectory> load(int max, Set<String> include, String dirName, String astDirName) {
		File assnDir = FileSystem.getAssnDir();
		File trajectoryDir = new File(assnDir, dirName);
		File countFile = new File(trajectoryDir, "counts.txt");
		Map<String, Integer> countMap = null;
		if(countFile.exists()) {
			countMap =  FileSystem.getFileMap(countFile);
		}
		Map<String, Integer> unitTestMap = ProgramLoader.loadUnitTestMap(astDirName);

		List<Trajectory> trajectories = new ArrayList<Trajectory>();
		int loaded = 0;


		for(File f : FileSystem.listNumericalFiles(trajectoryDir)) {
			String ext = FileSystem.getExtension(f.getName());
			if(ext.equals("txt") && !f.getName().equals("counts.txt")) {
				Trajectory t = loadTrajectory(f, unitTestMap, countMap, include);
				loaded++;
				trajectories.add(t);
				if(loaded % 100 == 0) {
					System.out.println("loaded: " + loaded);
				}
				if(max > 0 && loaded == max) {
					break;
				}
			}
		}

		return trajectories;
	}

	private static Trajectory loadTrajectory(
			File f, 
			Map<String, Integer> unitTestMap,
			Map<String, Integer> countMap, Set<String> include) {
		String id = FileSystem.getNameWithoutExtension(f.getName());
		int count = 1;
		if(countMap != null && countMap.containsKey(id)) {
			count = countMap.get(id);
		}
		List<String> lines = FileSystem.getFileLines(f);
		List<String> astIds = new ArrayList<String>();
		for(String line : lines) {
			String[] row = line.split(",");
			String astId = row[0];

			astIds.add(astId);
		}
		if(include == null) {
			return new Trajectory(id, astIds, count);
		}

		return new Trajectory(id, astIds, count, include);
	}

	public static Map<String, String> loadIdMap(String trajectoryDirName) {
		File trajectoryDir = new File(FileSystem.getAssnDir(), trajectoryDirName);
		File idMap = new File(trajectoryDir, "idMap.txt");
		return FileSystem.getFileMapString(idMap);
	}

	
}
