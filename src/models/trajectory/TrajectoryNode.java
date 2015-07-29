package models.trajectory;


public class TrajectoryNode {

	private int index;
	private String programId;
	private TrajectoryNode next;
	private Trajectory trajectory;
	
	public TrajectoryNode(
			int index, 
			String programId, 
			TrajectoryNode next,
			Trajectory trajectory) {
		super();
		this.index = index;
		this.programId = programId;
		this.next = next;
		this.trajectory = trajectory;
	}

	public int getIndex() {
		return index;
	}

	public String getProgramId() {
		return programId;
	}

	public TrajectoryNode getNext() {
		return next;
	}

	public Trajectory getTrajectory() {
		return trajectory;
	}
	
	/*@Override
	public int hashCode() {
		return programId.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		TrajectoryNode other = (TrajectoryNode)o;
		return other.programId.equals(programId);
	}*/
	
}
