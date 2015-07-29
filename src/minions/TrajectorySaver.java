package minions;

import java.io.File;
import java.util.*;

import util.FileSystem;
import util.IdCounter;
import models.trajectory.Trajectory;
import models.trajectory.TrajectoryNode;

public class TrajectorySaver {
	
	public static void save(List<Trajectory> tjs, String dirName, String idMapTxt) {
		new TrajectorySaver().saveAll(tjs, dirName, idMapTxt);

	}

	private IdCounter c = new IdCounter();
	private String countsTxt = "";
	
	private void saveAll(List<Trajectory> tjs, String dirName, String idMapTxt) {
		File assnDir = FileSystem.getAssnDir();
		File tjDir = new File(assnDir, dirName);
		
		int done = 0;
		for(Trajectory t : tjs) {
			saveTrajectory(t, tjDir);
			if(++done % 100 == 0)  System.out.println(done);
		}
		
		FileSystem.createFile(tjDir, "counts.txt", countsTxt);
		FileSystem.createFile(tjDir, "idMap.txt", idMapTxt);
	}

	private void saveTrajectory(Trajectory t, File tjDir) {
		String txt = "";
		for(TrajectoryNode n : t.getNodeList()) {
			txt += n.getProgramId() + "\n";
		}
		txt = txt.trim();
		
		String id = c.getNextId() + "";
		String fileName = id + ".txt";
		int count = t.getCount();
		FileSystem.createFile(tjDir, fileName, txt);
		countsTxt += id + "\t" + count + "\n";
	}

}
